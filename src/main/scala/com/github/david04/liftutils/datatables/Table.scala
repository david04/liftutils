package com.github.david04.liftutils.datatables

import net.liftweb._
import net.liftweb.http._
import common._
import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
import net.liftweb.http.js._
import JsCmds._
import JE._
import S._
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import scala.collection.mutable
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.util.StringHelpers.encJs

abstract class Table[T](
                         sel: String,
                         values: () => Seq[T],
                         sDom: String = """<"tbl-searchbox clearfix"lf<"clear">>,<"box-widget"<"widget-container"<"table_content"t>>>,<"tbl-bottom"p<"clear">>""",
                         showRow: Option[T => Boolean] = None,
                         displayLength: Int = 30) {

  val columns: List[Col[T]]

  case class Col[T](
                     title: String = "",
                     _value: (T) => JsonAST.JValue = (t: T) => JNull,
                     hidden: Boolean = false,
                     bSortable: Boolean = true,
                     defaultSort: Boolean = false,
                     defaultSortAscending: Boolean = true,
                     centerH: Boolean = false,
                     centerR: Boolean = false
                     ) {

    def center(b: Boolean, s: String) = if (b) s"<center>$s</center>" else s

    override def toString = s"{ " +
      s"'sTitle': ${encJs(center(centerH, title))}, " +
      s"'bSortable': ${"" + bSortable}, " +
      s"}"

    def value(t: T) = _value(t) match {
      case JString(s) => JString(center(centerR, s))
      case o => o
    }
  }

  def iDisplayStart = showRow.map(selector => {
    val sortCol = columns.find(_.defaultSort).get
    val data = (values().map(t => (t, sortCol._value(t))).sortBy(_._2 match {case JString(v) => v; case JDouble(v) => v.toString; case JInt(v) => v.toString; case JBool(v) => v.toString; case v => v.toString;}) match {
      case asc => if (sortCol.defaultSortAscending) asc else asc.reverse
    })
    data.indexWhere(e => selector(e._1))
  })

  def reload() = Run("$('" + sel + "').dataTable().fnReloadAjax()")

  def setUp(): Unit = {

    val f = (_: String) => JsonResponse("aaData" -> JArray(values().map(e => JArray(columns.map(_.value(e)))).toList))

    fmapFunc(SFuncHolder(f)) {
      func =>
        val where: String = encodeURL(S.contextPath + "/" + LiftRules.ajaxPath + "?" + func + "=foo")
        val cols = columns.filter(!_.hidden).map(_.toString).mkString("[", ", ", "]")
        val sorting = columns.zipWithIndex.find(_._1.defaultSort).map(c => s"  'aaSorting': [[ ${c._2}, '${if (c._1.defaultSortAscending) "asc" else "desc"}' ]],").getOrElse("")

        S.appendJs(OnLoad(Run("" +
          "    $(" + sel.encJs + ").dataTable({" +
          "          'sPaginationType': 'full_numbers'," +
          "          'oLanguage': {" +
          "              'sLengthMenu': \"<span class='lenghtMenu'> _MENU_</span><span class='lengthLabel'>Entries per page:</span>\"," +
          "          }," +
          // Must not be the last line!
          sorting +
          iDisplayStart.map(i => s" 'iDisplayStart': $i,").getOrElse("") +
          s"          'aoColumns': $cols," +
          s"          'iDisplayLength': $displayLength," +
          s"          'sAjaxSource': ${where.encJs}," +
          s"          'sDom': ${sDom.encJs}" +
          //          "          'bJQueryUI': true" +
          "      });" +
          "  $('div.tbl-searchbox select').addClass('tbl_length');")))
    }
  }
}