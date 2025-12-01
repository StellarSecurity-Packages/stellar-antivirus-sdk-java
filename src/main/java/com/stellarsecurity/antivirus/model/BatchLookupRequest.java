
package com.stellarsecurity.antivirus.model;

import java.util.List;

/**
 * Request payload for batch threat lookup.
 *
 * Adjust to match your backend contract if needed.
 */
public class BatchLookupRequest {

    private String apiKey;
    private List<FileHash> files;

    public BatchLookupRequest() {}

    public BatchLookupRequest(String apiKey, List<FileHash> files) {
        this.apiKey = apiKey;
        this.files = files;
    }

    public String getApiKey() {
        return apiKey;
    }

    public List<FileHash> getFiles() {
        return files;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setFiles(List<FileHash> files) {
        this.files = files;
    }
}
