package com.github.david04.liftutils.util

import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{InMemoryResponse, PlainTextResponse, Req}
import javax.activation.MimetypesFileTypeMap
import com.github.david04.liftutils.util.Util.__print

/**
 * Created by david at 9:37 PM
 */
object VirtualFiles extends RestHelper {

  val files = collection.mutable.Map[String, String]()

  def name(file: String) = ("/js/" + ## + file)

  def ext(name: String) = Map(
    "js" -> "text/javascript"
  ).get(name).getOrElse("application/octet-stream")

  serve {
    case Req("js" :: name :: Nil, suf, _) if (files.contains((name + "." + suf).replaceAllLiterally(## + "", ""))) =>
      val bytes = files((name + "." + suf).replace(## + "", "")).getBytes("UTF-8")
      InMemoryResponse(
        bytes,
        ("Content-Length", bytes.length.toString) ::
          ("Content-Type", ext(suf) + "; charset=utf-8") ::
          Nil,
        Nil,
        200)
  }
}
