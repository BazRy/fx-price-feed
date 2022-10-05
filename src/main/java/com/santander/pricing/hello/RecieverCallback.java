package com.santander.pricing.hello;

@FunctionalInterface
public interface RecieverCallback {

    void process(String email);
}
