package com.mt.proxy.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.proxy.domain.exception.DefinedRuntimeException;
import com.mt.proxy.domain.exception.HttpResponseCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckSumService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String getChecksum(Object object) {
        String serialize = serialize(object);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(serialize.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(thedigest);
        } catch (NoSuchAlgorithmException e) {
            log.error("unable to get sum value", e);
            throw new DefinedRuntimeException("unable to get sum value", "2003",
                HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    private <T> String serialize(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("error during object mapper serialize", e);
            throw new DefinedRuntimeException("error during object mapper serialize", "2005",
                HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

}
