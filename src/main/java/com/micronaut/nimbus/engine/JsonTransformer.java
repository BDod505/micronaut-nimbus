package com.micronaut.nimbus.engine;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.micronaut.nimbus.annotations.*;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

@Singleton
public class JsonTransformer {
    private static final Logger log = LoggerFactory.getLogger(JsonTransformer.class);
    public ObjectNode transform(Object input) throws IllegalAccessException {
        ObjectNode result = JsonNodeFactory.instance.objectNode();
        ObjectNode parentNode = JsonNodeFactory.instance.objectNode();
        processFields(result,input,parentNode,"");
        log.info("Final Transformation Result: {}",result);
        return result;
    }

    private void processFields(ObjectNode resultNode,Object input,ObjectNode parentNode,String parentPath) throws IllegalAccessException {
        Class<?> clazz = input.getClass();
        for(Field field: clazz.getDeclaredFields()){
            field.setAccessible(true);
            Object value = field.get(input);
            log.info("Processin Field: {} with Value: {} and Path: {}",field.getName(),value,parentPath);

            //Setting field path
            String fieldPath = (field.isAnnotationPresent(JsonNestedTransform.class))
                    ?field.getAnnotation(JsonNestedTransform.class).path()
                    :parentPath.equals("")?field.getName():parentPath+'.'+field.getName();

            //Processing @JsonCleanPrefix Annotation
            if(field.isAnnotationPresent(JsonCleanPrefix.class)){
                String prefix = field.getAnnotation(JsonCleanPrefix.class).prefix();
                fieldPath = parentPath.equals("")?cleanPrefix(field.getName(),prefix):parentPath+'.'+cleanPrefix(field.getName(),prefix);
                log.info("Cleaned Field Name: {}",fieldPath);
            }

            //Processing @JsonToUpper and @JsonToLower
            if(field.getName() instanceof String stringValue){
                if(field.isAnnotationPresent(JsonToUpper.class)){
                    fieldPath = parentPath.equals("")?stringValue.toUpperCase():parentPath+'.'+stringValue.toUpperCase();
                    log.info("Transformed to UpperCase: {}",fieldPath);
                } else if(field.isAnnotationPresent(JsonToLower.class)){
                    fieldPath = parentPath.equals("")?stringValue.toLowerCase():parentPath+'.'+stringValue.toLowerCase();
                    log.info("Transformed to LowerCase: {}",fieldPath);
                }
            }

            // Process Nested Objects Recursively
            if(value!=null && !isPrimitiveOrWrapper(value.getClass())){
                ObjectNode childNode = JsonNodeFactory.instance.objectNode();
                log.info("Traversing Nested Objects {} : {}",fieldPath,value);
                processFields(resultNode,value,childNode,fieldPath);
                parentNode.set(fieldPath,childNode);
            } else {
                setFieldValueInNode(resultNode,fieldPath,value);
            }
        }
    }

    private String cleanPrefix(String fieldName,String prefix){
        return fieldName.startsWith(prefix)?fieldName.substring(prefix.length()):fieldName;
    }

    private boolean isPrimitiveOrWrapper(Class<?> type){
        return type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type)
                || Boolean.class.isAssignableFrom(type);
    }

    private void setFieldValueInNode(ObjectNode resultNode,String path,Object value){
        log.info("Setting Field value {} : {} in Result :{}",path,value,resultNode);
        String[] pathSegments = path.split("\\.");
        ObjectNode currentNode = resultNode;
        for(String segment:pathSegments){
            if(!currentNode.has(segment)){
                currentNode.set(segment,JsonNodeFactory.instance.objectNode());
            }
            currentNode = (ObjectNode) currentNode.get(segment);
        }
        currentNode.putPOJO(pathSegments[pathSegments.length-1],value);
    }
}
