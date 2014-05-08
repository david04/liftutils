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

package net.liftweb
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

package http

import common._
import util._
import util.Helpers._
import http.js._
import JsCmds._
import scala.xml._

trait IdMemoizeTransform2 extends Function1[NodeSeq, NodeSeq] with IdMemoizeTransform {
  def f: IdMemoizeTransform2 => NodeSeqFuncOrSeqNodeSeqFunc

  var latestElem: Elem = <span/>

  var latestId = Helpers.nextFuncName

  def latestKids: NodeSeq = latestElem.child

  private def ensureId(e: Elem): Elem = {
    e.attribute("id") match {
      case Some(_id) => latestId = _id.text; e
      case None => e % ("id" -> latestId)
    }
  }

  def apply(ns: NodeSeq): NodeSeq = {
    latestElem = Helpers.findBox(ns)(Full(_)).openOr(<span/>)
    applyAgain()
  }

  def applyAgain(): NodeSeq = {
    //    println("TEMPLATE:\n " + latestElem)
    val r = ensureId(Helpers.findBox(f(this)(latestElem))(Full(_)).openOr(<span/>))
    //    println("RESULT:\n " + r)
    r
  }

  def replaceHtml(): JsCmd = Replace(latestId, f(this)(applyAgain()))

  def setHtml(): JsCmd = replaceHtml()
}

object SHtml2 {

  def memoizeElem(_f: IdMemoizeTransform2 => NodeSeqFuncOrSeqNodeSeqFunc): IdMemoizeTransform2 = {
    new IdMemoizeTransform2 {def f = _f}
  }

  def textMemoize(txt: => String) = SHtml.idMemoize(_ => (_: NodeSeq) => Text(txt))
}