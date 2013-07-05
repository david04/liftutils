package com.github.david04.liftutils.other

import net.liftweb.http.SHtml.ElemAttr
import scala.xml.NodeSeq
import java.util.UUID
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.{JsNull, JsRaw}
import net.liftweb.json._
import net.liftweb.http.js.JsCmds.{Replace, SetHtml}
import net.liftweb.http.js.JsCmd


object InstantEdit {

  def text(get: () => String, set: String => Unit, attrs: ElemAttr*): NodeSeq = {
    val uid = UUID.randomUUID().toString
    def display: NodeSeq = <span id={uid} onclick={SHtml.jsonCall(JsNull, (_: JValue) => Replace(uid, edit)).toJsCmd}>
      {get()}
    </span>
    def edit: NodeSeq = <div id={uid}>
      <input type="text" id={uid + "input"} value={get()} class="input-medium" style="margin-bottom: 0px;"></input>
      <button class="btn"
              onclick={SHtml.jsonCall(JsNull, (_: JValue) => Replace(uid, display)).toJsCmd}>Cancel</button>
      <button class="btn btn-primary"
              onclick={SHtml.jsonCall(JsRaw("$('#" + uid + "input').val()"), (v: JValue) => v match {
                case JString(s) => {
                  set(s)
                  Replace(uid, display)
                }
              }).toJsCmd}>Save</button>
    </div>
    display
  }

  def enum[ENUM <: Enumeration](e: ENUM, get: () => ENUM#Value, set: (ENUM#Value) => Unit, attrs: ElemAttr*): NodeSeq = {
    val uid = UUID.randomUUID().toString
    attrs.foldLeft(<select id={uid} onclick={SHtml.jsonCall(JsRaw("$(this).val()"), (v: JValue) => v match {
      case v: JString => {
        set(e(v.s.toInt))
        SetHtml(uid, enum(e, get, set, attrs: _*))
      }
    }).toJsCmd}>
      {e.values.toSeq.map(v => if (v == get())
        <option selected="true" value={v.id + ""}>
          {v.toString}
        </option>
      else
        <option value={v.id + ""}>
          {v.toString}
        </option>
      )}
    </select>)(_ % _)
  }

  def checkbox(caption: () => String, get: () => Boolean, set: (Boolean) => JsCmd, attrs: ElemAttr*): NodeSeq = {
    val uid = UUID.randomUUID().toString
    val r = attrs.foldLeft(<input type="checkbox" id={uid} onchange={SHtml.jsonCall(JsRaw("$(this).is(':checked')"), (v: JValue) => v match {
      case JBool(value) => set(value) & SetHtml(uid, checkbox(caption, get, set, attrs: _*))
    }).toJsCmd}>
      {caption()}
    </input>)(_ % _)
    if (get()) r % ElemAttr.pairToBasic("checked" -> "checked") else r
  }

  def multiSelect2Cols(caption: () => String, get: () => Boolean, set: (Boolean) => JsCmd, attrs: ElemAttr*): NodeSeq = {
    val uid = UUID.randomUUID().toString
    val r = attrs.foldLeft(<input type="checkbox" id={uid} onchange={SHtml.jsonCall(JsRaw("$(this).is(':checked')"), (v: JValue) => v match {
      case JBool(value) => set(value) & SetHtml(uid, checkbox(caption, get, set, attrs: _*))
    }).toJsCmd}>
      {caption()}
    </input>)(_ % _)
    if (get()) r % ElemAttr.pairToBasic("checked" -> "checked") else r
  }
}
