package com.github.david04.liftutils.lloader

import java.util.concurrent.{Future, Callable, Executors, ExecutorService}
import scala.xml.NodeSeq
import scala.xml.NodeSeq._
import net.liftweb.http.{NodeSeqFuncOrSeqNodeSeqFunc, IdMemoizeTransform, S, SHtml}
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.util.{Helpers, PassThru}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import scala.xml.Elem
import net.liftweb.common.Full
import scala.Some
import net.liftweb.http.js.JsCmds.Run

/**
 * Lazy Loader
 */

object LLoader {
  val defaultPoolSize = 100
  val defaultPool: ExecutorService = Executors.newFixedThreadPool(defaultPoolSize)
}

class LLoader(
               loadingTemplate: NodeSeq,
               interval: Int = 200,
               pool: ExecutorService = LLoader.defaultPool
               ) {

  def blockUI(id: String): JsCmd = Run("$('#" + id + "').block({ message: null });")
  def unblockUI(id: String): JsCmd = Run("$('#" + id + "').unblock();")

  val left = collection.mutable.ListBuffer[Future[JsCmd]]()

  def loadLazy(renderer: IdMemoizeTransform, loading: NodeSeq): NodeSeq => NodeSeq = {
    (template: NodeSeq) => {
      val loadingId = S.formFuncName

      val rslt = renderer.apply(<div id={loadingId}>{loading}</div>)
      left += pool.submit(new Callable[JsCmd] {def call(): JsCmd = renderer.setHtml()})
      rslt
    }
  }

  def loadLazy(f: NodeSeq => NodeSeq, loading: NodeSeq): NodeSeq => NodeSeq = {
    (template: NodeSeq) => {
      var firstPass = true
      val renderer = SHtml.idMemoize(_ => {
        if (firstPass) {firstPass = false; (_: NodeSeq) => loadingTemplate} else f
      })
      val rslt = renderer.apply(<div>{template}</div>)
      left += pool.submit(new Callable[JsCmd] {def call(): JsCmd = renderer.setHtml()})
      rslt
    }
  }

  def loadLazy(f: NodeSeq => NodeSeq): NodeSeq => NodeSeq = loadLazy(f, loadingTemplate)

  def callback(): JsCmd = {
    val variable = S.formFuncName
    Run(s"window.$variable = window.setInterval(function() {" +
      SHtml.ajaxInvoke(() => {
        val toRemove = left.filter(_.isDone)
        val updates = left.filter(_.isDone).map(_.get()).foldLeft(JsCmds.Noop)(_ & _)
        left --= toRemove
        updates //& Run(if (left.isEmpty) s";window.clearTimeout(window.$variable);" else "")
      }).toJsCmd + "}" +
      s",$interval);")
  }

  def loader(): NodeSeq = <tail>{Script(OnLoad(callback()))}</tail>
  def installLoader(): NodeSeq => NodeSeq = (ns: NodeSeq) => ns ++ loader()

  def idMemoize(f: IdMemoizeTransform => NodeSeqFuncOrSeqNodeSeqFunc): IdMemoizeTransform = {
    new IdMemoizeTransform {
      val self = this

      var latestElem: Elem = <span/>

      var latestKids: NodeSeq = NodeSeq.Empty

      var latestId = Helpers.nextFuncName

      private def fixElem(e: Elem): Elem = {
        e.attribute("id") match {
          case Some(id) => latestId = id.text; e
          case None => e % ("id" -> latestId)
        }
      }

      def apply(ns: NodeSeq): NodeSeq =
        Helpers.findBox(ns) {
          e => latestElem = fixElem(e);
            latestKids = e.child;
            Full(e)
        }.
          map(ignore => applyAgain()).openOr(NodeSeq.Empty)

      def load() = {
        left += pool.submit(new Callable[JsCmd] {def call(): JsCmd = SetHtml(latestId, f(self)(latestKids))})
        <div id={latestId}>{loadingTemplate}</div>
      }

      def applyAgain(): NodeSeq =
        new Elem(latestElem.prefix,
          latestElem.label,
          latestElem.attributes,
          latestElem.scope,
          load(): _*)

      def setHtml(): JsCmd = {
        left += pool.submit(new Callable[JsCmd] {
          def call(): JsCmd = SetHtml(latestId, f(self)(latestKids)) & unblockUI(latestId)
        })
        blockUI(latestId)
      }
    }
  }

}
