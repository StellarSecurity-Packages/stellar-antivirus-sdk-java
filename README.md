
# Stellar Antivirus Java SDK (Core)

Small Java library that lets you:

* Walk a directory (including APKs or any other file type)
* Hash each file with SHA-256
* Send hashes to your **Stellar Antivirus backend API** in **batches**
* Get back verdicts (CLEAN / INFECTED / SUSPICIOUS / UNKNOWN)

## Quick start

```java
import com.stellarsecurity.antivirus.StellarAntivirusClient;
import com.stellarsecurity.antivirus.config.StellarAntivirusConfig;
import com.stellarsecurity.antivirus.model.ScanSummary;

import java.nio.file.Paths;

public class Example {
    public static void main(String[] args) {
        StellarAntivirusConfig cfg = new StellarAntivirusConfig(
            "https://your-backend.example.com",
            "YOUR_API_KEY"
        );

        try (StellarAntivirusClient client = new StellarAntivirusClient(cfg)) {
            ScanSummary summary = client.scanDirectory(Paths.get("/home/user"), true);

            summary.getResults().forEach(r ->
                System.out.println(r.getPath() + " => " + r.getVerdict())
            );
        }
    }
}
```

The SDK will:

1. Walk the directory
2. Compute SHA‑256 on each file
3. Send hashes to: `POST {baseUrl}/api/v1/threats/lookup` in batches (default 256 hashes)
4. Map backend results back to files

Adjust `ThreatLookupClient` and the request/response models if your backend contract differs.

## Threading

* `scanFileAsync(...)` and `scanDirectoryAsync(...)` run on an internal thread pool.
* You can also pass your own `ExecutorService` to the constructor if you want to integrate
  with an existing scheduler in your app or SDK.
