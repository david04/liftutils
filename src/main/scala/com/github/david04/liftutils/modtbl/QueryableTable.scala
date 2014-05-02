package com.github.david04.liftutils.modtbl


trait QueryableTable extends Table {

  trait Query {}

  type Q <: Query

  protected def query(params: Q): Seq[R]

  protected def createQuery(): Q

  protected def prepareQuery(query: Q): Q = query

  protected def rows = query(prepareQuery(createQuery()))
}
