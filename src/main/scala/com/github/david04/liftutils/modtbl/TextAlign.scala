package com.github.david04.liftutils.modtbl


trait TextAlignTable extends Table {

  trait CenterThCol extends TableCol {
    self: C =>
    override def thStyle = "text-align:center;" :: super.thStyle
  }

  trait CenterTdCol extends TableCol {
    self: C =>
    override def tdStyle = "text-align:center;" :: super.tdStyle
  }

  trait CenterThTdCel extends CenterThCol with CenterTdCol {
    self: C =>
  }

  trait RightThCol extends TableCol {
    self: C =>
    override def thStyle = "text-align:right;" :: super.thStyle
  }

  trait RightTdCol extends TableCol {
    self: C =>
    override def tdStyle = "text-align:right;" :: super.tdStyle
  }

}

