package net.liftweb
package http

import S._
import common._
import util.Helpers._
import http.js._
import scala.xml._

object AjaxHelpers {

  def ajaxOnSubmitTo(formId: String)(func: () => JsCmd): (NodeSeq) => NodeSeq = {
    val fgSnap = S._formGroup.get

    (in: NodeSeq) => S._formGroup.doWith(fgSnap) {
      def runNodes(ns: NodeSeq): NodeSeq = {
        def addAttributes(elem: Elem, name: String) = {
          val clickJs = "liftAjax.lift_uriSuffix = '" + name + "=_'; $('#" + formId + "').submit();"

          elem % ("name" -> name) % ("onclick" -> clickJs)
        }

        ns.flatMap {
          case Group(nodes) => runNodes(nodes)

          case e: Elem if (e.label == "button") ||
            (e.label == "input" && e.attribute("type").map(_.text) == Some("submit")) =>
            _formGroup.is match {
              case Empty =>
                formGroup(1)(fmapFunc(func)(addAttributes(e, _)))
              case _ => fmapFunc(func)(addAttributes(e, _))
            }
        }
      }

      runNodes(in)
    }
  }

  def ajaxOnSubmit(func: () => JsCmd): (NodeSeq)=>NodeSeq = {
    val fgSnap = S._formGroup.get

    (in: NodeSeq) => S._formGroup.doWith(fgSnap) {
      def runNodes(ns: NodeSeq): NodeSeq = {
        def addAttributes(elem: Elem, name: String) = {
          val clickJs = "liftAjax.lift_uriSuffix = '" + name + "=_'; return true;"

          elem % ("name" -> name) % ("onclick" -> clickJs)
        }

        ns.flatMap {
          case Group(nodes) => runNodes(nodes)

          case e: Elem if (e.label == "button") ||
            (e.label == "input" && e.attribute("type").map(_.text) == Some("submit")) =>
            _formGroup.is match {
              case Empty =>
                formGroup(1)(fmapFunc(func)(addAttributes(e, _)))
              case _ => fmapFunc(func)(addAttributes(e, _))
            }
        }
      }

      runNodes(in)
    }
  }
}
