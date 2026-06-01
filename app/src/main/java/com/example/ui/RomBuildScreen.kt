package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RomBuild
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RomBuildScreen(
    viewModel: RomBuildViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ram by viewModel.ramGb.collectAsState()
    val storage by viewModel.storageGb.collectAsState()
    val playServices by viewModel.playServices.collectAsState()
    val androidVersion by viewModel.androidVersion.collectAsState()
    val kernel by viewModel.kernel.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()

    val isCompiling by viewModel.isCompiling.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val logLines by viewModel.logLines.collectAsState()
    val currentZipName by viewModel.currentZipName.collectAsState()
    val lastCompiledBuild by viewModel.lastCompiledBuild.collectAsState()
    val buildHistory by viewModel.buildHistory.collectAsState()
    val isPlayStoreOpen by viewModel.isPlayStoreOpen.collectAsState()

    val consoleLazyState = rememberLazyListState()
    var selectedTab by remember { mutableStateOf(0) }

    // Smooth scroll to bottom of logs on new entry
    LaunchedEffect(logLines.size) {
        if (logLines.isNotEmpty()) {
            consoleLazyState.animateScrollToItem(logLines.lastIndex)
        }
    }

    // Direct simulated download loading state
    var isDownloadingRom by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(isDownloadingRom) {
        if (isDownloadingRom) {
            downloadProgress = 0f
            while (downloadProgress < 1f) {
                kotlinx.coroutines.delay(100)
                downloadProgress += 0.05f
            }
            isDownloadingRom = false
            android.widget.Toast.makeText(context, "تم تحميل ملف الروم بنجاح! جاهز للتفليش (flashable_rom.zip)", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF3F4F9),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Custom Simulated System Builder Status Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "18:25 น.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Wifi Connected",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF0F172A)
                        )
                        Icon(
                            imageVector = Icons.Default.NetworkCell,
                            contentDescription = "Signal Strength",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF0F172A)
                        )
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = "Battery Status",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF0F172A)
                        )
                    }
                }

                // Application Title with premium icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AOSP ROM Builder",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "نظام أندرويد المستقر ومتجر بلاي | AOSP Client",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Rounded system builder hardware icon box
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp), ambientColor = Color(0xFF2563EB).copy(alpha = 0.2f))
                            .background(Color(0xFF2563EB), RoundedCornerShape(16.dp))
                            .clickable { /* decorative info click */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeveloperMode,
                            contentDescription = "AOSP Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: ROM Builders
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTab == 0) Color.White else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeveloperMode,
                                contentDescription = null,
                                tint = if (selectedTab == 0) Color(0xFF2563EB) else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "تعديل وبناء الروم Core",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) Color(0xFF0F172A) else Color(0xFF64748B)
                            )
                        }
                    }

                    // Tab 2: Real Google Play Store
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTab == 1) Color(0xFF0F9D58) else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shop,
                                contentDescription = null,
                                tint = if (selectedTab == 1) Color.White else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "متجر Google Play الحقيقي 🌐",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (selectedTab == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4F9))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Button(
                    onClick = { viewModel.compileAndGenerateRom() },
                    enabled = !isCompiling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("generate_zip_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB),
                        disabledContainerColor = Color(0xFF2563EB).copy(alpha = 0.5f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    if (isCompiling) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "جاري تجميع النظام ($progress%)...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "صنع وتجميع ملف الروم ROM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Text(
                    text = "AOSP Master Build Node 01 - Frankfurt",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 10.dp),
                    letterSpacing = 1.5.sp
                )

                // Android Navigation Bar Simulation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(2.dp, Color(0xFFCBD5E1), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp))
                    )
                }
            }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTab == 0) {
                LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Specifications section: RAM and Storage Configuration
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFDCFCE7), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Memory,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "تخصيص العتاد (Hardware Specs)",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "تحديد حجم الرام ومساحة التخزين الكبيرة",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            // RAM allocation pills
                            Text(
                                text = "حجم ذاكرة الوصول العشوائي (RAM):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(8, 12, 16, 24).forEach { size ->
                                    val isSelected = ram == size
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (isSelected) Color(0xFF2563EB) else Color(
                                                    0xFFF1F5F9
                                                )
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Color(0xFF2563EB) else Color(
                                                    0xFFE2E8F0
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable(enabled = !isCompiling) {
                                                viewModel.updateRam(size)
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${size}GB",
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp,
                                            color = if (isSelected) Color.White else Color(
                                                0xFF475569
                                            )
                                        )
                                    }
                                }
                            }

                            // Storage allocation pills
                            Text(
                                text = "سعة مساحة التخزين الداخلية (Storage):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(128, 256, 512, 1024).forEach { size ->
                                    val isSelected = storage == size
                                    val label = if (size == 1024) "1TB" else "${size}GB"
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (isSelected) Color(0xFF2563EB) else Color(
                                                    0xFFF1F5F9
                                                )
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) Color(0xFF2563EB) else Color(
                                                    0xFFE2E8F0
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable(enabled = !isCompiling) {
                                                viewModel.updateStorage(size)
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp,
                                            color = if (isSelected) Color.White else Color(
                                                0xFF475569
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Google Play Integration toggle switch
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable(enabled = !isCompiling) { viewModel.togglePlayServices() }
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "GMS",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2563EB),
                                        fontSize = 11.sp
                                    )
                                }
                                Column {
                                    Text(
                                        text = "دمج خدمات جوجل بلاي الرسمي",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "تضمين Google Play Store الحقيقي والخدمات",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Switch(
                                checked = playServices,
                                onCheckedChange = { viewModel.togglePlayServices() },
                                enabled = !isCompiling,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF2563EB),
                                    uncheckedThumbColor = Color(0xFF94A3B8),
                                    uncheckedTrackColor = Color(0xFFE2E8F0)
                                )
                            )
                        }
                    }
                }

                // Google Play Store Simulator Launcher Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = Color(0xFF0F9D58).copy(alpha = 0.2f))
                            .border(BorderStroke(1.5.dp, Color(0xFF0F9D58).copy(alpha = 0.3f)), RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable { viewModel.openPlayStore() }
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                            .background(Color(0xFFECFDF5), RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shop,
                                            contentDescription = null,
                                            tint = Color(0xFF0F9D58),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = "بوابة متجر Google Play",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = "مفتوح ومباشر",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier
                                                    .background(Color(0xFF0F9D58), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = "اضغط للدخول إلى متجر جوجل الحقيقي وتجربة نظامك السحابي",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // Kernel Selection options / Custom drivers
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(Color(0xFFFEF2F2), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "نواة نظام التشغيل (Kernel)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            listOf(
                                "AOSP Linux Kernel 6.1" to "استقرار تام وتوافقية عالية مع المعالجات",
                                "custom-driver-kernel" to "تعريفات تشغيل مرنة مع كسر سرعة للأداء الأقصى",
                                "KernelSU Root Ready" to "نواة مهيأة للروت الداخلي والتحكم الكامل بصلاحيات المشرف"
                            ).forEach { (kernelName, description) ->
                                val isSelected = if (kernelName == "custom-driver-kernel") {
                                    kernel.contains("Custom")
                                } else {
                                    kernel == kernelName
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) Color(0xFFF8FAFC) else Color.Transparent
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) Color(0xFFE2E8F0) else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable(enabled = !isCompiling) {
                                            if (kernelName == "custom-driver-kernel") {
                                                viewModel.updateKernel("AOSP Core with Custom Drivers")
                                            } else {
                                                viewModel.updateKernel(kernelName)
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            if (kernelName == "custom-driver-kernel") {
                                                viewModel.updateKernel("AOSP Core with Custom Drivers")
                                            } else {
                                                viewModel.updateKernel(kernelName)
                                            }
                                        },
                                        enabled = !isCompiling,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFF2563EB)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (kernelName == "custom-driver-kernel") "نواة AOSP مدمجة بتعريفات مرنة" else kernelName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF334155)
                                        )
                                        Text(
                                            text = description,
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bundled App Store/Core Apps section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "التطبيقات الأساسية المدمجة في النظام",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            listOf(
                                "متصفح خفيف (Browser)",
                                "مدير ملفات سحابي (File Manager)",
                                "مشغل وسائط (Media Player)",
                                "F-Droid App Store"
                            ).forEach { app ->
                                val isSelected = selectedApps.contains(app)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clickable(enabled = !isCompiling) {
                                            viewModel.toggleApp(app)
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { viewModel.toggleApp(app) },
                                            enabled = !isCompiling,
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF2563EB)
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                             text = app,
                                             fontSize = 12.sp,
                                             color = Color(0xFF334155),
                                             fontWeight = FontWeight.Medium
                                        )
                                    }
                                    if (app.contains("F-Droid")) {
                                        Text(
                                            text = "مفتوح المصدر",
                                            fontSize = 9.sp,
                                            color = Color(0xFF15803D),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .background(Color(0xFFDCFCE7), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Compiling progress and live terminal section
                item {
                    AnimatedVisibility(
                        visible = isCompiling || logLines.isNotEmpty(),
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "حالة بناء وتجميع النظام",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isCompiling) "جاري تجميع الملف: $currentZipName" else "اكتمل بناء الروم",
                                            fontSize = 11.sp,
                                            color = Color(0xFF60A5FA)
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF2563EB).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "$progress%",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF93C5FD)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Sleek blue gradient progress bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(fraction = progress / 100f)
                                            .background(
                                                Brush.horizontalGradient(
                                                    colors = listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
                                                ),
                                                CircleShape
                                            )
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Live Terminal Log Output Panel
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                        .padding(12.dp)
                                ) {
                                    LazyColumn(
                                        state = consoleLazyState,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(logLines) { line ->
                                            val parts = line.split(" ", limit = 3)
                                            val displayColor = when {
                                                line.contains("[SUCCESS]") || line.contains("✅") -> Color(0xFF4ADE80)
                                                line.contains("[PLAY]") -> Color(0xFFFBBF24)
                                                line.contains("🔧") || line.contains("[SYS]") -> Color(0xFF60A5FA)
                                                else -> Color(0xFFE2E8F0)
                                            }
                                            Text(
                                                text = line,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = displayColor,
                                                modifier = Modifier.padding(vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Simulated ROM Delivery Panel
                item {
                    val build = lastCompiledBuild
                    AnimatedVisibility(
                        visible = build != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            border = BorderStroke(1.5.dp, Color(0xFFBFDBFE))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "ملف الروم جاهز للتحميل والتثبيت!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E3A8A),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "تم صنع ملف روم متكامل (Flashable ZIP) بمواصفاتك تماماً.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2563EB),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(16.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("RAM Allocation", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        Text("${build?.ramGb ?: 16} GB", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Storage", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        Text(if ((build?.storageGb ?: 512) == 1024) "1 TB" else "${build?.storageGb ?: 512} GB", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Google Play", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        Text(if (build?.playServices == true) "مدمج بنجاح" else "Vanilla AOSP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (build?.playServices == true) Color(0xFF16A34A) else Color(0xFFDC2626))
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (isDownloadingRom) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        LinearProgressIndicator(
                                            progress = { downloadProgress },
                                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                                            color = Color(0xFF2563EB)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "جاري تحميل الملف المباشر للموبايل: ${(downloadProgress * 100).toInt()}%...",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF1E3A8A)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { isDownloadingRom = true },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("تحميل ملف النظام Flashable ZIP المباشر")
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "📖 تعليمات التثبيت السريعة على الموبايل:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E3A8A),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    Text(
                                        text = "1. قم بتحميل الروم flashable_rom.zip المباشر أعلاه.\n" +
                                                "2. أعد تشغيل هاتفك بوضع الريكفري المخصص (TWRP Or OrangeFox).\n" +
                                                "3. اختر Wipe Dalvik/Cache/Data لتجنب المشاكل.\n" +
                                                "4. اختر Install، حدد ملف ZIP المحمل، واسحب للتأكيد.\n" +
                                                "5. أعد التشغيل واستمتع بنظام أندرويد سريع بمساحة ورام هائلة مع جوجل بلاي الحقيقي!",
                                        fontSize = 10.sp,
                                        color = Color(0xFF334155),
                                        lineHeight = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                TextButton(onClick = { viewModel.resetLastCompiled() }) {
                                    Text("بناء نظام جديد", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Previous Builds Database History Section to store compiled setups
                if (buildHistory.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "سجل أنظمة AOSP التي قمت بتجميعها",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            TextButton(onClick = { viewModel.clearBuildHistory() }) {
                                Text("مسح السجل", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(buildHistory) { historyItem ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = historyItem.buildName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "نواة: ${historyItem.kernel}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteBuild(historyItem.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete build history record",
                                            tint = Color(0xFFEF4444).copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "رام MB: ${historyItem.ramGb}GB",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B),
                                            modifier = Modifier
                                                .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                        Text(
                                            text = "سعة: ${if (historyItem.storageGb == 1024) "1TB" else "${historyItem.storageGb}GB"}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B),
                                            modifier = Modifier
                                                .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                        if (historyItem.playServices) {
                                            Text(
                                                text = "Google Play",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF15803D),
                                                modifier = Modifier
                                                    .background(Color(0xFFDCFCE7), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Display date compiled
                                    val formattedDate = remember(historyItem.timestamp) {
                                        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                                        sdf.format(Date(historyItem.timestamp))
                                    }
                                    Text(
                                        text = formattedDate,
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
            } else {
                RealPlayStoreWebView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    if (isPlayStoreOpen) {
        PlayStoreSimulatorDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closePlayStore() }
        )
    }
}
