package com.github.dant3.irc

import org.scalatest.{Matchers, WordSpec}

class MessageSpec extends WordSpec with Matchers {
  "Message" should {
    "parse well" in {
      val sample = ":hobana.freenode.net 376 dant3bot :End of /MOTD command."
      val parsed = Message(sample)
      parsed shouldBe defined
      parsed should matchPattern {
        case Some(Message(Some(Server("hobana.freenode.net")), "376", List("dant3bot", "End of /MOTD command."))) ⇒
      }
    }
  }
}
