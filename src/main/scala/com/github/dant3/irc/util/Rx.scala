package com.github.dant3.irc.util

import java.util.concurrent.CopyOnWriteArraySet

import rx.lang.scala.{Observable, Observer, Subscriber}

import scala.collection.{JavaConversions, mutable}

class Rx[T] extends Observer[T] {
  private val subscribers:mutable.Set[Subscriber[T]] = JavaConversions.asScalaSet(new CopyOnWriteArraySet[Subscriber[T]])

  private def foreach(fn:Subscriber[T] ⇒ Any) = {
    subscribers.retain(!_.isUnsubscribed)
    subscribers.foreach(fn)
  }

  override def onNext(value:T) = foreach(_.onNext(value))
  override def onError(error:Throwable) = foreach(_.onError(error))
  override def onCompleted() = foreach(_.onCompleted())

  lazy val observe:Observable[T] = Observable { subscriber:Subscriber[T] ⇒ {
    subscribers.add(subscriber)
    onSubscriber(subscriber)
  }}

  protected def onSubscriber(subscriber: Subscriber[T]):Any = {}
}
