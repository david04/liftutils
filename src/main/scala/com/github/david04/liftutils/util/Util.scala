package com.github.david04.liftutils.util

import scala.xml._
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.JsNull
import net.liftweb.json.JsonAST.JValue
import net.liftweb.http.js.JsCmd
import scala.Some


object Util {

  var enableProfiling = true

  def iefix = Unparsed( """
                          | <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
                          | <!--[if lt IE 9]>
                          | <script src="/js/html5shiv.js"></script>
                          | <![endif]-->
                          |
                          | """.stripMargin)

  implicit def __print[T](v: T) = new {
    def p(s: String = "", t: T => String = _.toString): T = { println(s + t(v)); v }

    def p: T = p("")
  }

  def run(b: => JsCmd) = SHtml.jsonCall(JsNull, (_: JValue) => b).toJsCmd + ";"

  def runJsCmd(b: => JsCmd) = SHtml.jsonCall(JsNull, (_: JValue) => b)

  var idx = 0
  var lastOpen = false

  /**
   * Profile
   */
  def %?[T](s: String)(b: => T) =
    if (!enableProfiling) b
    else {
      val space = (0 until idx).map(_ => "  ").mkString

      if (lastOpen) println()
      print(space + s"Starting '$s'...")

      lastOpen = true
      idx = idx + 2
      val start = System.currentTimeMillis()
      val (ret, ex) = try {
        val ret = b
        (Some(ret), None)
      } catch {
        case t: Throwable => (None, Some(t))
      }
      val took = System.currentTimeMillis() - start
      idx = idx - 2

      val exception = ex match {case Some(t) => s" ! {Exception: '${t.getMessage}'}" case None => ""}

      println(
        if (lastOpen) s" [${took}ms]$exception" else s"${space}Finished '$s' [${took}ms]$exception"
      )
      lastOpen = false
      (ret, ex) match {
        case (Some(ret), _) => ret
        case (_, Some(t)) => throw t
      }
    }
}
