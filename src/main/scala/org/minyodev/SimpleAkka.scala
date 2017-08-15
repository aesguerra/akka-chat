package org.minyodev

import akka.actor.{Actor, ActorRef, ActorSystem, Inbox, Props}
import scala.collection.mutable.HashMap

case class Login(username: String)
case class Message(from: String, message: String)

class Session(userID: String) extends Actor {
  val loginTime = System.currentTimeMillis
  println("Session created time:" + loginTime)
  def receive = {
    case Message(from, message) => println(from + ": " + message)
  }
}

class SessionManager extends Actor {
  val system = ActorSystem("ChatSession")
  val sessions = new HashMap[String, ActorRef]
  val inbox = Inbox.create(system)

  def receive = {
    case Login(username) => {
      println(username + " logs in.")
      val session = system.actorOf(Props(new Session(username)))
      sessions += (username -> session)
      println("Session added: " + sessions)
    }
    case msg @ Message(from, message) => {
      sessions(from) ! msg
    }
  }
}

object SimpleAkka extends App {
  val system = ActorSystem("SimpleAkka")
  val user = system.actorOf(Props[SessionManager],"MyActor")
  val inbox = Inbox.create(system)

  user ! Login("Foo")
  user ! Login("Bar")
  user ! Message("Foo", "this is a testing message")
  user ! Message("Bar", "mic test mic test 123")
}