package com.henryye.cluster.config;

import com.henryye.cluster.controller.PriceController;
import com.henryye.cluster.controller.FrontEndController;
import com.henryye.cluster.frontend.RequestManager;
import com.henryye.cluster.model.PriceRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Configuration
@Import(FrontEndConfig.class)
public class ControllerConfig {

    @Bean
    FrontEndController userInterfaceController(RequestManager requestManager){
        return new FrontEndController(requestManager);
    }

    @Bean
    PriceController priceController(){
        return new PriceController();
    }

}
