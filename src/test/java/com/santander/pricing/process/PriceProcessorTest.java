package com.santander.pricing.process;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.santander.pricing.data.Instrument;
import com.santander.pricing.data.Price;
import com.santander.pricing.messaging.PriceListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

import static com.santander.pricing.TestConstants.csvPrices;
import static com.santander.pricing.process.PriceProcessor.PORT;
import static com.santander.pricing.process.PriceProcessor.URL_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PriceProcessorTest {

    private static final MockWebServer webServer = new MockWebServer();
    private final PriceListener priceListener = new PriceListener();
    private static final int RESPONSE_CODE = 200;
    private static final String EXPECTED_METHOD = "POST";

    @BeforeAll
    static void setup () throws Exception{
        webServer.start(PORT);
        webServer.enqueue(new MockResponse().setResponseCode(RESPONSE_CODE));
    }

    @AfterAll
    static void shutdown () throws Exception{
        webServer.close();
    }

    @Test
    void validatePostRequestToClientEndpointAsExpected () throws Exception{
        //assemble
        final PriceProcessor priceProcessor = new PriceProcessor(priceListener);

        //act
        //really we should mock the listener here instead of invoking it for the purpose of calling back to the processor
        priceListener.onMessage(csvPrices);

        //assert
        final List<Price> actualLatestPrices = priceProcessor.getAllPrices();
        final List<Price> expectedLatestPrices = constructExpectedLatestPrices();
        assertEquals(expectedLatestPrices.size(), actualLatestPrices.size());
        assertTrue(actualLatestPrices.containsAll(expectedLatestPrices));
        final RecordedRequest recordedRequest = webServer.takeRequest();
        assertEquals(URL_PATH, recordedRequest.getPath());
        assertEquals(EXPECTED_METHOD, recordedRequest.getMethod());

        //get the json from the request and assert it's as expected
        final List<String> expectedJsonFromRequest = constructExpectedJsonObjects();
        final List<String> actualJsonFromRequest = Lists.newArrayList();
        final String requestBody = getStringFromPostRequest(recordedRequest);
        final Gson gson = new Gson();
        gson.fromJson(requestBody, List.class).forEach(o -> actualJsonFromRequest.add(gson.toJson(o)));
        assertEquals(expectedJsonFromRequest.size(), actualJsonFromRequest.size());
        assertTrue(actualJsonFromRequest.containsAll(expectedJsonFromRequest));
    }

    private List<Price> constructExpectedLatestPrices () {
        final List<Price> expectedLatestPrices = Lists.newArrayList();
        expectedLatestPrices.add(Price.PriceBuilder.price().withId(106).withInstrument(Instrument.EUR_USD).withBid(BigDecimal.valueOf(1.0989)).withAsk(BigDecimal.valueOf(1.2012))
                .withPriceTime("01-06-2020 12:01:01:001").build());
        expectedLatestPrices.add(Price.PriceBuilder.price().withId(109).withInstrument(Instrument.GBP_USD).withBid(BigDecimal.valueOf(1.2487)).withAsk(BigDecimal.valueOf(1.2574))
                .withPriceTime("01-06-2020 12:01:02:100").build());
        expectedLatestPrices.add(Price.PriceBuilder.price().withId(110).withInstrument(Instrument.EUR_JPY).withBid(BigDecimal.valueOf(119.49)).withAsk(BigDecimal.valueOf(120.03))
                .withPriceTime("01-06-2020 12:01:02:110").build());
        return expectedLatestPrices;
    }
    private String getStringFromPostRequest (final RecordedRequest recordedRequest) throws Exception{
        final Buffer buffer = recordedRequest.getBody();
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        buffer.copyTo(outStream);
        return new String(outStream.toByteArray());
    }

    private List<String> constructExpectedJsonObjects() {
        return Lists.newArrayList("{\"id\":106.0,\"instrument\":\"EUR_USD\",\"bid\":1.0989,\"ask\":1.2012,\"priceTime\":\"01-06-2020 12:01:01:001\"}",
                "{\"id\":110.0,\"instrument\":\"EUR_JPY\",\"bid\":119.49,\"ask\":120.03,\"priceTime\":\"01-06-2020 12:01:02:110\"}",
                "{\"id\":109.0,\"instrument\":\"GBP_USD\",\"bid\":1.2487,\"ask\":1.2574,\"priceTime\":\"01-06-2020 12:01:02:100\"}");
    }
}
