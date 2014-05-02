package com.github.david04.liftutils.elem

trait Framework {

  def errorClass: String
  def warningClass: String
  def successClass: String

  def btnDefault: String
  def btnMute: String
  def btnPrimary: String
  def btnSuccess: String
  def btnInfo: String
  def btnWarning: String
  def btnDanger: String
}

trait Bootstrap3 extends Framework {

  def fw: Framework = this

  def errorClass = "has-error"
  def warningClass = "has-warning"
  def successClass = "has-success"

  def btnDefault: String = "btn-default"
  def btnMute: String = "btn-default"
  def btnPrimary: String = "btn-primary"
  def btnSuccess: String = "btn-success"
  def btnInfo: String = "btn-info"
  def btnWarning: String = "btn-warning"
  def btnDanger: String = "btn-danger"
}

trait Bootstrap2 extends Framework {

  def fw: Framework = this

  def errorClass = "error"
  def warningClass: String = ???
  def successClass: String = ???

  def btnDefault: String = ""
  def btnMute: String = ""
  def btnPrimary: String = "btn-primary"
  def btnSuccess: String = "btn-success"
  def btnInfo: String = "btn-info"
  def btnWarning: String = "btn-warning"
  def btnDanger: String = "btn-danger"
}