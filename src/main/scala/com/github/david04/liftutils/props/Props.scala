package com.github.david04.liftutils.props

import net.liftweb.util.BasicTypesHelpers
import net.liftweb.json.{DefaultFormats, JsonAST}
import net.liftweb.json.JsonAST.{JArray, JString, JField, JObject}


trait Props {

//  lazy val props = {
//    implicit val formats = DefaultFormats
//    var propsJson = net.liftweb.json.parse(props).asInstanceOf[JObject]
//    case class Prop(_prefix: String) {
//
//      def in(prefix: String) = Prop(_prefix + "." + prefix)
//
//      private def withPrefix(name: String) = _prefix + "." + name
//
//      val str = new {
//        def apply(name: String): Option[String] =
//          propsJson.values.get(withPrefix(name)).map(_.asInstanceOf[String])
//
//        def update(name: String, value: Option[String]) {
//          propsJson = propsJson.copy(obj = propsJson.obj.filter(_.name != withPrefix(name)) ++ value.map(value => JField(withPrefix(name), JString(value))).toList)
//          self.update(_.props = JsonAST.compactRender(propsJson))
//          println(propsJson)
//        }
//
//        def update(name: String, value: String): Unit = update(name, Some(value))
//      }
//      val int = new {
//        def apply(name: String): Option[Int] = str(name).flatMap(v => BasicTypesHelpers.tryo(v.toInt))
//
//        def update(name: String, value: Option[Int]) = str(name) = value.map(_ + "")
//
//        def update(name: String, value: Int): Unit = update(name, Some(value))
//      }
//      val long = new {
//        def apply(name: String): Option[Long] = str(name).flatMap(v => BasicTypesHelpers.tryo(v.toLong))
//
//        def update(name: String, value: Option[Long]) = str(name) = value.map(_ + "")
//
//        def update(name: String, value: Long): Unit = update(name, Some(value))
//      }
//      val strSeq = new {
//        def apply(name: String): Option[List[String]] = {
//          val prefixed = withPrefix(name)
//          propsJson.obj
//            .collectFirst({case JField(`prefixed`, JArray(values)) => values})
//            .map(_.collect({case JString(v) => v}))
//        }
//
//        def update(name: String, value: Option[Seq[String]]) {
//          propsJson = propsJson.copy(obj = propsJson.obj.filter(_.name != withPrefix(name)) ++ value.map(value => JField(withPrefix(name), JArray(value.map(JString(_)).toList))).toList)
//          self.update(_.props = JsonAST.compactRender(propsJson))
//          println(propsJson)
//        }
//
//        def update(name: String, value: Seq[String]): Unit = update(name, Some(value))
//      }
//
//      def remove(name: String) = str(name) = None
//    }
//    Prop("")
//  }
}
