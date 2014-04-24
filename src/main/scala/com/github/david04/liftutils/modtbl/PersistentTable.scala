package com.github.david04.liftutils.modtbl

import net.liftweb.util.FatLazy
import com.github.david04.liftutils.Loc.Loc
import com.github.david04.liftutils.props.Props
import com.github.david04.liftutils.elem.ID


trait PersistentTable extends Table {
  def props: Props
}
