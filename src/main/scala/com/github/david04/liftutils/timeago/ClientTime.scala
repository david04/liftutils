package com.github.david04.liftutils.timeago

import net.liftweb.http._
import org.joda.time.format.ISODateTimeFormat
import java.util.Date
import org.joda.time.DateTime
import com.github.david04.liftutils.util.VirtualFiles

object ClientTime {

  VirtualFiles.files("timeago.js") = "$('time').timeago();"

  def timeago(ts: Date) = {

    val jsLoc = S.contextPath + "/" + LiftRules.resourceServerPath + "/js/jquery.timeago.js"
    val str = ISODateTimeFormat.dateTime().print(new DateTime(ts))

    <tail>
      <script src={jsLoc} type="text/javascript"></script>
      <script src={VirtualFiles.name("timeago.js")} type="text/javascript"></script>
    </tail><time title={str}></time>
  }

  def localTime(ts: Date) =
    <script>{"document.write(new Date('%s').toLocaleString());"
      .format(ISODateTimeFormat.dateTime().print(new DateTime(ts)))}</script>
}
