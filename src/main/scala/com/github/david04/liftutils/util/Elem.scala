package com.github.david04.liftutils.util

import java.util.UUID
import scala.xml
import net.liftweb.http.js.JsCmds.Replace
import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmd


trait Elem {

  val id = UUID.randomUUID().toString

  def render: xml.Elem

  def update(): JsCmd = { Replace(id, render) }

}

object div {
  def apply(b: => NodeSeq) = new Elem {
    def render: xml.Elem = <div id={id}>
      {b}
    </div>
  }
}

