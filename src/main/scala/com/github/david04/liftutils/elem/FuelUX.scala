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

case class FuelUXNode(id: String, name: String, `type`: String)

class Tree(all: Array[String]) {

  val uid = S.formFuncName

  type ID = String
  type Type = String

  case class Node(id: ID, name: String, `type`: Type, children: () => Array[Node])

  private def lazyF[T](f: => T) = { lazy val v = f; () => v }

  val map = collection.mutable.Map[ID, Node]()

  def recur(pre: String, all: Array[Array[String]]): Array[Node] =
    (Array("All " + (if (pre == "") "Events" else pre + "*")) +: all)
      .groupBy(_.head).mapValues(_.map(_.tail).filterNot(_.isEmpty))
      .toArray
      .sortWith((s1, s2) => {
      if (s1._1.startsWith("All")) false
      else if (s2._1.startsWith("All")) true
      else s1._1.compareTo(s2._1) < 0
    })
      .map({
      case (title, children) =>
        val id = S.formFuncName
        val node =
          if (children.isEmpty) Node(id, title, "item", () => Array())
          else Node(id, title, "folder", lazyF[Array[Node]](recur(s"$pre$title.", children)))
        map(id) = node
        node
    })

  val root = recur("", all.map(_.split("\\.")))

  def toFuelUX(node: Node) = FuelUXNode(node.id, node.name, node.`type`)

  def render(ns: NodeSeq) = {
    implicit val formats = net.liftweb.json.DefaultFormats
    val script =
      Script(OnLoad(Run(
        "$('#" + uid + "').tree({dataSource: { data: function(opt, cb) { " +
          SHtml.jsonCall(JsRaw("opt"),
            new JsonContext(Full("function(v){console.log(v); cb({data: v});}"), Empty),
            (v: JValue) =>
              (for {
                obj <- Box.asA[JObject](v)
                idValue <- obj.values.get("id")
                id <- Box.asA[String](idValue)
              } yield {
                Extraction.decompose(map(id).children().map(toFuelUX(_)))
              }) openOr Extraction.decompose(root.map(toFuelUX(_)))
          ).toJsCmd +
          "}}});")))
    (".tree [id]" #> uid).apply(Templates("templates-hidden" :: "admin" :: "tree" :: Nil).get) ++
      <tail>
        {script}
      </tail>
  }
}

object Tree extends Tree(Array("hw.cpu.b", "hw.cpu.a", "hw.cpu.c", "hw.cpu.info", "hw.cpu.temp", "hw.cpu.speed")) {

  override def render(ns: NodeSeq) = super.render(ns)

}
