package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.Instance;
import com.mt.proxy.domain.InstanceIdService;
import com.mt.proxy.domain.InstanceInfo;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class HttpInstanceIdService implements InstanceIdService {
    private static final String ENDPOINT_URL = "/mgmt/instance";
    @Autowired
    private InstanceInfo instanceInfo;
    @Value("${mt.misc.url.access}")
    private String accessUrl;
    @Value("${server.port}")
    private int port;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void iniInstanceId(String name) {
        if (instanceInfo.getId() != null) {
            return;
        }
        synchronized (HttpInstanceIdService.class) {
            if (instanceInfo.getId() != null) {
                return;
            }
            log.info("init instance id");
            InstanceCreateCommand command = new InstanceCreateCommand();
            command.setName("mt-proxy");
            InetAddress ip;
            try {
                ip = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                log.error("unable to resolve host info", e);
                throw new InstanceIdInitException();
            }
            command.setUrl("http://" + ip.getHostAddress() + ":" + port);
            HttpEntity<InstanceCreateCommand> request1 =
                new HttpEntity<>(command, null);
            RestTemplate restTemplate =
                new RestTemplate(); //one time rest template to avoid dependency issue
            ResponseEntity<Instance> exchange;
            try {
                exchange = restTemplate
                    .exchange(accessUrl + ENDPOINT_URL, HttpMethod.POST, request1,
                        Instance.class);
            } catch (ResourceAccessException ex) {
                log.warn("mt-access is not available");
                return;
            }
            if (exchange.getStatusCode().is2xxSuccessful()) {
                log.info("instance id {}", exchange.getBody().getId());
                instanceInfo.setId(exchange.getBody().getId());
            } else {
                log.error("unable to init instance id");
                throw new InstanceIdInitException();
            }
        }
    }

    @Override
    public void removeInstanceId() {
        if (instanceInfo.getId() == null) {
            return;
        }
        log.info("removing instance id");
        InstanceRemoveCommand command = new InstanceRemoveCommand();
        command.setId(instanceInfo.getId());
        HttpEntity<InstanceRemoveCommand> request1 =
            new HttpEntity<>(command, null);
        ResponseEntity<Void> exchange = restTemplate
            .exchange(accessUrl + ENDPOINT_URL, HttpMethod.DELETE, request1,
                Void.class);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            log.info("instance released");
        } else {
            log.error("unable to release instance id");
        }
    }

    @Override
    public void renew() {
        if (instanceInfo.getId() == null) {
            return;
        }
        log.info("renewing instance id");
        InstanceRenewCommand command = new InstanceRenewCommand();
        command.setId(instanceInfo.getId());
        HttpEntity<InstanceRenewCommand> request1 = new HttpEntity<>(command, null);
        ResponseEntity<Void> exchange;
        try {
            exchange = restTemplate
                .exchange(accessUrl + ENDPOINT_URL, HttpMethod.PUT, request1,
                    Void.class);
        } catch (ResourceAccessException ex) {
            log.warn("mt-access is not available");
            return;
        }

        if (exchange.getStatusCode().is2xxSuccessful()) {
            log.info("instance renewed");
        } else {
            log.error("unable to renew instance id");
        }
    }

    @Data
    public static class InstanceCreateCommand {
        private String name;
        private String url;
    }

    @Data
    public static class InstanceRemoveCommand {
        private Integer id;
    }

    @Data
    public static class InstanceRenewCommand {
        private Integer id;
    }

    private static class InstanceIdInitException extends RuntimeException {
    }
}
