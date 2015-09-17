package im.actor.extension

import akka.actor.ActorSystem
import im.actor.config.ActorConfig

import scala.collection.JavaConversions._
import scala.util.Try

trait InternalExtension

object InternalExtensions {

  val DialogExtensions = "enabled-modules.messaging.extensions"

  private val config = ActorConfig.load()

  def getId(path: String, name: String) = {
    config.getInt(s"$path.$name.id")
  }

  def extensions(path: String): Map[Int, String] = {
    val extConfig = config.getConfig(path)
    val extensionsKeys = extConfig.root.keys
    (extensionsKeys map { extName ⇒
      extConfig.getInt(s"$extName.id") → extConfig.getString(s"$extName.class")
    }).toMap
  }

  def extensionOf[T](extensionFQN: String, system: ActorSystem, data: Array[Byte]): Try[T] = Try {
    val constructor = Class.forName(extensionFQN).getConstructors()(0)
    constructor.newInstance(system, data).asInstanceOf[T]
  }
}