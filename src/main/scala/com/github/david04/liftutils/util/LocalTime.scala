package com.github.david04.liftutils.util

import java.util.Date

import net.liftweb.http.js.JsCmds.{Run, OnLoad, Script}
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

object LocalTime {

  def localTime(date: Date, fmt: String = "Do MMM YYYY, h:mm a ZZ"): NodeSeq = {

    val id = nextFuncName
    <span id={id}></span> ++ <tail>{Script(OnLoad(Run(
      s"""$$('#$id').text(moment(${date.getTime}).format(${fmt.encJs}));"""
    )))}</tail>
  }

  def timeSince(date: Date): NodeSeq = {

    val id = nextFuncName
    <span id={id}></span> ++ <tail>{Script(OnLoad(Run(
      s"""setInterval(function() {$$('#$id').text(moment(${date.getTime}).fromNow());}, 800);""".stripMargin
    )))}</tail>
  }

  def timeSincePrecise(date: Date): NodeSeq = {

    val id = nextFuncName
    <span data-livestamp={date.getTime.toString} id={id}></span> ++ <tail>{Script(OnLoad(Run(
      s"""function pad(num, size){return ('000000000' + num).substr(-size);};
         |setInterval(function() {
         |  $$('#$id').each(function(idx,elem) {
         |    var start = $$(elem).attr('data-livestamp');
         |    var elapsed = (new Date().getTime() - start) / 1000;
         |    var seconds = elapsed % 60;
         |    var minutes = elapsed/60;
         |    $$(elem).text(pad(Math.floor(minutes),2)+':'+pad(Math.floor(seconds),2));
         |  })
         |}, 250);
         |""".stripMargin
    )))}</tail>
  }
}
