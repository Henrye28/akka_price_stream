package com.henryye.cluster.frontend;

import com.henryye.cluster.model.PriceRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestManager {
    private Map<String, PriceRequest> priceUnderlyingMap = new ConcurrentHashMap<>();

    public void updateUserRequestedCoin(PriceRequest priceRequest){
        String[] coins = priceRequest.getCoin().split(",");
        String[] quantities = priceRequest.getQuantity().split(",");

        for(int i = 0; i < coins.length; i++){
            priceUnderlyingMap.put(coins[i], PriceRequest.builder().coin(coins[i].trim()).quantity(quantities[i].trim()).build());
        }
    }

    public Map<String, PriceRequest> priceRequestMap(){
        return priceUnderlyingMap;
    }

}
