package com.github.dant3.irc

import java.io._
import java.net.Socket

import com.github.dant3.irc.IrcClient.Connection

import scala.annotation.tailrec
import scala.concurrent.Future

class IrcClient(val host:String, val port:Int, val nick:String, val login:String) {
  import scala.concurrent.ExecutionContext.Implicits.global

  def connect:Future[Connection] = Future {
    // Connect directly to the IRC server.
    val connection = IrcClient.connection(this)
    try {
      connection.send(Message("NICK", List(nick)))
      connection.send(Message("USER", List(login, "8", "*", "Scala IRC Bot")))
      waitForLoggedIn(connection)
      connection
    } catch {
      case ex:Throwable ⇒
        connection.close()
        throw ex
    }
  }

  private def waitForLoggedIn(connection: Connection):Unit = {
    connection.receive match {
      case Message(_, "433", _) ⇒ throw new IllegalStateException("Nickname is already in use!")
      case Message(_, "004", List(login, serverHost, _*)) ⇒ connection.updatePrefix(login, serverHost)
      case _ ⇒ waitForLoggedIn(connection)
    }
  }
}

object IrcClient {
  def connection(client: IrcClient) = {
    val socket = new Socket(client.host, client.port)
    val writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
    val reader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    new Connection(socket, reader, writer, client)
  }

  class Connection private[IrcClient] (socket:Socket, reader:BufferedReader,
                                       writer:BufferedWriter, ircClient:IrcClient) extends Closeable {
    private var myPrefix:Option[Prefix] = None

    private[IrcClient] def updatePrefix(login:String, serverHost:String) = {
      myPrefix = Some(Prefix(s"${ircClient.nick}!$login@$serverHost"))
    }

    def send(message:Message) = {
      val strToWrite = message.copy(myPrefix).asString
      println(s"IRC < $strToWrite")
      writer.write(strToWrite + "\r\n")
      writer.flush()
    }
    @tailrec final def receive:Message = readMessage match {
      case Some(message) ⇒
        println(s"IRC > $message")
        message
      case None ⇒ receive
    }

    private def readMessage:Option[Message] = Message(reader.readLine())

    override def close(): Unit = socket.close()
  }
}
