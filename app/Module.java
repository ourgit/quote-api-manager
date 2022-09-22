import actor.TimerActor;
import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import controllers.push.PushActor;
import play.Environment;
import play.libs.akka.AkkaGuiceSupport;
import services.AppInit;
import services.FixedTimeExecutor;
import utils.*;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule implements AkkaGuiceSupport {
    private final Environment environment;
    private final Config config;

    public Module(Environment environment, Config configuration) {
        this.environment = environment;
        this.config = configuration;
    }

    @Override
    public void configure() {
        bind(IdcardValidator.class).asEagerSingleton();
        bind(CacheUtils.class).asEagerSingleton();
        bind(BalanceUtils.class).asEagerSingleton();
        bind(DateUtils.class).asEagerSingleton();
        bind(EncodeUtils.class).asEagerSingleton();
        bind(Pinyin4j.class).asEagerSingleton();
        bind(AppInit.class).asEagerSingleton();
        bind(BizUtils.class).asEagerSingleton();
        bindActor(TimerActor.class, "timerActor");
        bindActor(PushActor.class, "pushActor", (p) -> p.withDispatcher("my-forkjoin-dispatcher"));
        bind(FixedTimeExecutor.class).asEagerSingleton();
    }
}
