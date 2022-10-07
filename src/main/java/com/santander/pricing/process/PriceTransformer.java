package com.santander.pricing.process;

import com.google.common.collect.Lists;
import com.santander.pricing.data.Instrument;
import com.santander.pricing.data.Price;
import com.santander.pricing.data.PriceAdjustment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Perform basic transformation of a CSV string of price information and return a List of {@code Price} objects;
 * It is assumed that most of the data is well-formed and of the correct type.
 * Only ask, bid and instrument will be checked for correctness,  primarily as instrument is used as the key for storage and
 * bid and ask will need to have an adjustment applied
 */
public class PriceTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceTransformer.class);

    private static final int EXPECTED_NUMBER_ELEMENTS = 5;
    private static final String DELIMETER = ",";
    private static final int ID_IDX = 0;
    private static final int INSTRUMENT_IDX = 1;
    private static final int BID_IDX = 2;
    private static final int ASK_IDX = 3;
    private static final int PRICETIME_IDX = 4;

    public static List<Price> transform(final List<String> rawPrices) {
        List<Price> prices = Lists.newArrayList();
        for (String rawPriceData : rawPrices) {
            Optional<Price> price = convertStringToPrice(rawPriceData);
            if(price.isPresent()) {
                prices.add(price.get());
            }
        }
        return prices;
    }

    /**
     * Rudimentary price sting converter.
     * Performs basic checking on number of elements is expected
     * @param rawPriceData
     * @return list of {@code Price} objects
     */
    private static Optional<Price> convertStringToPrice (final String rawPriceData) {
        if(StringUtils.isEmpty(rawPriceData)) {
            LOGGER.info("Raw price data is empty");
            return Optional.empty();
        }

        final List<String> dataBits = Lists.newArrayList(rawPriceData.split(DELIMETER));
        if (dataBits.size() < EXPECTED_NUMBER_ELEMENTS) {
            LOGGER.info("Raw price data does not contain expected number of elements: " + rawPriceData);
            return Optional.empty();
        }

        //There's undoubtedly a better way to do this with a parser
        final Integer id = Integer.parseInt(dataBits.get(ID_IDX));
        final Instrument instrument = Instrument.lookupByDisplayName(dataBits.get(INSTRUMENT_IDX));
        final BigDecimal bid = adjustPrice(PriceAdjustment.BID, new BigDecimal(dataBits.get(BID_IDX)));
        final BigDecimal ask = adjustPrice(PriceAdjustment.ASK, new BigDecimal(dataBits.get(ASK_IDX)));
        final String priceTime = dataBits.get(PRICETIME_IDX);

        return Optional.of(Price.PriceBuilder.price()
                .withId(id)
                .withInstrument(instrument)
                .withBid(bid)
                .withAsk(ask)
                .withPriceTime(priceTime)
                .build());
    }

    private static BigDecimal adjustPrice(final PriceAdjustment adjustment, final BigDecimal currentPrice) {

        final MathContext mc = new MathContext(5, RoundingMode.HALF_UP);
        final double adjustmentPercent = adjustment.getAdjustmentPercent();
        final BigDecimal adjustmentAmount = new BigDecimal(adjustmentPercent / 100.0, mc);
        final BigDecimal percentageAmount = currentPrice.multiply(adjustmentAmount, mc);
        BigDecimal adjustedPrice = currentPrice.add(percentageAmount, mc);

        return adjustedPrice;
    }
}
