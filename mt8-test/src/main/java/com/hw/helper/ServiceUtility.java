package com.hw.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class ServiceUtility {
    private static ObjectMapper mapper = new ObjectMapper();
    private static String USER_ID = "uid";
    private static String AUTHORITIES = "authorities";

    public static String getUsername(String bearerHeader) {
        String replace = bearerHeader.replace("Bearer ", "");
        String jwtBody;
        try {
            jwtBody = replace.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("malformed jwt token");
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(jwtBody);
        String s = new String(decode);
        try {
            Map<String, Object> var0 = mapper.readValue(s, new TypeReference<Map<String, Object>>() {
            });
            return (String) var0.get(USER_ID);
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to find uid in authorization header");
        }
    }

    public static List<String> getAuthority(String bearerHeader) {
        String replace = bearerHeader.replace("Bearer ", "");
        String jwtBody;
        try {
            jwtBody = replace.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("malformed jwt token");
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(jwtBody);
        String s = new String(decode);
        try {
            Map<String, Object> var0 = mapper.readValue(s, new TypeReference<Map<String, Object>>() {
            });
            return (List<String>) var0.get(AUTHORITIES);
        } catch (IOException e) {
            throw new IllegalArgumentException("unable to find authorities in authorization header");
        }
    }


    public static String getServerTimeStamp() {
        return OffsetDateTime.now(ZoneOffset.UTC).toString();
    }
}
