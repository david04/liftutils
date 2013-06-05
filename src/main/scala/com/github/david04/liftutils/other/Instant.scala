package com.github.david04.liftutils.other

import net.liftweb.http.SHtml.ElemAttr
import scala.xml.{MetaData, NodeSeq}
import java.util.UUID
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.json._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.http.js.JsCmd


object InstantEdit {

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
