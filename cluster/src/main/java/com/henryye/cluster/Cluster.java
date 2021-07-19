package com.henryye.cluster;

import com.henryye.cluster.config.ControllerConfig;
import com.henryye.cluster.config.MarketDataConfig;
import com.henryye.cluster.marketdata.MarketDataConsumer;
import com.henryye.cluster.marketdata.MarketDataProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Import({ControllerConfig.class, MarketDataConfig.class})
public class Cluster {

	public static void main(String[] args) {
		SpringApplication.run(Cluster.class, args);
	}


	@Bean
	public CommandLineRunner start(MarketDataProvider marketDataProvider, MarketDataConsumer marketDataConsumer) {
		return (args) -> {
			marketDataProvider.start();
			marketDataConsumer.start();
		};
	}
}
