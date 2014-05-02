package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.Run


trait ClientSideSearchableTable extends Table {

  override def keepClasses = "modtbl-search-around" :: super.keepClasses

  override protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    super.rowTransforms(row, rowId, rowIdx) andThen
      "tr [class+]" #> "modtbl-searchable"

  override protected def pageTransforms(): NodeSeq => NodeSeq =
    super.pageTransforms() andThen
      ".modtbl-search" #> {
        val inputId = Helpers.nextFuncName
        ".modtbl-search [id]" #> inputId &
          ".modtbl-search [onkeyup]" #>
            Run("" +
              "(function(){" +
              "  var query = $('#" + inputId + "').val();" +
              "  $('#" + id('table) + " tbody tr')" +
              "    .each(function(){" +
              "      if($(this).text().toLowerCase().indexOf(query) == -1) $(this).hide();" +
              "      else $(this).show();" +
              "    });" +
              "})()")
      }

}
