package com.github.david04.liftutils.elem


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
