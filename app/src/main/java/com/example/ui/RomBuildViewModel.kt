package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RomBuild
import com.example.data.RomBuildRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class RomBuildViewModel(private val repository: RomBuildRepository) : ViewModel() {

    // Configuration Settings State
    private val _ramGb = MutableStateFlow(16)
    val ramGb = _ramGb.asStateFlow()

    private val _storageGb = MutableStateFlow(512)
    val storageGb = _storageGb.asStateFlow()

    private val _playServices = MutableStateFlow(true)
    val playServices = _playServices.asStateFlow()

    private val _androidVersion = MutableStateFlow("Android 14 (AOSP Stable)")
    val androidVersion = _androidVersion.asStateFlow()

    private val _kernel = MutableStateFlow("AOSP Linux Kernel 6.1")
    val kernel = _kernel.asStateFlow()

    private val _selectedApps = MutableStateFlow(setOf("متصفح خفيف (Browser)", "مدير ملفات سحابي (File Manager)", "مشغل وسائط (Media Player)", "F-Droid App Store"))
    val selectedApps = _selectedApps.asStateFlow()

    // Compilation State
    private val _isCompiling = MutableStateFlow(false)
    val isCompiling = _isCompiling.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    private val _logLines = MutableStateFlow<List<String>>(emptyList())
    val logLines = _logLines.asStateFlow()

    private val _currentZipName = MutableStateFlow("")
    val currentZipName = _currentZipName.asStateFlow()

    private val _lastCompiledBuild = MutableStateFlow<RomBuild?>(null)
    val lastCompiledBuild = _lastCompiledBuild.asStateFlow()

    // History Database State
    val buildHistory: StateFlow<List<RomBuild>> = repository.allBuilds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Google Play Store Simulator state
    private val _isPlayStoreOpen = MutableStateFlow(false)
    val isPlayStoreOpen = _isPlayStoreOpen.asStateFlow()

    private val _playStoreScreen = MutableStateFlow("BUILDER") // "BUILDER", "LAUNCHER", "HOME", "DETAIL", "APP_RUNNING", etc.
    val playStoreScreen = _playStoreScreen.asStateFlow()

    private val _selectedPlayApp = MutableStateFlow<PlayApp?>(null)
    val selectedPlayApp = _selectedPlayApp.asStateFlow()

    // Recents / Multitasking Switcher simulation states
    private val _isRecentsOpen = MutableStateFlow(false)
    val isRecentsOpen = _isRecentsOpen.asStateFlow()

    private val _recentApps = MutableStateFlow<List<PlayApp>>(emptyList())
    val recentApps = _recentApps.asStateFlow()

    fun openRecents() {
        _isRecentsOpen.value = true
    }

    fun closeRecents() {
        _isRecentsOpen.value = false
    }

    fun clearRecents() {
        _recentApps.value = emptyList()
    }

    fun removeRecentApp(appId: String) {
        _recentApps.value = _recentApps.value.filter { it.id != appId }
    }

    private val _playApps = MutableStateFlow(listOf(
        PlayApp(
            id = "benchmark",
            name = "AnTuTu Benchmark v10 (مقياس الأداء)",
            developer = "AnTuTu Laboratories",
            rating = "4.9",
            downloads = "٥٠ مليون+",
            category = "أدوات النظام (Tools)",
            iconName = "benchmark",
            description = "أشهر تطبيق لقياس قوة المعالج والرام وكارت الشاشة لطلبك. سيعطيك تقييماً خارقاً بناءً على الرام الكبيرة والمساحة ونوع النواة التي حددتها في جهازك المخصص!"
        ),
        PlayApp(
            id = "booster",
            name = "Smart RAM Booster & Cleaner",
            developer = "AOSP Optimization Team",
            rating = "4.8",
            downloads = "١٠ مليون+",
            category = "الأداء والسرعة (Productivity)",
            iconName = "booster",
            description = "مسرع ذاكرة الوصول العشوائي الذكي. يقوم بتنظيف العمليات بالخلفية لزيادة حجم الرام الحر المتوفر وتحرير طاقة المعالج القصوى."
        ),
        PlayApp(
            id = "files",
            name = "Google Files Pro (مدير الملفات)",
            developer = "Google LLC",
            rating = "4.7",
            downloads = "٥ مليار+",
            category = "أدوات (Tools)",
            iconName = "files",
            description = "تطبيق إدارة الملفات الرسمي والذكي، يعرض مساحة تخزين جهازك الحقيقية التي قمت بتجميعها (مثل 512GB أو 1TB) ويتيح لك تصفح ونقل الملفات بسرعة البرق."
        ),
        PlayApp(
            id = "game",
            name = "PUBG Mobile (ببجي موبايل)",
            developer = "Level Infinite",
            rating = "4.6",
            downloads = "١ مليار+",
            category = "ألعاب (Games)",
            iconName = "game",
            description = "لعبة باتل رويال الأكثر شهرة. بفضل تجميعك لجهاز بذاكرة 16GB أو 24GB رام ونواة كسر السرعة، يمكنك الآن تشغيل اللعبة بأعظم إعدادات رسومية UHD بمعدل 90 إطار بالثانية دون أي تقطيع!"
        ),
        PlayApp(
            id = "freefire",
            name = "Garena Free Fire (فري فاير)",
            developer = "Garena International I",
            rating = "4.7",
            downloads = "١ مليار+",
            category = "ألعاب (Games)",
            iconName = "freefire",
            description = "أشهر لعبة بقاء وإطلاق نار للهواتف المحمولة. بفضل معالج كسر السرعة والرامات العالية التي قمت باختيارها، ستعمل فري فاير بسلاسة فائقة بمعدل 90 إطار وبدون أي توقف."
        ),
        PlayApp(
            id = "subway",
            name = "Subway Surfers (سيرفاي)",
            developer = "SYBO Games",
            rating = "4.5",
            downloads = "١.٥ مليار+",
            category = "ألعاب (Games)",
            iconName = "subway",
            description = "الركض والقفز ومراوغة القطارات والمأمور الغاضب. استمتع بألوان رائعة واستجابة اللمس الفورية على جهازك الجديد المطور."
        )
    ))
    val playApps = _playApps.asStateFlow()

    // Benchmark Execution State
    private val _isTestingBenchmark = MutableStateFlow(false)
    val isTestingBenchmark = _isTestingBenchmark.asStateFlow()

    private val _benchmarkResult = MutableStateFlow<String?>(null)
    val benchmarkResult = _benchmarkResult.asStateFlow()

    // Memory sweeping state
    private val _isSweepingMemory = MutableStateFlow(false)
    val isSweepingMemory = _isSweepingMemory.asStateFlow()

    private val _boostAmount = MutableStateFlow(0)
    val boostAmount = _boostAmount.asStateFlow()

    fun bootCustomRom() {
        _isPlayStoreOpen.value = true
        _playStoreScreen.value = "LAUNCHER"
        _selectedPlayApp.value = null
    }

    fun openPlayStoreApp() {
        _playStoreScreen.value = "HOME"
        _selectedPlayApp.value = null
    }

    fun openChrome() {
        _playStoreScreen.value = "CHROME"
    }

    fun openYoutube() {
        _playStoreScreen.value = "YOUTUBE"
    }

    fun openGmail() {
        _playStoreScreen.value = "GMAIL"
    }

    fun openPhotos() {
        _playStoreScreen.value = "PHOTOS"
    }

    fun openDialer() {
        _playStoreScreen.value = "DIALER"
    }

    fun goHome() {
        _playStoreScreen.value = "LAUNCHER"
    }

    fun goToBuilder() {
        _playStoreScreen.value = "BUILDER"
    }

    fun openPlayStore() {
        _isPlayStoreOpen.value = true
        _playStoreScreen.value = "HOME"
        _selectedPlayApp.value = null
    }

    fun closePlayStore() {
        _isPlayStoreOpen.value = false
    }

    fun selectPlayApp(app: PlayApp) {
        _selectedPlayApp.value = app
        _playStoreScreen.value = "DETAIL"
    }

    fun goBackToPlayHome() {
        _playStoreScreen.value = "HOME"
        _selectedPlayApp.value = null
    }

    fun runInstalledApp(app: PlayApp) {
        _selectedPlayApp.value = app
        _playStoreScreen.value = "APP_RUNNING"
        
        // Add to active recent apps
        val list = _recentApps.value.toMutableList()
        list.removeAll { it.id == app.id }
        list.add(0, app)
        _recentApps.value = list.toList()
        
        if (app.id == "benchmark") {
            _benchmarkResult.value = null
        } else if (app.id == "booster") {
            _boostAmount.value = 0
        }
    }

    fun installPlayApp(appId: String) {
        viewModelScope.launch {
            val appList = _playApps.value.toMutableList()
            val index = appList.indexOfFirst { it.id == appId }
            if (index != -1) {
                appList[index] = appList[index].copy(isInstalling = true, progress = 0)
                _playApps.value = appList.toList()
                _selectedPlayApp.value = appList[index]
                
                for (p in 10..100 step 15) {
                    delay(150)
                    val updated = _playApps.value.toMutableList()
                    updated[index] = updated[index].copy(progress = p)
                    _playApps.value = updated.toList()
                    if (_selectedPlayApp.value?.id == appId) {
                        _selectedPlayApp.value = updated[index]
                    }
                }
                
                delay(150)
                val finalUpdated = _playApps.value.toMutableList()
                finalUpdated[index] = finalUpdated[index].copy(isInstalled = true, isInstalling = false, progress = 100)
                _playApps.value = finalUpdated.toList()
                if (_selectedPlayApp.value?.id == appId) {
                    _selectedPlayApp.value = finalUpdated[index]
                }
            }
        }
    }

    fun uninstallPlayApp(appId: String) {
        val appList = _playApps.value.toMutableList()
        val index = appList.indexOfFirst { it.id == appId }
        if (index != -1) {
            appList[index] = appList[index].copy(isInstalled = false, progress = 0)
            _playApps.value = appList.toList()
            if (_selectedPlayApp.value?.id == appId) {
                _selectedPlayApp.value = appList[index]
            }
        }
    }

    fun startBenchmarkTest() {
        if (_isTestingBenchmark.value) return
        viewModelScope.launch {
            _isTestingBenchmark.value = true
            _benchmarkResult.value = "جاري تهيئة محرك الرسوميات واختبار GPU..."
            delay(800)
            _benchmarkResult.value = "جاري عمل اختبار الضغط للمعالج الذكي...\nالنواة الحالية: ${_kernel.value}"
            delay(900)
            _benchmarkResult.value = "اكتمل اختبار النواة والمنافذ.\nاختبار عرض النطاق الترددي للرام الكبيرة (تم حجز ${_ramGb.value}GB رتل افتراضي)..."
            delay(900)
            _benchmarkResult.value = "جاري تحميل وقراءة الكتل العشوائية على سعة ${_storageGb.value}GB بسرعة فائقة..."
            delay(900)
            
            val baseScore = 800000
            val ramBonus = _ramGb.value * 28000
            val storageBonus = if (_storageGb.value >= 512) 180000 else 60000
            val kernelBonus = if (_kernel.value.contains("Custom")) 150000 else if (_kernel.value.contains("Root")) 90000 else 20000
            val resultScore = baseScore + ramBonus + storageBonus + kernelBonus
            
            _benchmarkResult.value = "RESULT_SUCCESS:$resultScore"
            _isTestingBenchmark.value = false
        }
    }

    fun startSmartBoost() {
        if (_isSweepingMemory.value) return
        viewModelScope.launch {
            _isSweepingMemory.value = true
            delay(1200)
            _boostAmount.value = (400..1500).random()
            _isSweepingMemory.value = false
        }
    }

    fun updateRam(ram: Int) {
        if (!_isCompiling.value) _ramGb.value = ram
    }

    fun updateStorage(storage: Int) {
        if (!_isCompiling.value) _storageGb.value = storage
    }

    fun togglePlayServices() {
        if (!_isCompiling.value) _playServices.value = !_playServices.value
    }

    fun updateAndroidVersion(version: String) {
        if (!_isCompiling.value) _androidVersion.value = version
    }

    fun updateKernel(kernelType: String) {
        if (!_isCompiling.value) _kernel.value = kernelType
    }

    fun toggleApp(appName: String) {
        if (_isCompiling.value) return
        val current = _selectedApps.value.toMutableSet()
        if (current.contains(appName)) {
            current.remove(appName)
        } else {
            current.add(appName)
        }
        _selectedApps.value = current
    }

    fun resetLastCompiled() {
        _lastCompiledBuild.value = null
    }

    fun deleteBuild(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearBuildHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    // High fidelity Compilation Engine with incremental delay simulation
    fun compileAndGenerateRom() {
        if (_isCompiling.value) return

        viewModelScope.launch {
            _isCompiling.value = true
            _progress.value = 0
            _lastCompiledBuild.value = null
            
            // Build visual descriptor
            val playSuffix = if (_playServices.value) "gapps" else "vanilla"
            val fileZipName = "aosp_v14_${_ramGb.value}gb_${_storageGb.value}gb_${playSuffix}_custom_rom.zip"
            _currentZipName.value = fileZipName

            val logsList = mutableListOf<String>()
            fun addLog(line: String) {
                logsList.add(line)
                _logLines.value = logsList.toList()
            }

            // Phase 1: Initialize
            addLog("⚡ [SYS] [01] Initializing compile sequence...")
            delay(400)
            _progress.value = 8
            addLog("🔧 [SYS] Config resolved: RAM=${_ramGb.value}GB | Storage=${_storageGb.value}GB")
            delay(500)
            _progress.value = 15

            // Phase 2: Kernel Source Fetching & Driver checks
            addLog("📂 [AOSP] Fetching standard Android 14 tags (AOSP Stable)...")
            delay(600)
            _progress.value = 28
            addLog("🔌 [DRV] Integrating custom adaptable target drivers for SoC and peripherals...")
            delay(500)
            _progress.value = 35

            // Phase 3: Building and Optimizing Kernel
            addLog("⚙️ [KERNEL] Compiling: ${_kernel.value}")
            delay(700)
            _progress.value = 46
            addLog("🔬 [KERNEL] Kernel configured for high speed, battery efficiency, and standard RAM virtualization...")
            delay(550)
            _progress.value = 54

            // Phase 4: GMS or Vanilla configuration
            if (_playServices.value) {
                addLog("🎁 [PLAY] Realizing Google Play (GMS) core package inclusion & licensing configs...")
                delay(800)
                _progress.value = 68
                addLog("🔒 [PLAY] Setting up signature permissions and compatibility certs...")
            } else {
                addLog("🛡️ [AOSP] ROM configured vanilla. Privacy and lightness prioritized.")
                delay(600)
                _progress.value = 68
            }
            delay(400)
            _progress.value = 75

            // Phase 5: Bundling basic tools
            val bundled = _selectedApps.value.joinToString(", ")
            if (bundled.isNotEmpty()) {
                addLog("📦 [APPS] Bundling applications: $bundled")
            } else {
                addLog("🔘 [APPS] Minimal mode selected. No third-party system applications bundled.")
            }
            delay(700)
            _progress.value = 84
            addLog("📦 [GEN] Building final Android user interface layout files...")
            delay(500)

            // Phase 6: Final archiving
            addLog("🗄️ [IMG] Generating system.img, boot.img, and userdata.img parameters...")
            delay(600)
            _progress.value = 92
            addLog("💾 [ZIP] Formatting package compression into flashable: $fileZipName")
            delay(750)
            _progress.value = 97
            addLog("🔑 [SIGN] Signing compilation with release test-keys (v3 signatures)...")
            delay(500)

            // Completion
            _progress.value = 100
            addLog("✅ [SUCCESS] Compilation completed successfully! Verification hash is valid. ROM is bootable.")
            
            // Persist the verified ROM compile to local database history
            val entity = RomBuild(
                buildName = "Custom Rom for AOSP ${_androidVersion.value.replace(" (AOSP Stable)", "")}",
                ramGb = _ramGb.value,
                storageGb = _storageGb.value,
                playServices = _playServices.value,
                kernel = _kernel.value,
                bundledApps = if (bundled.isEmpty()) "Minimal Core" else bundled,
                status = "COMPLETED"
            )
            val buildId = repository.insert(entity)
            val savedBuild = entity.copy(id = buildId.toInt())
            
            _lastCompiledBuild.value = savedBuild
            _isCompiling.value = false
        }
    }
}

data class PlayApp(
    val id: String,
    val name: String,
    val developer: String,
    val rating: String,
    val downloads: String,
    val category: String,
    val iconName: String,
    val isInstalled: Boolean = false,
    val progress: Int = 0,
    val isInstalling: Boolean = false,
    val description: String = ""
)

