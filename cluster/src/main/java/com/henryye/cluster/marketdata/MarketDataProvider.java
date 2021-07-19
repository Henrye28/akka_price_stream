package com.henryye.cluster.marketdata;

import com.google.common.collect.ImmutableList;
import com.henryye.cluster.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MarketDataProvider {

    /***
     * SOD Coin Data
     * Coin name, sod price, mean price and volatility
     *
     * mean price and volatility are set for brownian motion simulation purpose
     * assumed mean price and volatility are calculated by daily close price
     */
    private final List<MarketData> sodStockDataList = ImmutableList.of(
            new MarketData("BTC", 31345d, 0.05d, 30d),
            new MarketData("ETH", 1886d, 0.2d, 420d),
            new MarketData("ADA", 1.17d, 0.1d, 20d),
            new MarketData("DOT", 12.34d, 0.09d, 80d),
            new MarketData("LINK", 15.19d, 0.15d, 110d));

    ExecutorService executorService;
    private ArrayBlockingQueue<Tuple<String, Double>> priceChannel;

    public MarketDataProvider(ArrayBlockingQueue<Tuple<String, Double>> priceChannel) {
        this.priceChannel = priceChannel;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public void start() {
        log.info("Start publishing securityId price");
        sodStockDataList.forEach(t -> {
            executorService.submit(new MarketDataStream(priceChannel, t.getSodPrice(), t, new Timer()));
        });
    }

}
