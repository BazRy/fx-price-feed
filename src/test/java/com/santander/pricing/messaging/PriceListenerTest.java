package com.santander.pricing.messaging;

import com.google.common.collect.Lists;
import com.santander.pricing.data.Price;
import com.santander.pricing.process.PriceTransformer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.santander.pricing.TestConstants.csvPrices;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceListenerTest {

	private static final int EXPECTED_LINES = 5;
	final PriceListener listener = new PriceListener();
	final MessageListenerCallback<List<String>> callback = rawPrices -> process(rawPrices);

	@BeforeAll
	void setup() {
		listener.registerCallback(callback);
	}

	final List<Price> prices = Lists.newArrayList();

	@Test
	void testOnMessage() {
		//act
		listener.onMessage(csvPrices);

		//assert
		//basic test to assert we're getting expected number of elements back from csv
		assertEquals(EXPECTED_LINES, prices.size());
	}

	private void process(final List<String> rawPrices) {
		this.prices.addAll(PriceTransformer.transform(rawPrices));
	}

}
