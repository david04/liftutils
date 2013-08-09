package com.github.david04.liftutils.mapper

import net.liftweb.mapper.{Mapper, MappedBinary}
import java.io.{ObjectOutputStream, ByteArrayOutputStream, ObjectInputStream, ByteArrayInputStream}

class MappedObject[T <: Mapper[T], OT](fieldOwner: T, dflt: OT = null) extends MappedBinary[T](fieldOwner) {

  override def defaultValue = if (dflt == null) null else encode(dflt)

  private var decoded: Option[OT] = None

  override protected def real_i_set_!(value: Array[Byte]): Array[Byte] = { decoded = None; super.real_i_set_!(value) }

  def get2: OT = decoded match {
    case Some(v) => v
    case None => {
      decoded = Some(get match {
        case null => dflt //null.asInstanceOf[OT]
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
      })
      get2
    }
  }

  private def encode(obj: OT) = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(obj)
    oos.close()
    baos.toByteArray
  }

  def set2(obj: OT) = { set(encode(obj)); fieldOwner }
}