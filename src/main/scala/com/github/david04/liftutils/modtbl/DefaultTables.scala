//  Copyright (c) 2014 David Miguel Antunes <davidmiguel {at} antunes.net>
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.

package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.util.PassThru

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







