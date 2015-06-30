package com.github.dant3.irc

sealed trait Prefix

case class User(nick:String, login:String, host:String) extends Prefix
case class Server(host:String) extends Prefix

object Prefix {
  def apply(string:String) = parse(string)

  def parse(string:String):Prefix = {
    if (string.contains('!') && string.contains('@')) {
      val (nick, afterNick) = splitBy(string, '!')
      val (login, afterLogin) = splitBy(string, '@')
      User(nick, login, afterLogin)
    } else {
      Server(string)
    }
  }

  private def splitBy(string:String, char:Char) = {
    val indexOfExclamation = string.indexOf(char)
    (string.substring(0, indexOfExclamation), string.substring(indexOfExclamation + 1))
  }
}
