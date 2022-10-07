package com.santander.pricing.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PriceListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceListener.class);

    private final List<MessageListenerCallback> callbacks = new ArrayList<>();

    public void registerCallback(final MessageListenerCallback callback) {
        this.callbacks.add(callback);
    }

    @Override
    @JmsListener(destination = "prices", containerFactory = "connectionFactory")
    public void onMessage(final String message) {
        if (StringUtils.isEmpty(message)){
            LOGGER.info("Message is empty, nothing to process");
            return;
        }
        final List<String> rawPrices = message.lines().collect(Collectors.toList());
        //although all prices are passed back,  there may be multiples for some instruments,  the caller decides what to keep or discard
        callbacks.forEach(callback -> callback.process(rawPrices));
    }
}
