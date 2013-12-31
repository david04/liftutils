package com.github.david04.liftutils.elem

import net.liftweb.http.SHtml.ElemAttr
import scala.xml.NodeSeq
import net.liftweb.http.S
import scala.util.parsing.combinator.RegexParsers
import com.github.david04.liftutils.Loc.Loc


object DefaultViewableElems {

  class Text(
              val elemName: String,
              get: => String,
              private[elem] val enabled: () => Boolean = () => true
              )(implicit val viewer: HTMLViewer) extends TextViewerElem {

    override def locPrefix = viewer.locPrefix

    def getStringValue() = get
  }

}
