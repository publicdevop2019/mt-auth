package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.LogService;
import com.mt.proxy.port.adapter.http.HttpUtility;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerWebExchange;

/**
 * log request & response access record
 */
@Slf4j
@Service
public class ReportService {
    public static final String REPORT_EXTENSION = ".txt";
    public static final String REPORT_DIR = "./reports";
    private static final String SENT_SUFFIX = "_send";
    private static final String SENT_ERROR_SUFFIX = "_error";
    private static final String REPORT_URL = "/reports/proxy";
    private final List<String> record = new ArrayList<>();
    @Autowired
    private EndpointService endpointService;
    @Autowired
    private LogService logService;
    @Value("${mt.misc.instance-id}")
    private String instanceId;
    @Autowired
    private HttpUtility httpHelper;

    /**
     * log response access record.
     * response is linked to request via span id, method & path is not logged because
     * fetch value requires reflection which is a performance concern
     *
     * @param exchange ServerWebExchange
     */
    public void logResponseDetail(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String spanId = Utility.getSpanId(exchange.getRequest());
        long responseTimestamp = Instant.now().toEpochMilli();
        int responseStatusCode = response.getStatusCode().value();
        long contentLength = response.getHeaders().getContentLength();
        record.add("type:response,timestamp:" + responseTimestamp +
            ",uuid:" + spanId + ",statusCode:" + responseStatusCode + ",contentLength:" +
            contentLength);
    }

    public void logRequestDetails(ServerHttpRequest request) {
        long requestTimestamp = Instant.now().toEpochMilli();
        String clientIp = Utility.getClientInfoForReport(request);
        String spanId = Utility.getSpanId(request);
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        Optional<Endpoint> endpoint = endpointService.findEndpoint(path, method, false);
        if (endpoint.isPresent()) {
            record.add("type:request,timestamp:" + requestTimestamp + ",uuid:" + spanId
                + ",clientIp:" + clientIp + ",method:" + method + ",path:" + path + ",endpointId:" +
                endpoint.get().getId());
        } else {
            record.add("type:request,timestamp:" + requestTimestamp + ",uuid:" + spanId
                + ",clientIp:" + clientIp + ",method:" + method + ",path:" + path +
                ",endpointId:not_found");
        }
    }

    /**
     * flush stored record to file
     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void flush() {
        logService.initTrace();
        log.trace("start of scheduled task 1");
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
            File dir = new File(REPORT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File csvOutputFile =
                new File(
                    REPORT_DIR + "/report_" + Instant.now().getEpochSecond() + getFileCount(dir) +
                        REPORT_EXTENSION);
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
        log.trace("end of scheduled task 1");
    }

    private String getFileCount(File dir) {
        File[] files = dir.listFiles();
        return "_" + (files == null ? "0" : String.valueOf(files.length));
    }

    /**
     * send file to mt-auth for analysis
     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void uploadReport() {
        logService.initTrace();
        log.trace("start of scheduled task 2");
        log.debug("start of sending api reports");
        AtomicInteger maxFileSend = new AtomicInteger(5);

        String url = httpHelper.getAccessUrl() + REPORT_URL;
        //read all unsend files
        File dir =
            new File(REPORT_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            int size =
                (int) Arrays.stream(files).filter(e -> !e.getName().contains(SENT_SUFFIX))
                    .count();
            if (size == 0) {
                log.debug("no unsent report found");
            }
            Arrays.stream(files).filter(e -> !e.getName().contains(SENT_SUFFIX) &&
                    !e.getName().contains(SENT_ERROR_SUFFIX))
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
                            log.error("error during file read", e);
                        }
                        if (!requestBody.isEmpty()) {
                            HttpHeaders headers1 = new HttpHeaders();
                            headers1.set("name", unsendFile.getName());
                            headers1.set("instanceId", instanceId);
                            HttpEntity<List<String>> request1 =
                                new HttpEntity<>(requestBody, headers1);
                            try {
                                httpHelper.getRestTemplate()
                                    .exchange(url, HttpMethod.POST, request1, Void.class);
                                boolean b = unsendFile
                                    .renameTo(
                                        new File(originalPath.replace(".txt", SENT_SUFFIX +
                                            REPORT_EXTENSION)));
                                //rename file
                                if (!b) {
                                    throw new ReportRenameException();
                                }
                            } catch (Exception ex) {
                                if (ex instanceof HttpClientErrorException) {
                                    log.warn(
                                        "unable to upload report, remote return 4xx, file name is {}",
                                        originalPath);
                                } else {
                                    log.warn(
                                        "unable to upload report, file name is {}, ex name {}",
                                        originalPath, ex.getClass().getName());
                                }
                                //rename file
                                boolean b = unsendFile
                                    .renameTo(
                                        new File(
                                            originalPath.replace(".txt", SENT_ERROR_SUFFIX +
                                                REPORT_EXTENSION)));
                                if (!b) {
                                    throw new ReportRenameException();
                                }
                            }
                        }

                    }
                });
        }
        log.debug("end of sending api reports");
        log.trace("end of scheduled task 2");
    }

    public static class ReportRenameException extends RuntimeException {
    }
}
