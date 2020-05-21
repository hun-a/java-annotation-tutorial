package io.huna.utils;

import io.huna.annotations.Init;
import io.huna.annotations.JsonElement;
import io.huna.annotations.JsonSerializable;
import io.huna.exception.JsonSerializationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonSerializer {

    public String serialize(Object object) throws JsonSerializationException {
        try {
            checkIfSerializable(object);
            initializeObject(object);
            return getJsonString(object);
        } catch (Exception e) {
            throw new JsonSerializationException(e.getMessage());
        }
    }

    private void checkIfSerializable(Object object) {
        if (Objects.isNull(object)) {
            throw new JsonSerializationException("The object to serialize is null");
        }

        Class<?> cls = object.getClass();
        if (!cls.isAnnotationPresent(JsonSerializable.class)) {
            throw new JsonSerializationException(
                    String.format("The class %s is not annotated with JsonSerializable", cls.getSimpleName())
            );
        }
    }

    private void initializeObject(Object object) throws Exception {
        Class<?> cls = object.getClass();
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Init.class)) {
                method.setAccessible(true);
                method.invoke(object);
            }
        }
    }

    private String getJsonString(Object object) throws Exception {
        Class<?> cls = object.getClass();
        Map<String, String> jsonElements = new HashMap<>();

        for (Field field: cls.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(JsonElement.class)) {
                jsonElements.put(getKey(field), (String) field.get(object));
            }
        }

        String jsonString = jsonElements.entrySet()
                .stream()
                .map(entry -> String.format("\"%s\":\"%s\"", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(","));
        return String.format("{%s}", jsonString);
    }

    private String getKey(Field field) {
        String value = field.getAnnotation(JsonElement.class).key();
        return value.isEmpty() ? field.getName() : value;
    }
}
