package com.github.david04.liftutils.modtbl

import net.liftweb.http.{SHtml, Templates}
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq
import net.liftweb.util.PassThru


trait NamedTable extends Table {

  override protected def pageTransforms(): NodeSeq => NodeSeq =
    super.pageTransforms() andThen
      ".modtbl-name *" #> loc("name")
}

trait ActionsTable extends Table {

  protected def actionsTemplatePath: List[String] = Nil
  protected def actionsTemplate = Templates(actionsTemplatePath).get

  override protected def pageTransforms(): NodeSeq => NodeSeq =
    super.pageTransforms() andThen
      ".modtbl-actions" #> actionsRenderer

  protected lazy val actionsRenderer = (_: NodeSeq) => SHtml.idMemoize(_ => actionsTransforms()).apply(actionsTemplate)
  protected def actionsTransforms(): NodeSeq => NodeSeq = PassThru
}
