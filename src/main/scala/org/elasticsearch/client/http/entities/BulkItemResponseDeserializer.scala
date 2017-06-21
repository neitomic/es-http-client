package org.elasticsearch.client.http.entities

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer}

/**
  * Created by user on 6/21/17.
  */
class BulkItemResponseDeserializer extends JsonDeserializer[BulkItemResponse]{

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): BulkItemResponse = {
  }
}
