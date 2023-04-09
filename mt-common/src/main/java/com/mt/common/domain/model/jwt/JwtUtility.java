package com.mt.common.domain.model.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class JwtUtility {
    public static final String JWT_CLAIM_UID = "uid";
    public static final String JWT_CLAIM_PERM = "permissionIds";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String HTTP_HEADER_BEARER = "Bearer ";
    private static final String JWT_CLAIM_SCOPES = "scope";
    private static final String JWT_CLAIM_CLIENT_ID = "client_id";

    private JwtUtility() {
    }

    public static String getUserId(String bearerHeader) {
        return getField(JWT_CLAIM_UID, bearerHeader);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getField(String field, String bearerHeader) {
        String replace = bearerHeader.replace(HTTP_HEADER_BEARER, "");
        String jwtBody;
        try {
            jwtBody = replace.split("\\.")[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new DefinedRuntimeException("unable to extract jwt token", "0014",
                HttpResponseCode.INTERNAL_SERVER_ERROR,
                ExceptionCatalog.OPERATION_ERROR, ex);
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(jwtBody);
        String s = new String(decode);
        try {
            Map<String, Object> var0 =
                mapper.readValue(s, new TypeReference<Map<String, Object>>() {
                });
            return (T) var0.get(field);
        } catch (IOException ex) {
            throw new DefinedRuntimeException("unable to extract jwt token", "0015",
                HttpResponseCode.INTERNAL_SERVER_ERROR,
                ExceptionCatalog.OPERATION_ERROR, ex);
        }
    }

    public static String getServerTimeStamp() {
        return OffsetDateTime.now(ZoneOffset.UTC).toString();
    }

    public static String getClientId(String bearerHeader) {
        return getField(JWT_CLAIM_CLIENT_ID, bearerHeader);
    }

    public static List<String> getPermissionIds(String bearerHeader) {
        return getField(JWT_CLAIM_PERM, bearerHeader);
    }

    public static List<String> getScopes(String bearerHeader) {
        return getField(JWT_CLAIM_SCOPES, bearerHeader);
    }
}
