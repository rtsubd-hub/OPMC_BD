# Full-Screen Android WebView App (Kotlin + Jetpack Compose)

This is a complete, single-activity Android app built using **Kotlin** and **Jetpack Compose**. It features a full-screen `WebView` embedded using Compose `AndroidView`, configured specifically to load the requested Google Apps Script web app with all essential enterprise settings.

## Features Implemented:
1. **Full-screen WebView** in Jetpack Compose (`Modifier.fillMaxSize()`)
2. **Target URL**: `https://script.google.com/macros/s/AKfycbzpvP1pr0XB8OFYXDaJOvFZaEvSpBL2S4WBNoQQMbEnqSq4DmNm8XriqPYKR3T56G6g/exec`
3. **JavaScript Enabled**: `settings.javaScriptEnabled = true`
4. **DOM Storage Enabled**: `settings.domStorageEnabled = true`
5. **Back Button History Navigation**: Integrated Jetpack Compose `BackHandler(enabled = canGoBack) { webView?.goBack() }`
6. **Loading Spinner**: Material 3 `CircularProgressIndicator` centered over the view while loading, synchronized with `WebViewClient.onPageStarted`, `onPageFinished`, and `WebChromeClient.onProgressChanged`.
7. **Edge-to-Edge & System Bars**: Configured with `enableEdgeToEdge()` and `systemBarsPadding()`.

## How to Run in Android Studio:
1. Extract the downloaded `.zip` archive or open the `android` directory in **Android Studio** (Ladybug / Koala / Hedgehog or newer).
2. Wait for Gradle Sync to complete.
3. Connect an Android device (via USB with USB Debugging enabled) or launch an Android Emulator.
4. Click **Run 'app'** (`Shift + F10`).
