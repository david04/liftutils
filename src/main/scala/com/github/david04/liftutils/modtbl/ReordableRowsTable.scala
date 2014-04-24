package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE.JsRaw

trait ReordableRowsTable extends Table {

  def reordered(row: R, idx: Int): JsCmd = Noop

  override protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    super.rowTransforms(row, rowId, rowIdx) andThen
      "tr [reorder]" #> SHtml.ajaxCall(JsRaw("idx"), idx => {
        reordered(row, idx.toInt) & rerenderTable()
      }).toJsCmd

  def reorderRowMinDistancePx = 15

  override protected def tableTransforms(): NodeSeq => NodeSeq =
    super.tableTransforms() andThen
      ((ns: NodeSeq) => ns ++
        <tail>{
          Script(Run(
            "$('#" + id('table) + " tbody')" +
              ".sortable({" +
              s"distance: $reorderRowMinDistancePx," +
              "update: " +
              "  function( event, ui ) {" +
              "    var sorted = " +
              "      $.map($.map($('#" + id('table) + " tr'), " +
              "        function(r) {return {id: $(r).attr('id'), offset: r.offsetTop};})" +
              "          .sort(function(a,b){return a.offset-b.offset;}), function(r) {return r.id;});" +
              "    var idx = sorted.indexOf(ui.item.attr('id'));" +
              "    window.sorted = sorted;" +
              "    window.idx = idx;" +
              "    eval('0, ' + ui.item.attr('reorder'));" +
              "  }," +
              "helper:" +
              "  function(e, ui) {" +
              "    ui.children().each(function() {" +
              "      $(this).width($(this).width());" +
              "    });" +
              "    return ui;" +
              "  }" +
              "})"))
        }</tail>)
}
