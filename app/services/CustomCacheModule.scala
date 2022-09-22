package services

import com.google.inject.AbstractModule
import play.api.cache.redis.configuration.RedisInstance
import play.cache.NamedCacheImpl

class CustomCacheModule extends AbstractModule {

  override def configure(): Unit = {
    System.out.println("CustomCacheModule")
    // NamedCacheImpl's input used to be "play"
    bind(classOf[RedisInstance]).annotatedWith(new NamedCacheImpl("redis")).to(classOf[CustomRedisInstance])
    ()
  }
}
