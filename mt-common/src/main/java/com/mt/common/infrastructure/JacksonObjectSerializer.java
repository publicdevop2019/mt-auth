package com.mt.common.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mt.common.domain.model.serializer.CustomObjectSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Slf4j
@Service
public class JacksonObjectSerializer implements CustomObjectSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] nativeSerialize(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("error during native serialize", e);
            throw new UnableToNativeSerializeException();
        }
    }

    @Override
    public Object nativeDeserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            log.error("error during native deserialize", e);
            throw new UnableToNativeDeserializeException();
        }
    }

    @Override
    public <T> T nativeDeepCopy(T object) {
        return (T) nativeDeserialize(nativeSerialize(object));
    }

    @Override
    public <T> String serialize(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("error during object mapper serialize", e);
            throw new UnableToSerializeException();
        }
    }

    @Override
    public <T> T deserialize(String str, Class<T> clazz) {
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            return objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            log.error("error during object mapper deserialize", e);
            throw new UnableToDeSerializeException();
        }
    }

    @Override
    public <T, Z> Map<T, Z> deserializeToMap(String str, Class<T> keyClass, Class<Z> valueClass) {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        MapType mapType = typeFactory.constructMapType(HashMap.class, keyClass, valueClass);
        try {
            return objectMapper.readValue(str, mapType);
        } catch (IOException e) {
            log.error("error during object mapper deserialize", e);
            throw new UnableToDeSerializeToMapException();
        }
    }

    @Override
    public <T> T deepCopy(T object, Class<T> clazz) {
        return deserialize(serialize(object), clazz);
    }

    @Override
    public <T> String serializeCollection(Collection<T> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("error during object mapper collection serialize", e);
            throw new UnableToSerializeCollectionException();
        }
    }

    @Override
    public <T> Collection<T> deserializeCollection(String str, Class<T> clazz) {
        CollectionType javaType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, clazz);
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
            Collection<T> o = objectMapper.readValue(str, javaType);
            if (o == null)
                return Collections.emptyList();
            return o;
        } catch (IOException e) {
            log.error("error during object mapper collection deserialize", e);
            throw new UnableToDeserializeCollectionException();
        }
    }

    @Override
    public <T> Collection<T> deepCopyCollection(Collection<T> object,Class<T> clazz) {
            return deserializeCollection(serializeCollection(object),clazz);
//        try {
//            return objectMapper.readValue(objectMapper.writeValueAsString(object), new TypeReference<Collection<T>>() {
//            });
//        } catch (IOException e) {
//            log.error("error during object mapper list deep copy", e);
//            throw new UnableToDeepCopyCollectionException();
//        }
    }


    @Override
    public <T> T applyJsonPatch(JsonPatch command, T beforePatch, Class<T> clazz) {
        try {
            JsonNode pathCommand = objectMapper.convertValue(beforePatch, JsonNode.class);
            JsonNode patchedNode = command.apply(pathCommand);
            return objectMapper.treeToValue(patchedNode, clazz);
        } catch (JsonPatchException | JsonProcessingException e) {
            log.error("error during object mapper json patch", e);
            throw new UnableToJsonPatchException();
        }
    }

    public static class UnableToSerializeException extends RuntimeException {
    }

    public static class UnableToDeSerializeException extends RuntimeException {
    }
    public static class UnableToDeSerializeToMapException extends RuntimeException {
    }

    public static class UnableToJsonPatchException extends RuntimeException {
    }

    public static class UnableToDeepCopyCollectionException extends RuntimeException {
    }

    public static class UnableToSerializeCollectionException extends RuntimeException {
    }

    public static class UnableToDeserializeCollectionException extends RuntimeException {
    }

    public static class UnableToNativeSerializeException extends RuntimeException {
    }

    public static class UnableToNativeDeserializeException extends RuntimeException {
    }
}
