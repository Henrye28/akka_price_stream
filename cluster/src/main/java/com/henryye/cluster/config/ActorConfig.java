package com.henryye.cluster.config;

import akka.actor.typed.ActorSystem;
//import akka.management.javadsl.AkkaManagement;
import com.henryye.cluster.actor.ActorRoot;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(FrontEndConfig.class)
public class ActorConfig {

    @Bean("clusterSystem")
    public ActorSystem actorSystem(SimpMessagingTemplate simpMessagingTemplate){
        Config config = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf");
        // Override the configuration of the port
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("akka.remote.artery.canonical.port", 25251);
        overrides.put("akka.cluster.roles", Collections.singletonList("cluster"));
        config.withFallback(ConfigFactory.parseMap(overrides));

        ActorSystem priceClusterSystem = ActorSystem.create(new ActorRoot(simpMessagingTemplate).getRootBehavior(), "PriceClusterSystem", config);
//        AkkaManagement.get(priceClusterSystem).start();
        return priceClusterSystem;
    }

}
