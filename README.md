# QR Code Scanner

A lightweight Android QR code scanner with a clean, monotone design.

## Features

- Scan QR codes using the device camera
- Open scanned links in your browser
- Copy any scanned content to the clipboard
- Scan history with automatic cleanup after 30 days
- Manual clear history button
- No background services — camera stops when the app is not visible

## Download APK

1. Go to [Actions](https://github.com/zapk13/qr-code-scanner/actions) → **Build APK**
2. Open the latest completed run (or trigger **Run workflow** manually)
3. Download the `qr-scanner-release` artifact from the run

## Build locally

Requirements: JDK 17, Android SDK

```bash
./gradlew assembleRelease
```

The APK is at `app/build/outputs/apk/release/app-release.apk`.

## Tech stack

- Kotlin
- CameraX + ML Kit Barcode Scanning
- Material 3 (monotone grayscale theme)
- SharedPreferences for lightweight history storage

## Permissions

- **Camera** — required for scanning QR codes

No internet permission is required. Links open in the system browser.
