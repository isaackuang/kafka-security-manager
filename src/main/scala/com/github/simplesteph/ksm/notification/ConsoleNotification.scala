package com.github.simplesteph.ksm.notification
import com.github.simplesteph.ksm.parser.CsvParserException
import com.typesafe.config.Config
import kafka.security.auth.{Acl, Resource}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Try, Success}

case class ConsoleNotification() extends Notification {

  val log: Logger =
    LoggerFactory.getLogger(classOf[ConsoleNotification].getSimpleName)

  /**
    * Config Prefix for configuring this module
    */
  override val CONFIG_PREFIX: String = "console"

  /**
    * internal config definition for the module
    */
  override def configure(config: Config): Unit = ()

  override def notifyErrors(errs: List[Try[Throwable]]): Unit = {
    errs.foreach {
      case Failure(cPE: CsvParserException) => log.error(s"${cPE.getLocalizedMessage} | Row: ${cPE.printRow()}")
      case Success(t) => log.error("refresh exception", t)
      case Failure(t) => log.error("refresh exception", t)
    }
  }

  override protected def notifyOne(action: String,
                                   acls: Set[(Resource, Acl)]): Unit = {
    if (acls.nonEmpty) {
      acls.foreach {
        case (resource, acl) =>
          val message = Notification.printAcl(acl, resource)
          log.info(s"$action $message")
      }
    }
  }

  override def close(): Unit = ()

}
