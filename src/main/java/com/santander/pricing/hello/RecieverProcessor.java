package com.santander.pricing.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecieverProcessor {

    @Autowired
    public RecieverProcessor(Receiver receiver) {
        receiver.registerCallback(email -> process(email));
    }

    private void process(String email) {
        System.out.println("Processing <" + email + ">");
    }
}
