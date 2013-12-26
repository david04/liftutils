package com.github.david04.liftutils.Loc

import net.liftweb.http.S

trait Loc {
  def locPrefix: String
  def loc(suffix: String) = S.?(locPrefix + "-" + suffix)
}