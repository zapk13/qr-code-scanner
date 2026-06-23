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

## Play Protect blocked the install?

Older APK builds were signed with a **debug certificate**, which Play Protect often flags.

Current builds use a **release keystore** automatically in CI (cached across runs). For your own signing key (recommended before Play Store), optionally add repository secrets — see below.

**If Play Protect still warns:** sideloaded apps from unknown developers may show “App not commonly installed.” Tap **More details** → **Install anyway**.

### Optional: use your own signing key

1. Run [Create release keystore](https://github.com/zapk13/qr-code-scanner/actions/workflows/create-keystore.yml) or `bash scripts/create-release-keystore.sh`
2. Add repository secrets: `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`
3. Re-run **Build APK**

## Build locally

Requirements: JDK 17, Android SDK

```bash
cp keystore.properties.example keystore.properties
# Edit keystore.properties with your release keystore details
./gradlew assembleRelease
```

The APK is at `app/build/outputs/apk/release/app-release.apk`.

Or generate a keystore locally:

```bash
bash scripts/create-release-keystore.sh
```

## Tech stack

- Kotlin
- CameraX + ML Kit Barcode Scanning
- Material 3 (monotone grayscale theme)
- SharedPreferences for lightweight history storage

## Permissions

- **Camera** — required for scanning QR codes

No internet permission is required. Links open in the system browser.
