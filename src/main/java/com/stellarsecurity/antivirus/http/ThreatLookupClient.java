
package com.stellarsecurity.antivirus.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stellarsecurity.antivirus.config.StellarAntivirusConfig;
import com.stellarsecurity.antivirus.model.BatchLookupRequest;
import com.stellarsecurity.antivirus.model.BatchLookupResponse;
import com.stellarsecurity.antivirus.model.FileHash;
import com.stellarsecurity.antivirus.model.ThreatMatch;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Low-level HTTP client that sends batches of file hashes
 * to your Stellar Antivirus backend.
 *
 * Default endpoint:
 *     POST {baseUrl}/api/v1/threats/lookup
 *
 * Adjust this in code if your backend uses another path.
 */
public class ThreatLookupClient {

    private final StellarAntivirusConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String lookupPath;

    public ThreatLookupClient(StellarAntivirusConfig config) {
        this(config, "/api/v1/threats/lookup");
    }

    public ThreatLookupClient(StellarAntivirusConfig config, String lookupPath) {
        this.config = config;
        this.lookupPath = lookupPath;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getConnectTimeoutMillis()))
                .build();
    }

    /**
     * Perform a single HTTP POST batch lookup.
     */
    public Map<String, ThreatMatch> lookup(List<FileHash> files) throws IOException, InterruptedException {
        if (files == null || files.isEmpty()) {
            return Map.of();
        }

        BatchLookupRequest payload = new BatchLookupRequest(config.getApiKey(), files);
        String json = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getBaseUrl() + lookupPath))
                .timeout(Duration.ofMillis(config.getReadTimeoutMillis()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Threat lookup failed, status=" + response.statusCode() + " body=" + response.body());
        }

        BatchLookupResponse parsed = mapper.readValue(response.body(), BatchLookupResponse.class);
        Map<String, ThreatMatch> index = new HashMap<>();
        if (parsed.getResults() != null) {
            for (ThreatMatch m : parsed.getResults()) {
                if (m.getSha256() != null) {
                    index.put(m.getSha256(), m);
                }
            }
        }
        return index;
    }
}
