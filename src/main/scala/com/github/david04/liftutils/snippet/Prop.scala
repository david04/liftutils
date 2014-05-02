package com.github.david04.liftutils.snippet

import scala.xml._
import net.liftweb.http.DispatchSnippet

object Prop extends DispatchSnippet {
  def dispatch = {
    case name =>
      (ns: NodeSeq) => ns match {
        case e@Elem(pre, lbl, att, scope, child@_*) =>
          e.asInstanceOf[Elem].copy(child = Seq(Text(net.liftweb.util.Props.get(name)
            .openOrThrowException(s"Could not find property '$name'"))))
      }
  }
}