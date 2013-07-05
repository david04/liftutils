package com.github.david04.liftutils.forms

import net.liftweb.mapper._
import net.liftweb.common._
import net.liftweb.http.{Templates, S, SHtml}
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

trait RederedField {
  val id = UUID.randomUUID().toString
  val html: NodeSeq

  def update: JsCmd
}

trait FormField[T, E <: Mapper[E]] {
  protected def fieldType: String

  def template(row: Boolean, name: String = "") = Templates("templates-crud-hidden" :: (fieldType + (if (row) "Row" else "") + name) :: Nil).openOrThrowException("")

  val name: String

  def render(instance: E, row: Boolean = false): RederedField

}

case class MappedTextFormField[E <: KeyedMapper[_, E]](name: String, field: E => MappedField[String, E], placeholder: String = "") extends FormField[String, E] {

  def fieldType = "Text"

  def render(instance: E, row: Boolean = false) = new RederedField {
    val html = {
      <div class="control-group" id={id}>
        <label class="control-label">
          {name.capitalize + ":"}
        </label>

        <div class="controls">
          {SHtml.text(field(instance).get, field(instance).apply _, "id" -> (id + "input"), "class" -> "span6", "placeholder" -> placeholder)}<!---->
          <span class="help-inline" id={id + "help"}></span>
        </div>
      </div>
    }

    def update = {
      field(instance).validate match {
        case err :: rest => SetHtml(id + "help", err.msg) & Run("$('#" + id + "').addClass('error');")
        case Nil => SetHtml(id + "help", NodeSeq.Empty) & Run("$('#" + id + "').removeClass('error');")
      }
    }
  }

}


abstract class MappedForeignKeyFormField[K, E <: KeyedMapper[_, E], O <: KeyedMapper[K, O]](
                                                                                             all: E => Seq[O],
                                                                                             field: E => MappedForeignKey[K, E, O],
                                                                                             fieldName: O => MappedText[O],
                                                                                             placeholder: String = "",
                                                                                             toStr: K => String,
                                                                                             fromStr: String => K
                                                                                             ) extends FormField[K, E] {

  def fieldType = "ForeignKey"

  def render(instance: E, row: Boolean = false) = new RederedField {

    val html = {
      val dlft = Option(field(instance).get).map(toStr)
      S.appendJs(Run(
        (dlft.map(d => "$(\"#" + id + "sel\").val(" + d.encJs + ");").getOrElse("") + "$(\"#" + id + "sel\").chosen();")))

      (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize + ":") &
          "@select [data-placeholder]" #> placeholder &
          "@select" #> SHtml.select(
            all(instance).map(o => (toStr(o.primaryKeyField.get), fieldName(o).get)),
            Box(dlft),
            s => if (all(instance).exists(_.primaryKeyField.get == fromStr(s))) field(instance).apply(fromStr(s)),
            "id" -> (id + "sel")) &
          "@help [id]" #> (id + "help")
        )(template(row))
    }

    def update = {
      field(instance).validate match {
        case err :: rest => SetHtml(id + "help", err.msg) & Run("$('#" + id + "').addClass('error');")
        case Nil => SetHtml(id + "help", NodeSeq.Empty) & Run("$('#" + id + "').removeClass('error');")
      }
    }
  }
}

abstract class MappedTableFormField[K, E <: OneToMany[K, E], O <: KeyedMapper[K, O]](
                                                                                      all: E => E#MappedOneToMany[O],
                                                                                      fields: Seq[FormField[_, O]],
                                                                                      create: E => O
                                                                                      ) extends FormField[K, E] {
  def fieldType = "Table"

  def render(instance: E, row: Boolean = false): RederedField = new RederedField() {

    var rendered: mutable.Buffer[(O, Seq[RederedField], String)] = all(instance).map(r => (r, fields.map(f => f.render(r, true)), UUID.randomUUID().toString))

    val html = {
      val tbodyId = UUID.randomUUID().toString

      def deleteButton(e: (O, Seq[RederedField], String)) = (("@btn [onclick]" #> run(delete(e)))).apply(template(row, "DelBtn"))

      def delete(e: (O, Seq[RederedField], String)) = {
        if (rendered.contains(e)) {
          e._1.delete_!
          all(instance) -= e._1
          rendered = rendered.filter(_ != e)
          Replace(e._3, Text(""))
        } else {
          Noop
        }
      }

      var rowTemplate: NodeSeq = NodeSeq.Empty
      def renderRow(e: (O, Seq[RederedField], String)) = e match {
        case (r, rdrd, _id) =>
          ("@tr [id]" #> _id &
            "@td *" #> (rdrd.map(_.html) :+ deleteButton(e)))(rowTemplate)
      }
      def renderRows(r: NodeSeq) = {
        rowTemplate = r
        rendered.map(renderRow _).reduceOption(_ ++ _).getOrElse(Text(""))
      }

      def newline() = run {
        val nw = create(instance)
        all(instance) += nw
        val nwRendered = (nw, fields.map(f => f.render(nw, true)), UUID.randomUUID().toString)
        nwRendered +=: rendered
        AppendHtml(tbodyId, renderRow(nwRendered))
      }

      (
        "@wrap [id]" #> id &
          "@title *" #> name &
          "@table @thead @tr @th *" #> (fields.map(_.name) :+ "").map(name => Text(name)) &
          "@table @tbody [id]" #> tbodyId &
          "@table @tbody @tr" #> renderRows _ &
          "@newline [onclick]" #> newline()
        ).apply({template(row, "")})
    }

    def update = rendered.map(_._2.map(_.update).reduceOption(_ & _).getOrElse(Noop)).reduceOption(_ & _).getOrElse(Noop)
  }
}

abstract class Form[E <: Mapper[E]](instance: E) {


  val primaryBtnText: String
  val back: String
  val fields: List[FormField[_, E]]

  private lazy val html = {

    val rendered = fields.map(_.render(instance))

    def submit() = {
      val errors = instance.validate
      if (errors.isEmpty) {
        instance.save()
        RedirectTo(back)
      } else {
        rendered.map(_.update).reduce(_ & _)
      }
    }

    <form class="lift:form.ajax">
      <div class="form-horizontal">
        {rendered.map(_.html)}<!---->{SHtml.hidden(submit _)}<!---->
        <div class="form-actions no-margin-bottom">
          <a class="btn" href={back}>Cancel</a>
          <input class="btn btn-primary" value={primaryBtnText} type="submit"></input>
        </div>
      </div>
    </form>
  }

  def render = html

}
