package com.mt.proxy.domain;

import static com.mt.proxy.infrastructure.AppConstant.REQ_CLIENT_IP;
import static com.mt.proxy.infrastructure.AppConstant.REQ_UUID;

import com.netflix.discovery.EurekaClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ReportService {
    private final List<String> record = new ArrayList<>();
    @Autowired
    private EurekaClient eurekaClient;
    @Value("${manytree.mt-access.appId}")
    private String appName;
    @Value("${manytree.instance-id}")
    private String instanceId;
    @Value("${manytree.url.report}")
    private String endpointUrl;
    @Autowired
    private RestTemplate restTemplate;

    public void logResponseDetail(ServerHttpResponse response,
                                  ServerHttpRequest request) {
        long responseTimestamp = Instant.now().getEpochSecond();
        int responseStatusCode = response.getStatusCode().value();
        String uuid = MDC.get(REQ_UUID);
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        record.add("type:response,timestamp:" + responseTimestamp
            + ",statusCode:" + responseStatusCode + ",uuid:" + uuid + ",method:" + method +
            ",path:" + path);
    }

    public void logRequestDetails(ServerHttpRequest request) {
        long requestTimestamp = Instant.now().getEpochSecond();
        String clientIp = MDC.get(REQ_CLIENT_IP);
        String uuid = MDC.get(REQ_UUID);
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        record.add("type:request,timestamp:" + requestTimestamp
            + ",clientIp:" + clientIp + ",uuid:" + uuid + ",method:" + method + ",path:" + path);
    }

    /**
     * flush stored record to file
     */
//    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void flush() {
        log.debug("start of flushing api reports");
        int size = record.size();
        if (size != 0) {
            List<String> copy = new ArrayList<>(record);
            if (copy.size() != size) {
                log.warn("record modified after copy, abort this");
                return;
            }
            record.clear();
            log.debug("total records found {}", copy.size());
            File dir = new File("./reports");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File csvOutputFile =
                new File("./reports/api_report_" + Instant.now().getEpochSecond() + ".txt");
            try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
                AtomicInteger count = new AtomicInteger(0);
                copy.stream().map(e -> "id:" + count.getAndIncrement() + "," + e)
                    .forEach(pw::println);
                log.debug("file generated");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            log.debug("no record found for api reports");
        }
        log.debug("end of flushing api reports");
    }

    /**
     * send file to mt-auth for analysis
     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 20 * 1000)
    public void uploadReport() {
        log.debug("start of sending api reports");
        AtomicInteger maxFileSend = new AtomicInteger(5);
        if (eurekaClient.getApplication(appName) != null) {
            String homePageUrl =
                eurekaClient.getApplication(appName).getInstances().get(0).getHomePageUrl();
            String url = homePageUrl + endpointUrl;
            //read all unsend files
            File dir =
                new File("./reports");
            File[] files = dir.listFiles();
            if (files != null) {
                Arrays.stream(files).filter(e -> !e.getName().contains("_send"))
                    .forEach(unsendFile -> {
                        int andDecrement = maxFileSend.getAndDecrement();
                        if (andDecrement > 0) {
                            //send file to api
                            String originalPath = unsendFile.getPath();
                            Path path = Paths.get(originalPath);
                            BufferedReader reader;
                            List<String> requestBody = new ArrayList<>();
                            try {
                                reader = Files.newBufferedReader(path);
                                String line = reader.readLine();
                                while (line != null) {
                                    requestBody.add(line);
                                    line = reader.readLine();
                                }
                            } catch (IOException e) {
                                log.error("error during file read",e);
                            }
                            if (!requestBody.isEmpty()) {
                                HttpHeaders headers1 = new HttpHeaders();
                                headers1.set("name", unsendFile.getName());
                                headers1.set("instanceId", instanceId);
                                HttpEntity<List<String>> request1 =
                                    new HttpEntity<>(requestBody, headers1);

                                ResponseEntity<?> exchange = restTemplate
                                    .exchange(url, HttpMethod.POST, request1, Void.class);
                                if (exchange.getStatusCode().is2xxSuccessful()) {
                                    //rename file
                                    boolean b = unsendFile
                                        .renameTo(
                                            new File(originalPath.replace(".txt", "_send.txt")));
                                    if (!b) {
                                        throw new ReportRenameException();
                                    }
                                } else {
                                    log.error("unable to upload report");
                                }
                            }

                        }
                    });
            }
        } else {
            log.error("send report request was ignore due to service is not ready");
            throw new IllegalStateException(
                "send report request was ignore due to service is not ready");
        }
        log.debug("end of sending api reports");
    }

    public static class ReportRenameException extends RuntimeException {
    }
}
