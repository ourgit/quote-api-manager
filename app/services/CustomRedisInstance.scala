package services

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.cache.redis.configuration.{RedisDelegatingSettings, RedisSettings, RedisStandalone}

@Singleton
class CustomRedisInstance @Inject() (config: Configuration)
//    @NamedCache("redisConnection") redisConnectionCache: AsyncCacheApi,
//    polarisExtensionService: PolarisExtensionService)(implicit asyncExecutionContext: AsyncExecutionContext)
  extends RedisStandalone
    with RedisDelegatingSettings {

  val pathPrefix = "play.cache.redis"

  def name = "play"

  private def defaultSettings =
    RedisSettings.load(
      // this should always be "play.cache.redis"
      // as it is the root of the configuration with all defaults
      config.underlying,
      "play.cache.redis")

  def settings: RedisSettings = {
    RedisSettings
      .withFallback(defaultSettings)
      .load(
        // this is the path to the actual configuration of the instance
        //
        // in case of named caches, this could be, e.g., "play.cache.redis.instances.my-cache"
        //
        // in that case, the name of the cache is "my-cache" and has to be considered in
        // the bindings in the CustomCacheModule (instead of "play", which is used now)
        config.underlying,
        "play.cache.redis")
  }

  //  def getConnectionInfoFromPolaris: Future[ServiceConnectionInfo] = {
  //    redisConnectionCache.getOrElseUpdate[ServiceConnectionInfo]("redisConnection") {
  //      PolarisConfig(config.underlying, s"$pathPrefix.polaris")
  //        .map { r =>
  //          getConnectionInfo(r.getServiceID)
  //        }
  //        .getOrElse {
  //          Future(
  //            ServiceConnectionInfo(
  //              "empty",
  //              config.get[String](s"$pathPrefix.host"),
  //              config.get[Int](s"$pathPrefix.port")))
  //        }
  //    }
  //  }
  //
  //  private def getConnectionInfo(service: String): Future[ServiceConnectionInfo] = {
  //    AtumLogger.App.info(s"get service:$service instance")
  //    polarisExtensionService.asyncGetOneInstance(service).map {
  //      _.map { response =>
  //        val instance = response.getInstances.head
  //        ServiceConnectionInfo(service, instance.getHost, instance.getPort)
  //      }.getOrElse {
  //        val s =
  //          ServiceConnectionInfo(service, config.get[String](s"$pathPrefix.host"), config.get[Int](s"$pathPrefix.port"))
  //        AtumLogger.App.error(s"get service:$service instance fail, default service:${s.toString}")
  //        s
  //      }
  //    }
  //  }

  def host: String = {
    //    val connectionInfoFuture = getConnectionInfoFromPolaris
    //    Try(Await.result(connectionInfoFuture, 10.seconds)) match {
    //      case Success(extractedVal) => extractedVal.host
    //      case Failure(_)            => config.get[String](s"$pathPrefix.host")
    //      case _                     => config.get[String](s"$pathPrefix.host")
    //    }
    config.get[String](s"$pathPrefix.host")
  }

  def port: Int = {
    //    val connectionInfoFuture = getConnectionInfoFromPolaris
    //    Try(Await.result(connectionInfoFuture, 10.seconds)) match {
    //      case Success(extractedVal) => extractedVal.port
    //      case Failure(_)            => config.get[Int](s"$pathPrefix.port")
    //      case _                     => config.get[Int](s"$pathPrefix.port")
    //    }
    config.get[Int](s"$pathPrefix.port")
  }

  def database: Option[Int] = Some(config.get[Int](s"$pathPrefix.database"))

  def password: Option[String] = Some(config.get[String](s"$pathPrefix.password"))

}
