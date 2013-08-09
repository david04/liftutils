package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{FormField, RederedField}
import net.liftweb.common._
import scala.xml.NodeSeq
import scala.xml.Text
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.util.Util._
import java.util.UUID
import scala.collection.mutable
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import com.github.david04.liftutils.entity.{MetaEntity, Entity}
import com.github.david04.liftutils.crud.{Editable, MetaEditable, MetaCrudable, Crudable}

/**
 * Created by david at 5:33 PM
 */
abstract class MappedTableFormField[E <: Entity[E], O <: Editable[O]](
                                                                       all: E => E#MappedOneToMany[O],
                                                                       ot: MetaEditable[O]
                                                                       ) extends Logger with FormField[E] {

  def fieldType = "Table"

  def render(saveAndRedirect: String => JsCmd, instance: E, row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]): RederedField = new RederedField {

    var rendered: mutable.Buffer[(O, Seq[RederedField], String)] = all(instance).map(r => (r, ot.fFields.map(f => f.render(
      saveAndRedirect,
      r,
      true,
      true,
      v => r.tmpMap(System.identityHashCode(f)) = v,
      () => r.tmpMap.get(System.identityHashCode(f))
    )), UUID.randomUUID().toString))

    def validate = (rendered flatMap {_._2 flatMap {_.validate}}).toList

    def emtpyClass = "table-empty"

    val html = {
      val tbodyId = UUID.randomUUID().toString

      def deleteButton(e: (O, Seq[RederedField], String)) = (("@btn [onclick]" #> run(delete(e)))).apply(template(row, "DelBtn"))

      def delete(e: (O, Seq[RederedField], String)) = {
        if (rendered.contains(e)) {
          e._1.delete_!
          all(instance) -= e._1
          rendered = rendered.filter(_ != e)
          Replace(e._3, Text("")) & (if (rendered.isEmpty) Run("$('#" + id + "').addClass(" + emtpyClass.encJs + ");") else Noop)
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
        val nw = ot.newInstance()
        all(instance) += nw
        val nwRendered = (nw, ot.fFields.map(f => f.render(
          saveAndRedirect,
          nw,
          true,
          true,
          v => nw.tmpMap(System.identityHashCode(f)) = v,
          () => nw.tmpMap.get(System.identityHashCode(f))
        )), UUID.randomUUID().toString)
        nwRendered +=: rendered
        AppendHtml(tbodyId, renderRow(nwRendered)) & Run("$('#" + id + "').removeClass(" + emtpyClass.encJs + ");")
      }

      (
        "@wrap [id]" #> id &
          "@wrap [class+]" #> (if (rendered.isEmpty) emtpyClass else "") &
          "@title *" #> name &
          "@table @thead @tr @th *" #> (ot.fFields.map(_.name) :+ "").map(name => Text(name)) &
          "@table @tbody [id]" #> tbodyId &
          "@table @tbody @tr" #> renderRows _ &
          "@newline [onclick]" #> newline()
        ).apply({template(row, "")})
    }

    override def update = rendered.map(_._2.map(_.update).reduceOption(_ & _).getOrElse(Noop)).reduceOption(_ & _).getOrElse(Noop)
  }
}
