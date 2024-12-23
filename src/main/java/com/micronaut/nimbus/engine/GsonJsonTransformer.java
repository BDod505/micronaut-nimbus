package com.micronaut.nimbus.engine;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.micronaut.nimbus.annotations.JsonCleanPrefix;
import com.micronaut.nimbus.annotations.JsonNestedTransform;
import com.micronaut.nimbus.annotations.JsonToLower;
import com.micronaut.nimbus.annotations.JsonToUpper;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

@Singleton
public class GsonJsonTransformer {

    private static final Logger log = LoggerFactory.getLogger(GsonJsonTransformer.class);
    private static final Gson gson = new Gson();

    public JsonObject transform(Object input) throws IllegalAccessException {
        JsonObject result = new JsonObject();
        JsonObject parentNode = new JsonObject();
        processFields(result, input, parentNode, "");
        log.debug("Final Transformation Result: {}", result);
        return result;
    }

    private void processFields(JsonObject resultNode, Object input, JsonObject parentNode, String parentPath)
            throws IllegalAccessException {
        Class<?> clazz = input.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(input);
            log.debug("Processing Field: {} with Value: {}", field.getName(), value);
            // Determine the target field name or path
            String fieldPath = (field.isAnnotationPresent(JsonNestedTransform.class))
                    ? field.getAnnotation(JsonNestedTransform.class).path()
                    : parentPath.isEmpty() ? field.getName() : parentPath + '.' + field.getName();

            // Apply @JsonCleanPrefix to remove prefixes
            if (field.isAnnotationPresent(JsonCleanPrefix.class)) {
                String prefix = field.getAnnotation(JsonCleanPrefix.class).prefix();
                fieldPath = parentPath.equals("") ? cleanPrefix(field.getName(), prefix) : parentPath + "." + cleanPrefix(field.getName(), prefix);
                log.debug("Cleaned Field Name: {}", fieldPath);
            }

            // Apply string transformations
            if (value instanceof String stringValue) {
                if (field.isAnnotationPresent(JsonToUpper.class)) {
                    value = stringValue.toUpperCase();
                    log.debug("Transformed to Uppercase: {} -> {}", fieldPath, value);
                } else if (field.isAnnotationPresent(JsonToLower.class)) {
                    value = stringValue.toLowerCase();
                    log.debug("Transformed to Lowercase: {} -> {}", fieldPath, value);
                }
            }

            log.debug("--> {} ==> {} =:{}", fieldPath, parentPath.isEmpty() ? "root" : parentPath, value);

            // Process nested objects recursively
            if (value != null && !isPrimitiveOrWrapper(value.getClass())) {
                JsonObject childNode = new JsonObject();
                processFields(resultNode, value, childNode, fieldPath);
                parentNode.add(fieldPath, childNode);
            } else {
                // Add the field value to the parent node
                setFieldValueInNode(resultNode, parentNode, fieldPath, value);
            }
        }
    }

    private void setFieldValueInNode(JsonObject resultNode, JsonObject parentNode, String path, Object value) {
        log.debug("Setting Field Value {} : {} : {}", parentNode, path, value);
        String[] pathSegments = path.split("\\.");
        JsonObject currentNode = resultNode;

        for (int i = 0; i < pathSegments.length - 1; i++) {
            String segment = pathSegments[i];
            if (!currentNode.has(segment)) {
                currentNode.add(segment, new JsonObject());
            }
            currentNode = currentNode.getAsJsonObject(segment);
        }

        if (value instanceof Number) {
            currentNode.addProperty(pathSegments[pathSegments.length - 1], (Number) value);
        } else if (value instanceof Boolean) {
            currentNode.addProperty(pathSegments[pathSegments.length - 1], (Boolean) value);
        } else if (value instanceof String) {
            currentNode.addProperty(pathSegments[pathSegments.length - 1], (String) value);
        } else {
            currentNode.add(pathSegments[pathSegments.length - 1], gson.toJsonTree(value));
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type)
                || Boolean.class.isAssignableFrom(type);
    }

    private String cleanPrefix(String fieldName, String prefix) {
        return fieldName.startsWith(prefix) ? fieldName.substring(prefix.length()) : fieldName;
    }
}