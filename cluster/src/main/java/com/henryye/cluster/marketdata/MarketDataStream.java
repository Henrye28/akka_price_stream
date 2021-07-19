package com.henryye.cluster.marketdata;

import com.henryye.cluster.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

@Slf4j
public class MarketDataStream extends TimerTask {

    private ArrayBlockingQueue priceChannel;
    private MarketData marketData;
    private final Timer timer;
    private double currentPrice;


    public MarketDataStream(ArrayBlockingQueue<Tuple<String, Double>> priceChannel, double currentPrice, MarketData marketData, Timer timer) {
        this.priceChannel = priceChannel;
        this.currentPrice = currentPrice;
        this.marketData = marketData;
        this.timer = timer;
    }

    private double geometricBrownianMotion(double currentPrice, double delay, double mean, double vol){
        double t = delay / (43200 * 1000); // secs of one day
        double nextPrice = currentPrice + currentPrice * (mean * t + vol * Math.sqrt(t) * new Random().nextGaussian());
        return nextPrice;
    }

    /**
     * Ref Value provider here only provides raw stock price data
     *
     * To mock the real market data streaming:
     * - Each security has a thread to publish its price with random interval.
     * - Each security has a new random interval once a price tick is published.
     * - Each security price move is simulated by brownian motion
     */
    @Override
    public void run() {
        try {
            /**
             *  TODO: Price ticking should be published to a queue and consumed by consumers from referencePriceSource for decoupling purpose
             */
            double delay = (Math.random() + 1) * 1000;
            currentPrice = geometricBrownianMotion(currentPrice, delay, marketData.getMean(), marketData.getVol());

            priceChannel.put(Tuple.of(marketData.getCoin(), currentPrice));

            timer.schedule(new MarketDataStream(priceChannel, currentPrice, marketData, timer), (long) delay);

            log.debug("Thread: {}, Price ticking for {} : {}",Thread.currentThread().getId(), marketData.getCoin(), currentPrice);
        } catch (Exception e) {
            log.error("Error streaming price {} : {}, with error {}", currentPrice, marketData, e);
            e.printStackTrace();
        }
    }

}
