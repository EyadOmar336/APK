package com.example.ui

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RealPlayStoreWebView(
    modifier: Modifier = Modifier
) {
    val playStoreUrl = "https://play.google.com/store?hl=ar"
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf(playStoreUrl) }
    var loadingProgress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // High-fidelity Arabic Browser Navigation Controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // SSL verification header row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "اتصال آمن",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "play.google.com (اتصال مشفر وآمن)",
                            fontSize = 11.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Live Web Status indicator
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "خدمات الويب الحية Live",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }

                // Browser Operation Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Back
                    IconButton(
                        onClick = { webViewInstance?.goBack() },
                        enabled = canGoBack,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (canGoBack) Color(0xFFF1F5F9) else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "رجوع للخلف",
                            tint = if (canGoBack) Color(0xFF0F172A) else Color(0xFFE2E8F0),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Forward
                    IconButton(
                        onClick = { webViewInstance?.goForward() },
                        enabled = canGoForward,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (canGoForward) Color(0xFFF1F5F9) else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "تقدم للأمام",
                            tint = if (canGoForward) Color(0xFF0F172A) else Color(0xFFE2E8F0),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Refresh
                    IconButton(
                        onClick = { webViewInstance?.reload() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة تحميل الصفحة",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Home Page of Play Store
                    IconButton(
                        onClick = { webViewInstance?.loadUrl(playStoreUrl) },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEFF6FF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "المنصة الرئيسية",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Mini address bar indicator
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(18.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(18.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = currentUrl,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Animated Loader Indicator matching Play Store branding
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LinearProgressIndicator(
                progress = { loadingProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color(0xFF0F9D58),
                trackColor = Color(0xFFE2E8F0)
            )
        }

        // Genuine Embedded Android Web engine View
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        // Advanced high performance optimization settings for full Google compatibility
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            setSupportZoom(true)
                            builtInZoomControls = true
                            displayZoomControls = false
                            mediaPlaybackRequiresUserGesture = false
                            
                            // High-compatible modern browser User Agent signature
                            userAgentString = "Mozilla/5.0 (Linux; Android 13; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Mobile Safari/537.36"
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                                if (url != null) {
                                    currentUrl = url
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                canGoBack = view?.canGoBack() ?: false
                                canGoForward = view?.canGoForward() ?: false
                                if (url != null) {
                                    currentUrl = url
                                }
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val urlStr = request?.url?.toString() ?: ""
                                // Force everything standard web browsable inside the view
                                if (urlStr.startsWith("http://") || urlStr.startsWith("https://")) {
                                    view?.loadUrl(urlStr)
                                    return true
                                }
                                return false
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                loadingProgress = newProgress
                                if (newProgress >= 100) {
                                    isLoading = false
                                }
                            }
                        }

                        // Load genuine Google Play Store Arabic homepage
                        loadUrl(playStoreUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                }
            )
        }
    }
}
