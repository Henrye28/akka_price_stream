package com.henryye.cluster.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.pubsub.Topic;
import akka.actor.typed.receptionist.Receptionist;
import akka.cluster.typed.ClusterSingleton;
import akka.cluster.typed.SingletonActor;
import com.henryye.cluster.model.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static com.henryye.cluster.actor.SingletonWorkManger.WORKERS_SERVICE_KEY;

public class ActorRoot {

    private Behavior<Object> rootBehavior;
    private SimpMessagingTemplate simpMessagingTemplate;

    public ActorRoot(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        init();
    }

    public void init(){
        rootBehavior = Behaviors.setup(context -> {
            ActorRef<Message> globalPriceManger
                    = ClusterSingleton.get(context.getSystem()).init(SingletonActor.of(SingletonWorkManger.create(), "GlobalPriceManger"));

            final int numberOfWorkers = context.getSystem().settings().config().getInt("price-worker.workers-per-node");
            context.getLog().info("Starting {} price workers", numberOfWorkers);

            ActorRef<Topic.Command<Message>> topic =
                    context.spawn(Topic.create(Message.class, "priceResult"), "priceResult");

            ActorRef<Message> priceResultProcessor =
                    context.spawn(PriceResultProcessor.create(simpMessagingTemplate), "PriceResultProcessor");

            topic.tell(Topic.subscribe(priceResultProcessor));

            for (int i = 0; i < numberOfWorkers; i++) {
                ActorRef<Message> worker = context.spawn(PriceWorker.create(topic), "PriceWorker" + i);

                context.getLog().info("Starting price worker {}", i);
                context.getSystem().receptionist().tell(Receptionist.register(WORKERS_SERVICE_KEY, worker.narrow()));
            }


            return Behaviors.empty();
        });
    }

    public Behavior<Object> getRootBehavior() {
        return rootBehavior;
    }
}
