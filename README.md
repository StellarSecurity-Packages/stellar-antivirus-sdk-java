# **Stellar Antivirus SDK (Java)**

Modern cloud-powered malware detection SDK for developers.\
Designed for high performance, privacy, batch scanning, and seamless
integration into Java applications.

## **🔒 What is Stellar Antivirus SDK?**

Stellar Antivirus SDK is a lightweight Java library that provides a
powerful file-scanning engine powered by Stellar's cloud-based threat
intelligence API.

### Key Features

-   Multi-threaded scanning\
-   Batch hash submission\
-   File privacy (only SHA-256 is sent)\
-   APK scanning\
-   Cross‑platform\
-   Lightweight Java API

## Installation

### Maven

``` xml
<dependency>
    <groupId>com.stellarsecurity</groupId>
    <artifactId>stellar-antivirus-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

``` gradle
implementation 'com.stellarsecurity:stellar-antivirus-sdk:1.0.0'
```

## Basic Usage

``` java
StellarAV av = new StellarAV("YOUR_API_KEY");
FileScanResult result = av.scanFile("/path/to/file.exe");
System.out.println(result.verdict());
```

## Scan Directory

``` java
ScanSummary summary = av.scanDirectory("/path");
System.out.println(summary.threats());
```

## APK Scanning

``` java
FileScanResult apk = av.scanApk("/path/app.apk");
```

## Privacy

-   No files uploaded\
-   Only SHA-256 hashes leave device\
-   Zero-knowledge scanning

## API Contract

``` json
{
  "api_key": "YOUR_KEY",
  "hashes": ["sha256"]
}
```

## License

MIT License