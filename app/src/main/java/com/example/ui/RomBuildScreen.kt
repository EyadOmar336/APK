package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontFamily

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RomBuildScreen(
    viewModel: RomBuildViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ramGb by viewModel.ramGb.collectAsState()
    val storageGb by viewModel.storageGb.collectAsState()
    val activeKernel by viewModel.kernel.collectAsState()
    val playServices by viewModel.playServices.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()
    
    val screenState by viewModel.playStoreScreen.collectAsState()
    val playApps by viewModel.playApps.collectAsState()
    val selectedApp by viewModel.selectedPlayApp.collectAsState()
    
    val isRecentsOpen by viewModel.isRecentsOpen.collectAsState()
    val recentApps by viewModel.recentApps.collectAsState()
    
    val isCompiling by viewModel.isCompiling.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val logLines by viewModel.logLines.collectAsState()
    val lastCompiledBuild by viewModel.lastCompiledBuild.collectAsState()
    val buildHistory by viewModel.buildHistory.collectAsState()
    val currentZipName by viewModel.currentZipName.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Build, 1: History

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Render simulation content vs configuration dashboard with beautiful animated transitions
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = screenState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(350))
                    },
                    modifier = Modifier.fillMaxSize()
                ) { targetScreen ->
                    when (targetScreen) {
                        "BUILDER" -> {
                            // Custom AOSP ROM Construction Dashboard
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                // 1. Title Header Block
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, bottom = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "AOSP ROM Builder ⚙️",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp
                                        )
                                        Text(
                                            text = "لوحة بناء وتجميع نظام أندرويد المخصص | AOSP Client",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "V14 Stable",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // 2. Selector Tabs row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                        .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                                        .padding(4.dp)
                                ) {
                                    TabButton(
                                        text = "تعديل وبناء الروم Core",
                                        isSelected = activeTab == 0,
                                        modifier = Modifier.weight(1f),
                                        onClick = { activeTab = 0 }
                                    )
                                    
                                    TabButton(
                                        text = "متجر Google Play الحقيقي 🌐",
                                        isSelected = activeTab == 1,
                                        modifier = Modifier.weight(1f),
                                        onClick = { activeTab = 1 }
                                    )
                                    
                                    TabButton(
                                        text = "أرشيف Zip 🗂️",
                                        isSelected = activeTab == 2,
                                        modifier = Modifier.weight(0.8f),
                                        onClick = { activeTab = 2 }
                                    )
                                }

                                // 3. Tab Area Body Content
                                Box(modifier = Modifier.weight(1f)) {
                                    when (activeTab) {
                                        0 -> BuilderSetupTab(
                                            ramGb = ramGb,
                                            storageGb = storageGb,
                                            activeKernel = activeKernel,
                                            playServices = playServices,
                                            selectedApps = selectedApps,
                                            isCompiling = isCompiling,
                                            progress = progress,
                                            logLines = logLines,
                                            lastCompiledBuild = lastCompiledBuild,
                                            currentZipName = currentZipName,
                                            onUpdateRam = { viewModel.updateRam(it) },
                                            onUpdateStorage = { viewModel.updateStorage(it) },
                                            onUpdateKernel = { viewModel.updateKernel(it) },
                                            onToggleGms = { viewModel.togglePlayServices() },
                                            onToggleApp = { viewModel.toggleApp(it) },
                                            onCompile = { viewModel.compileAndGenerateRom() },
                                            onBoot = { viewModel.bootCustomRom() }
                                        )
                                        1 -> PlayStoreWebLanding(
                                            onLaunchMockSearch = {
                                                viewModel.openChrome()
                                            }
                                        )
                                        2 -> CompilationHistoryTab(
                                            buildHistory = buildHistory,
                                            onBootBuild = { build ->
                                                viewModel.updateRam(build.ramGb)
                                                viewModel.updateStorage(build.storageGb)
                                                viewModel.updateKernel(build.kernel)
                                                viewModel.bootCustomRom()
                                            },
                                            onDeleteBuild = { id ->
                                                viewModel.deleteBuild(id)
                                            },
                                            onClearAll = {
                                                viewModel.clearBuildHistory()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        "LAUNCHER" -> {
                            AospLauncherHomescreen(
                                playApps = playApps,
                                kernel = activeKernel,
                                ramGb = ramGb,
                                storageGb = storageGb,
                                onOpenPlayStore = { viewModel.openPlayStore() },
                                onOpenChrome = { viewModel.openChrome() },
                                onOpenYoutube = { viewModel.openYoutube() },
                                onOpenGmail = { viewModel.openGmail() },
                                onOpenPhotos = { viewModel.openPhotos() },
                                onOpenDialer = { viewModel.openDialer() },
                                onOpenApp = { app -> viewModel.runInstalledApp(app) },
                                onPowerOff = {
                                    android.widget.Toast.makeText(context, "🔄 إعادة التشغيل والرجوع إلى لوحة البناء والرام...", android.widget.Toast.LENGTH_SHORT).show()
                                    viewModel.goToBuilder()
                                },
                                onGoToBuilder = {
                                    viewModel.goToBuilder()
                                }
                            )
                        }
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
                        "CHROME" -> {
                            RealPlayStoreWebView(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        "YOUTUBE" -> {
                            YouTubeAppSimulator(
                                onClose = { viewModel.goHome() }
                            )
                        }
                        "GMAIL" -> {
                            GmailAppSimulator(
                                kernel = activeKernel,
                                ramGb = ramGb,
                                storageGb = storageGb,
                                onClose = { viewModel.goHome() }
                            )
                        }
                        "PHOTOS" -> {
                            PhotosAppSimulator(
                                ram = ramGb,
                                storage = storageGb,
                                onClose = { viewModel.goHome() }
                            )
                        }
                        "DIALER" -> {
                            DialerAppSimulator(
                                ram = ramGb,
                                storage = storageGb,
                                kernel = activeKernel,
                                onClose = { viewModel.goHome() }
                            )
                        }
                    }
                }
            }

            // Custom Hardware System Navigation Bar (software keys) at bottom of screen
            // ONLY shown when inside simulated OS views to logical separate from the Configuration Dashboard.
            if (screenState != "BUILDER") {
                SystemNavigationBar(
                    onBack = {
                        if (screenState == "APP_RUNNING") {
                            val sel = selectedApp
                            if (sel != null) {
                                viewModel.selectPlayApp(sel)
                            } else {
                                viewModel.goBackToPlayHome()
                            }
                        } else if (screenState == "DETAIL") {
                            viewModel.goBackToPlayHome()
                        } else if (screenState == "HOME") {
                            viewModel.goHome()
                        } else if (screenState != "LAUNCHER") {
                            viewModel.goHome()
                        }
                    },
                    onHome = {
                        viewModel.closeRecents()
                        viewModel.goHome()
                    },
                    onRecents = {
                        if (viewModel.isRecentsOpen.value) {
                            viewModel.closeRecents()
                        } else {
                            viewModel.openRecents()
                        }
                    }
                )
            }
        }

        // Overlay Multitasking switcher
        if (isRecentsOpen) {
            RecentsTaskSwitcher(
                recentApps = recentApps,
                ramGb = ramGb,
                activeKernel = activeKernel,
                onClose = { viewModel.closeRecents() },
                onAppSelect = { app ->
                    viewModel.closeRecents()
                    viewModel.runInstalledApp(app)
                },
                onRemoveApp = { appId ->
                    viewModel.removeRecentApp(appId)
                },
                onClearAll = {
                    viewModel.clearRecents()
                    android.widget.Toast.makeText(context, "🧹 تم تنظيف الرام وإغلاق كافة المهام النشطة!", android.widget.Toast.LENGTH_SHORT).show()
                    viewModel.closeRecents()
                }
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFF38BDF8) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF090D16) else Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BuilderSetupTab(
    ramGb: Int,
    storageGb: Int,
    activeKernel: String,
    playServices: Boolean,
    selectedApps: Set<String>,
    isCompiling: Boolean,
    progress: Int,
    logLines: List<String>,
    lastCompiledBuild: com.example.data.RomBuild?,
    currentZipName: String,
    onUpdateRam: (Int) -> Unit,
    onUpdateStorage: (Int) -> Unit,
    onUpdateKernel: (String) -> Unit,
    onToggleGms: () -> Unit,
    onToggleApp: (String) -> Unit,
    onCompile: () -> Unit,
    onBoot: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isCompiling && lastCompiledBuild == null) {
            // RAM Config
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = Color(0xFF38BDF8))
                            Text("الذاكرة العشوائية (RAM)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text("${ramGb} GB RAM", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(8, 12, 16, 24).forEach { size ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (ramGb == size) Color(0xFF38BDF8) else Color(0xFF1E293B))
                                    .clickable { onUpdateRam(size) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$size GB",
                                    color = if (ramGb == size) Color(0xFF090D16) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Storage Config
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = Color(0xFF10B981))
                            Text("مساحة التخزين الداخلية", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text(if (storageGb == 1024) "1 TB" else "$storageGb GB", color = Color(0xFF10B981), fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(128, 256, 512, 1024).forEach { size ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (storageGb == size) Color(0xFF10B981) else Color(0xFF1E293B))
                                    .clickable { onUpdateStorage(size) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (size == 1024) "1 TB" else "$size GB",
                                    color = if (storageGb == size) Color(0xFF090D16) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Kernel Config
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("نواة النظام التشغيلي (AOSP Kernel)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    listOf(
                        "AOSP Linux Kernel 6.1" to "نواة مستقرة مدمجة مع تعريفات مرنة",
                        "KernelSU Root Ready" to "نواة مجهزة بروت ومستويات حماية قابلة للتعديل",
                        "Custom Game-Enhancer Kernel" to "نواة كسر السرعة لإعطاء أقصى أداء مع الألعاب"
                    ).forEach { (kernelType, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (activeKernel == kernelType) Color(0xFF1E293B) else Color.Transparent)
                                .border(1.dp, if (activeKernel == kernelType) Color(0xFF6366F1) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                .clickable { onUpdateKernel(kernelType) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = activeKernel == kernelType,
                                onClick = { onUpdateKernel(kernelType) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6366F1), unselectedColor = Color(0xFF64748B))
                            )
                            
                            Column {
                                Text(kernelType, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(desc, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // Google Services Toggle (Vanilla vs GMS)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shop, contentDescription = null, tint = Color(0xFFEF4444))
                            Text("تضمين خدمات Google Play (GMS)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text("يتيح تشغيل متجر جوجل بلاي الرسمي ومزامنة بيانات الألعاب والتطبيقات بسلاسة.", color = Color(0xFF94A3B8), fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }

                    Switch(
                        checked = playServices,
                        onCheckedChange = { onToggleGms() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFEF4444), checkedTrackColor = Color(0xFFEF4444).copy(alpha = 0.3f))
                    )
                }
            }

            // Core App Bundles checklist
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("التطبيقات والخدمات مسبقة التضمين", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    listOf(
                        "متصفح خفيف (Browser)" to "لتصفح الويب الآمن والخفيف في النظام",
                        "مدير ملفات سحابي (File Manager)" to "لتوليد وإدارة الملفات ونقلها داخلياً",
                        "مشغل وسائط (Media Player)" to "لتشغيل الصوت ومقاطع الفيديو بأعلى دقة",
                        "F-Droid App Store" to "لتنزيل التطبيقات والحزم مفتوحة المصدر بدون قيود"
                    ).forEach { (appName, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleApp(appName) }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedApps.contains(appName),
                                onCheckedChange = { onToggleApp(appName) },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF38BDF8), uncheckedColor = Color(0xFF64748B))
                            )
                            Column {
                                Text(appName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(desc, color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            // Compilation Trigger Box Buttons
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onCompile,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.OfflineBolt, contentDescription = null, tint = Color(0xFF090D16))
                        Text("صنع وتجميع ملف ROM ⚡", color = Color(0xFF090D16), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                OutlinedButton(
                    onClick = onBoot,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF10B981))
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF10B981))
                        Text("تخطي والتشغيل الفوري للمحاكي 🚀", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        } else if (isCompiling) {
            // Compilation Terminal Log and Loading Animation
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                progress = { progress / 100f },
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF38BDF8),
                                strokeWidth = 2.dp
                            )
                            Text("جاري معالجة وتجميع ملف الـ ROM...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text("$progress%", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }

                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF38BDF8),
                        trackColor = Color(0xFF1E293B)
                    )

                    Text(
                        "ملف الاستهداف: $currentZipName",
                        color = Color(0xFF38BDF8).copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Logs panel
                    Divider(color = Color.White.copy(alpha = 0.05f))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(Color.Black)
                            .padding(12.dp)
                    ) {
                        LazyColumn(reverseLayout = true, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(logLines.reversed()) { line ->
                                Text(
                                    text = line,
                                    color = if (line.contains("SUCCESS") || line.contains("OK")) Color(0xFF10B981) else if (line.contains("SYS")) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Success Compilation Screen! Output result
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text("اكتمل بناء الروم بنجاح! 🎉", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("تم تجميع حزم أندرويد 14 بنجاح وتوليد الروم المخصصة القابلة للتفليش والتمهيد.", color = Color(0xFF94A3B8), fontSize = 12.sp, textAlign = TextAlign.Center)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoLine("الملف المتولد", lastCompiledBuild?.buildName ?: "custom_aosp.zip")
                            InfoLine("مساحة الرام", "${lastCompiledBuild?.ramGb ?: 16} GB")
                            InfoLine("حجم التخزين", "${if(lastCompiledBuild?.storageGb == 1024) "1 TB" else "${lastCompiledBuild?.storageGb ?: 512} GB"}")
                            InfoLine("نوع النواة", lastCompiledBuild?.kernel ?: "AOSP Linux Kernel")
                            InfoLine("GMS خدمات", if (lastCompiledBuild?.playServices == true) "مدمجة" else "Vanilla (خالية)")
                        }
                    }

                    Button(
                        onClick = onBoot,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Text("تمهيد وتشغيل الروم المجمعة في المحاكي 🚀", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PlayStoreWebLanding(
    onLaunchMockSearch: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF2563EB).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = "متجر Google Play الحقيقي 🌐",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "يدعم التطبيق الوصول والربط لجميع حزم APK الرسمية وتنزيلها الحقيقي على هاتفك ومحاكاة محرك متجر جوجل بلاي المباشر عبر خدمات الويب.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Feature pills
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillItem("اتصال آمن SSL 🔒")
                    PillItem("بحث مباشر Live Search")
                    PillItem("دعم APK الرسمي 📦")
                }
            }

            Button(
                onClick = onLaunchMockSearch,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = Color.White)
                    Text("فتح متجر جوجل بلاي وتصفح التطبيقات الآن 🌐", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun PillItem(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CompilationHistoryTab(
    buildHistory: List<com.example.data.RomBuild>,
    onBootBuild: (com.example.data.RomBuild) -> Unit,
    onDeleteBuild: (Int) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("الرومات المجمعة مسبقاً (${buildHistory.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (buildHistory.isNotEmpty()) {
                TextButton(onClick = onClearAll) {
                    Text("حذف السجل", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        if (buildHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        imageVector = Icons.Default.LayersClear,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "لا توجد رومات مجمعة حالياً.\nقم بالانتقال إلى تبويب التعديل وبناء روم جديد وبدء البرمجة والتجميع!",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(buildHistory) { rom ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.OfflineBolt, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                    Text(rom.buildName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                IconButton(onClick = { onDeleteBuild(rom.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                MiniSpecChip("${rom.ramGb} GB RAM")
                                MiniSpecChip(if (rom.storageGb == 1024) "1 TB" else "${rom.storageGb} GB")
                                MiniSpecChip(rom.kernel.split(" ").lastOrNull() ?: "Standard")
                            }

                            Button(
                                onClick = { onBootBuild(rom) },
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                    Text("تمهيد وتشغيل هذا الروم ⚡", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniSpecChip(text: String) {
    Box(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(6.dp))
            .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SystemNavigationBar(
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRecents: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF0F172A))
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Key
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp).testTag("system_nav_back")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Home Key
        IconButton(
            onClick = onHome,
            modifier = Modifier.size(48.dp).testTag("system_nav_home")
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }

        // Recents Key
        IconButton(
            onClick = onRecents,
            modifier = Modifier.size(48.dp).testTag("system_nav_recents")
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .border(2.dp, Color.White, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun RecentsTaskSwitcher(
    recentApps: List<PlayApp>,
    ramGb: Int,
    activeKernel: String,
    onClose: () -> Unit,
    onAppSelect: (PlayApp) -> Unit,
    onRemoveApp: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .clickable { onClose() }
            .testTag("recents_overlay")
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF0F172A))
                .padding(20.dp)
                .clickable(enabled = false) { /* prevent click propagation */ }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "المهام والبرامج النشطة 📱",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "ذاكرة مخصصة: ${ramGb}GB | نواة: ${activeKernel.replace("AOSP Linux Kernel ", "")}",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }
                
                if (recentApps.isNotEmpty()) {
                    Button(
                        onClick = onClearAll,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("مسح الكل 🧹", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (recentApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LayersClear,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "لا توجد تطبيقات نشطة بالخلفية حالياً",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(recentApps) { app ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAppSelect(app) }
                                .testTag("recent_app_item_${app.id}"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF0F172A), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (app.id) {
                                                "benchmark" -> Icons.Default.Bolt
                                                "booster" -> Icons.Default.Speed
                                                "files" -> Icons.Default.FolderOpen
                                                "game" -> Icons.Default.Gamepad
                                                "freefire" -> Icons.Default.Whatshot
                                                "subway" -> Icons.Default.DirectionsRun
                                                else -> Icons.Default.Android
                                            },
                                            contentDescription = null,
                                            tint = when (app.id) {
                                                "benchmark" -> Color(0xFFFF5A5F)
                                                "booster" -> Color(0xFF2563EB)
                                                "files" -> Color(0xFF0F9D58)
                                                "game" -> Color(0xFFEAB308)
                                                "freefire" -> Color(0xFFF97316)
                                                "subway" -> Color(0xFF8B5CF6)
                                                else -> Color(0xFF38BDF8)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = app.name,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = app.category,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onRemoveApp(app.id) },
                                    modifier = Modifier.testTag("remove_recent_app_${app.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "إغلاق المهمة",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
