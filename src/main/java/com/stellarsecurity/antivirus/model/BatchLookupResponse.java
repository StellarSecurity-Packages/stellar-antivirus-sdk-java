
package com.stellarsecurity.antivirus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Response payload for batch threat lookup.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchLookupResponse {

    private List<ThreatMatch> results;

    public BatchLookupResponse() {}

    public List<ThreatMatch> getResults() {
        return results;
    }

    public void setResults(List<ThreatMatch> results) {
        this.results = results;
    }
}
