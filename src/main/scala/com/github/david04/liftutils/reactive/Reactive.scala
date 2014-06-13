//  Copyright (c) 2014 David Miguel Antunes <davidmiguel {at} antunes.net>
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.

package com.github.david04.liftutils.reactive

import net.liftweb.http.js.{JsExp, JsCmds, JsCmd}
import scala.xml.{Elem, NodeSeq}
import scala.ref.WeakReference
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers._
import scala.collection.mutable.ListBuffer
import net.liftweb.http.js.JsCmds._
import net.liftweb.json.JsonAST.JValue
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds.Replace
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds.Run
import com.github.david04.liftutils.util.Util.RichJsCmd

// ======== Server side ========

trait RxVal[T] {

  protected var dependents: ListBuffer[WeakReference[RxDependent]] = ListBuffer()
  def addRxDependent(d: RxDependent): JsCmd = { dependents += WeakReference(d); Noop }
  protected def notifyRxDependents() = dependents.map(_.underlying.get()).filter(_ != null).map(_.rxChanged()).foldLeft(JsCmds.Noop)(_ & _)

  def getRx: T
}

trait RxDependent {

  def rxChanged(): JsCmd
}

class RxFunc[D1, T](d1: RxVal[D1], f: D1 => T) extends RxVal[T] with RxDependent {

  d1.addRxDependent(this)

  def rxChanged(): JsCmd = notifyRxDependents()

  def getRx: T = f(d1.getRx)
}

trait RxVar[T] extends RxVal[T] {
  protected val initialRx: T
  protected def extSet: Option[T => JsCmd] = None

  protected var current: Option[T] = None

  def setRx(v: T): JsCmd = {
    current = Some(v)
    extSet.map(_(v)).getOrElse(JsCmds.Noop) & notifyRxDependents()
  }

  def getRx(): T = {
    if (current.isEmpty) current = Some(initialRx)
    current.get
  }
}

object RxVar {
  def apply[T](_initial: T, _extSet: T => JsCmd) = new RxVar[T] {
    protected lazy val initialRx = _initial
    override protected val extSet: Option[T => JsCmd] = Some(_extSet)
  }
}

object RxRender {

  def apply[T](v: RxVal[T])(f: T => NodeSeq => NodeSeq): NodeSeq => NodeSeq = (ns: NodeSeq) => {

    val elem: Elem = ns.find {
      case e: Elem => true
      case _ => false
    }.map(_.asInstanceOf[Elem]).getOrElse(<span id={Helpers.nextFuncName}>{ns}</span>)

    val (withId: Elem, id: String) = Helpers.findOrAddId(elem)

    v.addRxDependent(new RxDependent {
      def rxChanged() = Replace(id, f(v.getRx)(withId))
    })

    f(v.getRx)(withId)
  }
}

// ======== Client side ========

trait RXVal {

  val id = Helpers.nextFuncName

  def addRXDependent(d: RXDependent): JsCmd = {
    Run {
      s"{" +
        s"  var cur = window.change$id || (function() {});" +
        s"  window.change$id = function() {cur(); ${d.rXChanged().toJsCmd}};" +
        s"};"
    }
  }

  protected def notifyRXDependents() = Run(s"if(window.change$id) {window.change$id();}")

  def getRX: JsExp
}

trait RXDependent {

  def rXChanged(): JsCmd
}

trait RXVar extends RXVal {

  protected val initialRX: JsExp

  def getRX(): JsExp = JsRaw(s"(window.V$id ? window.V$id : ${initialRX.toJsCmd})")

  def setRX(v: JsExp) = Run(s"window.V$id = " + v.toJsCmd) & notifyRXDependents()

  def initRX() = setRX(initialRX)
  def initRXScript() = <tail>{Script(setRX(initialRX))}</tail>
}

class RXFunc(d1: RXVal, f: JsExp => JsExp) extends RXVal {

  override def addRXDependent(d: RXDependent): JsCmd = d1.addRXDependent(d)

  override protected def notifyRXDependents() = ???

  def getRX: JsExp = f(d1.getRX)
}

object RXVar {
  def apply[X](initial: JsExp) = new RXVar {
    protected lazy val initialRX = initial
  }
}

// ======== Server & Client side ========

trait RxXVal[T] extends RxVal[T] with RXVal {

  /**
   * To be called on the server side.
   */
  def addRxXDependent(d: RxXDependent): JsCmd = addRxDependent(d) & addRXDependent(d)

  /**
   * To be called on the client side.
   */
  def addRXxDependent(d: RxXDependent): JsCmd = addRXDependent(d) & SHtml.ajaxInvoke(() => addRxDependent(d))

  /**
   * To be called on the server side.
   */
  protected def notifyRxXDependents() = notifyRxDependents() & notifyRXDependents()

  /**
   * To be called on the client side.
   */
  protected def notifyRXxDependents() = notifyRXDependents() & SHtml.ajaxInvoke(() => notifyRxDependents())
}

trait RxXDependent extends RxDependent with RXDependent {}

trait RxXVar[T] extends RxVar[T] with RXVar {

  protected val toRX: T => JsExp
  protected val fromRX: JValue => T

  protected lazy val initialRX: JsExp = toRX(initialRx)

  def setRxX(v: T): JsCmd = setRx(v) & setRX(toRX(v))

  def setRXx(v: JsExp, afterSetClientSide: JsCmd = Noop, afterSetServerSide: () => JsCmd = () => Noop): JsCmd =
    setRX(v) &
      afterSetClientSide &
      SHtml.jsonCall(v, (v: JValue) => super.setRx(fromRX(v)) & afterSetServerSide())
}

object RxXVar {
  def apply[T](
                initial: T,
                _toRX: T => JsExp,
                _fromRX: JValue => T,
                _extSet: Option[T => JsCmd] = None) = new RxXVar[T] {
    protected val initialRx = initial
    protected val toRX = _toRX
    protected val fromRX = _fromRX
    override protected val extSet = _extSet
  }
}

trait RXStr extends RXVal

object RichRXStr {

  implicit class RichRXStr(s: RXStr) {
    def rXRender(): NodeSeq = {
      val id = Helpers.nextFuncName
      <tail>{Script(OnLoad {
        s.addRXDependent(new RXDependent {
          override def rXChanged(): JsCmd = Run(s"${'$'}('#$id').text(" + s.getRX.toJsCmd + ");").P
        }) & Run(s"${'$'}('#$id').text(" + s.getRX.toJsCmd + ");").P
      })}</tail> ++
        <span id={id}></span>
    }

    def transform(f: String => String) = new RXFunc(s, jsExp => JsRaw(f(jsExp.toJsCmd))) with RXStr
    def maxLength(len: Int, ellipsis: String) = transform(s => s"(($s.length < $len) ? ($s) : ($s.substring(0,$len)+${ellipsis.encJs})+'')")
    def ifEmpty(dflt: String) = transform(s => s"((($s.length == 0) ? ${dflt.encJs} : $s)+'')")
  }

}


