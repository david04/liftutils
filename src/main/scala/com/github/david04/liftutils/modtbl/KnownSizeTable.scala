package com.github.david04.liftutils.modtbl

import net.liftweb.http.S
import net.liftweb.util.FatLazy
import net.liftweb.http.js.JsCmds.Run
import com.github.david04.liftutils.elem.ID


trait KnownSizeTable extends Table {

  protected def rowsSize: Int
}