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

package com.github.david04.liftutils.util

import net.liftweb.http.SHtml
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE.{JsRaw, ValById}
import scala.xml.NodeSeq
import java.util.regex.Pattern
import net.liftweb.json.JsonAST.{JString, JValue}
import net.liftweb.http.js.JsCmd

class InPlace[T](
                  get: => T,
                  toStr: T => String,
                  set: T => JsCmd,
                  fromStr: String => Option[T],
                  ifEmpty: String = "",
                  setParentWidth: Boolean = false,
                  serverVal: Boolean = true
                  ) {

  def render = {
    def current = Some(toStr(get)).filter(_ != "").getOrElse(ifEmpty)

    val id = Helpers.nextFuncName

    val selectText =
      """
        |  var range, selection;
        |  if (document.body.createTextRange) {
        |    range = document.body.createTextRange();
        |    range.moveToElementText(this);
        |    range.select();
        |  } else if (window.getSelection) {
        |    selection = window.getSelection();
        |    range = document.createRange();
        |    range.selectNodeContents(this);
        |    selection.removeAllRanges();
        |    selection.addRange(range);
        |  };
      """.stripMargin

    lazy val save: String = {
      Run(s"${'$'}('#$id')" +
        s".attr('contenteditable','false')" +
        s".attr('class','inplace-display')" +
        s".removeAttr('onblur');") &
        SHtml.ajaxCall(JsRaw(s"${'$'}('#$id').text()"), v => {
          fromStr(Some(v).filter(_ != ifEmpty).getOrElse("")).map(set).foldLeft(Noop)(_ & _) &
            Run(s"${'$'}('#$id')" +
              s".attr('onfocus'," + edit.encJs + ")" +
              s".attr('onclick'," + (selectText + edit).encJs + ")" +
              s".text(${current.encJs});")
        }).cmd
    }.toJsCmd

    lazy val edit = {
      Run(s"${'$'}('#$id')" +
        s".attr('contenteditable','true')" +
        s".attr('class','inplace-edit')" +
        s".attr('onblur'," + save.encJs + ");")
    }.toJsCmd

    <span tabindex="0" id={id} class="inplace-display" onfocus={edit} onclick={selectText + edit}>{current}</span> ++
      Script(OnLoad(Run(s"${'$'}('#$id').keydown(function(event){if(event.keyCode == 13){" + save + "; return false;}});")))
  }

}

class InPlaceSelect[T](
                        all: Seq[T],
                        toStr: T => String,
                        selected: => T,
                        set: T => JsCmd
                        ) {

  def render = {
    val displayId = Helpers.nextFuncName
    val editId = Helpers.nextFuncName

    val display = Run(s"${'$'}('#$editId').hide();${'$'}('#$displayId').show();")
    val edit = Run(s"${'$'}('#$displayId').hide();${'$'}('#$editId').show();").toJsCmd

    val onselect =
      SHtml.ajaxCall(
        JsRaw("$(this).val()"),
        (v: String) => {
          tryo(v.toInt).map(v => {
            set(all(v)) &
              SetValById(editId, JString(all.indexOf(selected).toString)) &
              Run(s"console.log('Value is: '+${toStr(selected).encJs});") &
              Run(s"${'$'}('#$displayId').text(${toStr(selected).encJs});") &
              display
          }).getOrElse(Noop)
        }).toJsCmd

    <span class="inplace-display" onclick={edit} id={displayId}>{toStr(selected)}</span> ++
      <select class="inplace-edit" id={editId} onblurr={display.toJsCmd} style="display:none">{
        all.zipWithIndex.map({
          case (v, idx) if selected == v => <option selected="selected" value={idx.toString}>{toStr(v)}</option>
          case (v, idx) => <option value={idx.toString}>{toStr(v)}</option>
        })
      }</select> ++
      Script(OnLoad(Run(s"${'$'}('#$editId').change(function(event){console.log('here');" + onselect + "});")))
  }
}

object InPlace {

  def str(get: => String, set: String => JsCmd, ifEmpty: String = "", setParentWidth: Boolean = false, serverVal: Boolean = true) =
    new InPlace[String](get, s => s, set, s => Some(s), ifEmpty, setParentWidth, serverVal).render

  def double(get: => Double, set: Double => JsCmd, ifEmpty: String = "", setParentWidth: Boolean = false, serverVal: Boolean = true, append: String = "", fmt: String = "%.2f") =
    new InPlace[Double](get, _.formatted(fmt) + append, set, s => tryo(s.replaceAll(Pattern.quote(append) + "$", "").toDouble).toOption, ifEmpty, setParentWidth, serverVal).render

  def select[T](all: Seq[T], toStr: T => String, selected: => T, set: T => JsCmd) =
    new InPlaceSelect[T](all, toStr, selected, set).render

  def enum[E <: Enumeration](enum: E, selected: => E#Value, set: E#Value => JsCmd) =
    new InPlaceSelect[E#Value](enum.values.toSeq, _.toString, selected, set).render

}
