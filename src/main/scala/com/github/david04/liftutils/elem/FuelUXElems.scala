package com.github.david04.liftutils.elem

import scala.xml._

import net.liftweb.http.js.JsCmds._
import net.liftweb.http._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.json._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.common.Full
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd

case class FuelUXNode(id: String, name: String, `type`: String)

object FuelUXTreeValidation {

  trait Req extends FuelUXTree {
    override private[elem] def error() =
      (if (getCurrentValue() == "") Some(Text(labelStr("errorReq"))) else None) orElse super.error()
  }

}

trait FuelUXTree extends HTMLEditableElem with LabeledElem {

  override protected def htmlEditableElemTemplatePath: List[String] = "templates-hidden" :: "elem-edit-tree-dflt" :: Nil

  type ID = String
  type Type = String

  def all: Array[String]
  def get: () => String
  def set: String => Unit
  var value = get()

  private[elem] def save(): Unit = set(getCurrentValue())

  def getCurrentValue(): String = value

  case class Node(id: ID, value: String, name: String, `type`: Type, children: () => Array[Node])

  private def lazyF[T](f: => T) = { lazy val v = f; () => v }

  def toFuelUX(node: Node) = FuelUXNode(node.id, node.name, node.`type`)

  override protected def htmlEditableElemRendererTransforms = {

    implicit val formats = net.liftweb.json.DefaultFormats

    val map = collection.mutable.Map[ID, Node]()

    def recur(pre: String, all: Array[Array[String]]): Array[Node] =
      (Array("*") +: all)
        .groupBy(_.head).mapValues(_.map(_.tail).filterNot(_.isEmpty))
        .toArray
        .sortWith((s1, s2) => {
        if (s1._1.startsWith("*")) false
        else if (s2._1.startsWith("*")) true
        else s1._1.compareTo(s2._1) < 0
      })
        .map({
        case (cur, children) =>
          val id = S.formFuncName
          val node =
            if (children.isEmpty) Node(id, pre + cur, cur, "item", () => Array())
            else Node(id, pre, cur, "folder", lazyF[Array[Node]](recur(s"$pre$cur.", children)))
          map(id) = node
          node
      })

    val root = recur("", all.map(_.split("\\.")))

    val currentSelectionRenderer = SHtml.idMemoize(_ => (_: NodeSeq) =>
      Text(labelStr("current").replaceAllLiterally("{value}", getCurrentValue())))

    val script =
      Script(OnLoad(Run(
        "$('#" + id('tree) + "').tree({dataSource: { data: function(opt, cb) { " +
          SHtml.jsonCall(JsRaw("opt"),
            new JsonContext(Full("function(v){cb({data: v});}"), Empty),
            (v: JValue) =>
              (for {
                obj <- Box.asA[JObject](v)
                idValue <- obj.values.get("id")
                id <- Box.asA[String](idValue)
              } yield {
                Extraction.decompose(map(id).children().map(toFuelUX(_)))
              }) openOr Extraction.decompose(root.map(toFuelUX(_)))
          ).toJsCmd +
          "}}}).on('selected', function(sel) {" +
          SHtml.ajaxCall(JsRaw("$('#" + id('tree) + "').tree('selectedItems').map(function(i) {return i.id;})[0]"),
            (s: String) => {
              value = map(s).value
              currentSelectionRenderer.setHtml() & onChangeServerSide()
            }).toJsCmd +
          "});")))
    super.htmlEditableElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".elem-error [id]" #> id('error) &
        ".tree-title-lbl" #> currentSelectionRenderer
      ) andThen (
      ".tree [id]" #> id('tree)
      ) andThen ((ns: NodeSeq) =>
      ns ++ <tail>
        {script}
      </tail>)
  }
}

trait FuelUXModalEditTree extends FuelUXTree with ModalEditElem {
  override protected def htmlModalEditableElemViewTemplatePath: List[String] =
    "templates-hidden" :: "elem-modaledit-tree-view-dflt" :: Nil

  protected def getCurrentViewString(): String = value

  override protected def onChangeServerSide(): JsCmd = super.onChangeServerSide() & setCurrentViewString(value)

}