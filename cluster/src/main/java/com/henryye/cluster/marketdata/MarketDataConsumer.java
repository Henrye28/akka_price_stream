package com.henryye.cluster.marketdata;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.cluster.typed.ClusterSingleton;
import akka.cluster.typed.SingletonActor;
import com.henryye.cluster.Tuple;
import com.henryye.cluster.actor.SingletonWorkManger;
import com.henryye.cluster.frontend.RequestManager;
import com.henryye.cluster.model.Message;
import com.henryye.cluster.model.PriceRequest;
import com.henryye.cluster.model.Task;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

@Slf4j
public class MarketDataConsumer {

    private ArrayBlockingQueue<Tuple<String, Double>> priceChannel;
    private ActorSystem actorSystem;
    private RequestManager requestManager;

    @SneakyThrows
    public MarketDataConsumer(@Qualifier("priceChannel") ArrayBlockingQueue<Tuple<String, Double>> priceChannel,
                              ActorSystem actorSystem,
                              RequestManager requestManager) {
        this.priceChannel = priceChannel;
        this.actorSystem = actorSystem;
        this.requestManager = requestManager;
    }

    public void start(){
        log.info("Starting market data consumer...");
        new Thread(() -> {
            try {
                while(true){
                    Tuple<String, Double> marketPriceData = priceChannel.take();
                    Map<String, PriceRequest> stringPriceRequestMap = requestManager.priceRequestMap();
                    String coin = marketPriceData.getT1();
                    if(stringPriceRequestMap.containsKey(coin) &&
                            StringUtils.hasLength(stringPriceRequestMap.get(coin).quantity)){

                        singletonManagerProxy(actorSystem)
                                .tell(Task.builder()
                                        .coinId(coin)
                                        .price(Double.valueOf(marketPriceData.getT2()))
                                        .quantity(Double.valueOf(stringPriceRequestMap.get(coin).quantity))
                                        .build());

                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public ActorRef<Message> singletonManagerProxy(ActorSystem actorSystem){
        SingletonActor<Message> globalPriceManger = SingletonActor.of(SingletonWorkManger.create(), "GlobalPriceManger");
        return ClusterSingleton.get(actorSystem).init(globalPriceManger);
    }

}
