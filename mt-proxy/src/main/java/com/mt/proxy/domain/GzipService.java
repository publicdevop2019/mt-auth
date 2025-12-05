package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.LogService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

@Slf4j
public class GzipService {
    public static byte[] updateGzip(ServerHttpRequest request, byte[] responseBody,
                                    ServerHttpResponse originalResponse) {
        if (originalResponse.getHeaders().getContentType() != null
            && originalResponse.getHeaders().getContentType()
            .equals(MediaType.APPLICATION_JSON)
        ) {
            boolean minLength = responseBody.length > 1024;
            if (minLength) {
                byte[] compressed = new byte[0];
                try {
                    compressed = compress(responseBody);
                } catch (IOException e) {
                    LogService.reactiveLog(request, () -> log.error("error during compress", e));
                }
                ;
                byte[] finalCompressed = compressed;
                LogService.reactiveLog(request,
                    () -> log.debug("gzip response length before {} after {}",
                        responseBody.length, finalCompressed.length));
                originalResponse.getHeaders().setContentLength(compressed.length);
                originalResponse.getHeaders()
                    .set(HttpHeaders.CONTENT_ENCODING, "gzip");
                return compressed;
            }
        }
        return responseBody;
    }

    private static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }
}
