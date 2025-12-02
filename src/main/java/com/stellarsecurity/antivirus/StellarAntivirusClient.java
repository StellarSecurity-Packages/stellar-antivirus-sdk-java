
package com.stellarsecurity.antivirus;

import com.stellarsecurity.antivirus.config.StellarAntivirusConfig;
import com.stellarsecurity.antivirus.http.ThreatLookupClient;
import com.stellarsecurity.antivirus.model.FileHash;
import com.stellarsecurity.antivirus.model.FileScanResult;
import com.stellarsecurity.antivirus.model.ScanSummary;
import com.stellarsecurity.antivirus.model.ThreatMatch;
import com.stellarsecurity.antivirus.model.ThreatVerdict;
import com.stellarsecurity.antivirus.util.HashUtils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * High-level entrypoint for Stellar Antivirus Java SDK.
 *
 * Typical usage:
 *
 *   StellarAntivirusConfig cfg = new StellarAntivirusConfig(
 *       "https://your-backend.example.com",
 *       "YOUR_API_KEY"
 *   );
 *
 *   StellarAntivirusClient client = new StellarAntivirusClient(cfg);
 *   ScanSummary summary = client.scanDirectory(Paths.get("/home/user"), true);
 *
 *   summary.getResults().forEach(result -> {
 *       System.out.println(result.getPath() + " => " + result.getVerdict());
 *   });
 */
public class StellarAntivirusClient implements AutoCloseable {

    private final StellarAntivirusConfig config;
    private final ThreatLookupClient lookupClient;
    private final ExecutorService executor;

    public StellarAntivirusClient(StellarAntivirusConfig config) {
        this(config, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    public StellarAntivirusClient(StellarAntivirusConfig config, ExecutorService executor) {
        this.config = Objects.requireNonNull(config, "config");
        this.executor = Objects.requireNonNull(executor, "executor");
        this.lookupClient = new ThreatLookupClient(config);
    }

    /**
     * Scan a single file synchronously.
     */
    public FileScanResult scanFile(Path path) {
        Instant started = Instant.now();
        try {
            long size = Files.size(path);
            String sha256 = HashUtils.sha256Hex(path);

            FileHash fh = new FileHash(path.toString(), sha256, size);
            List<FileHash> singleton = List.of(fh);

            Map<String, ThreatMatch> idx = lookupClient.lookup(singleton);
            ThreatMatch match = idx.get(sha256);

            ThreatVerdict verdict = match != null && match.getVerdict() != null
                    ? match.getVerdict()
                    : ThreatVerdict.UNKNOWN;

            return new FileScanResult(path, sha256, size, verdict, match, null);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return new FileScanResult(path, null, -1L, ThreatVerdict.ERROR, null, e.getMessage());
        }
    }

    /**
     * Async wrapper for scanFile(...) that runs hashing + network
     * on the SDK's own ExecutorService.
     */
    public CompletableFuture<FileScanResult> scanFileAsync(Path path) {
        return CompletableFuture.supplyAsync(() -> scanFile(path), executor);
    }

    /**
     * Recursively scan a directory. Set recursive=false to only scan direct children.
     */
    public ScanSummary scanDirectory(Path root, boolean recursive) {
        Instant started = Instant.now();
        List<Path> files = collectFiles(root, recursive);

        List<FileHash> hashes = new ArrayList<>(files.size());
        List<FileScanResult> results = new ArrayList<>(files.size());

        // 1. Hash all files locally
        for (Path p : files) {
            try {
                long size = Files.size(p);
                String sha256 = HashUtils.sha256Hex(p);
                hashes.add(new FileHash(p.toString(), sha256, size));
            } catch (IOException e) {
                results.add(new FileScanResult(p, null, -1L, ThreatVerdict.ERROR, null, e.getMessage()));
            }
        }

        // 2. Call backend in batches
        Map<String, ThreatMatch> index;
        try {
            index = lookupInBatches(hashes);
        } catch (IOException | InterruptedException e) {
            // If lookup fails entirely, mark all as ERROR except those already errored
            for (FileHash fh : hashes) {
                Path p = Path.of(fh.getPath());
                results.add(new FileScanResult(p, fh.getSha256(), fh.getSize(),
                        ThreatVerdict.ERROR, null, "Lookup failed: " + e.getMessage()));
            }
            return finalizeSummary(results, started);
        }

        // 3. Merge results
        for (FileHash fh : hashes) {
            ThreatMatch match = index.get(fh.getSha256());
            ThreatVerdict verdict = ThreatVerdict.UNKNOWN;
            if (match != null && match.getVerdict() != null) {
                verdict = match.getVerdict();
            }
            results.add(new FileScanResult(Path.of(fh.getPath()), fh.getSha256(), fh.getSize(), verdict, match, null));
        }

        return finalizeSummary(results, started);
    }

    /**
     * Async directory scan. Hashing + network run in SDK-owned threads.
     */
    public CompletableFuture<ScanSummary> scanDirectoryAsync(Path root, boolean recursive) {
        return CompletableFuture.supplyAsync(() -> scanDirectory(root, recursive), executor);
    }

    private Map<String, ThreatMatch> lookupInBatches(List<FileHash> files) throws IOException, InterruptedException {
        int batchSize = Math.max(1, config.getBatchSize());
        Map<String, ThreatMatch> combined = new java.util.HashMap<>();
        for (int i = 0; i < files.size(); i += batchSize) {
            int end = Math.min(files.size(), i + batchSize);
            List<FileHash> slice = files.subList(i, end);
            Map<String, ThreatMatch> partial = lookupClient.lookup(slice);
            combined.putAll(partial);
        }
        return combined;
    }

    private List<Path> collectFiles(Path root, boolean recursive) {
        List<Path> files = new ArrayList<>();
        if (recursive) {
            try {
                Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (attrs.isRegularFile()) {
                            files.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to walk directory " + root, e);
            }
        } else {
            try {
                Files.list(root)
                        .filter(p -> Files.isRegularFile(p))
                        .forEach(files::add);
            } catch (IOException e) {
                throw new RuntimeException("Failed to list directory " + root, e);
            }
        }
        return files;
    }

    private ScanSummary finalizeSummary(List<FileScanResult> results, Instant startedAt) {
        int infected = 0;
        int suspicious = 0;
        int clean = 0;
        int unknown = 0;

        for (FileScanResult r : results) {
            if (r.getVerdict() == null) {
                unknown++;
                continue;
            }
            switch (r.getVerdict()) {
                case INFECTED:
                    infected++;
                    break;
                case SUSPICIOUS:
                    suspicious++;
                    break;
                case CLEAN:
                    clean++;
                    break;
                default:
                    unknown++;
                    break;
            }

        }

        return new ScanSummary(results, infected, suspicious, clean, unknown, startedAt, Instant.now());
    }

    /**
     * Shut down the SDK's internal thread pool.
     */
    @Override
    public void close() {
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
