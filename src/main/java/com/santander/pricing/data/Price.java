package com.santander.pricing.data;

import java.math.BigDecimal;
import java.util.Objects;

public class Price {

    private Integer id;
    private Instrument instrument;
    private BigDecimal bid;
    private BigDecimal ask;
    private String priceTime;

    private Price(Integer id, Instrument instrument, BigDecimal bid, BigDecimal ask, String priceTime)  {
        this.id = id;
        this.instrument = instrument;
        this.bid = bid;
        this.ask = ask;
        this.priceTime = priceTime;
    }

    public Integer getId() {
        return id;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public void setPriceTime(String priceTime) {
        this.priceTime = priceTime;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", instrument=" + instrument +
                ", bid=" + bid +
                ", ask=" + ask +
                ", priceTime=" + priceTime +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(id, price.id) && instrument == price.instrument && Objects.equals(bid, price.bid) && Objects.equals(ask, price.ask) && Objects.equals(priceTime, price.priceTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instrument, bid, ask, priceTime);
    }

    public static final class PriceBuilder {
        private Integer id;
        private Instrument instrument;
        private BigDecimal bid;
        private BigDecimal ask;
        private String priceTime;

        private PriceBuilder() {
        }

        public static PriceBuilder price() {
            return new PriceBuilder();
        }

        public PriceBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public PriceBuilder withInstrument(Instrument instrument) {
            this.instrument = instrument;
            return this;
        }

        public PriceBuilder withBid(BigDecimal bid) {
            this.bid = bid;
            return this;
        }

        public PriceBuilder withAsk(BigDecimal ask) {
            this.ask = ask;
            return this;
        }

        public PriceBuilder withPriceTime(String priceTime) {
            this.priceTime = priceTime;
            return this;
        }

        public Price build() {
            return new Price(id, instrument, bid, ask, priceTime);
        }
    }
}
