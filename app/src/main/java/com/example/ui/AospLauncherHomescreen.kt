package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AospLauncherHomescreen(
    playApps: List<PlayApp>,
    kernel: String,
    ramGb: Int,
    storageGb: Int,
    onOpenPlayStore: () -> Unit,
    onOpenChrome: () -> Unit,
    onOpenYoutube: () -> Unit,
    onOpenGmail: () -> Unit,
    onOpenPhotos: () -> Unit,
    onOpenDialer: () -> Unit,
    onOpenApp: (PlayApp) -> Unit,
    onPowerOff: () -> Unit
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        while (true) {
            val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val sdfDate = SimpleDateFormat("EEEE, MMMM dd", Locale("ar"))
            val now = Date()
            currentTime = sdfTime.format(now)
            currentDate = sdfDate.format(now)
            delay(1000)
        }
    }

    // High fidelity premium dark space-star-gradient wallpaper matching modern Pixel look
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Cosmic blue-black
                        Color(0xFF1E1B4B), // Velvet violet
                        Color(0xFF090D16)  // Pitch deep black
                    )
                )
            )
    ) {
        // Decorative background nebula circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                radius = size.width * 0.6f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.1f, size.height * 0.2f)
            )
            drawCircle(
                color = Color(0xFFD946EF).copy(alpha = 0.12f),
                radius = size.width * 0.5f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.9f, size.height * 0.7f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // 1. Simulated Android Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Network indicators
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "5G",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wifi",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.NetworkCell,
                        contentDescription = "Signal",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Center status notifier
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AOSP ROM Pro 🚀",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Power & Battery
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = "Battery",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(14.dp)
                    )
                    
                    // Power off shortcut
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Power Off",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onPowerOff() }
                    )
                }
            }

            // 2. Beautiful Google Pixel Clock Widget
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentTime.ifEmpty { "02:24 م" },
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = currentDate.ifEmpty { "الاثنين، 1 يونيو" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Microchip info chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "RAM: ${ramGb}GB",
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Kernel: ${kernel.replace("AOSP Linux Kernel", "Linux")}",
                            fontSize = 10.sp,
                            color = Color(0xFFF472B6),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 3. Pixel Floating Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clickable { onOpenChrome() },
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Colored G Logo imitation
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFFEA4335), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "البحث في الويب والهاتف...",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "البحث الصوتي",
                            tint = Color(0xFF4285F4),
                            modifier = Modifier.size(18.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "عدسة جوجل",
                            tint = Color(0xFFFBBC05),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 4. Launcher App Grid (Builtin + Installed)
            Text(
                text = "التطبيقات والخدمات النشطة",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Define Builtin apps list
            val coreApps = listOf(
                LauncherAppItem("store", "متجر بلاي", Icons.Default.Shop, Color(0xFF0F9D58), action = onOpenPlayStore),
                LauncherAppItem("browser", "كروم ويب", Icons.Default.Language, Color(0xFF4285F4), action = onOpenChrome),
                LauncherAppItem("youtube", "يوتيوب", Icons.Default.VideoLibrary, Color(0xFFFF0000), action = onOpenYoutube),
                LauncherAppItem("gmail", "جيميل", Icons.Default.Email, Color(0xFFEA4335), action = onOpenGmail),
                LauncherAppItem("photos", "المعرض", Icons.Default.Photo, Color(0xFFF7B500), action = onOpenPhotos),
                LauncherAppItem("dialer", "الهاتف", Icons.Default.Phone, Color(0xFF0F9D58), action = onOpenDialer)
            )

            // Dynamic installed apps list from store
            val installedStoreApps = playApps.filter { it.isInstalled }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add Core Builtin Apps
                items(coreApps) { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { app.action() }
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(4.dp, CircleShape)
                                .background(Color.White, CircleShape)
                                .border(1.dp, app.brandColor.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = app.icon,
                                contentDescription = app.name,
                                tint = app.brandColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = app.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Add Installed Play Store Apps dynamically
                items(installedStoreApps) { app ->
                    val brandColor = when (app.id) {
                        "benchmark" -> Color(0xFFFF5A5F)
                        "booster" -> Color(0xFF2563EB)
                        "files" -> Color(0xFF0F9D58)
                        "game" -> Color(0xFFEAB308)
                        "freefire" -> Color(0xFFF97316)
                        "subway" -> Color(0xFF8B5CF6)
                        else -> Color(0xFF64748B)
                    }
                    val appIcon = when (app.id) {
                        "benchmark" -> Icons.Default.Bolt
                        "booster" -> Icons.Default.Speed
                        "files" -> Icons.Default.FolderOpen
                        "game" -> Icons.Default.Gamepad
                        "freefire" -> Icons.Default.Whatshot
                        "subway" -> Icons.Default.DirectionsRun
                        else -> Icons.Default.Android
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onOpenApp(app) }
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(6.dp, CircleShape)
                                .background(Color.White, CircleShape)
                                .border(1.5.dp, brandColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = appIcon,
                                contentDescription = app.name,
                                tint = brandColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = app.name.split(" ")[0], // short name
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 5. Classic Glassmorphic Desktop Dock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 8.dp)
                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(24.dp))
                    .padding(vertical = 12.dp, horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DockIconItem(Icons.Default.Phone, "Phone", Color(0xFF10B981), onOpenDialer)
                    DockIconItem(Icons.Default.ChatBubble, "Messages", Color(0xFF3B82F6), {
                        onOpenGmail() // link messages to a custom inbox notification
                    })
                    DockIconItem(Icons.Default.Language, "Chrome", Color(0xFFFBBF24), onOpenChrome)
                    DockIconItem(Icons.Default.Shop, "Play Store", Color(0xFF10B981), onOpenPlayStore)
                }
            }
        }
    }
}

data class LauncherAppItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val brandColor: Color,
    val action: () -> Unit
)

@Composable
fun DockIconItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .shadow(4.dp, CircleShape)
            .background(Color.White, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ==========================================
// MOCK APPS SCREENS IMPLEMENTATIONS
// ==========================================

@Composable
fun YouTubeAppSimulator(onClose: () -> Unit) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableStateOf(0.4f) }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (currentProgress < 1f) {
                delay(300)
                currentProgress += 0.01f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        // Red Top Bar Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "YouTube Pro",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        // Active Player Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.77f)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (isPlaying) {
                // Animated wave representing video rendering
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        val midY = size.height / 2f
                        moveTo(0f, midY)
                        for (x in 0..size.width.toInt() step 5) {
                            val y = midY + kotlin.math.sin(x.toFloat() * 0.05f + System.currentTimeMillis() * 0.015f) * 30f
                            lineTo(x.toFloat(), y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color.Red.copy(alpha = 0.5f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Click Overlay target
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isPlaying = !isPlaying }
            )

            // Progress Bar
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(4.dp),
                color = Color.Red,
                trackColor = Color.DarkGray
            )
        }

        // Information Meta
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "مراجعة نظام AOSP ROM المطور الموجه لكيل الألعاب وتقييم الأداء الخارق 🚀",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("المطور: AOSP Build Node 01", color = Color.Gray, fontSize = 11.sp)
                Text("١.٢ مليون مشاهدة", color = Color.Gray, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Channels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF2563EB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("AOSP Masters", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("500K Subscribers", color = Color.Gray, fontSize = 10.sp)
                    }
                }
                
                Button(
                    onClick = { /* Subscribed toast */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) {
                    Text("اشترك الآن", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun GmailAppSimulator(kernel: String, ramGb: Int, storageGb: Int, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Red Box Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEA4335))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("علبة الوارد (Gmail)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        // Email Listing
        val emails = listOf(
            GmailItem(
                sender = "فريق تجميع AOSP الروم",
                title = "تم تجميع الروم الخاص بك بنجاح! 🎉",
                content = "مرحباً يا بطل، لقد قمنا بتنزيل وتجميع كافة ملفات الروم flashable_rom.zip بنجاح تام وفق راماتك المميزة (وحجزنا ${ramGb}GB رتل افتراضي) وعينّا النواة بوضع فائق السرعة واستقرار البطارية.",
                time = "الآن"
            ),
            GmailItem(
                sender = "Google Safety Check",
                title = "حماية Google Play Protect نشطة 🛡️",
                content = "تم فحص نظامك بالكامل ومطابقة التوقيع، جهازك يعتبر آمناً ومحمياً بنسبة 100% ومتوافق تماماً مع خدمات جوجل سيرفس دون الحاجة لروت.",
                time = "منذ ١٠ د"
            ),
            GmailItem(
                sender = "Linux Kernel Mainline",
                title = "تحديث النواة: $kernel قيد التشغيل",
                content = "النواة نشطة الآن ومزودة بخاصية كسر السرعة لزيادة معدل الإطارات في ألعاب كـ PUBG Mobile و Free Fire إلى 90FPS مستقرة بدون أي تذبذب أو تقطيع حراري.",
                time = "منذ ساعة"
            ),
            GmailItem(
                sender = "الفريق المطور AOSP Node",
                title = "إعداد التخزين: تم توسيع السعة لـ ${if(storageGb==1024) "1TB" else "${storageGb}GB"} 💾",
                content = "النظام يعرض الآن السعة المجمعة بالكامل بشكل حقيقي في مدير الملفات ومتاحة فورياً لكتابة الملفات وتخزين الـ APKs الكبيرة.",
                time = "منذ ساعتين"
            )
        )

        Divider(color = Color(0xFFE2E8F0))

        LazyColumn {
            items(emails) { email ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* expand/read email */ }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(email.sender, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF1E293B))
                        Text(email.time, fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                    Text(email.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF475569), modifier = Modifier.padding(top = 4.dp))
                    Text(email.content, fontSize = 11.sp, color = Color(0xFF64748B), maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 4.dp))
                }
                Divider(color = Color(0xFFF1F5F9))
            }
        }
    }
}

data class GmailItem(
    val sender: String,
    val title: String,
    val content: String,
    val time: String
)

@Composable
fun PhotosAppSimulator(ram: Int, storage: Int, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Photo, contentDescription = null, tint = Color(0xFF3B82F6))
                Spacer(modifier = Modifier.width(8.dp))
                Text("معرض الصور (Google Photos)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
            }
        }

        Divider()

        Column(modifier = Modifier.padding(16.dp)) {
            Text("المجلدات المحلية والجهاز", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))

            val photoItems = listOf(
                PhotoCard("AOSP Kernel Logs", "شاشة اللوغ البرمجية للنواة والذاكرة المفتوحة.", Color(0xFF1E1E2E)),
                PhotoCard("PUBG Screenshot", "لقطة شاشة للعبة ببجي بمعدل 90 إطار بجودة UHD.", Color(0xFF0D0D12)),
                PhotoCard("Google Play Protect", "لقطة فحص التطبيقات الخالية من البرامج الضارة.", Color(0xFF047857)),
                PhotoCard("Memory Allocation Map", "شكل الرام المقسمة ${ram}GB مع التخزين ${storage}GB.", Color(0xFF4338CA))
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(photoItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(item.color)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "🖼️\n${item.title}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.align(Alignment.TopStart)
                            )
                            Text(
                                text = item.desc,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.BottomStart)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class PhotoCard(val title: String, val desc: String, val color: Color)

@Composable
fun DialerAppSimulator(
    ram: Int,
    storage: Int,
    kernel: String,
    onClose: () -> Unit
) {
    var dialNumber by remember { mutableStateOf("") }
    var displayedInfo by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F9D58))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("لوحة الاتصال وتجربة الهاتف", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        // Dialer Display Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (displayedInfo != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "💡 الرموز السرية للنظام الروم المجمع:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            displayedInfo!!,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { displayedInfo = null; dialNumber = "" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
                        ) {
                            Text("رجوع للوحة المفاتيح")
                        }
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dialNumber,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "جرب طلب الرموز السرية التالية:\n" +
                                "*#06# لرؤية الـ IMEI ونواة الروم المخصص\n" +
                                "*#*#4636#*#* لفحص الرامات والتخزين النشط",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (displayedInfo == null) {
            // Dialer Keys Panel
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("*", "0", "#")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE2E8F0))
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                keys.forEach { rowKeys ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowKeys.forEach { key ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .shadow(2.dp, CircleShape)
                                    .background(Color.White, CircleShape)
                                    .clickable {
                                        if (dialNumber.length < 16) {
                                            dialNumber += key
                                        }
                                        
                                        // Trigger Code Action check
                                        if (dialNumber == "*#06#") {
                                            displayedInfo = "AOSP ROM IMEI: 35898510842${(100..999).random()}\n" +
                                                    "AOSP Device Core: Node 01 - Frankfurt\n" +
                                                    "System Kernel: $kernel"
                                        } else if (dialNumber == "*#*#4636#*#*") {
                                            displayedInfo = "[بيانات الذاكرة الدقيقة لجهازك]\n" +
                                                    "سرعة المعالج: 3.2 GHz High-Speed\n" +
                                                    "الذاكرة العشوائية: ${ram}GB DDR5 Virtualized\n" +
                                                    "شبكة الاتصال: 5G LTE Active\n" +
                                                    "الحالة الحرارية: 34°C (مستقر جداً)"
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    key,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(50.dp))
                    // Call Button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .shadow(4.dp, CircleShape)
                            .background(Color(0xFF0F9D58), CircleShape)
                            .clickable {
                                if (dialNumber.isNotEmpty()) {
                                    displayedInfo = "جاري الاتصال بـ $dialNumber...\n" +
                                            "سرعة مكالمة فائقة مدعومة بـ VoLTE على نظام AOSP المبني بنجاح."
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                    }
                    // Delete Button
                    IconButton(onClick = { if (dialNumber.isNotEmpty()) dialNumber = dialNumber.dropLast(1) }) {
                        Icon(Icons.Default.Backspace, contentDescription = "Delete", tint = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}
