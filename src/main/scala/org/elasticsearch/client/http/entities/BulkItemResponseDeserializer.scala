package org.elasticsearch.client.http.entities

import java.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonNode}

/**
  * Created by user on 6/21/17.
  */
class BulkItemResponseDeserializer extends JsonDeserializer[BulkItemResponse] {

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): BulkItemResponse = {
    val node = p.getCodec.readTree[JsonNode](p)
    node.fieldNames().next() match {
      case "index" => BulkItemResponse("index", p.getCodec.treeToValue(node.get("index"), classOf[IndexResponse]))
      case "update" => BulkItemResponse("update", p.getCodec.treeToValue(node.get("index"), classOf[UpdateResponse]))
      case "delete" => BulkItemResponse("delete", p.getCodec.treeToValue(node.get("index"), classOf[DeleteResponse]))
      case s => null
    }
  }
}
