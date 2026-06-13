# Generates HiredIn Shared Library file structure
$base = "c:\Users\My Computer\AndroidStudioProjects\hiredinshared\shared\src\main\java\com\nordstern\hiredin\shared"
$pkg = "com.nordstern.hiredin.shared"

function New-KotlinFile($relPath, $content) {
    $full = Join-Path $base $relPath
    $dir = Split-Path $full -Parent
    if (!(Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    Set-Content -Path $full -Value $content -Encoding UTF8
}

function Stub-Class($pkgPath, $className, $body = "") {
    $pkg = "$pkg.$pkgPath".Replace("/", ".")
    $b = if ($body) { $body } else { "// Stub implementation" }
    @"
package $pkg

class $className {
    $b
}
"@
}

function Stub-Object($pkgPath, $className, $body = "") {
    $pkg = "$pkg.$pkgPath".Replace("/", ".")
    $b = if ($body) { $body } else { "// Stub implementation" }
    @"
package $pkg

object $className {
    $b
}
"@
}

function Stub-Interface($pkgPath, $className) {
    $pkg = "$pkg.$pkgPath".Replace("/", ".")
    @"
package $pkg

interface $className
"@
}

function Stub-Enum($pkgPath, $className, $values) {
    $pkg = "$pkg.$pkgPath".Replace("/", ".")
    $vals = ($values | ForEach-Object { "    $_" }) -join ",`n"
    @"
package $pkg

enum class $className {
$vals
}
"@
}

function Stub-DataClass($pkgPath, $className, $fields) {
    $pkg = "$pkg.$pkgPath".Replace("/", ".")
    @"
package $pkg

data class $className(
    $fields
)
"@
}

# UI Components - Compose stubs
$uiFiles = @{
    "ui/components/HiredInTheme.kt" = @"
package $pkg.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.nordstern.hiredin.shared.ui.theme.HiredInTheme

@Composable
fun HiredInThemeWrapper(content: @Composable () -> Unit) {
    HiredInTheme(content = content)
}
"@
    "ui/components/LoadingButton.kt" = @"
package $pkg.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingButton(text: String, isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, enabled = !isLoading, modifier = modifier.fillMaxWidth()) {
        if (isLoading) CircularProgressIndicator() else Text(text)
    }
}
"@
    "ui/components/SearchBar.kt" = @"
package $pkg.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, placeholder: String = "Search", modifier: Modifier = Modifier) {
    OutlinedTextField(value = query, onValueChange = onQueryChange, placeholder = { Text(placeholder) }, modifier = modifier, singleLine = true)
}
"@
}

# Generate all stub files from manifest
$stubs = @(
    @{p="api/metrics"; n="ApiMetricsCollector"; t="class"},
    @{p="api/metrics"; n="NetworkLogger"; t="class"},
    @{p="api/metrics"; n="PerformanceTracker"; t="class"},
    @{p="auth/models"; n="AuthToken"; t="data"; f="val accessToken: String, val refreshToken: String, val expiresIn: Long"},
    @{p="auth/models"; n="UserSession"; t="data"; f="val userId: String, val email: String, val role: String"},
    @{p="auth/models"; n="DeviceInfo"; t="data"; f="val deviceId: String, val deviceType: String = `"android`""},
    @{p="auth/models"; n="Permission"; t="data"; f="val name: String, val granted: Boolean"},
    @{p="auth/security"; n="TokenEncryption"; t="class"},
    @{p="auth/security"; n="SecureStorage"; t="class"},
    @{p="auth/security"; n="CertificatePinner"; t="class"},
    @{p="auth/security"; n="RequestSigner"; t="class"},
    @{p="database"; n="BaseDao"; t="interface"},
    @{p="database"; n="SyncableEntity"; t="interface"},
    @{p="database"; n="MigrationHelper"; t="object"},
    @{p="database"; n="DatabaseCallback"; t="class"},
    @{p="database/converters"; n="Converters"; t="class"},
    @{p="database/converters"; n="DateConverter"; t="class"},
    @{p="database/converters"; n="JsonConverter"; t="class"},
    @{p="database/converters"; n="EnumConverter"; t="class"},
    @{p="database/migrations"; n="Migration_1_2"; t="object"},
    @{p="database/migrations"; n="Migration_2_3"; t="object"},
    @{p="database/migrations"; n="MigrationHelper"; t="object"},
    @{p="database/queries"; n="BaseQueries"; t="object"},
    @{p="database/queries"; n="DynamicQueryBuilder"; t="class"},
    @{p="database/queries"; n="PaginationHelper"; t="object"},
    @{p="sync"; n="ConflictResolver"; t="class"},
    @{p="sync"; n="SyncWorker"; t="class"},
    @{p="sync"; n="SyncScheduler"; t="class"},
    @{p="sync"; n="SyncStateManager"; t="class"},
    @{p="sync/strategies"; n="SyncStrategy"; t="interface"},
    @{p="sync/strategies"; n="IncrementalSyncStrategy"; t="class"},
    @{p="sync/strategies"; n="FullSyncStrategy"; t="class"},
    @{p="sync/strategies"; n="SmartSyncStrategy"; t="class"},
    @{p="sync/conflict"; n="ConflictHandler"; t="interface"},
    @{p="sync/conflict"; n="LastWriteWins"; t="class"},
    @{p="sync/conflict"; n="MergeStrategy"; t="class"},
    @{p="sync/conflict"; n="ManualResolutionHandler"; t="class"},
    @{p="sync/queue"; n="ActionProcessor"; t="class"},
    @{p="sync/queue"; n="QueuePersistence"; t="class"},
    @{p="sync/queue"; n="RetryPolicy"; t="class"},
    @{p="notifications"; n="NotificationDataMapper"; t="class"},
    @{p="notifications"; n="NotificationService"; t="class"},
    @{p="notifications"; n="LocalNotificationBuilder"; t="class"},
    @{p="notifications"; n="NotificationScheduler"; t="class"},
    @{p="notifications/models"; n="Notification"; t="data"; f="val id: String, val title: String, val body: String, val type: String"},
    @{p="notifications/models"; n="NotificationType"; t="enum"; v=@("GENERAL","LEAVE","PAYROLL","ANNOUNCEMENT","TASK","APPROVAL","COMPLIANCE")},
    @{p="notifications/models"; n="NotificationPayload"; t="data"; f="val data: Map<String, String>"},
    @{p="notifications/models"; n="NotificationAction"; t="data"; f="val action: String, val label: String"},
    @{p="notifications/channels"; n="ChannelDefinitions"; t="object"},
    @{p="notifications/channels"; n="ChannelManager"; t="class"},
    @{p="notifications/channels"; n="ChannelUpdater"; t="class"},
    @{p="notifications/handlers"; n="LeaveNotificationHandler"; t="class"},
    @{p="notifications/handlers"; n="PayrollNotificationHandler"; t="class"},
    @{p="notifications/handlers"; n="AnnouncementNotificationHandler"; t="class"},
    @{p="notifications/handlers"; n="TaskNotificationHandler"; t="class"},
    @{p="notifications/handlers"; n="ApprovalNotificationHandler"; t="class"},
    @{p="notifications/handlers"; n="ComplianceNotificationHandler"; t="class"}
)

foreach ($item in $stubs) {
    $path = "$($item.p)/$($item.n).kt"
    switch ($item.t) {
        "class" { $c = Stub-Class $item.p $item.n }
        "object" { $c = Stub-Object $item.p $item.n }
        "interface" { $c = Stub-Interface $item.p $item.n }
        "enum" { $c = Stub-Enum $item.p $item.n ($item.v -join ",") }
        "data" { $c = Stub-DataClass $item.p $item.n $item.f }
    }
    New-KotlinFile $path $c
}

foreach ($kv in $uiFiles.GetEnumerator()) {
    New-KotlinFile $kv.Key $kv.Value
}

# More UI stubs
$uiStubs = @(
    "FilterChipGroup","DatePicker","FilePicker","ImagePicker","PdfViewer","WebViewScreen",
    "common/HiredInTextField","common/HiredInDropdown","common/HiredInCheckbox","common/HiredInRadioButton",
    "common/HiredInSwitch","common/HiredInSlider","common/HiredInProgressBar","common/HiredInBottomSheet",
    "dialogs/ConfirmationDialog","dialogs/LoadingDialog","dialogs/ProgressDialog","dialogs/ErrorDialog",
    "dialogs/SuccessDialog","dialogs/FilterDialog","dialogs/DateRangeDialog","dialogs/BiometricDialog",
    "cards/InfoCard","cards/StatsCard","cards/MetricCard","cards/AlertCard","cards/SummaryCard","cards/ActionCard",
    "lists/PaginatedList","lists/EndlessScrollList","lists/SwipeToRefreshList","lists/SectionedList","lists/ExpandableList",
    "forms/FormField","forms/FormValidator","forms/MultiStepForm","forms/DynamicForm","forms/FormErrorHandler",
    "charts/LineChart","charts/BarChart","charts/PieChart","charts/DonutChart","charts/AreaChart","charts/ProgressChart","charts/ChartDataMapper",
    "feedback/ToastMessage","feedback/SnackbarMessage","feedback/InAppNotification","feedback/EmptyState","feedback/ErrorState","feedback/LoadingState","feedback/ShimmerEffect",
    "navigation/BottomNavigationBar","navigation/TopAppBar","navigation/NavigationDrawer","navigation/TabLayout","navigation/BreadcrumbNav",
    "media/ImageLoader","media/ImageViewer","media/VideoPlayer","media/AudioPlayer","media/DocumentViewer","media/AttachmentPreview",
    "input/OTPInputField","input/PhoneInputField","input/CurrencyInputField","input/PasswordStrengthMeter","input/AutoCompleteField","input/TagsInputField","input/RichTextEditor",
    "layouts/AdaptiveLayout","layouts/ResponsiveGrid","layouts/SplitScreen","layouts/CollapsibleSection","layouts/StepperLayout"
)

foreach ($s in $uiStubs) {
    $parts = $s -split "/"
    if ($parts.Length -eq 2) {
        $c = Stub-Object "ui/components/$($parts[0])" $parts[1] "fun Placeholder() {}"
    } else {
        $c = Stub-Object "ui/components" $s "fun Placeholder() {}"
    }
    New-KotlinFile "ui/components/$s.kt" $c
}

# Theme
$themeFiles = @("Color","Theme","DarkTheme","LightTheme","Typography","Shapes","Elevation","Animations","ThemeManager")
foreach ($t in $themeFiles) {
    New-KotlinFile "ui/theme/$t.kt" (Stub-Object "ui/theme" $t)
}

# Utils UI
$uiUtils = @("ViewExtensions","DateFormatters","NumberFormatters","ValidationUtils","ScreenUtils","KeyboardUtils","AnimUtils","DrawableUtils","ColorUtils")
foreach ($u in $uiUtils) { New-KotlinFile "ui/utils/$u.kt" (Stub-Object "ui/utils" $u) }

# Main utils
$utils = @("CryptoUtils","FileUtils","NetworkUtils","PermissionHelper","AnalyticsTracker","CrashReporter","AppLifecycleManager","ForegroundChecker","MemoryCache","DiskCache","ImageCompressor","ZipUtils","JsonUtils","XmlParser","CsvParser","HtmlParser","MarkdownParser","RegexUtils","DeviceInfo","AppInfo","BatteryUtils","LocationUtils","ClipboardUtils")
foreach ($u in $utils) { New-KotlinFile "utils/$u.kt" (Stub-Object "utils" $u) }

# Analytics
$analytics = @("AnalyticsManager","EventTracker","ScreenTracker","UserPropertyManager","providers/FirebaseAnalyticsProvider","providers/MixpanelProvider","providers/AmplitudeProvider","providers/CustomAnalyticsProvider","events/AnalyticsEvent","events/UserEvent","events/BusinessEvent","events/ErrorEvent")
foreach ($a in $analytics) {
    $parts = $a -split "/"
    if ($parts.Length -eq 2) { New-KotlinFile "analytics/$a.kt" (Stub-Object "analytics/$($parts[0])" $parts[1]) }
    else { New-KotlinFile "analytics/$a.kt" (Stub-Object "analytics" $a) }
}

# Cache
$cache = @("CacheManager","MemoryCache","DiskCache","LruCache","ExpiringCache","strategies/CacheStrategy","strategies/LRUStrategy","strategies/FIFOStrategy","strategies/TTLStrategy","serializers/JsonSerializer","serializers/ProtoSerializer","serializers/KryoSerializer")
foreach ($c in $cache) {
    $parts = $c -split "/"
    if ($parts.Length -eq 2) { New-KotlinFile "cache/$c.kt" (if ($c -match "Strategy|Serializer") { Stub-Interface "cache/$($parts[0])" $parts[1] } else { Stub-Object "cache/$($parts[0])" $parts[1] }) }
    else { New-KotlinFile "cache/$c.kt" (Stub-Object "cache" $c) }
}

# Security
$sec = @("SecurityManager","EncryptionManager","KeyStoreManager","DataProtection","CertificateValidator","ProguardRules","obfuscation/DataObfuscator","obfuscation/LogObfuscator","biometric/BiometricManager","biometric/BiometricPromptHelper","biometric/BiometricCallback")
foreach ($s in $sec) {
    $parts = $s -split "/"
    if ($parts.Length -eq 2) { New-KotlinFile "security/$s.kt" (Stub-Object "security/$($parts[0])" $parts[1]) }
    else { New-KotlinFile "security/$s.kt" (Stub-Object "security" $s) }
}

# Workers
$workers = @("BaseWorker","PeriodicWorker","OneTimeWorker","WorkerScheduler","workers/DataSyncWorker","workers/CacheCleanupWorker","workers/AnalyticsUploadWorker","workers/LogCleanupWorker","workers/TokenRefreshWorker","workers/NotificationCleanupWorker","constraints/NetworkConstraint","constraints/BatteryConstraint","constraints/StorageConstraint","constraints/TimeConstraint")
foreach ($w in $workers) {
    $parts = $w -split "/"
    if ($parts.Length -eq 2) { New-KotlinFile "workers/$w.kt" (Stub-Class "workers/$($parts[0])" $parts[1]) }
    else { New-KotlinFile "workers/$w.kt" (Stub-Class "workers" $w) }
}

# Permissions
$perms = @("PermissionManager","PermissionRequestor","PermissionResultHandler","permissions/CameraPermission","permissions/LocationPermission","permissions/StoragePermission","permissions/NotificationPermission","permissions/CalendarPermission","permissions/ContactsPermission","permissions/MicrophonePermission","permissions/BiometricPermission","RationaleDialog")
foreach ($p in $perms) {
    $parts = $p -split "/"
    if ($parts.Length -eq 2) { New-KotlinFile "permissions/$p.kt" (Stub-Object "permissions/$($parts[0])" $parts[1]) }
    else { New-KotlinFile "permissions/$p.kt" (Stub-Object "permissions" $p) }
}

# Network
$net = @("NetworkManager","ConnectivityObserver","BandwidthEstimator","RequestQueueManager","ResponseCacheManager","NetworkQuality")
foreach ($n in $net) { New-KotlinFile "network/$n.kt" (Stub-Class "network" $n) }

# Logging
$log = @("LogManager","LogLevel","LogEntry","loggers/ConsoleLogger","loggers/FileLogger","loggers/RemoteLogger","loggers/CrashlyticsLogger","formatters/JsonLogFormatter","formatters/PrettyLogFormatter","formatters/CsvLogFormatter")
foreach ($l in $log) {
    $parts = $l -split "/"
    if ($parts.Length -eq 2) {
        if ($l -eq "LogLevel") { New-KotlinFile "logging/$l.kt" (Stub-Enum "logging" "LogLevel" @("DEBUG","INFO","WARN","ERROR")) }
        elseif ($l -eq "LogEntry") { New-KotlinFile "logging/$l.kt" (Stub-DataClass "logging" "LogEntry" "val level: LogLevel, val message: String, val timestamp: Long = System.currentTimeMillis()") }
        else { New-KotlinFile "logging/$l.kt" (Stub-Class "logging/$($parts[0])" $parts[1]) }
    }
    else { New-KotlinFile "logging/$l.kt" (Stub-Object "logging" $l) }
}

# Localization
$loc = @("LocaleManager","LocalizationHelper","RtlHelper","CurrencyFormatter","DateFormatter")
foreach ($l in $loc) { New-KotlinFile "localization/$l.kt" (Stub-Object "localization" $l) }

# Testing
$test = @("TestDataFactory","MockResponseInterceptor","TestCoroutineRule","TestDispatcherProvider","fixtures/UserFixtures","fixtures/JobFixtures","fixtures/LeaveFixtures","fixtures/PayrollFixtures")
foreach ($t in $test) {
    $parts = $t -split "/"
    if ($parts.Length -eq 2) { New-KotlinFile "testing/$t.kt" (Stub-Object "testing/$($parts[0])" $parts[1]) }
    else { New-KotlinFile "testing/$t.kt" (Stub-Object "testing" $t) }
}

# Build
$build = @("BuildConfig","VersionInfo","Environment","FeatureFlags","constants/ApiEndpoints","constants/ErrorCodes","constants/SharedConstants","constants/TimeConstants")
foreach ($b in $build) {
    $parts = $b -split "/"
    if ($parts.Length -eq 2) { New-KotlinFile "build/$b.kt" (Stub-Object "build/$($parts[0])" $parts[1]) }
    else { New-KotlinFile "build/$b.kt" (Stub-Object "build" $b) }
}

Write-Host "Generated stub files successfully"
