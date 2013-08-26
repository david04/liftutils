package com.github.david04.liftutils.forms

import net.liftweb.mapper._
import net.liftweb.common._
import net.liftweb.http.{SessionVar, Templates, S, SHtml}
import scala.xml.NodeSeq
import scala.xml.Text
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.util.Util._
import java.util.UUID
import scala.collection.mutable
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import com.github.david04.liftutils.entity.{ChildEntity, Entity}
import net.liftweb.http.S._
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.JsCmds.SetHtml
import scala.Some

trait RederedField {
  val id = UUID.randomUUID().toString
  val html: NodeSeq

  def validate: List[NodeSeq]

  def update = {
    validate match {
      case err :: rest => SetHtml(id + "help", err) & Run("$('#" + id + "').addClass('error');")
      case Nil => SetHtml(id + "help", NodeSeq.Empty) & Run("$('#" + id + "').removeClass('error');")
    }
  }
}

abstract class RederedFieldImpl[E](setTmp: Any => Unit, getTmp: () => Option[Any]) extends RederedField {

  def value: Option[E] = getTmp().asInstanceOf[Option[E]]

  def value_=(v: E) = setTmp(v)
}

trait FormField {
  protected def fieldType: String

  val field = this

  def validate: List[NodeSeq] = Nil

  def templateRoot = "templates-crud-hidden" :: Nil

  def template(row: Boolean, name: String = "") =
    Templates(templateRoot ::: ("edit" :: (if (row) "row" else "field") :: (fieldType + name) :: Nil)).openOrThrowException("")

  def name: String

  def render(
              row: Boolean,
              edit: Boolean,
              setTmp: Any => Unit,
              getTmp: () => Option[Any]
              ): RederedField

}

case class FormCache() {

  val tmpMap = collection.mutable.Map[Int, Any]()
}

abstract class Form(protected val cache: FormCache = FormCache()) {

  def template: NodeSeq

  def primaryBtnText: String
  def cancelBtnText: String
  def onCancel: JsCmd
//  def onSuccess: JsCmd
  def fields: List[FormField]

  protected def submit(): JsCmd

  val id = ## + ""

  val extCancelJs = Run("$('#" + id + "-cancel').trigger('onclick');")
  val extSubmitJs = Run("$('#" + id + "-submit').trigger('submit');")

  private lazy val html = {

    val submitId = nextFuncName
    var url: Option[String] = None
    lazy val rendered: List[RederedField] = fields.map(f => f.render(
      false,
      true,
      v => cache.tmpMap(System.identityHashCode(f)) = v,
      () => cache.tmpMap.get(System.identityHashCode(f))
    ))

    def doSubmit() = {
      if (rendered forall {_.validate.isEmpty}) {
        submit()
      } else {
        rendered.map(_.update).reduce(_ & _)
      }
    }

    def temporarySave() =
      url.map(url => Run("window.location = " + S.encodeURL(url).encJs + ";")).getOrElse(Noop)

    def saveAndRedirect(_url: String) = {
      url = Some(_url);
      val name = "z" + nextFuncName
      addFunctionMap(name, temporarySave _)
      Run("$('#" + submitId + "').attr('name', " + name.encJs + ");" +
        "$('#" + submitId + "').submit();")
    }

    (
      "@fields" #> rendered.map(_.html) &
        "@cancelbtn [id]" #> (id + "-cancel") &
        "@cancelbtn [onclick]" #> onCancel.toJsCmd &
        "@cancelbtn *" #> cancelBtnText &
        "@submitbtn [id]" #> (id + "-submit") &
        "@submitbtn [value]" #> primaryBtnText &
        "@hidden" #> S.formGroup(10000)(SHtml.hidden(doSubmit _, "id" -> submitId))
      )(template)
  }

  def render = html

}
