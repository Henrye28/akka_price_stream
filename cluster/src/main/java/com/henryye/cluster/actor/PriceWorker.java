package com.henryye.cluster.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.pubsub.Topic;
import com.henryye.cluster.model.Message;
import com.henryye.cluster.model.PriceResult;
import com.henryye.cluster.model.Task;

public final class PriceWorker extends AbstractBehavior<Message> {
  private ActorRef<Topic.Command<Message>> topicActor;

  private PriceWorker(ActorContext<Message> context, ActorRef<Topic.Command<Message>> topicActor) {
    super(context);
    this.topicActor = topicActor;
  }

  public static Behavior<Message> create(ActorRef<Topic.Command<Message>> topicActor) {
    return Behaviors.setup(context -> new PriceWorker(context, topicActor));
  }

  @Override
  public Receive<Message> createReceive() {
    return newReceiveBuilder()
        .onMessage(Task.class, task -> process(task))
        .onMessageEquals(Task.Stop.INSTANCE, () -> Behaviors.stopped())
        .build();
  }

  private Behavior<Message> process(Task task) throws InterruptedException {
    getContext().getLog().info(this.getContext().getSelf().toString() + " Price Worker processing request [{}]", task);
    double resultPrice = task.price * task.quantity;
    /*
    Mock price calculation work
     */
    Thread.sleep(1000);
    getContext().getLog().info("Price Worker completed calculation, price result: [{}]", resultPrice);
    /**
     * Publish results to topic
     * Topic actor is accessible for actors in other cluster nodes
     * Each node needs to create a topic actor instance
     */
    topicActor.tell(Topic.publish(PriceResult.builder().coin(task.coinId).priceResult(resultPrice).build()));
    return Behaviors.same();
  }

}
