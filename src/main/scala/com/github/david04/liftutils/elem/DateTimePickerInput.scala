package com.github.david04.liftutils.elem

import net.liftweb.util.Helpers._
import scala.xml.NodeSeq
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.util.Helpers

trait DateTimePickerInput extends HTMLEditableElem with LabeledElem {
  implicit def self = this

  override protected def htmlElemTemplatePath: List[String] = "templates-hidden" :: "elem-edit-datetimepicker-dflt" :: Nil

  def get: () => (Long, Long)

  def set: ((Long, Long)) => Unit

  var value = get()

  private[elem] def save(): Unit = set(getCurrentValue())

  def getCurrentValue(): (Long, Long) = value

  def setupDatePicker(dpId: Symbol, initial: Long, set: Long => JsCmd) = {
    val v = "window." + Helpers.nextFuncName
    Script(OnLoad(Run("" +
      s"$v = ${sel(dpId)}.datetimepicker({autoclose: true, isRTL: false, format: 'dd/mm/yy - hh:ii', pickerPosition: 'bottom-right'})" +
      "  .on('changeDate', function(e) {" + SHtml.ajaxCall(JsRaw("e.date.getTime()"), v => tryo(v.toLong).map(set(_)).openOr(Noop)).toJsCmd + "});" +
      s"$v.data('datetimepicker').setDate(new Date($initial));"
    )))
  }

  override protected def htmlElemRendererTransforms =
    super.htmlElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".timerangepicker-from [onkeydown]" #> "return false;" &
        ".timerangepicker-to [onkeydown]" #> "return false;" &
        ".timerangepicker-from [id]" #> id('from) &
        ".timerangepicker-to [id]" #> id('to) &
        ".elem-error [id]" #> id('error)
      ) andThen {
      (ns: NodeSeq) => ns ++
        setupDatePicker('from, value._1, v => {value = (v, value._2); onChangeClientSide()}) ++
        setupDatePicker('to, value._2, v => {value = (value._1, v); onChangeClientSide()})
    }
}
