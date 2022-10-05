package com.santander.pricing.hello;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Receiver {

	private final List<RecieverCallback> callbacks = new ArrayList<>();

	public void registerCallback(RecieverCallback callback) {
		this.callbacks.add(callback);
	}

	@JmsListener(destination = "prices", containerFactory = "myFactory")
	public void onMessage(String rawPrices) {
		System.out.println("Received <" + rawPrices + ">");
		callbacks.forEach(callback -> callback.process(rawPrices));
	}

}
