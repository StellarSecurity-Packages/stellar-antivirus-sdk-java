
package com.stellarsecurity.antivirus.model;

import java.nio.file.Path;

/**
 * Result of scanning a single file.
 */
public class FileScanResult {

    private Path path;
    private String sha256;
    private long size;
    private ThreatVerdict verdict;
    private ThreatMatch threat;
    private String error;

    public FileScanResult() {}

    public FileScanResult(Path path,
                          String sha256,
                          long size,
                          ThreatVerdict verdict,
                          ThreatMatch threat,
                          String error) {
        this.path = path;
        this.sha256 = sha256;
        this.size = size;
        this.verdict = verdict;
        this.threat = threat;
        this.error = error;
    }

    public Path getPath() {
        return path;
    }

    public String getSha256() {
        return sha256;
    }

    public long getSize() {
        return size;
    }

    public ThreatVerdict getVerdict() {
        return verdict;
    }

    public ThreatMatch getThreat() {
        return threat;
    }

    public String getError() {
        return error;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setVerdict(ThreatVerdict verdict) {
        this.verdict = verdict;
    }

    public void setThreat(ThreatMatch threat) {
        this.threat = threat;
    }

    public void setError(String error) {
        this.error = error;
    }
}
