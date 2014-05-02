package com.github.david04.liftutils.reactive

import net.liftweb.http.js.{JsCmds, JsCmd}
import scala.xml.{Elem, NodeSeq}
import scala.ref.WeakReference
import net.liftweb.util.Helpers
import scala.collection.mutable.ListBuffer
import net.liftweb.http.js.JsCmds.Replace

trait RVal[T] {

  def addDependent(d: Dependent): Unit

  def get: T
}

trait RVar[T] extends RVal[T] {def set(v: T): JsCmd}

trait Dependent {

  def changed(): JsCmd
}

class RFunc[D1, T](d1: RVal[D1], f: D1 => T) extends RVal[T] with Dependent {

  private var dependents: ListBuffer[WeakReference[Dependent]] = ListBuffer()

  def addDependent(d: Dependent) = dependents += WeakReference(d)

  d1.addDependent(this)

  def changed(): JsCmd = dependents.map(_.underlying.get()).filter(_ != null).map(_.changed()).foldLeft(JsCmds.Noop)(_ & _)

  def get: T = f(d1.get)
}

class RValue[T](initial: => T, extSet: Option[T => JsCmd] = None) extends RVar[T] {

  def this(initial: => T, set: T => JsCmd) = this(initial, Some(set))

  private var dependents: ListBuffer[WeakReference[Dependent]] = ListBuffer()
  protected var current: Option[T] = None

  def addDependent(d: Dependent) = dependents += WeakReference(d)

  def set(v: T): JsCmd = {
    current = Some(v)
    extSet.map(_(v)).getOrElse(JsCmds.Noop) &
      dependents.map(_.underlying.get()).filter(_ != null).map(_.changed()).foldLeft(JsCmds.Noop)(_ & _)
  }

  def get: T = {
    if (current.isEmpty) current = Some(initial)
    current.get
  }
}

object RxRender {

  def apply[T](v: RVal[T])(f: T => NodeSeq => NodeSeq): NodeSeq => NodeSeq = (ns: NodeSeq) => {

    val elem: Elem = ns.find {
      case e: Elem => true
      case _ => false
    }.map(_.asInstanceOf[Elem]).getOrElse(<span id={Helpers.nextFuncName}>{ns}</span>)

    val (withId: Elem, id: String) = Helpers.findOrAddId(elem)

    v.addDependent(new Dependent {
      def changed() = Replace(id, f(v.get)(withId))
    })

    f(v.get)(withId)
  }
}
