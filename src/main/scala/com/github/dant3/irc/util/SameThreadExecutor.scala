package com.github.dant3.irc.util

import scala.concurrent.ExecutionContextExecutor

object SameThreadExecutor extends ExecutionContextExecutor {
  override def reportFailure(cause: Throwable): Unit = cause.printStackTrace()

  override def execute(command: Runnable): Unit = {
    command.run()
  }
}
