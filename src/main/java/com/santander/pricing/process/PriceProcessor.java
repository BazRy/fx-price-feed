package com.santander.pricing.process;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.santander.pricing.data.Instrument;
import com.santander.pricing.data.Price;
import com.santander.pricing.messaging.MessageListenerCallback;
import com.santander.pricing.messaging.PriceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handler of Price objects populated from inbound string messages.
 * A callback is registered with the message listener and the raw string data is received and processed.
 * A json representation of the structure is sent to a downstream endpoint for consumption
 * The latest instrument price information is preserved to furnish price GET requests
 */
@Component
public class PriceProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceProcessor.class);
    protected static final String HOST = "localhost";
    protected static final int PORT = 8080;
    protected static final String URL_PATH = "/prices";

    final MessageListenerCallback<List<String>> callback = rawPrices -> process(rawPrices);

    final Map<Instrument, Price> latestInstrumentPrices = Maps.newConcurrentMap();

    @Autowired
    public PriceProcessor(PriceListener listener) {
        listener.registerCallback(callback);
    }

    private void process(final List<String> rawPrices) {

        final List<Price> prices = PriceTransformer.transform(rawPrices);
        if (prices.isEmpty()) {
            LOGGER.info("No prices to send to end point");
            return;
        }
        final List<Price> latestPricesByInstrument = filterPricesByInstrumentId(prices);
        final String json = new Gson().toJson(latestPricesByInstrument);
        LOGGER.info("Processing <" + json + ">");

        try {
            final HttpClient client = HttpClient.newHttpClient();
            final String fullUrl = (new StringBuilder("http://")).append(HOST).append(":").append(PORT).append(URL_PATH).toString();
            final HttpRequest postRequest = HttpRequest.newBuilder(new URI(fullUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            client.send(postRequest, HttpResponse.BodyHandlers.ofString());

            //finally update the latest prices map
            addPricesToLatestInstrumentPricesMap(prices);

        } catch (IOException | InterruptedException | URISyntaxException ex) {
            LOGGER.error("Error sending prices to endpoint", ex);
        }
    }

    /**
     * Send a copy of prices to caller
     * @return list of latest prices
     */
    public List<Price> getAllPrices () {
        return Lists.newArrayList(latestInstrumentPrices.values());
    }

    private void addPricesToLatestInstrumentPricesMap(List<Price> prices) {
        prices.forEach(price -> latestInstrumentPrices.put(price.getInstrument(), price));
    }

    /**
     * Some instrument may have multiple entries, we will filter out the older prices and preserve the price with the highest ID for an instrument
     * @param allPrices
     * @return latest prices
     */
    private List<Price> filterPricesByInstrumentId(List<Price> allPrices) {
        return Lists.newArrayList(allPrices.stream()
                .collect(Collectors.toMap(Price::getInstrument, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(Price::getId)))).values());
    }
}
