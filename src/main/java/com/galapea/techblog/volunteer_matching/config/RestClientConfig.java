package com.galapea.techblog.volunteer_matching.config;

import com.galapea.techblog.volunteer_matching.ForbiddenGridDbConnectionException;
import com.galapea.techblog.volunteer_matching.griddb.GridDbException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    final Logger LOGGER = LoggerFactory.getLogger(RestClientConfig.class);

    @Bean("GridDbRestClient")
    public RestClient gridDbRestClient(
            @NonNull @Value("${griddbcloud.base-url}") final String baseUrl,
            @NonNull @Value("${griddbcloud.auth-token}") final String authToken) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + authToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(
                        status -> status.is4xxClientError() || status.is5xxServerError(), (request, response) -> {
                            String responseBody = getResponseBody(response);
                            LOGGER.error("GridDB API error: status={} body={}", response.getStatusCode(), responseBody);
                            if (response.getStatusCode().value() == 403) {
                                LOGGER.error("Access forbidden - please check your auth token and permissions.");
                                throw new ForbiddenGridDbConnectionException("Access forbidden to GridDB Cloud API.");
                            }
                            throw new GridDbException("GridDB API error: ", response.getStatusCode(), responseBody);
                        })
                .requestInterceptor((request, body, execution) -> {
                    final long begin = System.currentTimeMillis();
                    ClientHttpResponse response = execution.execute(request, body);
                    logDuration(request, body, begin, response);
                    return response;
                })
                .build();
    }

    private void logDuration(HttpRequest request, byte[] body, final long begin, ClientHttpResponse response)
            throws IOException {
        long duration = System.currentTimeMillis() - begin;
        LOGGER.info(
                "[GridDbRestClientRequest] {} {} {} duration={}s Body: {}",
                request.getMethod(),
                request.getURI(),
                response.getStatusCode().value(),
                TimeUnit.MILLISECONDS.toSeconds(duration),
                body != null ? new String(body, StandardCharsets.UTF_8) : "<no-body>");
    }

    private String getResponseBody(ClientHttpResponse response) {
        try (InputStream in = response.getBody()) {
            byte[] bytes = in.readAllBytes();
            if (bytes.length == 0) {
                return "<empty>";
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "<error-reading-body: " + e.getMessage() + ">";
        }
    }
}
