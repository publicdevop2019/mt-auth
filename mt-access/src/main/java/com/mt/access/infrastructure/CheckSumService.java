package com.mt.access.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckSumService {
    public static String getChecksum(Object object) {
        String serialize = CommonDomainRegistry.getCustomObjectSerializer().serialize(object);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(serialize.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(thedigest);
        } catch (NoSuchAlgorithmException e) {
            throw new DefinedRuntimeException("unable to get sum value", "1068",
                HttpResponseCode.INTERNAL_SERVER_ERROR, e);
        }
    }
}
