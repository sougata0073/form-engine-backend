package com.sougata.form_data_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonUtil {

    private static final com.fasterxml.jackson.databind.ObjectMapper oldMapper =
            new com.fasterxml.jackson.databind.ObjectMapper();
    private static final tools.jackson.databind.ObjectMapper newMapper =
            new tools.jackson.databind.ObjectMapper();

    public static String oldJsonNodeToString(com.fasterxml.jackson.databind.JsonNode jsonNode) {
        try {
            return oldMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static com.fasterxml.jackson.databind.JsonNode objectToOldJsonNode(Object obj) {
        return oldMapper.valueToTree(obj);
    }

    public static <T> T oldJsonNodeToObject(com.fasterxml.jackson.databind.JsonNode jsonNode, Class<T> returnType) throws JsonProcessingException {
        return oldMapper.treeToValue(jsonNode, returnType);
    }

    public static String getValueFromOldJsonNode(com.fasterxml.jackson.databind.JsonNode jsonNode, String fieldName) throws JsonProcessingException {
        return jsonNode.get(fieldName).asText();
    }

    public static String getValueFromNewJsonNode(tools.jackson.databind.JsonNode jsonNode, String fieldName) throws JsonProcessingException {
        return jsonNode.get(fieldName).asString();
    }

}
