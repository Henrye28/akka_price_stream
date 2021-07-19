package com.henryye.worker;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.henryye.worker.actor.PriceWorker;
import com.henryye.worker.model.Message;
import com.henryye.worker.model.Task;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Worker {
    public static ServiceKey<Task> WORKERS_SERVICE_KEY = ServiceKey.create(Task.class, "price-worker");

    public static void main(String[] args){
        startWorkerNode(25252);
        startWorkerNode(25254);
    }

    public static Behavior<Void> rootBehavior(){
        return Behaviors.setup(context -> {
            final int numberOfWorkers = context.getSystem().settings().config().getInt("price-worker.workers-per-node");
            for (int i = 0; i < numberOfWorkers; i++) {
                ActorRef<Message> worker = context.spawn(PriceWorker.create(), "PriceWorker" + i);
                context.getLog().info("Starting price worker {} in {}", i, context.getSystem().address());
                context.getSystem().receptionist().tell(Receptionist.register(WORKERS_SERVICE_KEY, worker.narrow()));
            }
            return Behaviors.empty();
        });
    }


    public static void startWorkerNode(int port){
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("akka.remote.artery.canonical.port", port);
        overrides.put("akka.cluster.roles", Collections.singletonList("worker"));

        Config config = ConfigFactory.parseMap(overrides)
                .withFallback(ConfigFactory.load(Worker.class.getClassLoader(), "application.conf"));

        ActorSystem<Void> system = ActorSystem.create(rootBehavior(), "PriceClusterSystem", config);
    }

}
