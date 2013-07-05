package com.github.david04.liftutils.datatables

import net.liftweb.http._
import net.liftweb.util.Helpers._
import net.liftweb.http.js._
import JsCmds._
import S._
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import net.liftweb.util.StringHelpers.encJs

case class OLanguage(
                      sEmptyTable: String = "No data available in table",
                      sLengthMenu: String = "<span class='lenghtMenu'> _MENU_</span><span class='lengthLabel'>Entries per page:</span>"
                      ) {
  def js = s""" "oLanguage": {
    "sEmptyTable": "$sEmptyTable",
    "sLengthMenu": "$sLengthMenu"
  }"""
}

case class Col[T](
                   title: String = "",
                   _value: (T) => JsonAST.JValue = (t: T) => JNull,
                   hidden: Boolean = false,
                   bSortable: Boolean = true,
                   defaultSort: Boolean = false,
                   defaultSortAscending: Boolean = true,
                   centerH: Boolean = null.asInstanceOf[Boolean],
                   centerR: Boolean = null.asInstanceOf[Boolean]
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

abstract class Table[T](
                         values: () => Seq[T],
                         showRow: Option[T => Boolean] = None,
                         displayLength: Int = 30,
                         defaultCenterH: Boolean = false,
                         defaultCenterR: Boolean = false,
                         tableClass: String = "table table-bordered table-condensed table-hover table-striped"
                         ) {

  val sDom = """<'pull-right'>t<'row-fluid'<'span6'><'span6'p>>"""
  val sPaginationType = "bootstrap"
  val id: String = ## + ""
  val bJQueryUI = false

  def defaultOLanguage = OLanguage()

  val columns: List[Col[T]]

  def iDisplayStart = showRow.map(selector => {
    val sortCol = columns.find(_.defaultSort).get
    val data = (values().map(t => (t, sortCol._value(t))).sortBy(_._2 match {case JString(v) => v; case JDouble(v) => v.toString; case JInt(v) => v.toString; case JBool(v) => v.toString; case v => v.toString;}) match {
      case asc => if (sortCol.defaultSortAscending) asc else asc.reverse
    })
    data.indexWhere(e => selector(e._1))
  })

  def reload() = Run("$('#" + id + "').dataTable().fnReloadAjax()")

  var initialized = false

  val f = (_: String) => JsonResponse("aaData" -> JArray(values().map(e => JArray(columns.map(_.value(e)))).toList))

  lazy val setUpJs = fmapFunc(SFuncHolder(f)) {
    func =>
      val where: String = encodeURL(S.contextPath + "/" + LiftRules.ajaxPath + "?" + func + "=foo")
      val cols = columns.filter(!_.hidden).map(_.toString).mkString("[", ", ", "]")
      val sorting = columns.zipWithIndex.find(_._1.defaultSort).map(c => s"  'aaSorting': [[ ${c._2}, '${if (c._1.defaultSortAscending) "asc" else "desc"}' ]],").getOrElse("")

      Run("" +
        "    $(" + ("#" + id).encJs + ").dataTable({" +
        "          'sPaginationType': 'full_numbers'," +
        s"          ${defaultOLanguage.js}," +
        // Must not be the last line!
        sorting +
        iDisplayStart.map(i => s" 'iDisplayStart': $i,").getOrElse("") +
        s"          'aoColumns': $cols," +
        s"          'iDisplayLength': $displayLength," +
        s"          'sAjaxSource': ${where.encJs}," +
        s"          'sDom': ${sDom.encJs}," +
        s"          'sPaginationType': ${sPaginationType.encJs}," +
        s"          'bJQueryUI': $bJQueryUI" +
        "      });")
  }

  def setUpOnLoad(): Unit = if (!initialized) {
    initialized = true
    S.appendJs(OnLoad(setUpJs))
  }

  def setUpOrReload() = if (!initialized) {initialized = true; setUpJs} else {reload()}

  def render = {
    setUpOnLoad()
    <table id={id} class={tableClass}></table>
  }
}