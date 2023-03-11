package com.mt.proxy.infrastructure;

import static com.mt.proxy.infrastructure.AppConstant.REQ_CLIENT_IP;
import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import com.mt.proxy.domain.Utility;
import java.util.function.Consumer;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class LogHelper {
    public static void log(ServerHttpRequest request,
                           Consumer<Void> consumer) {
        String uuid = Utility.getUuid(request);
        String clientIp = Utility.getClientIp(request);
        MDC.put(REQ_UUID, uuid);
        MDC.put(REQ_CLIENT_IP, clientIp);
        consumer.accept(null);
        MDC.clear();
    }
}
