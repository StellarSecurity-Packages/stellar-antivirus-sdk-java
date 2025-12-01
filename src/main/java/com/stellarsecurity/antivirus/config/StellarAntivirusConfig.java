
package com.stellarsecurity.antivirus.config;

/**
 * Configuration for Stellar Antivirus SDK.
 *
 * You can point this at your own backend and API key.
 */
public class StellarAntivirusConfig {

    private final String baseUrl;
    private final String apiKey;
    private final int batchSize;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;

    public StellarAntivirusConfig(String baseUrl, String apiKey) {
        this(baseUrl, apiKey, 256, 10_000, 30_000);
    }

    public StellarAntivirusConfig(String baseUrl,
                                  String apiKey,
                                  int batchSize,
                                  int connectTimeoutMillis,
                                  int readTimeoutMillis) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.apiKey = apiKey;
        this.batchSize = batchSize;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }
}
