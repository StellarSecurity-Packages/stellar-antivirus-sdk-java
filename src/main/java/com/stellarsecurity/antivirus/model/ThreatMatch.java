
package com.stellarsecurity.antivirus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Single threat match returned from backend for a file hash.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreatMatch {
    private String sha256;
    private String threatId;
    private String name;
    private String family;
    private String severity;
    private ThreatVerdict verdict;

    public ThreatMatch() {}

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getThreatId() {
        return threatId;
    }

    public void setThreatId(String threatId) {
        this.threatId = threatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public ThreatVerdict getVerdict() {
        return verdict;
    }

    public void setVerdict(ThreatVerdict verdict) {
        this.verdict = verdict;
    }
}
