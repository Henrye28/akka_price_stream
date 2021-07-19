package com.henryye.worker.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.henryye.worker.model.Message;
import com.henryye.worker.model.Task;

public final class PriceWorker extends AbstractBehavior<Message> {
  private PriceWorker(ActorContext<Message> context) {
    super(context);
  }

  public static Behavior<Message> create() {
    return Behaviors.setup(PriceWorker::new);
  }

  @Override
  public Receive<Message> createReceive() {
    return newReceiveBuilder()
        .onMessage(Task.class, task -> process(task))
        .onMessageEquals(Task.Stop.INSTANCE, () -> Behaviors.stopped())
        .build();
  }

  private Behavior<Message> process(Task task) throws InterruptedException {
    getContext().getLog().info("Price Worker processing request [{}]", task);
    double resultPrice = task.price * task.quantity;
    /*
    Mock price calculation work
     */
    Thread.sleep(1000);
    getContext().getLog().info("Price Worker completed calculation, price result: [{}]", resultPrice);
    return Behaviors.same();
  }
}
