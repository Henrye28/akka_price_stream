package com.henryye.cluster.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.henryye.cluster.model.Message;
import com.henryye.cluster.model.PriceResult;
import com.henryye.cluster.model.Task;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * For production environment, this processor and akka pubsub implementation is not ideal
 * It should be replaced by kafka, which supports message distribution(msg only consumes once by one of the consumers, rather than simple pub-sub)
 * And kafka supports better scalability on consumers
 */
public class PriceResultProcessor extends AbstractBehavior<Message> {

    private SimpMessagingTemplate simpMessagingTemplate;

    public PriceResultProcessor(ActorContext<Message> context, SimpMessagingTemplate simpMessagingTemplate) {
        super(context);
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public static Behavior<Message> create(SimpMessagingTemplate simpMessagingTemplate) {
        return Behaviors.setup(context -> new PriceResultProcessor(context, simpMessagingTemplate));
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(PriceResult.class, task -> process(task))
                .onMessageEquals(Task.Stop.INSTANCE, () -> Behaviors.stopped())
                .build();
    }

    private Behavior<Message> process(PriceResult result) {
        getContext().getLog().info("Received result [{}]", result);

        switch (result.getCoin()){
            case "BTC":
                simpMessagingTemplate.convertAndSend("/queue/btc", result.getPriceResult());
                break;
            case "ETH":
                simpMessagingTemplate.convertAndSend("/queue/eth", result.getPriceResult());
                break;
            case "DOT":
                simpMessagingTemplate.convertAndSend("/queue/dot", result.getPriceResult());
                break;
            case "ADA":
                simpMessagingTemplate.convertAndSend("/queue/ada", result.getPriceResult());
                break;
            case "LINK":
                simpMessagingTemplate.convertAndSend("/queue/link", result.getPriceResult());
                break;
            default:
                getContext().getLog().info("Received unknown [{}]", result);
                break;
        }

        return Behaviors.same();
    }

}
