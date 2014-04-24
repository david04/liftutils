package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.http.{SHtml, Templates, S}
import net.liftweb.util.{ClearNodes, ClearClearable, PassThru}
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.Loc.Loc
import com.github.david04.liftutils.elem.ID

trait Col {

  def tdClasses = ""

  def thClasses = ""

  def tdStyle = List[String]()

  def thStyle = List[String]()
}

trait Table extends Loc with ID {

  def table = this

  def sel(id: String): String = "$('#" + id + "')"

  def sel(id: String, rest: String): String = sel(id) + rest

  trait TableCol extends Col with Loc {
    self: C =>
    override def parentLoc = table

    def renderHead: NodeSeq => NodeSeq =
      "th [class+]" #> thClasses &
        "th [style+]" #> thStyle

    def renderRow(row: R, rowId: String, idx: Int, colId: String): NodeSeq => NodeSeq =
      "td [class+]" #> tdClasses &
        "td [style+]" #> tdStyle &
        "td [id]" #> colId
  }

  /** Row type */
  type R
  /** Column type */
  type C <: TableCol

  /** Get all rows. */
  protected def rows: Seq[R]

  /** Get all columns. */
  protected def columns: Seq[C]

  protected def templatePath: List[String] = "templates-hidden" :: "modtbl-dflt" :: Nil

  def keepClasses: List[String] = Nil

  protected lazy val template = {
    val pass1 = ClearClearable(Templates(templatePath).get)
    val pass2 = (keepClasses.map(clas => s".$clas [class!]" #> "modtbl-clearable").reduceOption(_ & _).getOrElse(PassThru)).apply(pass1)
    (".modtbl-clearable" #> ClearNodes).apply(pass2)
  }

  protected lazy val pageRenderer = SHtml.idMemoize(_ => pageTransforms())
  protected lazy val tableRenderer = SHtml.idMemoize(_ => tableTransforms())
  protected lazy val tableBodyRenderer = SHtml.idMemoize(_ => tableBodyTransforms())

  protected def rerenderPage() = pageRenderer.setHtml()
  protected def rerenderTable() = tableRenderer.setHtml()
  protected def rerenderTableBody() = tableBodyRenderer.setHtml()

  protected def pageTransforms(): NodeSeq => NodeSeq = ".modtbl-table [id]" #> id('table) andThen ".modtbl-table" #> tableRenderer

  protected def tableTransforms(): NodeSeq => NodeSeq = "thead tr th" #> columns.map(col => col.renderHead) & "tbody" #> tableBodyRenderer

  protected def tableBodyTransforms(): NodeSeq => NodeSeq = "tr" #> rowsTransforms()

  protected def rowsTransforms(): Seq[NodeSeq => NodeSeq] = rows.zipWithIndex.map(row => rowTransforms(row._1, S.formFuncName, row._2))

  protected def trStyle = List[String]()

  protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    "tr [class+]" #> trStyle.mkString(" ") &
      "tr [id]" #> rowId &
      "td" #> columns.map(col => col.renderRow(row, rowId, rowIdx, S.formFuncName))


  def renderedTable(): NodeSeq =
    (".modtbl-around [modtbl]" #> locPrefix &
      ".modtbl-around [id]" #> id('around) andThen
      ".modtbl-around" #> pageRenderer).apply(template)

  def renderTable(): NodeSeq => NodeSeq = (_: NodeSeq) => renderedTable()
}


