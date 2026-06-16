package com.example.aisports.airecord.client;

import com.example.aisports.airecord.dto.AiTaskResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class AiServiceClient {
    private final RestClient restClient;
    private final String internalToken;

    public AiServiceClient(
        RestClient.Builder builder,
        @Value("${app.ai.base-url}") String baseUrl,
        @Value("${app.ai.internal-token}") String internalToken
    ) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.internalToken = internalToken;
    }

    public AiTaskResponse generateDailyAnalysis(Map<String, Object> payload) {
        return post("/internal/ai/v1/daily-analysis", payload);
    }

    public AiTaskResponse generatePlan(Map<String, Object> payload) {
        return post("/internal/ai/v1/plan-generation", payload);
    }

    public AiTaskResponse generateTermReport(Map<String, Object> payload) {
        return post("/internal/ai/v1/term-report", payload);
    }

    private AiTaskResponse post(String path, Map<String, Object> payload) {
        return restClient.post()
            .uri(path)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + internalToken)
            .body(payload)
            .retrieve()
            .body(AiTaskResponse.class);
    }
}

