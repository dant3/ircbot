package com.github.dant3.irc

import scala.annotation.tailrec
import scala.concurrent.Await

import scala.concurrent.duration._
import scala.language.postfixOps

object SimpleBot {
  def main(args:Array[String]) = {
    // The server to connect to and our details.
    val server = "irc.freenode.net"
    val nick = "dant3bot"
    val login = "dant3bot"

    // The channel which the bot will join.
    val masterNick = "dant3"
    val channel = "#dant3"

    // Connect directly to the IRC server.
    val irc = Await.result(new IrcClient(server, 6667, nick, login).connect, 120 seconds)

    // Join the channel.
    irc.send(Message("JOIN", List(channel)))

    @tailrec def processInput():Unit = {
      irc.receive match {
        case Message(_, "PING", pingers) ⇒
          irc.send(Message("PONG", pingers))
          irc.send(Message("PRIVMSG", List(channel, s"I was pinged by $pingers!")))
          processInput()
        case msg @ Message(sender, "PRIVMSG", List(`channel`, message, _*)) ⇒
          sender match {
            case Some(User(senderNick, _, _)) ⇒
              if (message.startsWith(s"$nick:")) {
                if (message.contains("quit")) {
                  irc.send(Message("PRIVMSG", List(channel, "Shutting down...")))
                } else {
                  irc.send(Message("PRIVMSG", List(channel, s"$senderNick:${message.substring(nick.length + 1)} :P")))
                  processInput()
                }
              } else {
                processInput()
              }
            case _ ⇒
              processInput()
          }



        case Message(_, "ERROR", _) ⇒ /* quit */
        case Message(_, "QUIT", _) ⇒ /* quit */
        case in ⇒
          processInput()
      }
    }

    processInput()

    irc.close()
  }
}
