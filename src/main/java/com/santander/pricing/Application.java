
package com.santander.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

/**
 * Rudimentary spring boot application,  with minimal config to enable connection to activemq instance
 */
@SpringBootApplication
public class Application {


	public static void main(String[] args) {
		// Launch the spring boot app
		final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

		// Send prices to the prices queue
		final String csvPrices = "106,EUR/USD,1.1000,1.2000,01-06-2020 12:01:01:001\n107,EUR/JPY,119.60,119.90,01-06-2020 12:01:02:002\n108,GBP/USD,1.2500,1.2560,01-06-2020 12:01:02:002\n" +
				"109,GBP/USD,1.2499,1.2561,01-06-2020 12:01:02:100\n110,EUR/JPY,119.61,119.91,01-06-2020 12:01:02:110";
		context.getBean(JmsTemplate.class).convertAndSend("prices", csvPrices);
	}

}
