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
