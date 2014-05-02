package com.github.david04.liftutils.modtbl


trait PersistentSortableQueryableTable extends SortableQueryableTable with NamedColTable with PersistentTable {

  type C <: SortCol with NamedCol

  protected lazy val _currentSortColNameVar = props.strVar("sortCol", columns.head.name)
  override protected def currentSortCol: C = columns.find(_.name == _currentSortColNameVar.get).getOrElse(columns.head)
  override protected def currentSortCol_=(c: C): Unit = {
    _currentSortColNameVar() = c.name
  }

  override protected val currentSortAsc = props.boolVar("sortAsc", currentSortCol.defaultSortAsc)
}
