package com.github.thanhtien522.eshttpclient.entities

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonNode}

/**
  * @author Tien Nguyen
  */
class BulkItemResponseDeserializer extends JsonDeserializer[BulkItemResponse] {

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): BulkItemResponse = {
    val node = p.getCodec.readTree[JsonNode](p)
    node.fieldNames().next() match {
      case "index" => BulkItemResponse("index", p.getCodec.treeToValue(node.get("index"), classOf[IndexResponse]))
      case "update" => BulkItemResponse("update", p.getCodec.treeToValue(node.get("update"), classOf[UpdateResponse]))
      case "delete" => BulkItemResponse("delete", p.getCodec.treeToValue(node.get("delete"), classOf[DeleteResponse]))
      case s => throw new Exception("Deserialize BulkItemResponse failure. Unhandled action name `" + s + "`")
    }
  }
}
