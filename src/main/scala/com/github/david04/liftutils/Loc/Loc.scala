package com.github.david04.liftutils.Loc

import net.liftweb.common._
import net.liftweb.http.S
import com.github.david04.liftutils.util.Util.__print

trait Loc {

  def defaultPrefix: String = "*"

  def parentLoc: Loc = null

  def locPrefix: String =
    Stream.iterate[Class[_]](this.getClass)(_.getSuperclass).dropWhile(_.getSimpleName.filter(_.isLetter).toLowerCase.startsWith("anon")).head
      .getSimpleName.filter(_.isLetter).splitAt(1) match {
      case (a, b) => a.toLowerCase + b
    }

  def fullPrefix: List[String] =
    Option(parentLoc).map(_.fullPrefix.map(_ + "-")).getOrElse("" :: Nil).flatMap(p => List(p + locPrefix, p + "*"))

  def locOpt(suffix: String): Option[String] =
    fullPrefix.view.map(prefix => S.loc(prefix + "-" + suffix)).flatten.headOption.map(_.toString())

  def loc(suffix: String) = locOpt(suffix).getOrElse(fullPrefix.head + "-" + suffix)

  def locParam(suffix: String, params: (String, String)*): String =
    params.foldLeft(loc(suffix))((str, param) => str.replaceAllLiterally("{" + param._1 + "}", param._2))

  def loc(suffix: String, param: (String, String), rest: (String, String)*): String = locParam(suffix, (param +: rest): _*)
}

trait LocEnum extends Enumeration {
  private def self = this
  private val locPrefix = this.getClass.getSimpleName.filter(_.isLetter).splitAt(1) match {
    case (a, b) => a.toLowerCase + b
  }
  private val _loc = new Loc {
    override def locPrefix: String = self.locPrefix
  }
  implicit def toLocValue(v: Value) = new {def loc: String = self._loc.loc(v.toString)}
}