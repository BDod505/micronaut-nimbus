package com.micronaut.nimbus.engine;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.micronaut.nimbus.annotations.*;
import io.micronaut.serde.ObjectMapper;

import java.lang.reflect.Field;

public class JsonTransformer {
    private final ObjectMapper mapper;


    public JsonTransformer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectNode transform(Object input) throws IllegalAccessException {
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        Class<?> clazz = input.getClass();
        for(Field field: clazz.getDeclaredFields()){
            field.setAccessible(true);
            Object value = field.get(input);
            if(value==null && field.isAnnotationPresent(JsonDefault.class)){
                value = field.getAnnotation(JsonDefault.class).value();
            }
            String fieldName = field.getName();
            if(field.isAnnotationPresent(JsonCleanPrefix.class)){
                String prefix = field.getAnnotation(JsonCleanPrefix.class).prefix();
                fieldName = cleanPrefix(fieldName,prefix);
            }
            if(fieldName instanceof String stringValue){
                if (field.isAnnotationPresent(JsonToUpper.class)){
                    fieldName = stringValue.toUpperCase();
                } else if(field.isAnnotationPresent(JsonToLower.class)) {
                    fieldName = stringValue.toLowerCase();
                }
            }
            result.putPOJO(fieldName,value);
        }
        return result;
    }
    private String cleanPrefix(String fieldName,String prefix){
        return fieldName.startsWith(prefix)?fieldName.substring(prefix.length()):fieldName;
    }
}
