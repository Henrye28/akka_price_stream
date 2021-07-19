package com.henryye.cluster.marketdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MarketData {

    private String coin;
    private double sodPrice;
    private double vol;
    private double mean;

}
