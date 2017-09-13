import sbt.{settingKey, config, Test}

/**
  * Created by haqa on 13/09/2017.
  */
object BuildConfig {
  // Settings Keys
  lazy val publishRepo = settingKey[String]("publishRepo")

  // key-bindings
  lazy val ITest = config("it") extend Test

  // env vars
  publishRepo := sys.props.getOrElse("publishRepo", default = "Unused transient repository")

}
