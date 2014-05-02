package com.github.david04.liftutils.lloader

import java.util.concurrent.{Future, Callable, Executors, ExecutorService}
import scala.xml.NodeSeq
import scala.xml.NodeSeq._
import net.liftweb.http.{NodeSeqFuncOrSeqNodeSeqFunc, IdMemoizeTransform, S, SHtml}
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import scala.xml.Elem
import net.liftweb.common.Full
import scala.Some
import net.liftweb.http.js.JsCmds.Run

/**
 * Lazy Loader
 */

object LazyLoader {
  val defaultInterval = 200
  val defaultPoolSize = 6
  def createDefaultPool(): ExecutorService = Executors.newFixedThreadPool(defaultPoolSize)
}

class LazyLoader(
                  defaultLoadingTemplate: => NodeSeq,
                  interval: Int = LazyLoader.defaultInterval,
                  val pool: ExecutorService = LazyLoader.createDefaultPool()
                  ) {

  def blockUI(id: String): JsCmd = Run("$('#" + id + "').block({ message: null });")
  def unblockUI(id: String): JsCmd = Run("$('#" + id + "').unblock();")

  val left = collection.mutable.ListBuffer[Future[JsCmd]]()

  def callback(): JsCmd = {
    val variable = S.formFuncName
    val running = "window." + S.formFuncName
    Run(
      s"window.$variable = window.setInterval(function() {" +
        s"if(!$running) {" +
        s"  $running = true;" +
        SHtml.ajaxInvoke(() => {
          val toRemove = left.filter(f => f.isDone || f.isCancelled)
          val updates = left.filter(_.isDone).flatMap(f => tryo(f.get())).foldLeft(JsCmds.Noop)(_ & _)
          left --= toRemove
          Run(s"$running = false;") & updates
          //& Run(if (left.isEmpty) s";window.clearTimeout(window.$variable);" else "")
        }).toJsCmd +
        "  }" +
        "}" +
        s",$interval);")
  }

  def loaderScript(): JsCmd = callback()
  def loader(): NodeSeq = <tail>{Script(OnLoad(callback()))}</tail>
  def installLoader(): NodeSeq => NodeSeq = (ns: NodeSeq) => ns ++ loader()

  def idMemoize(f: IdMemoizeTransform => NodeSeqFuncOrSeqNodeSeqFunc, loadingTemplate: NodeSeq = defaultLoadingTemplate): IdMemoizeTransform = {
    new IdMemoizeTransform {
      val self = this
      var loadedOnce = false

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
        left += pool.submit(new Callable[JsCmd] {def call(): JsCmd = try {SetHtml(latestId, f(self)(latestKids))} finally {loadedOnce = true}})

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
        if (loadedOnce) blockUI(latestId) else Noop
      }
    }
  }

}
