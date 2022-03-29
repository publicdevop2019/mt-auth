package com.mt.access.infrastructure;

import com.mt.common.domain.CommonDomainRegistry;
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
            e.printStackTrace();
            throw new RuntimeException("unable to get sum value");
        }
    }
}
