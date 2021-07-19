package com.henryye.cluster.config;

import com.henryye.cluster.frontend.RequestManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class FrontEndConfig implements WebSocketMessageBrokerConfigurer {

    @Bean
    RequestManager requestManager(){
        return new RequestManager();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue");
//        config.enableSimpleBroker("/queue/dot");
//        config.enableSimpleBroker("/queue/eth");
//        config.enableSimpleBroker("/queue/ada");
//        config.enableSimpleBroker("/queue/link");
//        config.enableSimpleBroker("/queue/eth");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/pricecoin");
        registry.addEndpoint("/pricecoin").withSockJS();
    }
    
}
