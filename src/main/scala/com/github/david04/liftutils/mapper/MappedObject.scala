package com.github.david04.liftutils.mapper

import net.liftweb.mapper.{Mapper, MappedBinary}
import java.io.{ObjectOutputStream, ByteArrayOutputStream, ObjectInputStream, ByteArrayInputStream}

class MappedObject[T <: Mapper[T], OT](fieldOwner: T) extends MappedBinary[T](fieldOwner) {

  def get2 = {
    get match {
      case null => null.asInstanceOf[OT]
      case arr => {
        try {
          val bais = new ByteArrayInputStream(arr)
          val ois = new ObjectInputStream(bais)
          val o = ois.readObject().asInstanceOf[OT]
          ois.close()
          o
        } catch {
          case e: Exception => {
            e.printStackTrace()
            null.asInstanceOf[OT]
          }
        }
      }
    }
  }

  def set2(obj: OT) = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(obj)
    oos.close()
    set(baos.toByteArray)
    fieldOwner
  }
}