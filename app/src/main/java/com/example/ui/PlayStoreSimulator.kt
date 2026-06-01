package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlayStoreSimulatorDialog(
    viewModel: RomBuildViewModel,
    onDismiss: () -> Unit
) {
    val screenState by viewModel.playStoreScreen.collectAsState()
    val playApps by viewModel.playApps.collectAsState()
    val selectedApp by viewModel.selectedPlayApp.collectAsState()
    val ramGb by viewModel.ramGb.collectAsState()
    val storageGb by viewModel.storageGb.collectAsState()
    val activeKernel by viewModel.kernel.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            color = Color(0xFFF8FAFC)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Play Store Header Bar
                PlayStoreHeader(
                    screenState = screenState,
                    selectedApp = selectedApp,
                    onBack = {
                        if (screenState == "APP_RUNNING") {
                            viewModel.selectPlayApp(selectedApp!!)
                        } else {
                            viewModel.goBackToPlayHome()
                        }
                    },
                    onClose = onDismiss
                )

                AnimatedContent(
                    targetState = screenState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    modifier = Modifier.weight(1f)
                ) { targetScreen ->
                    when (targetScreen) {
                        "HOME" -> {
                            PlayStoreHome(
                                playApps = playApps,
                                ramGb = ramGb,
                                storageGb = storageGb,
                                onAppClick = { viewModel.selectPlayApp(it) }
                            )
                        }
                        "DETAIL" -> {
                            selectedApp?.let { app ->
                                PlayStoreDetail(
                                    app = app,
                                    onInstall = { viewModel.installPlayApp(app.id) },
                                    onUninstall = { viewModel.uninstallPlayApp(app.id) },
                                    onOpen = { viewModel.runInstalledApp(app) }
                                )
                            }
                        }
                        "APP_RUNNING" -> {
                            selectedApp?.let { app ->
                                PlayStoreAppRunner(
                                    app = app,
                                    viewModel = viewModel,
                                    ramGb = ramGb,
                                    storageGb = storageGb,
                                    activeKernel = activeKernel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayStoreHeader(
    screenState: String,
    selectedApp: PlayApp?,
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (screenState != "HOME") {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = Color(0xFF1E293B)
                        )
                    }
                } else {
                    // Google Play Colorful Triangle Logo in vector
                    PlayStoreLogoIcon(modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Text(
                    text = when (screenState) {
                        "HOME" -> "Google Play"
                        "DETAIL" -> selectedApp?.name ?: "التفاصيل"
                        else -> selectedApp?.name ?: "تشغيل التطبيق"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 220.dp)
                )
                
                if (screenState == "HOME") {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "محاكي رسمي",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7),
                        modifier = Modifier
                            .background(Color(0xFFE0F2FE), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun PlayStoreLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, size.height / 2f)
            lineTo(0f, size.height)
            close()
        }
        
        // Simulating the dynamic triangle colors
        drawPath(
            path = path,
            color = Color(0xFF0F9D58) // Green base fall back
        )
        
        // Visual sectors overlay
        drawCircle(
            color = Color(0xFF4285F4),
            radius = size.width / 3f,
            center = Offset(size.width * 0.25f, size.height * 0.25f)
        )
        drawCircle(
            color = Color(0xFFEA4335),
            radius = size.width / 3f,
            center = Offset(size.width * 0.25f, size.height * 0.75f)
        )
        drawCircle(
            color = Color(0xFFFBBC05),
            radius = size.width / 4f,
            center = Offset(size.width * 0.7f, size.height * 0.5f)
        )
    }
}

@Composable
fun PlayStoreHome(
    playApps: List<PlayApp>,
    ramGb: Int,
    storageGb: Int,
    onAppClick: (PlayApp) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredApps = remember(searchQuery, playApps) {
        if (searchQuery.isEmpty()) playApps
        else playApps.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Play Store Search Bar Simulation
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(24.dp))
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                "البحث عن التطبيقات والألعاب...",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                        // Custom text input emulator
                        BasicTextFieldEmulator(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = Color(0xFF2563EB)
                    )
                }
            }
        }

        // Custom device specs banner inside Google Play
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "جهازك الخارق نشط الآن",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AOSP RAM: ${ramGb}GB | Storage: ${if(storageGb==1024) "1TB" else "${storageGb}GB"}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "خدمات متجر جوجل بلاي الرسمي متصلة بالنواة ومهيأة للسرعة التامة وتنزيل أدوات التقييم.",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeveloperBoard,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Tab Categories Indicators
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "لك" to true,
                    "أدوات النظام" to false,
                    "ألعاب" to false,
                    "الأكثر رواجاً" to false
                ).forEach { (tab, isSelected) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF01875F) else Color(0xFFE2E8F0))
                            .clickable { }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tab,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }
        }

        // App Catalog List Header
        item {
            Text(
                text = "التطبيقات والملفات الموصى بها لك",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Vertical custom rows of App Cards with dynamic installation states
        items(filteredApps) { app ->
            PlayStoreAppListItem(
                app = app,
                onAppClick = { onAppClick(app) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun BasicTextFieldEmulator(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Simple custom textbox input since user can type directly
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF0F172A)),
        modifier = modifier
    )
}

@Composable
fun PlayStoreAppListItem(
    app: PlayApp,
    onAppClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAppClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Dynamic icon background based on type
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when (app.iconName) {
                            "benchmark" -> Color(0xFFFFF1F2)
                            "booster" -> Color(0xFFECFDF5)
                            "files" -> Color(0xFFEFF6FF)
                            else -> Color(0xFFFDF4FF)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (app.iconName) {
                        "benchmark" -> Icons.Default.Assessment
                        "booster" -> Icons.Default.Bolt
                        "files" -> Icons.Default.FolderOpen
                        else -> Icons.Default.Gamepad
                    },
                    contentDescription = null,
                    tint = when (app.iconName) {
                        "benchmark" -> Color(0xFFE11D48)
                        "booster" -> Color(0xFF059669)
                        "files" -> Color(0xFF2563EB)
                        else -> Color(0xFFC084FC)
                    },
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.developer,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(text = app.rating, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                    Text(text = "•", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Text(text = app.downloads, fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Install State Badge
            Column(horizontalAlignment = Alignment.End) {
                if (app.isInstalled) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("مُثبّت", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                    }
                } else if (app.isInstalling) {
                    CircularProgressIndicator(
                        progress = { app.progress / 100f },
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp,
                        color = Color(0xFF01875F)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("تنزيل", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF01875F))
                    }
                }
            }
        }
    }
}

@Composable
fun PlayStoreDetail(
    app: PlayApp,
    onInstall: () -> Unit,
    onUninstall: () -> Unit,
    onOpen: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Info Card
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when (app.iconName) {
                                "benchmark" -> Color(0xFFFFF1F2)
                                "booster" -> Color(0xFFECFDF5)
                                "files" -> Color(0xFFEFF6FF)
                                else -> Color(0xFFFDF4FF)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (app.iconName) {
                            "benchmark" -> Icons.Default.Assessment
                            "booster" -> Icons.Default.Bolt
                            "files" -> Icons.Default.FolderOpen
                            else -> Icons.Default.Gamepad
                        },
                        contentDescription = null,
                        tint = when (app.iconName) {
                            "benchmark" -> Color(0xFFE11D48)
                            "booster" -> Color(0xFF059669)
                            "files" -> Color(0xFF2563EB)
                            else -> Color(0xFFC084FC)
                        },
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = app.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = app.developer,
                        fontSize = 14.sp,
                        color = Color(0xFF01875F),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = app.category,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Stats Row (Rating, Downloads, Size)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = app.rating, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                    }
                    Text("تقييم التطبيق", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE2E8F0)))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = app.downloads, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text("التنزيلات", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFE2E8F0)))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when(app.id) {
                            "benchmark" -> "٤٢ ميجابايت"
                            "booster" -> "١٢ ميجابايت"
                            "files" -> "٢٨ ميجابايت"
                            else -> "١.٨ جيجابايت"
                        }, 
                        fontSize = 15.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF1E293B)
                    )
                    Text("المساحة", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }
        }

        // Installation actions button
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (app.isInstalling) {
                    // Installing Animation Bar
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { app.progress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape),
                            color = Color(0xFF01875F),
                            trackColor = Color(0xFFE2E8F0)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "جاري التثبيت في الهاتف السحابي... ${app.progress}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF01875F)
                        )
                    }
                } else if (app.isInstalled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onUninstall,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Text("إلغاء التثبيت", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onOpen,
                            modifier = Modifier
                                .weight(1.5f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875F))
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فتح التشغيل", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = onInstall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01875F))
                    ) {
                        Text("تثبيت تطبيق", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        // Divider
        item {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE2E8F0)))
        }

        // App Detailed Description
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "حول هذا التطبيق",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = app.description,
                    fontSize = 13.sp,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )
            }
        }

        // Safety verification info
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "محمي بواسطة Google Play Protect",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "تم التحقق من سلامة التطبيق وهو خالٍ تماماً من البرمجيات الضارة.",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayStoreAppRunner(
    app: PlayApp,
    viewModel: RomBuildViewModel,
    ramGb: Int,
    storageGb: Int,
    activeKernel: String
) {
    when (app.id) {
        "benchmark" -> AntutuBenchmarkRunner(viewModel, ramGb, storageGb, activeKernel)
        "booster" -> SmartBoosterRunner(viewModel, ramGb)
        "files" -> FileBrowserRunner(storageGb)
        "game" -> PubgRunner(ramGb, activeKernel)
        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("تطبيق افتراضي مستقر يعمل بنجاح!")
            }
        }
    }
}

@Composable
fun AntutuBenchmarkRunner(
    viewModel: RomBuildViewModel,
    ramGb: Int,
    storageGb: Int,
    activeKernel: String
) {
    val isTesting by viewModel.isTestingBenchmark.collectAsState()
    val terminalOutput by viewModel.benchmarkResult.collectAsState()

    var rotationAngle by remember { mutableStateOf(0f) }

    LaunchedEffect(isTesting) {
        if (isTesting) {
            while (isTesting) {
                rotationAngle += 15f
                delay(80)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2E))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Identity Brand
        Text(
            text = "AnTuTu Benchmark v10.2",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFF5A5F),
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (terminalOutput == null) {
            // Unstarted state
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
                    .border(2.dp, Color(0xFFFF5A5F).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color(0xFFFF5A5F),
                        modifier = Modifier.size(54.dp)
                    )
                    Text(
                        "جهازك جاهز للقياس",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Text(
                text = "سعة الرام الحالية: ${ramGb}GB | التخزين: ${storageGb}GB",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { viewModel.startBenchmarkTest() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5A5F)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("بدء اختبار الأداء الأقصى 🚀", fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else if (isTesting) {
            // Live testing stage
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White.copy(alpha = 0.03f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(130.dp),
                    color = Color(0xFFFF5A5F),
                    strokeWidth = 6.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(42.dp)
                            .rotate(rotationAngle)
                    )
                    Text(
                        "يقيس الآن...",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Box(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = terminalOutput ?: "",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFF818CF8)
                    )
                }
            }
        } else if (terminalOutput?.startsWith("RESULT_SUCCESS:") == true) {
            // Test Success showing final dynamic scores
            val scoreString = terminalOutput!!.substringAfter("RESULT_SUCCESS:")
            val scoreInt = scoreString.toIntOrNull() ?: 1245900

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE11D48), Color(0xFFF43F5E))
                        ),
                        RoundedCornerShape(32.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "النتيجة النهائية الخارقة",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format("%,d", scoreInt),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "نقاط AnTuTu v10",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "أسرع من 99.4٪ من الأجهزة العالمية 👑",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Specs breakout
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CPU Core performance", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text("${(scoreInt * 0.32).toInt()} pts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("GPU 3D (العاب فائقة)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text("${(scoreInt * 0.40).toInt()} pts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ذاكرة RAM وعرض النطاق", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text("(${ramGb}GB) ${(scoreInt * 0.18).toInt()} pts", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("النواة والتعريفات", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text(activeKernel, color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Button(
                onClick = { viewModel.startBenchmarkTest() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("إعادة تشغيل الاختبار 🔄", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SmartBoosterRunner(
    viewModel: RomBuildViewModel,
    ramGb: Int
) {
    val isSweeping by viewModel.isSweepingMemory.collectAsState()
    val boostAmount by viewModel.boostAmount.collectAsState()

    var animProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(isSweeping) {
        if (isSweeping) {
            animProgress = 0f
            while (animProgress < 1f) {
                animProgress += 0.08f
                delay(90)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFECFDF5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFD1FAE5), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                "مسرع الذاكرة الذكي",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF065F46),
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (!isSweeping && boostAmount == 0) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color(0xFF10B981), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(54.dp)
                    )
                    Text("انقر لتنظيف الرام", fontSize = 11.sp, color = Color(0xFF065F46))
                }
            }

            Text(
                text = "جهازك المخصص: ${ramGb}GB RAM كلي",
                color = Color(0xFF065F46),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { viewModel.startSmartBoost() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("تنظيف فوري للذاكرة 🧹", fontWeight = FontWeight.Bold)
            }
        } else if (isSweeping) {
            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF10B981),
                    strokeWidth = 6.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Cached,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(42.dp)
                    )
                    Text("تحرير الذاكرة...", fontSize = 11.sp, color = Color(0xFF065F46), fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "جاري تصفية العمليات المهدرة وقفل مخازن الكاش...",
                color = Color(0xFF047857),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        } else {
            // Boost Success
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color.White, CircleShape)
                    .border(3.dp, Color(0xFF059669), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(54.dp)
                    )
                    Text("نشط ومثالي", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "تم تحرير $boostAmount ميجابايت بنجاح!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF065F46),
                textAlign = TextAlign.Center
            )

            Text(
                text = "تم غلق ١٢ عملية بالخلفية لزيادة أداء الرام ${ramGb}GB.",
                fontSize = 12.sp,
                color = Color(0xFF047857),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { viewModel.startSmartBoost() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("تنظيف مرة أخرى 🔄", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FileBrowserRunner(storageGb: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Storage Status Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("حالة مساحة التخزين الداخلية", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "٢٤.٥ جيجابايت مستخدمة",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "من أصل ${storageGb}GB",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFE2E8F0), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = 24.5f / storageGb)
                            .background(Color(0xFF3B82F6), CircleShape)
                    )
                }
            }
        }

        Text("المجلدات الرئيسية للنظام:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(
                listOf(
                    "Download (التحميلات)" to "٢.١ جيجابايت • ٤٨ ملف",
                    "Documents (المستندات)" to "٤٠ ميجابايت • ١٢ ملف",
                    "AOSP Rom Builds" to "٦.٤ جيجابايت • ٣ ملفات (.zip)",
                    "Google Services Cache" to "١٠٢ ميجابايت • كاش منظم",
                    "PUBG Game Files" to "١٥.٨ جيجابايت • ملفات obb"
                )
            ) { (folder, details) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(folder, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text(details, fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}

@Composable
fun PubgRunner(
    ramGb: Int,
    activeKernel: String
) {
    var hasBoostedFps by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pubg game badge simulator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFD97706), Color(0xFF0F172A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "PUBG Mobile Emulator Pro",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "أداء رسوميات اللعبة بناءً على عتادك:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("مستوى الرسوميات:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text("Ultra HD (فائقة الوضوح)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("معدل الإطارات:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(if (hasBoostedFps) "90 FPS (ثابت وجبار)" else "60 FPS (مستقر جداً)", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("استخدام الرام:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text("تم حجز ٦ جيجابايت من ${ramGb}GB كلي", color = Color.White, fontSize = 12.sp)
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("كفائة النواة والتعريفات:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Text(activeKernel, color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (!hasBoostedFps) {
            Button(
                onClick = { hasBoostedFps = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("تفعيل قوة 90 إطار (FPS Booster) ⚡", fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillModifier()
                    .background(Color(0xFF15803D).copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF15803D), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "اكتمل تفعيل وضع 90fps الخارق بنجاح! اللعبة تعمل الآن بأقصى سلاسة ووضوح UHD بفضل تجميع الرام الكبيرة والتعريفات المرنة للنواة.",
                    color = Color(0xFF4ADE80),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun Modifier.fillModifier(): Modifier = this.fillMaxWidth()
