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
      case "create" => BulkItemResponse("create", p.getCodec.treeToValue(node.get("create"), classOf[IndexResponse]))
      case "update" => BulkItemResponse("update", p.getCodec.treeToValue(node.get("update"), classOf[UpdateResponse]))
      case "delete" => BulkItemResponse("delete", p.getCodec.treeToValue(node.get("delete"), classOf[DeleteResponse]))
      case s => throw new Exception("Deserialize BulkItemResponse failure. Unhandled action name `" + s + "`")
    }
  }
}

class ErrorDeserializer extends JsonDeserializer[Error] {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): Error = {
    val node = p.getCodec.readTree[JsonNode](p)

    if (node.isTextual) {
      Error("exception", node.asText(), null)
    } else if (node.isObject) {
      val `type` = node.get("type").asText()
      val reason = node.get("reason").asText()
      val caused = if (node.has("caused_by")) p.getCodec.treeToValue(node.get("caused_by"), classOf[Error]) else null
      Error(`type`, reason, caused)
    } else {
      throw new Exception(s"Deserialize Error failure. Expected STRING or OBJECT, actual ${node.getNodeType.toString}")
    }
  }
}
