import com.github.david04.liftutils.jacksonxml.{JsonSerializable, JSON}
package com.fasterxml.jackson.module.scala.modifiers {

import com.fasterxml.jackson.module.scala.JacksonModule
import com.github.david04.liftutils.jacksonxml.JsonSerializable

private object JsonSerializableTypeModifier extends CollectionLikeTypeModifier {
  def BASE = classOf[JsonSerializable]
}

trait JsonSerializableTypeModifierModule extends JacksonModule {
  this += JsonSerializableTypeModifier
}

}

package com.github.david04.liftutils.jacksonxml {

import com.fasterxml.jackson.module.scala.JacksonModule
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.ser.Serializers
import java.{util => ju}
import com.fasterxml.jackson.module.scala.deser.UntypedObjectDeserializerModule
import com.fasterxml.jackson.annotation.JsonInclude

trait JsonSerializable {
  def json(): Option[String]
}

private class JsonSerializableSerializer extends JsonSerializer[JsonSerializable] {

  def serialize(value: JsonSerializable, jgen: JsonGenerator, provider: SerializerProvider) {
    value.json().foreach(v => jgen.writeRawValue(v))
  }
}

private object JsonSerializableSerializerResolver extends Serializers.Base {

  override def findSerializer(config: SerializationConfig, javaType: JavaType, beanDesc: BeanDescription) = {
    val cls = javaType.getRawClass
    if (!classOf[JsonSerializable].isAssignableFrom(cls)) null
    else new JsonSerializableSerializer
  }

}

trait JsonSerializableSerializerModule extends JacksonModule {
  this += (_ addSerializers JsonSerializableSerializerResolver)
}

object JSON extends ObjectMapper {

  import com.fasterxml.jackson.module.scala._

  registerModule(
    new JacksonModule
        with IteratorModule
        with EnumerationModule
        with OptionModule
        with SeqModule
        with IterableModule
        with TupleModule
        with MapModule
        with CaseClassModule
        with SetModule
        with JsonSerializableSerializerModule
        with UntypedObjectDeserializerModule {
      override def getModuleName = "DefaultScalaModule"
    })
  setSerializationInclusion(JsonInclude.Include.NON_NULL)
}

}


object Main extends App {
  println(JSON.writeValueAsString(List(1, 2, new JsonSerializable {
    def json(): Option[String] = Some("new Date()")
  })))
}