package com.henryye.cluster.controller;

import com.henryye.cluster.frontend.RequestManager;
import com.henryye.cluster.model.PriceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@RequestMapping("/price")
@Slf4j
public class FrontEndController {


    private RequestManager requestManager;

    public FrontEndController(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @ResponseBody
    @PostMapping("/underlying")
    public String priceUnderlying(@ModelAttribute PriceRequest request){
        requestManager.updateUserRequestedCoin(request);
        log.info(request.toString());
        return "Refreshed";
    }



}
