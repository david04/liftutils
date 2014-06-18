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
package http

import common._
import util._
import util.Helpers._
import http.js._
import JsCmds._
import scala.xml._

object SHtml2 {

  def memoizeElem(f: IdMemoizeTransform => NodeSeqFuncOrSeqNodeSeqFunc): IdMemoizeTransform = new IdMemoizeTransform {

    var _latestId = Helpers.nextFuncName + "dflt"
    def latestId = _latestId
    var latestElem: Elem = <span id={latestId}></span>
    def latestKids: NodeSeq = latestElem.child

    def apply(ns: NodeSeq): NodeSeq =
      Helpers.findBox(f(this)(ns))(e => {
        e.attribute("id") match {
          case Some(id) =>
            _latestId = id.text
            latestElem = e
            Full(e)
          case None =>
            latestElem = e % ("id" -> latestId)
            Full(e)
        }
      }).openOr(latestElem)

    def applyAgain(): NodeSeq = {
      Helpers
        .findBox[NodeSeq](f(this)(latestElem))(e => Full((e % ("id" -> latestId)): NodeSeq))
        .openOr(<span id={latestId}></span>)
    }

    def setHtml(): JsCmd = Replace(latestId, f(this)(latestElem))
  }

  def textMemoize(txt: => String) = SHtml.idMemoize(_ => (_: NodeSeq) => Text(txt))
}