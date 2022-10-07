package com.santander.pricing.data;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public enum Instrument {
    EUR_USD ("EUR/USD"),
    GBP_USD ("GBP/USD"),
    EUR_JPY ("EUR/JPY");

    private String instrumentId;

    Instrument (final String instrumentId) {
        this.instrumentId = instrumentId;
    }

    private static final Map<String, Instrument> instrumentsById =  Maps.newHashMapWithExpectedSize(Instrument.values().length);

    static {
        for (Instrument instrument : Instrument.values()) {
            instrumentsById.put(instrument.getInstrumentId(), instrument);
        }
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public static Instrument lookupByDisplayName(String name) {
        return instrumentsById.get(name);
    }
}