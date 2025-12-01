
package com.stellarsecurity.antivirus.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Summary for a directory / batch scan.
 */
public class ScanSummary {

    private final List<FileScanResult> results;
    private final int totalFiles;
    private final int infected;
    private final int suspicious;
    private final int clean;
    private final int unknown;
    private final Instant startedAt;
    private final Instant finishedAt;

    public ScanSummary(List<FileScanResult> results,
                       int infected,
                       int suspicious,
                       int clean,
                       int unknown,
                       Instant startedAt,
                       Instant finishedAt) {
        this.results = Collections.unmodifiableList(results);
        this.totalFiles = results.size();
        this.infected = infected;
        this.suspicious = suspicious;
        this.clean = clean;
        this.unknown = unknown;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public List<FileScanResult> getResults() {
        return results;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getInfected() {
        return infected;
    }

    public int getSuspicious() {
        return suspicious;
    }

    public int getClean() {
        return clean;
    }

    public int getUnknown() {
        return unknown;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }
}
