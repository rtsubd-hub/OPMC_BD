package com.example.webviewapp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

// Target Google Apps Script Web App URL
private const val TARGET_URL =
    "https://script.google.com/macros/s/AKfycbzpvP1pr0XB8OFYXDaJOvFZaEvSpBL2S4WBNoQQMbEnqSq4DmNm8XriqPYKR3T56G6g/exec"

/**
 * Single-Activity Android Application using Jetpack Compose
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge display support for full-screen immersive experience
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                FullScreenWebView(url = TARGET_URL)
            }
        }
    }
}

/**
 * Full-screen Compose WebView with JavaScript, DOM storage,
 * history-aware BackHandler, and a centered loading spinner.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FullScreenWebView(
    url: String,
    modifier: Modifier = Modifier
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Intercept system back button: navigate back in WebView history if available
    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        // AndroidView bridges the native Android WebView into Jetpack Compose
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // WebSettings configuration
                    settings.apply {
                        javaScriptEnabled = true            // [Req] Enable JavaScript execution
                        domStorageEnabled = true            // [Req] Enable DOM Storage (localStorage/sessionStorage)
                        databaseEnabled = true              // Enable HTML5 Web DB/IndexedDB
                        loadWithOverviewMode = true         // Scale content to fit screen width
                        useWideViewPort = true              // Respect <meta name="viewport">
                        cacheMode = WebSettings.LOAD_DEFAULT// Standard cache policy
                        allowFileAccess = false             // Security hygiene: disable local file access
                        allowContentAccess = false          // Security hygiene: disable content providers
                    }

                    // Cookie persistence for web session state
                    CookieManager.getInstance().apply {
                        setAcceptCookie(true)
                        setAcceptThirdPartyCookies(this@apply, true)
                    }

                    // WebViewClient to handle page load lifecycle and history state
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            canGoBack = view?.canGoBack() == true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            canGoBack = view?.canGoBack() == true
                        }

                        override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                            super.doUpdateVisitedHistory(view, url, isReload)
                            canGoBack = view?.canGoBack() == true
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            isLoading = false
                            canGoBack = view?.canGoBack() == true
                        }
                    }

                    // WebChromeClient to track granular progress updates
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            isLoading = newProgress < 100
                            canGoBack = view?.canGoBack() == true
                        }
                    }

                    // Initial URL load
                    loadUrl(url)
                    webView = this
                }
            },
            update = { view ->
                webView = view
            },
            modifier = Modifier.fillMaxSize()
        )

        // [Req] Show a loading spinner centered while the page loads
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Clean up WebView instance when leaving composition to prevent memory leaks
    DisposableEffect(Unit) {
        onDispose {
            webView?.apply {
                stopLoading()
                clearHistory()
                removeAllViews()
                destroy()
            }
            webView = null
        }
    }
}
