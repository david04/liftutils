package com.github.david04.liftutils.util

import scala.xml.Unparsed
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.JsNull
import net.liftweb.json.JsonAST.JValue
import net.liftweb.http.js.JsCmd


object Util {

  def iefix = Unparsed( """
                          | <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
                          | <!--[if lt IE 9]>
                          | <script src="/js/html5shiv.js"></script>
                          | <![endif]-->
                          |
                          | """.stripMargin)

  implicit def ___printable[T](o: T) = new Object() {
    def p(s: String = "", fmt: (T) => String = _.toString) = { println(s + fmt(o)); o }
  }

  def run(b: => JsCmd) = SHtml.jsonCall(JsNull, (_: JValue) => b).toJsCmd
}
