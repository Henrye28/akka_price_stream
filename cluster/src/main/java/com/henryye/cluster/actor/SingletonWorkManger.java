package com.henryye.cluster.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akka.actor.typed.receptionist.ServiceKey;
import com.henryye.cluster.model.Message;
import com.henryye.cluster.model.Task;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SingletonWorkManger extends AbstractBehavior<Message> {
    private ActorRef<Task> workRouter;
    public static ServiceKey<Task> WORKERS_SERVICE_KEY = ServiceKey.create(Task.class, "price-worker");

    public static Behavior<Message> create(ActorRef<Task> workersRouter) {
        return Behaviors.<Message>setup(context -> new SingletonWorkManger(context, workersRouter));
    }

    public static Behavior<Message> create() {
        return Behaviors.setup(context -> {
            GroupRouter<Task> workerGroupBehavior = Routers
                    .group(WORKERS_SERVICE_KEY)
                    .withConsistentHashingRouting(1, task -> task.getCoinId());

            ActorRef<Task> workersRouter =
                    context.spawn(workerGroupBehavior, "WorkersRouter");

            return SingletonWorkManger.create(workersRouter);
        });
    }

    private Behavior<Message> onMessage(Message task) {
        getContext().getLog().info("WorkManager Received task {}", task);
        workRouter.tell((Task) task);
        return Behaviors.same();
    }

    public SingletonWorkManger(ActorContext<Message> context, ActorRef<Task> workRouter) {
        super(context);
        this.workRouter = workRouter;
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
                .onMessage(Task.class, task -> onMessage(task))
                .onMessageEquals(Task.Stop.INSTANCE, () -> Behaviors.stopped())
                .build();
    }

}
