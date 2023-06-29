package com.mt.common.domain.model.jwt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.common.CommonConstant;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.service_discovery.ServiceDiscovery;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ResourceServiceTokenHelper {
    @Value("${security.oauth2.client.accessTokenUri:#{null}}")
    private String tokenUrl;

    @Value("${security.oauth2.client.clientId:#{null}}")
    private String clientId;

    @Value("${security.oauth2.client.clientSecret:#{null}}")
    private String clientSecret;

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    @Autowired
    private RestTemplate restTemplate;

    private String storedJwtToken = null;

    private String getJwtToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBasicAuth(clientId, clientSecret);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        String token = null;
        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<String> resp = restTemplate.exchange(
                serviceDiscovery.getApplicationUrl(CommonConstant.APP_ID_PROXY) + tokenUrl,
                HttpMethod.POST, request, String.class);
            token = this.extractToken(resp);
        } catch (Exception e) {
            log.error("unable to get jwt token", e);
        }
        return token;
    }

    private String extractToken(ResponseEntity<String> resp) {
        ObjectMapper om = new ObjectMapper();
        om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        try {
            JsonNode nodes = om.readTree(resp.getBody());
            return nodes.get("access_token").asText();
        } catch (IOException e) {
            log.error("unable to extract jwt token", e);
            return null;
        }
    }

    /**
     * wrap request with jwt token, re try if jwt expired for first time,
     * only re-try with 401 error code.
     *
     * @param url url
     * @param httpMethod http method
     * @param httpEntity http entity
     * @param clazz clazz
     * @param <T> return type
     * @return response entity
     */
    public <T> ResponseEntity<T> exchange(String url, HttpMethod httpMethod,
                                          HttpEntity<?> httpEntity, Class<T> clazz) {
        if (storedJwtToken == null) {
            storedJwtToken = getJwtToken();
        }
        if (storedJwtToken == null) {
            throw new DefinedRuntimeException("unable to retrieve jwt token", "0016",
                HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders httpHeaders = HttpHeaders.writableHttpHeaders(httpEntity.getHeaders());
        httpHeaders.setBearerAuth(storedJwtToken);
        HttpEntity<?> httpEntity1 = new HttpEntity<>(httpEntity.getBody(), httpHeaders);
        try {
            return restTemplate.exchange(url, httpMethod, httpEntity1, clazz);
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 401) {
                storedJwtToken = getJwtToken();
                if (storedJwtToken == null) {
                    throw new DefinedRuntimeException("unable to retrieve jwt token", "0017",
                        HttpResponseCode.INTERNAL_SERVER_ERROR);
                }
                httpHeaders.setBearerAuth(storedJwtToken);
                HttpEntity<?> httpEntity2 = new HttpEntity<>(httpEntity.getBody(), httpHeaders);
                return restTemplate.exchange(url, httpMethod, httpEntity2, clazz);
            }
            throw ex;
        }
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod httpMethod,
                                          HttpEntity<?> httpEntity,
                                          ParameterizedTypeReference<T> responseType) {
        if (storedJwtToken == null) {
            storedJwtToken = getJwtToken();
        }
        if (storedJwtToken == null) {
            throw new DefinedRuntimeException("unable to retrieve jwt token", "0018",
                HttpResponseCode.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders httpHeaders = HttpHeaders.writableHttpHeaders(httpEntity.getHeaders());
        httpHeaders.setBearerAuth(storedJwtToken);
        HttpEntity<?> httpEntity1 = new HttpEntity<>(httpEntity.getBody(), httpHeaders);
        try {
            return restTemplate.exchange(url, httpMethod, httpEntity1, responseType);
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 401) {
                storedJwtToken = getJwtToken();
                if (storedJwtToken == null) {
                    throw new DefinedRuntimeException("unable to retrieve jwt token", "0019",
                        HttpResponseCode.INTERNAL_SERVER_ERROR);
                }
                httpHeaders.setBearerAuth(storedJwtToken);
                HttpEntity<?> httpEntity2 = new HttpEntity<>(httpEntity.getBody(), httpHeaders);
                return restTemplate.exchange(url, httpMethod, httpEntity2, responseType);
            }
            throw ex;
        }
    }

}