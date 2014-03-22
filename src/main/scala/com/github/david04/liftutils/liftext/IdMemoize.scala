package net.liftweb
package http

import S._
import common._
import util._
import util.Helpers._
import http.js._
import http.js.AjaxInfo
import JE._
import JsCmds._
import scala.xml._
import json._

trait IdMemoizeTransform2 extends Function1[NodeSeq, NodeSeq] with IdMemoizeTransform {
  def f: IdMemoizeTransform2 => NodeSeqFuncOrSeqNodeSeqFunc

  var latestElem: Elem = <span/>

  var latestId = Helpers.nextFuncName

  def latestKids: NodeSeq = latestElem.child

  private def ensureId(e: Elem): Elem = {
    e.attribute("id") match {
      case Some(_id) => latestId = _id.text; e
      case None => e % ("id" -> latestId)
    }
  }

  def apply(ns: NodeSeq): NodeSeq = {
    latestElem = Helpers.findBox(ns)(Full(_)).openOr(<span/>)
    applyAgain()
  }

  def applyAgain(): NodeSeq = {
//    println("TEMPLATE:\n " + latestElem)
    val r = ensureId(Helpers.findBox(f(this)(latestElem))(Full(_)).openOr(<span/>))
//    println("RESULT:\n " + r)
    r
  }

  def replaceHtml(): JsCmd = Replace(latestId, f(this)(applyAgain()))

  def setHtml(): JsCmd = replaceHtml()
}

object SHtml2 {

  def memoizeElem(_f: IdMemoizeTransform2 => NodeSeqFuncOrSeqNodeSeqFunc): IdMemoizeTransform2 = {
    new IdMemoizeTransform2 {def f = _f}
  }

  def textMemoize(txt: => String) = SHtml.idMemoize(_ => (_: NodeSeq) => Text(txt))
}