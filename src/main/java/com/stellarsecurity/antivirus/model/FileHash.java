
package com.stellarsecurity.antivirus.model;

/**
 * Minimal file descriptor used in batch lookup.
 */
public class FileHash {
    private String path;
    private String sha256;
    private long size;

    public FileHash() {}

    public FileHash(String path, String sha256, long size) {
        this.path = path;
        this.sha256 = sha256;
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public String getSha256() {
        return sha256;
    }

    public long getSize() {
        return size;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
