package com.github.dant3.irc

case class Message(prefix:Option[Prefix], command:String, params:List[String]) {
  def asString = {
    prefix.map(":" + _ + " ").getOrElse("") + command + (Message.paramsToString(params) match {
      case Some(str) ⇒ " " + str
      case None ⇒ ""
    })
  }
}

object Message {
  def apply(command:String, params:List[String]):Message = Message(None, command, params)

  def apply(rawSting:String):Option[Message] = {
    if (rawSting == null) None
    else Some(parse(rawSting.trim))
  }

  private def parse(rawString:String) = {
    // 1. parse first ':' prefix piece (if present)
    val (prefix, afterPrefix) = parsePrefix(rawString)
    val (command, afterCommand) = parseCommand(afterPrefix)
    val params = parseParams(afterCommand)

    Message(prefix.map(Prefix.parse), command, params)
  }

  private def parsePrefix(rawString:String):(Option[String],String) = if (rawString.startsWith(":")) {
    // parse prefix part
    val colonIndex = rawString.indexOf(':')
    val prefixEnd = rawString.indexOf(' ', colonIndex + 1)
    val prefix = rawString.substring(colonIndex + 1, prefixEnd)
    (Some(prefix), rawString.substring(prefixEnd + 1))
  } else {
    (None, rawString)
  }

  private def parseCommand(rawString:String):(String, String) = {
    val commandEnd = rawString.indexOf(' ')
    (rawString.substring(0, commandEnd), rawString.substring(commandEnd + 1))
  }

  private def parseParams(rawString:String):List[String] = rawString match {
    case trailing if trailing.startsWith(":") ⇒
      trailing.substring(1) :: Nil
    case paramsList if paramsList.indexOf(' ') >= 0 ⇒
      val delim = paramsList.indexOf(' ')
      val param = paramsList.substring(0, delim)
      val rest = paramsList.substring(delim + 1)
      param :: parseParams(rest)
    case noColonTrailing ⇒
      noColonTrailing :: Nil
  }


  def paramsToString(params:List[String]):Option[String] = {
    val str = paramsToString(new StringBuilder, params).toString()
    if (str.isEmpty) None
    else Some(str)
  }

  def paramsToString(str:StringBuilder, params:List[String]):StringBuilder = params match {
    case Nil ⇒ str
    case trailingElement :: Nil ⇒
      str.append(s":$trailingElement")
      str
    case element :: rest ⇒
      str.append(element).append(' ')
      paramsToString(str, rest)
  }
}
