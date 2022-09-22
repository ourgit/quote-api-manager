package services;

import akka.actor.ActorSystem;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 定时脚本处理器
 */
@Singleton
public class FixedTimeExecutor {
    private final ActorSystem system;

    @Inject
    AppInit appInit;

    private final ApplicationLifecycle appLifecycle;
    Logger.ALogger logger = Logger.of(FixedTimeExecutor.class);
    @Inject
    public FixedTimeExecutor(ActorSystem system, ApplicationLifecycle appLifecycle) {
        this.system = system;
        this.appLifecycle = appLifecycle;
        Executor executor = Executors.newCachedThreadPool();
        CompletableFuture.runAsync(() -> schedule(), executor);
        appLifecycle.addStopHook(() -> {
            system.terminate();
            return CompletableFuture.completedFuture(null);
        });
    }

    public void schedule() {
        logger.info("schedule");
        appInit.saveToCache();
    }


}
