package com.github.david04.liftutils.datatables

import net.liftweb.http._
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.Serialization.write

import com.github.david04.liftutils.util.Util.__print
import scala.collection.mutable.ListBuffer

case class oAria(
                  sSortAscending: Option[String] = None,
                  sSortDescending: Option[String] = None
                  )

case class oPaginate(
                      sFirst: Option[String] = None,
                      sLast: Option[String] = None,
                      sNext: Option[String] = None,
                      sPrevious: Option[String] = None
                      )

case class oLanguage(
                      sEmptyTable: Option[String] = None,
                      sInfo: Option[String] = None,
                      sInfoEmpty: Option[String] = None,
                      sInfoFiltered: Option[String] = None,
                      sInfoPostFix: Option[String] = None,
                      sInfoThousands: Option[String] = None,
                      sLengthMenu: Option[String] = None,
                      sLoadingRecords: Option[String] = None,
                      sProcessing: Option[String] = None,
                      sSearch: Option[String] = None,
                      sUrl: Option[String] = None,
                      sZeroRecords: Option[String] = None,
                      oAria: Option[oAria] = None,
                      oPaginate: Option[oPaginate] = None
                      )

case class Col[T](
                   title: String = "",
                   _value: (T) => JsonAST.JValue = (t: T) => JNull,
                   hidden: Boolean = false,
                   bSortable: Boolean = true,
                   defaultSort: Boolean = false,
                   defaultSortAscending: Boolean = true,
                   centerH: Boolean = null.asInstanceOf[Boolean],
                   centerR: Boolean = null.asInstanceOf[Boolean],
                   sClass: String = ""
                   ) {

  def center(b: Boolean, s: String) = if (b) s"<center>$s</center>" else s

  override def toString = s"{ " +
    s"'sTitle': ${(center(centerH, title)).encJs}, " +
    s"'bSortable': ${"" + bSortable}, " +
    s"'sClass': ${sClass.encJs}, " +
    s"}"

  def value(t: T) = _value(t) match {
    case JString(s) => JString(center(centerR, s))
    case o => o
  }
}

trait TableBase {

  val sDom: String
  val sPaginationType = "bootstrap"
  val id: String = ## + ""
  val bJQueryUI = false
  val deferRender = true

  val refreshEveryMillis: Option[Int] = None
  val infiniteScrollHeight: Option[String] = None

  def reload() = Run("$('#" + id + "').dataTable().fnReloadAjax(null, null, true);")

  val oLanguage = new oLanguage()
}

abstract class Table[T](
                         values: () => Seq[T],
                         showRow: Option[T => Boolean] = None,
                         displayLength: Int = 30,
                         defaultCenterH: Boolean = false,
                         defaultCenterR: Boolean = false,
                         tableClass: String = "table table-bordered table-condensed table-hover table-striped"
                         ) extends TableBase {

  val columns: List[Col[T]]

  def iDisplayStart = showRow.map(selector => {
    val sortCol = columns.find(_.defaultSort).get
    val data = (values().map(t => (t, sortCol._value(t))).sortBy(_._2 match {case JString(v) => v; case JDouble(v) => v.toString; case JInt(v) => v.toString; case JBool(v) => v.toString; case v => v.toString;}) match {
      case asc => if (sortCol.defaultSortAscending) asc else asc.reverse
    })
    data.indexWhere(e => selector(e._1))
  })

  private var initialized = false

  val currentRendered: ListBuffer[T] = ListBuffer[T]()

  def f(s: String) = JsonResponse("aaData" -> JArray({
    val nw = values()
    currentRendered.synchronized({
      currentRendered.clear()
      currentRendered.insertAll(0, nw)
    })
    currentRendered.map(e => JArray(columns.map(_.value(e)))).toList
  }))

  def update(old: T, nw: T, col: Col[T]) = {
    val idx = currentRendered.synchronized({
      val _idx = currentRendered.indexOf(old)
      currentRendered(_idx) = nw
      _idx
    })
    Run("$('#" + id + "').dataTable({'bRetrieve': true}).fnUpdate(" +
      (col.value(nw) match {case JString(s) => s.encJs}) + ", " +
      idx + ", " +
      columns.indexOf(col) + "," +
      // Do not redraw:
      "false);" +
      "$('#" + id + "').dataTable({'bRetrieve': true}).fnStandingRedraw();"
    )
  }

  lazy val setUpJs = S.fmapFunc(S.SFuncHolder(f _)) {
    func =>
      val where: String = S.encodeURL(S.contextPath + "/" + LiftRules.ajaxPath + "?" + func + "=foo")
      val cols = columns.filter(!_.hidden).map(_.toString).mkString("[", ", ", "]")
      val sorting = columns.zipWithIndex.find(_._1.defaultSort).map(c => s"  'aaSorting': [[ ${c._2}, '${if (c._1.defaultSortAscending) "asc" else "desc"}' ]],").getOrElse("")

      implicit val formats = Serialization.formats(NoTypeHints)

      ("sPaginationType" -> "full_numbers")
      Run("" +
        "    $(" + ("#" + id).encJs + ").dataTable({" +
        "          'sPaginationType': 'full_numbers'," +
        s"         'oLanguage': ${write(oLanguage).p}," +
        // Must not be the last line!
        sorting +
        iDisplayStart.map(i => s" 'iDisplayStart': $i,").getOrElse("") +
        s"          'aoColumns': $cols," +
        s"          'iDisplayLength': $displayLength," +
        s"          'sAjaxSource': ${where.encJs}," +
        s"          'bDeferRender': ${deferRender}," +
        s"          'sDom': ${sDom.encJs}," +
        s"          'sPaginationType': ${sPaginationType.encJs}," +
        s"          'fnInitComplete': function() { this.fnAdjustColumnSizing(true); }," +
        infiniteScrollHeight.map(h => s"'bDeferRender': true, 'sScrollY': '$h',").getOrElse("") +
        s"          'bJQueryUI': $bJQueryUI" +
        "      });" +
        refreshEveryMillis.map(t => s"setInterval(function(){${reload().toJsCmd}},$t);").getOrElse(""))
  }

  def setUpOnLoad(): Unit = if (!initialized) {
    initialized = true
    S.appendJs(OnLoad(setUpJs))
  }

  def setUpOrReload() = if (!initialized) {initialized = true; setUpJs} else {reload()}

  def render =
    <table id={id} class={tableClass}></table> ++
      Script(setUpOnLoad())
}