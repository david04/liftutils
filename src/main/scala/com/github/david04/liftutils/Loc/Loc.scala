package com.github.david04.liftutils.Loc

import net.liftweb.common._
import net.liftweb.http.S
import com.github.david04.liftutils.util.Util.__print

trait Loc {

  def defaultPrefix: String = "dflt"

  def locPrefix: String = this.getClass.getSimpleName.filter(_.isLetter).splitAt(1) match {case (a, b) => a.toLowerCase + b }

  def locOpt(suffix: String): Option[String] =
    (S.loc(locPrefix + "-" + suffix).map(_.toString()) or
      S.loc(defaultPrefix + "-" + suffix).map(_.toString()))

  def loc(suffix: String) = locOpt(suffix).getOrElse(locPrefix + "-" + suffix)

  def locParam(suffix: String, params: (String, String)*): String =
    params.foldLeft(loc(suffix))((str, param) => str.replaceAllLiterally("{" + param._1 + "}", param._2))

  def loc(suffix: String, param: (String, String), rest: (String, String)*): String = locParam(suffix, (param +: rest): _*)
}

trait LocEnum extends Enumeration with Loc {
  private def self = this
  implicit def toLocValue(v: Value) = new {def loc: String = self.loc(v.toString)}
}