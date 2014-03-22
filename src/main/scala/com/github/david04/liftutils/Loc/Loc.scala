package com.github.david04.liftutils.Loc

import net.liftweb.common._
import net.liftweb.http.S
import com.github.david04.liftutils.util.Util.__print
import scala.xml._
import net.liftweb.util.StringHelpers

object Loc {
  val missing = collection.mutable.HashSet[String]()
  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() {
      println("Missing i18n keys:\n" + missing.toSeq.map(_ + "=").sorted.mkString("\n"))
    }
  })
}

trait Loc {

  def parentLoc: Loc = null

  def withPrefix(prefix: String) = {
    val t = this
    new Loc {
      override def parentLoc = t

      override def locPrefix: String = prefix
    }
  }

  def locPrefix: String =
    Stream.iterate[Class[_]](this.getClass)(_.getSuperclass).dropWhile(_.getSimpleName.filter(_.isLetter).toLowerCase.startsWith("anon")).head
      .getSimpleName.filter(_.isLetter).splitAt(1) match {
      case (a, b) => a.toLowerCase + b
    }

  def fullPrefix: List[String] =
    Option(parentLoc).map(_.fullPrefix.map(_ + "-")).getOrElse("" :: Nil).flatMap(p => List(p + locPrefix, p + "*"))


  def locParamOpt(suffix: String, params: (String, String)*): Option[String] = {
    if (suffix.startsWith("{")) {
      import net.liftweb.json._
      implicit val formats = DefaultFormats
      parse(suffix) match {
        case JObject(JField("suffix", JString(suffix)) :: JField("prefix", JString(prefix)) :: JField("params", JObject(params)) :: Nil) =>
          S.loc(prefix + "-" + suffix).map(_ => S.?(prefix + "-" + suffix)).map(loc =>
            params.foldLeft(loc)((str, param) => {
              val value = param.value.asInstanceOf[JString].s
              str.replaceAllLiterally("{" + param.name + "}", if (value.startsWith("{")) locProc(value) else value)
            }))
      }
    } else {
      fullPrefix.view.map(prefix => S.loc(prefix + "-" + suffix).map(_ => S.?(prefix + "-" + suffix))).flatten.headOption match {
        case Some(loc) => Some(params.foldLeft(loc)((str, param) => str.replaceAllLiterally("{" + param._1 + "}", param._2)))
        case None =>
          Loc.missing += fullPrefix.head + "-" + suffix
          None
      }
    }
  }

  def locParam(suffix: String, params: (String, String)*): String =
    locParamOpt(suffix, params: _*).getOrElse(fullPrefix.head + "-" + suffix)

  def loc(suffix: String, param: (String, String), rest: (String, String)*): String = locParam(suffix, (param +: rest): _*)

  def locOpt(suffix: String): Option[String] = locParamOpt(suffix)

  def loc(suffix: String) = locParam(suffix)


  def locProc(value: String) = loc(value)

  def locUnproc(suffix: String, params: (String, String)*): String = {
    import net.liftweb.json._
    import net.liftweb.json.JsonDSL._
    val json =
      ("suffix" -> suffix) ~
        ("prefix" -> fullPrefix.head) ~
        ("params" -> JObject(params.toList.map(p => JField(p._1, p._2))))
    compactRender(json)
  }

  def processLoc(): NodeSeq => NodeSeq = {
    def runNodes(in: NodeSeq): NodeSeq = in.flatMap {
      case Group(g) => runNodes(g)
      case e: Elem => {
        e.attributes.collectFirst({case UnprefixedAttribute("loc", key, _) => key.toString}) match {
          case Some(key) => new Elem(e.prefix, e.label,
            e.attributes.filter({case UnprefixedAttribute("loc", key, _) => false case _ => true}),
            e.scope, Text(loc(key)))
          case None =>
            if (e.label == "loc") Text(e.child.headOption.map(_.toString).map(loc(_)).getOrElse(""))
            else new Elem(e.prefix, e.label, e.attributes, e.scope, e.child.map(runNodes(_).head): _*)
        }
      }
      case x => x
    }
    runNodes(_)
  }
}

trait LocEnum extends Enumeration {
  private def self = this

  private val locPrefix = this.getClass.getSimpleName.filter(_.isLetter).splitAt(1) match {
    case (a, b) => a.toLowerCase + b
  }
  private val _loc = new Loc {
    override def locPrefix: String = self.locPrefix
  }

  implicit def toLocValue(v: Value) = new {def loc: String = self._loc.loc(v.toString)}
}