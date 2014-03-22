package code.modtbl

import scala.xml.NodeSeq
import net.liftweb.http.{SHtml, Templates, S}
import net.liftweb.util.{FatLazy, ClearClearable, PassThru}
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.Loc.Loc
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.http.js.JsCmds.Run
import com.github.david04.liftutils.props.Props
import com.github.david04.liftutils.elem.ID

trait Col {

  def tdClasses = ""

  def thClasses = ""

  def tdStyle = ""

  def thStyle = ""
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
  protected val columns: Seq[C]

  protected def templatePath: List[String] = "templates-hidden" :: "modtbl-dflt" :: Nil

  protected lazy val template = ClearClearable(Templates(templatePath).get)

  protected lazy val pageRenderer = SHtml.idMemoize(_ => pageTransforms())

  protected def rerenderPage() = pageRenderer.setHtml()

  protected def pageTransforms(): NodeSeq => NodeSeq =
    ".modtbl-table [id]" #> id('table) andThen
      ".modtbl-table" #> tableRenderer

  protected lazy val tableRenderer = SHtml.idMemoize(_ => tableTransforms())

  protected def rerenderTable() = tableRenderer.setHtml()

  protected def tableTransforms(): NodeSeq => NodeSeq =
    "thead tr th" #> columns.map(col => col.renderHead) &
      "tbody tr" #> rows.zipWithIndex.map(row => rowTransforms(row._1, S.formFuncName, row._2))

  protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    "tr [id]" #> rowId &
      "td" #> columns.map(col => col.renderRow(row, rowId, rowIdx, S.formFuncName))

  def renderedTable(): NodeSeq =
    (".modtbl-around [modtbl]" #> locPrefix &
      ".modtbl-around [id]" #> id('around) andThen
      ".modtbl-around" #> pageRenderer).apply(template)

  def renderTable(): NodeSeq => NodeSeq = (_: NodeSeq) => renderedTable()
}

trait ZebraTable extends Table {

  protected def zebraTableEvenClass = "even"

  protected def zebraTableOddClass = "odd"

  override protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    super.rowTransforms(row, rowId, rowIdx) andThen
      "tr [class+]" #> (if (rowIdx % 2 == 0) zebraTableEvenClass else zebraTableOddClass)
}

trait ClickableRowTable extends Table {
  type C <: ClickableRowCol

  protected def onClick(row: R, rowId: String, rowIdx: Int, col: C, colId: String): JsCmd = JsCmds.Noop

  trait ClickableRowCol extends TableCol {
    self: C =>

    override def renderRow(row: R, rowId: String, rowIdx: Int, colId: String): NodeSeq => NodeSeq =
      super.renderRow(row, rowId, rowIdx, colId) andThen
        "td [onclick]" #> SHtml.onEvent(_ => onClick(row, rowId, rowIdx, this, colId)).toJsCmd
  }

}

trait RowDetailsTable extends ClickableRowTable {

  protected def rowDetailsTemplatePath: List[String] = "templates-hidden" :: "modtbl-rowDetails-dflt" :: Nil

  protected def rowDetailsTemplate = Templates(rowDetailsTemplatePath).get(0).descendant(3)

  protected var currentDetailsRow: Option[(R, JsCmd)] = None

  protected def rowDetailsClasses: List[String] = "details" :: Nil

  protected def rowDetailsContentClass: String = "details-contents"

  protected def openDetailsRow(row: R, rowId: String, rowIdx: Int): JsCmd = Run {
    sel(rowId, ".after(" + rowDetailsTransforms(row, rowId, rowIdx, false)(rowDetailsTemplate).toString().encJs + ");") +
      sel(s"$rowId-details .$rowDetailsContentClass", ".slideDown(400);")
  }

  protected def closeDetailsRow(row: R, rowId: String, rowIdx: Int): JsCmd = Run {
    sel(s"$rowId-details .$rowDetailsContentClass", ".slideUp(400, function() {" +
      sel(s"$rowId-details", ".remove();") +
      "});")
  }

  override protected def onClick(row: R, rowId: String, rowIdx: Int, col: C, colId: String): JsCmd = {
    currentDetailsRow match {
      case Some((prev, close)) if prev == row =>
        currentDetailsRow = None
        close
      case None =>
        currentDetailsRow = Some((row, closeDetailsRow(row, rowId, rowIdx)))
        openDetailsRow(row, rowId, rowIdx)
      case Some((_, close)) =>
        currentDetailsRow = Some((row, closeDetailsRow(row, rowId, rowIdx)))
        close & openDetailsRow(row, rowId, rowIdx)
    }
  }

  protected def rowDetailsTransforms(row: R): NodeSeq => NodeSeq

  protected def rowDetailsTransforms(row: R, rowId: String, rowIdx: Int, visible: Boolean): NodeSeq => NodeSeq =
    "tr [id]" #> s"$rowId-details" &
      s"tr .$rowDetailsContentClass [style+]" #> (if (visible) "" else ";display:none;") andThen
      "td [colspan]" #> columns.size &
        "td [class+]" #> rowDetailsClasses.mkString(" ") &
        "td" #> rowDetailsTransforms(row)

  override protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    super.rowTransforms(row, rowId, rowIdx) andThen {
      currentDetailsRow match {
        case Some((open, _)) if open == row =>
          currentDetailsRow = Some((row, closeDetailsRow(row, rowId, rowIdx)))
          (ns: NodeSeq) => ns ++ rowDetailsTransforms(row, rowId, rowIdx, true)(rowDetailsTemplate)
        case _ =>
          PassThru
      }
    }
}

trait PersistentTable extends Table {
  def props: Props
}

trait QueryableTable extends Table {

  trait Query {}

  type Q <: Query

  protected def query(params: Q): Seq[R]

  protected def createQuery(): Q

  protected def prepareQuery(query: Q): Q = query

  protected def rows = query(prepareQuery(createQuery()))
}

trait KnownSizeQueryableTable extends QueryableTable {

  protected val rowsSize: Int
}

trait PaginatedQueryableTable extends QueryableTable {

  trait PagQuery extends Query {
    var pageSize: Int
    var pageOffset: Int
  }

  type Q <: PagQuery

  protected lazy val pagBtnsCurrentClass = "active"
  protected lazy val pagBtnsDisabledClass = "disabled"
  protected lazy val pagNBtns = 5
  protected lazy val defaultPageSize = 40

  protected var currentPage = 0
  protected var pageSize = defaultPageSize
  protected val nPages: Int

  override protected def prepareQuery(_query: Q): Q = {
    val query = super.prepareQuery(_query)
    query.pageSize = pageSize
    query.pageOffset = currentPage * pageSize
    query
  }

  override protected def pageTransforms() =
    super.pageTransforms() andThen
      ".modtbl-pag-btns-around" #> paginationButtonsRenderer &
        ".modtbl-pag-info-around" #> paginationInfoRenderer

  protected def firstPage() = SHtml.onEvent(_ => {
    currentPage = 0
    rerenderPage()
  }).cmd & Run("return false;")

  protected def prevPage() = SHtml.onEvent(_ => {
    currentPage = math.max(0, currentPage - 1)
    rerenderPage()
  }).cmd & Run("return false;")

  protected def toPage(n: Int) = SHtml.onEvent(_ => {
    currentPage = n
    rerenderPage()
  }).cmd & Run("return false;")

  protected def nextPage() = SHtml.onEvent(_ => {
    currentPage = math.min(nPages - 1, currentPage + 1)
    rerenderPage()
  }).cmd & Run("return false;")

  protected def lastPage() = SHtml.onEvent(_ => {
    currentPage = nPages - 1
    rerenderPage()
  }).cmd & Run("return false;")

  protected def currentButtons() = {
    val side = pagNBtns - 1
    val all = ((currentPage - side) until currentPage) ++ ((currentPage + 1) to (currentPage + side))
    all.filter(_ >= 0).filter(_ < nPages).sortBy(n => math.abs(currentPage - n)).take(side).:+(currentPage).sorted
  }

  protected lazy val paginationButtonsRenderer = SHtml.idMemoize(_ => paginationButtonsTransforms())

  protected def paginationButtonsTransforms(): NodeSeq => NodeSeq =
    ".modtbl-pag-first [class+]" #> (if (currentPage == 0) pagBtnsDisabledClass else "") &
      ".modtbl-pag-first" #> {".modtbl-pag-btn [onclick]" #> firstPage()} &
      ".modtbl-pag-prev [class+]" #> (if (currentPage == 0) pagBtnsDisabledClass else "") &
      ".modtbl-pag-prev" #> {".modtbl-pag-btn [onclick]" #> prevPage()} &
      ".modtbl-pag-next [class+]" #> (if (currentPage == nPages - 1) pagBtnsDisabledClass else "") &
      ".modtbl-pag-next" #> {".modtbl-pag-btn [onclick]" #> nextPage()} &
      ".modtbl-pag-last [class+]" #> (if (currentPage == nPages - 1) pagBtnsDisabledClass else "") &
      ".modtbl-pag-last" #> {".modtbl-pag-btn [onclick]" #> lastPage()} andThen
      ".modtbl-pag-n" #> currentButtons().map(n =>
        ".modtbl-pag-n [class+]" #> (if (currentPage == n) pagBtnsCurrentClass else "") &
          ".modtbl-pag-btn *" #> (n + 1).toString &
          ".modtbl-pag-btn [onclick]" #> toPage(n))

  protected lazy val paginationInfoRenderer = SHtml.idMemoize(_ => paginationInfoTransforms())

  protected def paginationInfoTransforms(): NodeSeq => NodeSeq
}

trait KnownSizePaginatedQueryableTable extends PaginatedQueryableTable with KnownSizeQueryableTable {
  protected lazy val nPages = math.ceil(rowsSize / pageSize.toDouble).toInt

  protected def paginationInfoTransforms(): NodeSeq => NodeSeq =
    ".modtbl-pag-info *" #>
      loc("pagInfo",
        "from" -> (currentPage * pageSize + 1).toString,
        "to" -> ((currentPage + 1) * pageSize).toString,
        "total" -> rowsSize.toString)
}

trait UnknownSizePaginatedQueryableTable extends PaginatedQueryableTable {

  protected lazy val nPages = Int.MaxValue / 2

  protected def paginationInfoTransforms(): NodeSeq => NodeSeq =
    ".modtbl-pag-info *" #>
      loc("pagInfo",
        "from" -> (currentPage * pageSize + 1).toString,
        "to" -> ((currentPage + 1) * pageSize).toString)
}


trait SortableQueryableTable extends QueryableTable {

  protected def sortThNone = "sorting"

  protected def sortThAsc = "sorting_asc"

  protected def sortThDesc = "sorting_desc"

  trait SortCol extends TableCol {
    self: C =>

    def defaultSortAsc: Boolean = true

    def sortable: Boolean

    override def renderHead: NodeSeq => NodeSeq =
      super.renderHead andThen
        (if (sortable)
          "th [class+]" #> (if (this == currentSortCol) (if (currentSortAsc.get) sortThAsc else sortThDesc) else sortThNone) &
            "th [onclick]" #> clickedSortableHeader(this)
        else PassThru)
  }

  trait SortQuery extends Query {
    var sortColumn: C
    var sortAsc: Boolean
  }

  type C <: SortCol
  type Q <: SortQuery

  protected var _currentSortCol: C = columns.head
  protected def currentSortCol: C = _currentSortCol
  protected def currentSortCol_=(c: C): Unit = _currentSortCol = c
  protected val currentSortAsc: FatLazy[Boolean] = FatLazy(currentSortCol.defaultSortAsc)

  protected def clickedSortableHeader(col: C) = SHtml.onEvent(_ => {
    if (col == currentSortCol) {
      currentSortAsc() = !currentSortAsc.get
    } else {
      currentSortCol = col
      currentSortAsc() = col.defaultSortAsc
    }

    tableRenderer.setHtml()
  })

  override protected def prepareQuery(_query: Q): Q = {
    val query = super.prepareQuery(_query)
    query.sortColumn = currentSortCol
    query.sortAsc = currentSortAsc.get
    query
  }
}

trait PersistentSortableQueryableTable extends SortableQueryableTable with NamedColTable with PersistentTable {

  type C <: SortCol with NamedCol

  protected lazy val _currentSortColIdx = props.intVar("sortCol", 0)
  override protected def currentSortCol: C = columns.sortBy(_.name).drop(_currentSortColIdx.get).headOption.getOrElse(columns.head)
  override protected def currentSortCol_=(c: C): Unit = {
    _currentSortColIdx() = columns.sortBy(_.name).indexOf(c)
  }

  override protected val currentSortAsc = props.boolVar("sortAsc", currentSortCol.defaultSortAsc)
}

abstract class DefaultTable extends Table
                                    with NamedTable
                                    with QueryableTable
                                    with PaginatedQueryableTable
                                    with SortableQueryableTable
                                    with LocStrHeadTable
                                    with StrRowTable
                                    with ZebraTable {
  type Q = DefaultQuery
  type C <: DefaultColumn

  case class DefaultQuery(
                           var pageSize: Int,
                           var pageOffset: Int,
                           var sortColumn: C,
                           var sortAsc: Boolean) extends Query
                                                         with PagQuery
                                                         with SortQuery

  def createQuery = DefaultQuery(0, 0, columns.head, false)

  abstract class DefaultColumn(
                                name: String,
                                rowValue: R => String) extends TableCol
                                                               with LocStrHeadCol
                                                               with StrRowCol
                                                               with SortCol {
    self: C =>

    def rowStrValue: R => String = rowValue
  }

}

abstract class DefaultSimpleTable extends Table
                                          with NamedTable
                                          with LocStrHeadTable
                                          with StrRowTable {
  override protected def templatePath: List[String] = "templates-hidden" :: "modtbl-simple" :: Nil

  trait DefaultColumn extends TableCol with LocStrHeadCol {}

  type C = DefaultColumn

  case class DefaultStringColumn(
                                  name: String,
                                  rowValue: R => String) extends DefaultColumn
                                                                 with StrRowCol {
    self: C =>

    def rowStrValue: R => String = rowValue
  }

  case class DefaultNodeSeqColumn(
                                   name: String,
                                   rowValue: R => NodeSeq) extends DefaultColumn
                                                                   with NodeSeqRowCol {
    self: C =>

    def rowNodeSeqValue: R => NodeSeq = rowValue
  }

}


abstract class DefaultOpenableTable extends Table
                                            with NamedTable
                                            with QueryableTable
                                            with PaginatedQueryableTable
                                            with SortableQueryableTable
                                            with RowDetailsTable
                                            with LocStrHeadTable
                                            with StrRowTable
                                            with ZebraTable {
  type Q = DefaultQuery
  type C <: DefaultColumn

  case class DefaultQuery(
                           var pageSize: Int,
                           var pageOffset: Int,
                           var sortColumn: C,
                           var sortAsc: Boolean) extends Query
                                                         with PagQuery
                                                         with SortQuery

  def createQuery = DefaultQuery(0, 0, columns.head, false)

  abstract class DefaultColumn(
                                name: String,
                                rowValue: R => String) extends TableCol
                                                               with ClickableRowCol
                                                               with LocStrHeadCol
                                                               with StrRowCol
                                                               with SortCol {
    self: C =>
  }

}







