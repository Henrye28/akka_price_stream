package com.henryye.cluster.config;

import akka.actor.typed.ActorSystem;
import com.henryye.cluster.Tuple;
import com.henryye.cluster.frontend.RequestManager;
import com.henryye.cluster.marketdata.MarketDataConsumer;
import com.henryye.cluster.marketdata.MarketDataProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ArrayBlockingQueue;

@Import(FrontEndConfig.class)
@Configuration
public class MarketDataConfig {

    @Bean(name = "priceChannel")
    public ArrayBlockingQueue<Tuple<String, Double>> priceChannel(){
        return new ArrayBlockingQueue<>(100);
    }


    @Bean
    public MarketDataProvider marketDataProvider(@Qualifier("priceChannel") ArrayBlockingQueue<Tuple<String, Double>> priceChannel){
        return new MarketDataProvider(priceChannel);
    }

    @Bean
    public MarketDataConsumer marketDataConsumer(@Qualifier("priceChannel") ArrayBlockingQueue<Tuple<String, Double>> priceChannel,
                                                 @Qualifier("clusterSystem") ActorSystem actorSystem,
                                                 RequestManager requestManager){
        return new MarketDataConsumer(priceChannel, actorSystem, requestManager);
    }

}
