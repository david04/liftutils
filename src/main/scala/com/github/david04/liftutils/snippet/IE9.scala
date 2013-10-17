package com.github.david04.liftutils.snippet

import scala.xml.{Unparsed, NodeSeq}

/**
 * Created by david at 12:45 PM
 */
object IE {

  def _9(ns: NodeSeq) = <head_merge>{Unparsed(
    s"""<!--[if lt IE 9]>
      |${ns.head.descendant.map(_.toString()).mkString}
      |<![endif]-->
    """.stripMargin
  )}</head_merge>
}
