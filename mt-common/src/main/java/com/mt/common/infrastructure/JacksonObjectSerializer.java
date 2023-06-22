package com.mt.common.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.serializer.CustomObjectSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JacksonObjectSerializer implements CustomObjectSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void setUp() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    }

    @Override
    public byte[] nativeSerialize(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            throw new DefinedRuntimeException("error during native serialize", "0050",
                HttpResponseCode.BAD_REQUEST, ex);
        }
    }

    @Override
    public Object nativeDeserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            throw new DefinedRuntimeException("error during native deserialize", "0051",
                HttpResponseCode.BAD_REQUEST, ex);
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
        } catch (JsonProcessingException ex) {
            throw new DefinedRuntimeException("error during object mapper serialize", "0052",
                HttpResponseCode.BAD_REQUEST, ex);
        }
    }

    @Override
    public <T> T deserialize(String str, Class<T> clazz) {
        try {
            return objectMapper.readValue(str, clazz);
        } catch (IOException ex) {
            throw new DefinedRuntimeException("error during object mapper deserialize", "0053",
                HttpResponseCode.BAD_REQUEST, ex);
        }
    }

    @Override
    public <T, Z> Map<T, Z> deserializeToMap(String str, Class<T> keyClass, Class<Z> valueClass) {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        MapType mapType = typeFactory.constructMapType(HashMap.class, keyClass, valueClass);
        try {
            return objectMapper.readValue(str, mapType);
        } catch (IOException ex) {
            throw new DefinedRuntimeException("error during object mapper deserialize", "0054",
                HttpResponseCode.BAD_REQUEST, ex);
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
        } catch (JsonProcessingException ex) {
            throw new DefinedRuntimeException("error during object mapper collection serialize",
                "0055",
                HttpResponseCode.BAD_REQUEST, ex);
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
            if (o == null) {
                return Collections.emptyList();
            }
            return o;
        } catch (IOException ex) {
            throw new DefinedRuntimeException("unable to deserialize collection", "0056",
                HttpResponseCode.BAD_REQUEST, ex);
        }
    }

    @Override
    public <T> Collection<T> deepCopyCollection(Collection<T> object, Class<T> clazz) {
        return deserializeCollection(serializeCollection(object), clazz);
    }


    @Override
    public <T> T applyJsonPatch(JsonPatch command, T beforePatch, Class<T> clazz) {
        try {
            JsonNode pathCommand = objectMapper.convertValue(beforePatch, JsonNode.class);
            JsonNode patchedNode = command.apply(pathCommand);
            return objectMapper.treeToValue(patchedNode, clazz);
        } catch (JsonPatchException | JsonProcessingException ex) {
            throw new DefinedRuntimeException("unable to json patch", "0057",
                HttpResponseCode.BAD_REQUEST, ex);

        }
    }
}
