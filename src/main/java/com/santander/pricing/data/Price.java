package com.santander.pricing.data;

public class Price {

    private Integer id;
    private Instrument instrument;

    public Price(Integer id, Instrument instrument) {
        this.id = id;
        this.instrument = instrument;
    }

    public Integer getId() {
        return id;
    }

    public Instrument getInstrument() {
        return instrument;
    }
}
