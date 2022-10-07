package com.santander.pricing.messaging;

@FunctionalInterface
public interface MessageListenerCallback<Messages> {
    void process(Messages messages);
}
