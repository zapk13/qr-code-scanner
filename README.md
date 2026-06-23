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

The first APK builds were signed with a **debug certificate**, which Play Protect often flags as unsafe.

**Fix (one-time setup):**

1. Go to [Actions → Create release keystore](https://github.com/zapk13/qr-code-scanner/actions/workflows/create-keystore.yml)
2. Click **Run workflow**, choose a strong password, and run it
3. Download the `release-keystore` artifact
4. Add these [repository secrets](https://github.com/zapk13/qr-code-scanner/settings/secrets/actions):
   - `ANDROID_KEYSTORE_BASE64` — contents of `release.keystore.base64`
   - `ANDROID_KEYSTORE_PASSWORD` — your keystore password
   - `ANDROID_KEY_ALIAS` — `qrscanner` (or the alias you chose)
   - `ANDROID_KEY_PASSWORD` — your key password
5. Run **Build APK** again and install the new artifact (v1.0.1+)

**If Play Protect still warns:** sideloaded apps from unknown developers may show “App not commonly installed.” Tap **More details** → **Install anyway**. Publishing to Google Play removes this over time.

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
