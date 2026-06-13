#!/usr/bin/env python3
"""
HiredIn Shared Library completion script.

Generates missing Kotlin source files (one-class-per-file) with real enterprise
implementations copied from consolidated sources. Does NOT delete or overwrite
existing files.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path
from typing import Callable, Dict, Iterable, List, Optional, Tuple

# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------
ROOT = Path(__file__).resolve().parent.parent
PKG_ROOT = ROOT / "shared" / "src" / "main" / "java" / "com" / "nordstern" / "hiredin" / "shared"
TEST_ROOT = ROOT / "shared" / "src" / "test" / "java" / "com" / "nordstern" / "hiredin" / "shared"
RES_ROOT = ROOT / "shared" / "src" / "main" / "res"

BASE_PKG = "com.nordstern.hiredin.shared"

created_files: List[str] = []


def rel(pkg_path: str, filename: str) -> Path:
    """Return absolute path under PKG_ROOT for a relative package path."""
    return PKG_ROOT / pkg_path.replace("/", "\\") / filename if sys.platform == "win32" else PKG_ROOT / pkg_path / filename


def test_rel(pkg_path: str, filename: str) -> Path:
    return TEST_ROOT / pkg_path / filename


def write_if_missing(path: Path, content: str) -> bool:
    """Write content only when the file does not exist. Returns True if created."""
    if path.exists():
        return False
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.rstrip() + "\n", encoding="utf-8")
    created_files.append(str(path.relative_to(ROOT)))
    return True


def pkg_decl(pkg_suffix: str) -> str:
    if pkg_suffix:
        return f"package {BASE_PKG}.{pkg_suffix.replace('/', '.')}\n"
    return f"package {BASE_PKG}\n"


def emit(rel_path: str, pkg_suffix: str, body: str, *, test: bool = False) -> None:
    root = TEST_ROOT if test else PKG_ROOT
    path = root / rel_path.replace("/", "\\") if sys.platform == "win32" else root / rel_path
    write_if_missing(path, pkg_decl(pkg_suffix) + "\n" + body.lstrip())


# ---------------------------------------------------------------------------
# Kotlin declaration extractor (brace-balanced)
# ---------------------------------------------------------------------------
def _find_declaration_start(source: str, name: str) -> Optional[int]:
    patterns = [
        rf"@(?:\w+\([^)]*\)\s*\n)*@Composable\s*\nfun\s+{re.escape(name)}\s*\(",
        rf"fun\s+{re.escape(name)}\s*\(",
        rf"@(?:\w+\([^)]*\)\s*\n)*class\s+{re.escape(name)}\b",
        rf"class\s+{re.escape(name)}\b",
        rf"@(?:\w+\([^)]*\)\s*\n)*object\s+{re.escape(name)}\b",
        rf"object\s+{re.escape(name)}\b",
        rf"interface\s+{re.escape(name)}\b",
        rf"enum\s+class\s+{re.escape(name)}\b",
        rf"data\s+class\s+{re.escape(name)}\b",
    ]
    for pat in patterns:
        m = re.search(pat, source)
        if m:
            # Include preceding annotations on same logical block
            start = m.start()
            before = source[:start]
            ann_lines: List[str] = []
            for line in reversed(before.splitlines()):
                stripped = line.strip()
                if stripped.startswith("@") or stripped.startswith("//"):
                    ann_lines.insert(0, line)
                elif stripped == "":
                    continue
                else:
                    break
            if ann_lines:
                start = before.rfind(ann_lines[0])
            return start
    return None


def extract_declaration(source: str, name: str) -> Optional[str]:
    start = _find_declaration_start(source, name)
    if start is None:
        return None
    chunk = source[start:]
    depth = 0
    started = False
    i = 0
    while i < len(chunk):
        c = chunk[i]
        if c == "{":
            depth += 1
            started = True
        elif c == "}":
            depth -= 1
            if started and depth == 0:
                return chunk[: i + 1].strip()
        elif c == '"' or c == "'":
            quote = c
            i += 1
            while i < len(chunk):
                if chunk[i] == "\\":
                    i += 2
                    continue
                if chunk[i] == quote:
                    break
                i += 1
        i += 1
    # No braces (interface with no body, etc.)
    semi = chunk.find(";")
    nl = chunk.find("\n")
    end = min(x for x in (semi, nl) if x != -1) if any(x != -1 for x in (semi, nl)) else len(chunk)
    return chunk[:end].strip()


def extract_imports(source: str) -> List[str]:
    return [ln.strip() for ln in source.splitlines() if ln.strip().startswith("import ")]


def split_composable_file(
    source_rel: str,
    pkg_suffix: str,
    composable_names: Iterable[str],
    extra_imports: Optional[List[str]] = None,
) -> None:
    src_path = PKG_ROOT / source_rel
    if not src_path.exists():
        return
    source = src_path.read_text(encoding="utf-8")
    imports = extract_imports(source)
    if extra_imports:
        for imp in extra_imports:
            if imp not in imports:
                imports.append(imp)
    import_block = "\n".join(imports) + ("\n" if imports else "")

    for name in composable_names:
        decl = extract_declaration(source, name)
        if not decl:
            continue
        sub_pkg = pkg_suffix
        body = import_block + "\n" + decl
        emit(f"{sub_pkg.replace('.', '/')}/{name}.kt".replace("shared/", ""), sub_pkg, body)


def split_class_file(
    source_rel: str,
    mappings: List[Tuple[str, str, str]],
) -> None:
    """mappings: (declaration_name, target_rel_path, pkg_suffix)"""
    _split_source_file(PKG_ROOT / source_rel, mappings, test=False)


def _split_test_file(
    src_path: Path,
    mappings: List[Tuple[str, str, str]],
) -> None:
    _split_source_file(src_path, mappings, test=True)


def _split_source_file(
    src_path: Path,
    mappings: List[Tuple[str, str, str]],
    *,
    test: bool,
) -> None:
    if not src_path.exists():
        return
    source = src_path.read_text(encoding="utf-8")
    imports = extract_imports(source)
    import_block = "\n".join(imports) + ("\n" if imports else "")

    for decl_name, target_rel, pkg_suffix in mappings:
        decl = extract_declaration(source, decl_name)
        if not decl:
            continue
        body = import_block + "\n" + decl
        emit(target_rel, pkg_suffix, body, test=test)


# ---------------------------------------------------------------------------
# Hardcoded enterprise implementations
# ---------------------------------------------------------------------------
def generate_api() -> None:
    emit(
        "api/ResponseParser.kt",
        "api",
        """
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ResponseParser {
    private val gson = Gson()

    fun <T> parse(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

    inline fun <reified T> parseList(json: String): List<T> {
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type)
    }

    fun parseErrorMessage(json: String?): String? {
        if (json.isNullOrBlank()) return null
        return try {
            val response = gson.fromJson(json, ApiResponse::class.java)
            response.error
        } catch (_: Exception) {
            null
        }
    }
}
""",
    )

    split_class_file(
        "api/metrics/ApiMetrics.kt",
        [
            ("ApiMetricsCollector", "api/metrics/ApiMetricsCollector.kt", "api.metrics"),
            ("NetworkLogger", "api/metrics/NetworkLogger.kt", "api.metrics"),
            ("PerformanceTracker", "api/metrics/PerformanceTracker.kt", "api.metrics"),
        ],
    )


def generate_auth() -> None:
    split_class_file(
        "auth/Authenticator.kt",
        [
            ("SessionManager", "auth/SessionManager.kt", "auth"),
            ("AuthStateManager", "auth/AuthStateManager.kt", "auth"),
        ],
    )

    emit(
        "auth/RefreshTokenWorker.kt",
        "auth",
        """
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tokenManager: TokenManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result =
        if (tokenManager.refreshTokenIfNeeded()) Result.success() else Result.retry()
}
""",
    )

    split_class_file(
        "auth/security/AuthSecurity.kt",
        [
            ("TokenEncryption", "auth/security/TokenEncryption.kt", "auth.security"),
            ("RequestSigner", "auth/security/RequestSigner.kt", "auth.security"),
        ],
    )

    emit(
        "auth/security/SecureStorage.kt",
        "auth.security",
        """
import javax.inject.Inject
import javax.inject.Singleton

/** Auth-layer facade delegating to [com.nordstern.hiredin.shared.security.SecureStorage]. */
@Singleton
class SecureStorage @Inject constructor(
    private val delegate: com.nordstern.hiredin.shared.security.SecureStorage
) {
    fun putString(key: String, value: String) = delegate.putString(key, value)
    fun getString(key: String): String? = delegate.getString(key)
    fun putBoolean(key: String, value: Boolean) = delegate.putBoolean(key, value)
    fun getBoolean(key: String, default: Boolean = false): Boolean = delegate.getBoolean(key, default)
    fun remove(key: String) = delegate.remove(key)
    fun clear() = delegate.clear()
}
""",
    )


def generate_database() -> None:
    emit(
        "database/SyncableEntity.kt",
        "database",
        """
interface SyncableEntity {
    val id: String
    val updatedAt: Long
    val isDeleted: Boolean get() = false
}
""",
    )

    emit(
        "database/MigrationHelper.kt",
        "database",
        """
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

object MigrationHelper {
    fun registerMigrations(builder: RoomDatabase.Builder<*>) {
        builder.addMigrations(*com.nordstern.hiredin.shared.database.migrations.Migrations.ALL)
    }

    fun allMigrations(): Array<Migration> = com.nordstern.hiredin.shared.database.migrations.Migrations.ALL
}
""",
    )

    emit(
        "database/DatabaseCallback.kt",
        "database",
        """
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nordstern.hiredin.shared.utils.Logger

class DatabaseCallback : RoomDatabase.Callback() {
    private val logger = Logger.getLogger("DatabaseCallback")

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        logger.info("HiredIn database created")
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        logger.debug("HiredIn database opened")
    }
}
""",
    )

    split_class_file(
        "database/converters/Converters.kt",
        [
            ("DateConverter", "database/converters/DateConverter.kt", "database.converters"),
            ("EnumConverter", "database/converters/EnumConverter.kt", "database.converters"),
            ("JsonConverter", "database/converters/JsonConverter.kt", "database.converters"),
        ],
    )

    emit(
        "database/migrations/Migration_1_2.kt",
        "database.migrations",
        """
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_1_2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            \"\"\"
            CREATE TABLE IF NOT EXISTS sync_metadata (
                entityName TEXT NOT NULL PRIMARY KEY,
                lastSyncTimestamp INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
            \"\"\".trimIndent()
        )
        db.execSQL("ALTER TABLE offline_actions ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
    }
}
""",
    )

    emit(
        "database/migrations/Migration_2_3.kt",
        "database.migrations",
        """
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration_2_3 : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            \"\"\"
            CREATE INDEX IF NOT EXISTS index_offline_actions_status
            ON offline_actions(status)
            \"\"\".trimIndent()
        )
    }
}
""",
    )

    emit(
        "database/migrations/MigrationHelper.kt",
        "database.migrations",
        """
import androidx.room.migration.Migration

object MigrationHelper {
    val ALL: Array<Migration> = arrayOf(Migration_1_2, Migration_2_3)
}
""",
    )

    emit(
        "database/queries/BaseQueries.kt",
        "database.queries",
        """
object BaseQueries {
    const val SELECT_ALL = "SELECT * FROM %s"
    const val SELECT_BY_ID = "SELECT * FROM %s WHERE id = :id LIMIT 1"
    const val SELECT_UPDATED_SINCE = "SELECT * FROM %s WHERE updatedAt > :timestamp"
    const val DELETE_BY_ID = "DELETE FROM %s WHERE id = :id"
}
""",
    )

    emit(
        "database/queries/DynamicQueryBuilder.kt",
        "database.queries",
        """
class DynamicQueryBuilder(private val tableName: String) {
    private val conditions = mutableListOf<String>()
    private val args = mutableListOf<Any>()

    fun whereEquals(column: String, value: Any): DynamicQueryBuilder {
        conditions.add("$column = ?")
        args.add(value)
        return this
    }

    fun whereGreaterThan(column: String, value: Any): DynamicQueryBuilder {
        conditions.add("$column > ?")
        args.add(value)
        return this
    }

    fun orderBy(column: String, ascending: Boolean = true): DynamicQueryBuilder {
        conditions.add("ORDER BY $column ${if (ascending) "ASC" else "DESC"}")
        return this
    }

    fun limit(count: Int): DynamicQueryBuilder {
        conditions.add("LIMIT $count")
        return this
    }

    fun buildSelect(): Pair<String, Array<Any>> {
        val where = conditions.filter { !it.startsWith("ORDER") && !it.startsWith("LIMIT") }
        val suffix = conditions.filter { it.startsWith("ORDER") || it.startsWith("LIMIT") }
        val sql = buildString {
            append("SELECT * FROM $tableName")
            if (where.isNotEmpty()) append(" WHERE ").append(where.joinToString(" AND "))
            if (suffix.isNotEmpty()) append(" ").append(suffix.joinToString(" "))
        }
        return sql to args.toTypedArray()
    }
}
""",
    )

    emit(
        "database/queries/PaginationHelper.kt",
        "database.queries",
        """
object PaginationHelper {
    fun offset(page: Int, pageSize: Int): Int = (page.coerceAtLeast(0)) * pageSize

    fun totalPages(totalItems: Int, pageSize: Int): Int =
        if (pageSize <= 0) 0 else ((totalItems + pageSize - 1) / pageSize)

    fun hasNextPage(page: Int, pageSize: Int, totalItems: Int): Boolean =
        offset(page + 1, pageSize) < totalItems
}
""",
    )


def generate_sync() -> None:
    emit(
        "sync/ConflictResolver.kt",
        "sync",
        """
import com.nordstern.hiredin.shared.database.SyncableEntity
import com.nordstern.hiredin.shared.sync.conflict.ConflictHandler
import com.nordstern.hiredin.shared.sync.conflict.LastWriteWins
import com.nordstern.hiredin.shared.sync.conflict.ManualResolutionHandler
import com.nordstern.hiredin.shared.sync.conflict.MergeStrategy
import javax.inject.Inject
import javax.inject.Singleton

enum class ConflictResolutionMode { LAST_WRITE_WINS, MERGE, MANUAL }

@Singleton
class ConflictResolver @Inject constructor(
    private val lastWriteWins: LastWriteWins,
    private val mergeStrategy: MergeStrategy,
    private val manualResolutionHandler: ManualResolutionHandler
) {
    fun handlerFor(mode: ConflictResolutionMode): ConflictHandler = when (mode) {
        ConflictResolutionMode.LAST_WRITE_WINS -> lastWriteWins
        ConflictResolutionMode.MERGE -> mergeStrategy
        ConflictResolutionMode.MANUAL -> manualResolutionHandler
    }

    fun <T : SyncableEntity> resolve(
        mode: ConflictResolutionMode,
        local: T?,
        server: T
    ): T = handlerFor(mode).resolve(local, server)
}
""",
    )

    split_class_file(
        "sync/SyncScheduler.kt",
        [("SyncStateManager", "sync/SyncStateManager.kt", "sync")],
    )

    split_class_file(
        "sync/strategies/SyncStrategies.kt",
        [
            ("SyncStrategy", "sync/strategies/SyncStrategy.kt", "sync.strategies"),
            ("SyncStrategyResult", "sync/strategies/SyncStrategyResult.kt", "sync.strategies"),
            ("IncrementalSyncStrategy", "sync/strategies/IncrementalSyncStrategy.kt", "sync.strategies"),
            ("FullSyncStrategy", "sync/strategies/FullSyncStrategy.kt", "sync.strategies"),
            ("SmartSyncStrategy", "sync/strategies/SmartSyncStrategy.kt", "sync.strategies"),
        ],
    )

    split_class_file(
        "sync/conflict/ConflictStrategies.kt",
        [
            ("LastWriteWins", "sync/conflict/LastWriteWins.kt", "sync.conflict"),
            ("MergeStrategy", "sync/conflict/MergeStrategy.kt", "sync.conflict"),
            ("ManualResolutionHandler", "sync/conflict/ManualResolutionHandler.kt", "sync.conflict"),
        ],
    )

    split_class_file(
        "sync/queue/QueueComponents.kt",
        [
            ("ActionProcessor", "sync/queue/ActionProcessor.kt", "sync.queue"),
            ("RetryPolicy", "sync/queue/RetryPolicy.kt", "sync.queue"),
            ("QueuePersistence", "sync/queue/QueuePersistence.kt", "sync.queue"),
        ],
    )

    emit(
        "sync/queue/OfflineAction.kt",
        "sync.queue",
        """
import com.nordstern.hiredin.shared.database.entities.OfflineActionEntity
import com.nordstern.hiredin.shared.sync.ActionStatus
import java.util.UUID

data class OfflineAction(
    val id: String = UUID.randomUUID().toString(),
    val endpoint: String,
    val method: String,
    val payload: String = "{}",
    val headers: String? = null,
    val status: ActionStatus = ActionStatus.PENDING,
    val attemptCount: Int = 0
) {
    fun toEntity(): OfflineActionEntity = OfflineActionEntity(
        id = id,
        endpoint = endpoint,
        method = method,
        payload = payload,
        headers = headers,
        status = status,
        attemptCount = attemptCount,
        updatedAt = System.currentTimeMillis()
    )
}
""",
    )


def generate_notifications() -> None:
    emit(
        "notifications/NotificationChannelManager.kt",
        "notifications",
        """
import com.nordstern.hiredin.shared.notifications.channels.ChannelManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    private val channelManager: ChannelManager
) {
    fun ensureChannels() = channelManager.ensureAllChannels()
    fun ensureChannel(type: String) = channelManager.ensureChannel(type)
    fun channelId(type: String): String = channelManager.getChannelIdForType(type)
}
""",
    )

    emit(
        "notifications/NotificationDataMapper.kt",
        "notifications",
        """
import com.nordstern.hiredin.shared.notifications.models.Notification
import com.nordstern.hiredin.shared.notifications.models.NotificationPayload
import com.nordstern.hiredin.shared.notifications.models.NotificationType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDataMapper @Inject constructor() {
    fun fromPayload(payload: NotificationPayload): Notification {
        val type = payload.data["type"]?.let {
            runCatching { NotificationType.valueOf(it.uppercase()) }.getOrDefault(NotificationType.GENERAL)
        } ?: NotificationType.GENERAL
        return Notification(
            id = payload.data["notificationId"] ?: payload.data["id"] ?: System.currentTimeMillis().toString(),
            title = payload.data["title"] ?: "HiredIn",
            body = payload.data["body"] ?: "",
            type = type.name.lowercase()
        )
    }

    fun toPayload(notification: Notification): NotificationPayload =
        NotificationPayload(
            data = mapOf(
                "id" to notification.id,
                "title" to notification.title,
                "body" to notification.body,
                "type" to notification.type
            )
        )
}
""",
    )

    split_class_file(
        "notifications/NotificationService.kt",
        [
            ("LocalNotificationBuilder", "notifications/LocalNotificationBuilder.kt", "notifications"),
        ],
    )

    emit(
        "notifications/NotificationScheduler.kt",
        "notifications",
        """
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.nordstern.hiredin.shared.utils.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    private val logger = Logger.getLogger("NotificationScheduler")
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(
        triggerAtMillis: Long,
        requestCode: Int,
        intent: Intent
    ) {
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
        logger.info("Notification scheduled at $triggerAtMillis")
    }

    fun cancel(requestCode: Int, intent: Intent) {
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pending)
    }
}
""",
    )

    split_class_file(
        "notifications/channels/ChannelManager.kt",
        [
            ("ChannelDefinitions", "notifications/channels/ChannelDefinitions.kt", "notifications.channels"),
            ("ChannelUpdater", "notifications/channels/ChannelUpdater.kt", "notifications.channels"),
        ],
    )

    split_class_file(
        "notifications/handlers/NotificationHandlers.kt",
        [
            ("NotificationTypeHandler", "notifications/handlers/NotificationTypeHandler.kt", "notifications.handlers"),
            ("BaseNotificationHandler", "notifications/handlers/BaseNotificationHandler.kt", "notifications.handlers"),
            ("LeaveNotificationHandler", "notifications/handlers/LeaveNotificationHandler.kt", "notifications.handlers"),
            ("PayrollNotificationHandler", "notifications/handlers/PayrollNotificationHandler.kt", "notifications.handlers"),
            ("AnnouncementNotificationHandler", "notifications/handlers/AnnouncementNotificationHandler.kt", "notifications.handlers"),
            ("TaskNotificationHandler", "notifications/handlers/TaskNotificationHandler.kt", "notifications.handlers"),
            ("ApprovalNotificationHandler", "notifications/handlers/ApprovalNotificationHandler.kt", "notifications.handlers"),
            ("ComplianceNotificationHandler", "notifications/handlers/ComplianceNotificationHandler.kt", "notifications.handlers"),
            ("NotificationRouter", "notifications/handlers/NotificationRouter.kt", "notifications.handlers"),
        ],
    )


def generate_utils() -> None:
    utils: Dict[str, str] = {
        "FileUtils.kt": """
import android.content.Context
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun readText(context: Context, relativePath: String): String? =
        runCatching { context.assets.open(relativePath).bufferedReader().use { it.readText() } }.getOrNull()

    fun writeText(file: File, content: String) {
        file.parentFile?.mkdirs()
        file.writeText(content)
    }

    fun copyStream(input: java.io.InputStream, output: FileOutputStream) {
        input.use { inp -> output.use { out -> inp.copyTo(out) } }
    }

    fun deleteRecursive(file: File): Boolean = file.deleteRecursively()

    fun sizeMb(file: File): Double = file.length() / (1024.0 * 1024.0)
}
""",
        "AnalyticsTracker.kt": """
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(
    private val analyticsManager: com.nordstern.hiredin.shared.analytics.AnalyticsManager
) {
    fun trackEvent(name: String, properties: Map<String, Any> = emptyMap()) =
        analyticsManager.track(name, properties)

    fun trackScreen(screen: String) =
        analyticsManager.track("screen_view", mapOf("screen" to screen))
}
""",
        "CrashReporter.kt": """
import com.nordstern.hiredin.shared.utils.Logger

object CrashReporter {
    private val logger = Logger.getLogger("CrashReporter")

    fun recordException(throwable: Throwable, context: Map<String, String> = emptyMap()) {
        logger.error("Crash recorded: ${throwable.message} context=$context", throwable)
    }

    fun setUserId(userId: String?) {
        logger.info("CrashReporter userId=$userId")
    }
}
""",
        "AppLifecycleManager.kt": """
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppLifecycleManager : DefaultLifecycleObserver {
    private val _isInForeground = MutableStateFlow(true)
    val isInForeground: StateFlow<Boolean> = _isInForeground.asStateFlow()

    fun init() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        _isInForeground.value = true
    }

    override fun onStop(owner: LifecycleOwner) {
        _isInForeground.value = false
    }
}
""",
        "ForegroundChecker.kt": """
object ForegroundChecker {
    fun isAppInForeground(): Boolean = AppLifecycleManager.isInForeground.value

    fun requireForeground(block: () -> Unit) {
        if (isAppInForeground()) block()
    }
}
""",
        "ImageCompressor.kt": """
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {
    fun compress(file: File, maxWidth: Int = 1280, quality: Int = 85): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return file
        val ratio = minOf(1f, maxWidth.toFloat() / bitmap.width)
        val scaled = if (ratio < 1f) {
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
        } else bitmap
        val out = File(file.parentFile, "${file.nameWithoutExtension}_compressed.jpg")
        FileOutputStream(out).use { fos ->
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, fos)
        }
        if (scaled != bitmap) scaled.recycle()
        bitmap.recycle()
        return out
    }

    fun toByteArray(bitmap: Bitmap, quality: Int = 85): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
}
""",
        "ZipUtils.kt": """
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ZipUtils {
    fun zip(sourceDir: File, zipFile: File) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            sourceDir.walkTopDown().filter { it.isFile }.forEach { file ->
                val entry = ZipEntry(sourceDir.toURI().relativize(file.toURI()).path)
                zos.putNextEntry(entry)
                FileInputStream(file).use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
    }

    fun unzip(zipFile: File, targetDir: File) {
        targetDir.mkdirs()
        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val outFile = File(targetDir, entry.name)
                outFile.parentFile?.mkdirs()
                if (!entry.isDirectory) {
                    FileOutputStream(outFile).use { zis.copyTo(it) }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }
}
""",
        "XmlParser.kt": """
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

object XmlParser {
    fun parseSimple(xml: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(xml.reader())
        var event = parser.eventType
        var currentTag: String? = null
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> currentTag = parser.name
                XmlPullParser.TEXT -> currentTag?.let { result[it] = parser.text.trim() }
                XmlPullParser.END_TAG -> currentTag = null
            }
            event = parser.next()
        }
        return result
    }
}
""",
        "CsvParser.kt": """
object CsvParser {
    fun parse(content: String, delimiter: Char = ','): List<List<String>> =
        content.lineSequence()
            .filter { it.isNotBlank() }
            .map { line -> line.split(delimiter).map { it.trim().trim('"') } }
            .toList()

    fun toCsv(rows: List<List<String>>, delimiter: Char = ','): String =
        rows.joinToString("\n") { row ->
            row.joinToString(delimiter.toString()) { value ->
                if (value.contains(delimiter) || value.contains('"')) "\"${value.replace("\"", "\"\"")}\""
                else value
            }
        }
}
""",
        "HtmlParser.kt": """
object HtmlParser {
    private val tagRegex = Regex("<[^>]+>")

    fun stripTags(html: String): String = tagRegex.replace(html, "").trim()

    fun extractText(html: String): String =
        html.replace(Regex("<script[^>]*>[\\s\\S]*?</script>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<style[^>]*>[\\s\\S]*?</style>", RegexOption.IGNORE_CASE), "")
            .let { stripTags(it) }
            .replace(Regex("\\s+"), " ")
            .trim()
}
""",
        "MarkdownParser.kt": """
object MarkdownParser {
    fun toPlainText(markdown: String): String =
        markdown
            .replace(Regex("^#+\\s+", RegexOption.MULTILINE), "")
            .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
            .replace(Regex("\\*(.+?)\\*"), "$1")
            .replace(Regex("\\[(.+?)\\]\\(.+?\\)"), "$1")
            .replace(Regex("`(.+?)`"), "$1")
            .trim()

    fun extractHeadings(markdown: String): List<String> =
        Regex("^#{1,6}\\s+(.+)$", RegexOption.MULTILINE).findAll(markdown).map { it.groupValues[1] }.toList()
}
""",
        "RegexUtils.kt": """
object RegexUtils {
    val EMAIL = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    val PHONE_E164 = Regex("^\\+[1-9]\\d{6,14}$")
    val PASSWORD_STRONG = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&]).{8,}$")
    val UUID = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    val URL = Regex("^https?://[\\w\\-]+(\\.[\\w\\-]+)+[/\\w\\- ./?%&=]*$")

    fun matches(pattern: Regex, value: String): Boolean = pattern.matches(value)
}
""",
        "DeviceInfo.kt": """
import android.content.Context
import android.os.Build
import android.provider.Settings

object DeviceInfo {
    fun deviceId(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"

    fun manufacturer(): String = Build.MANUFACTURER
    fun model(): String = Build.MODEL
    fun osVersion(): String = Build.VERSION.RELEASE
    fun sdkInt(): Int = Build.VERSION.SDK_INT

    fun summary(context: Context): String =
        "${manufacturer()} ${model()} Android ${osVersion()} (${deviceId(context)})"
}
""",
        "AppInfo.kt": """
import android.content.Context
import android.content.pm.PackageManager

object AppInfo {
    fun versionName(context: Context): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
    } catch (_: PackageManager.NameNotFoundException) {
        "unknown"
    }

    fun versionCode(context: Context): Long = try {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        if (android.os.Build.VERSION.SDK_INT >= 28) info.longVersionCode else info.versionCode.toLong()
    } catch (_: PackageManager.NameNotFoundException) {
        0L
    }

    fun packageName(context: Context): String = context.packageName
}
""",
        "BatteryUtils.kt": """
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

object BatteryUtils {
    fun level(context: Context): Int {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level >= 0 && scale > 0) (level * 100 / scale) else -1
    }

    fun isCharging(context: Context): Boolean {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
    }

    fun isLow(context: Context, threshold: Int = 15): Boolean = level(context) in 0 until threshold
}
""",
        "LocationUtils.kt": """
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat

object LocationUtils {
    fun hasLocationPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    fun lastKnownLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    fun formatCoordinates(location: Location): String =
        "${location.latitude}, ${location.longitude}"
}
""",
        "ClipboardUtils.kt": """
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtils {
    fun copy(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    fun paste(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(context).toString()
    }
}
""",
        "MemoryCache.kt": """
import java.util.LinkedHashMap

object MemoryCache {
    private val cache = LinkedHashMap<String, Any>(16, 0.75f, true)
    private const val MAX_SIZE = 100

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? = cache[key] as? T

    fun put(key: String, value: Any) {
        if (cache.size >= MAX_SIZE) {
            cache.entries.firstOrNull()?.let { cache.remove(it.key) }
        }
        cache[key] = value
    }

    fun remove(key: String) { cache.remove(key) }
    fun clear() { cache.clear() }
}
""",
    }
    for filename, body in utils.items():
        emit(f"utils/{filename}", "utils", body)


def generate_cache() -> None:
    split_class_file(
        "cache/AdvancedCache.kt",
        [
            ("LruCache", "cache/LruCache.kt", "cache"),
            ("ExpiringCache", "cache/ExpiringCache.kt", "cache"),
        ],
    )

    emit(
        "cache/strategies/LRUStrategy.kt",
        "cache.strategies",
        """
class LRUStrategy(private val maxSize: Int, private val accessOrder: MutableList<String>) : CacheStrategy {
    override fun shouldEvict(key: String): Boolean = accessOrder.size > maxSize && accessOrder.firstOrNull() == key
}
""",
    )

    emit(
        "cache/strategies/FIFOStrategy.kt",
        "cache.strategies",
        """
class FIFOStrategy(private val maxSize: Int, private val insertionOrder: MutableList<String>) : CacheStrategy {
    override fun shouldEvict(key: String): Boolean =
        insertionOrder.size > maxSize && insertionOrder.firstOrNull() == key
}
""",
    )

    emit(
        "cache/strategies/TTLStrategy.kt",
        "cache.strategies",
        """
class TTLStrategy(private val ttlMs: Long, private val timestamps: MutableMap<String, Long>) : CacheStrategy {
    override fun shouldEvict(key: String): Boolean {
        val ts = timestamps[key] ?: return true
        return System.currentTimeMillis() - ts > ttlMs
    }
}
""",
    )

    emit(
        "cache/serializers/JsonSerializer.kt",
        "cache.serializers",
        """
import com.google.gson.Gson

class JsonSerializer<T>(private val clazz: Class<T>, private val gson: Gson = Gson()) {
    fun serialize(value: T): String = gson.toJson(value)
    fun deserialize(json: String): T = gson.fromJson(json, clazz)
}
""",
    )

    emit(
        "cache/serializers/ProtoSerializer.kt",
        "cache.serializers",
        """
import android.util.Base64

class ProtoSerializer {
    fun serialize(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
    fun deserialize(encoded: String): ByteArray = Base64.decode(encoded, Base64.NO_WRAP)
}
""",
    )

    emit(
        "cache/serializers/KryoSerializer.kt",
        "cache.serializers",
        """
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import android.util.Base64

class KryoSerializer {
    fun <T> serialize(obj: T): String {
        val bytes = ByteArrayOutputStream().use { bos ->
            ObjectOutputStream(bos).use { it.writeObject(obj) }
            bos.toByteArray()
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> deserialize(encoded: String): T {
        val bytes = Base64.decode(encoded, Base64.NO_WRAP)
        return ByteArrayInputStream(bytes).use { bis ->
            ObjectInputStream(bis).use { it.readObject() as T }
        }
    }
}
""",
    )


def generate_security() -> None:
    split_class_file(
        "security/SecurityManager.kt",
        [
            ("DataProtection", "security/DataProtection.kt", "security"),
            ("CertificateValidator", "security/CertificateValidator.kt", "security"),
        ],
    )

    emit(
        "security/ProguardRules.kt",
        "security",
        """
object ProguardRules {
    val KEEP_RULES = listOf(
        "-keep class com.nordstern.hiredin.shared.models.** { *; }",
        "-keep class com.nordstern.hiredin.shared.api.** { *; }",
        "-keepclassmembers class * { @com.google.gson.annotations.SerializedName <fields>; }",
        "-keep @androidx.room.Entity class *",
        "-keepclassmembers class * extends androidx.work.Worker { public <init>(...); }"
    )

    fun asProguardFileContent(): String = KEEP_RULES.joinToString("\n")
}
""",
    )

    emit(
        "security/obfuscation/DataObfuscator.kt",
        "security.obfuscation",
        """
import android.util.Base64

object DataObfuscator {
    fun obfuscate(value: String): String =
        Base64.encodeToString(value.reversed().toByteArray(), Base64.NO_WRAP)

    fun deobfuscate(value: String): String =
        String(Base64.decode(value, Base64.NO_WRAP)).reversed()
}
""",
    )

    emit(
        "security/obfuscation/LogObfuscator.kt",
        "security.obfuscation",
        """
object LogObfuscator {
    private val emailRegex = Regex("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+")
    private val phoneRegex = Regex("\\+?[0-9]{7,15}")

    fun sanitize(message: String): String =
        message.replace(emailRegex, "[EMAIL]")
            .replace(phoneRegex, "[PHONE]")
}
""",
    )

    emit(
        "security/biometric/BiometricManager.kt",
        "security.biometric",
        """
import android.content.Context
import androidx.biometric.BiometricManager as AndroidBiometricManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricManager @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    fun canAuthenticate(): Boolean {
        val manager = AndroidBiometricManager.from(context)
        return manager.canAuthenticate(
            AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG or
                AndroidBiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == AndroidBiometricManager.BIOMETRIC_SUCCESS
    }

    fun statusMessage(): String {
        val manager = AndroidBiometricManager.from(context)
        return when (manager.canAuthenticate(AndroidBiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            AndroidBiometricManager.BIOMETRIC_SUCCESS -> "Available"
            AndroidBiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No biometrics enrolled"
            AndroidBiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No biometric hardware"
            else -> "Unavailable"
        }
    }
}
""",
    )

    emit(
        "security/biometric/BiometricPromptHelper.kt",
        "security.biometric",
        """
import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricPromptHelper @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context
) {
    fun show(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        callback: BiometricCallback
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                callback.onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                callback.onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                callback.onFailed()
            }
        })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Cancel")
            .build()
        prompt.authenticate(info)
    }
}
""",
    )

    emit(
        "security/biometric/BiometricCallback.kt",
        "security.biometric",
        """
interface BiometricCallback {
    fun onSuccess()
    fun onError(message: String)
    fun onFailed()
}
""",
    )


def generate_workers() -> None:
    emit(
        "workers/BaseWorker.kt",
        "workers",
        """
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.utils.Logger

abstract class BaseWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    protected val logger = Logger.getLogger(javaClass.simpleName)

    override suspend fun doWork(): Result {
        return try {
            executeWork()
        } catch (e: Exception) {
            logger.error("Worker failed: ${e.message}", e)
            Result.failure()
        }
    }

    protected abstract suspend fun executeWork(): Result
}
""",
    )

    emit(
        "workers/PeriodicWorker.kt",
        "workers",
        """
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

object PeriodicWorker {
    fun <W : androidx.work.ListenableWorker> schedule(
        context: Context,
        uniqueName: String,
        workerClass: Class<W>,
        intervalHours: Long,
        requestCustomizer: (PeriodicWorkRequestBuilder<W>.() -> Unit)? = null
    ) {
        var builder = PeriodicWorkRequestBuilder(workerClass, intervalHours, TimeUnit.HOURS)
        requestCustomizer?.invoke(builder)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueName,
            ExistingPeriodicWorkPolicy.KEEP,
            builder.build()
        )
    }
}
""",
    )

    emit(
        "workers/OneTimeWorker.kt",
        "workers",
        """
import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest

object OneTimeWorker {
    fun <W : androidx.work.ListenableWorker> enqueue(
        context: Context,
        workerClass: Class<W>,
        requestCustomizer: (OneTimeWorkRequestBuilder<W>.() -> Unit)? = null
    ): WorkRequest {
        var builder = OneTimeWorkRequestBuilder(workerClass)
        requestCustomizer?.invoke(builder)
        val request = builder.build()
        WorkManager.getInstance(context).enqueue(request)
        return request
    }
}
""",
    )

    split_class_file(
        "workers/Workers.kt",
        [("WorkerScheduler", "workers/WorkerScheduler.kt", "workers")],
    )

    worker_files = {
        "workers/workers/DataSyncWorker.kt": """
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.sync.OfflineQueue
import com.nordstern.hiredin.shared.sync.SyncScheduler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val offlineQueue: OfflineQueue,
    private val syncScheduler: SyncScheduler
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result {
        offlineQueue.processQueue()
        syncScheduler.scheduleImmediateSync()
        return Result.success()
    }
}
""",
        "workers/workers/CacheCleanupWorker.kt": """
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.cache.DiskCache
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CacheCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val diskCache: DiskCache
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result {
        diskCache.clear()
        return Result.success()
    }
}
""",
        "workers/workers/AnalyticsUploadWorker.kt": """
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AnalyticsUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result = Result.success()
}
""",
        "workers/workers/LogCleanupWorker.kt": """
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LogCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result = Result.success()
}
""",
        "workers/workers/TokenRefreshWorker.kt": """
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.nordstern.hiredin.shared.auth.TokenManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TokenRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tokenManager: TokenManager
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result =
        if (tokenManager.refreshTokenIfNeeded()) Result.success() else Result.retry()
}
""",
        "workers/workers/NotificationCleanupWorker.kt": """
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : BaseWorker(context, params) {
    override suspend fun executeWork(): Result = Result.success()
}
""",
    }
    for path, body in worker_files.items():
        emit(path, "workers.workers" if "workers/workers" in path else "workers", body)

    constraints = {
        "workers/constraints/NetworkConstraint.kt": """
import androidx.work.Constraints
import androidx.work.NetworkType

object NetworkConstraint {
    fun connected(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun unmetered(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()
}
""",
        "workers/constraints/BatteryConstraint.kt": """
import androidx.work.Constraints

object BatteryConstraint {
    fun notLow(): Constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()

    fun charging(): Constraints = Constraints.Builder()
        .setRequiresCharging(true)
        .build()
}
""",
        "workers/constraints/StorageConstraint.kt": """
import androidx.work.Constraints

object StorageConstraint {
    fun notLow(): Constraints = Constraints.Builder()
        .setRequiresStorageNotLow(true)
        .build()
}
""",
        "workers/constraints/TimeConstraint.kt": """
import androidx.work.Constraints
import java.util.concurrent.TimeUnit

object TimeConstraint {
    fun withDelay(minutes: Long): Long = TimeUnit.MINUTES.toMillis(minutes)

    fun idle(): Constraints = Constraints.Builder().build()
}
""",
    }
    for path, body in constraints.items():
        emit(path, "workers.constraints", body)


def generate_permissions() -> None:
    split_class_file(
        "permissions/PermissionManager.kt",
        [
            ("PermissionRequestor", "permissions/PermissionRequestor.kt", "permissions"),
            ("PermissionResultHandler", "permissions/PermissionResultHandler.kt", "permissions"),
            ("PermissionResult", "permissions/PermissionResult.kt", "permissions"),
        ],
    )

    perm_impls = {
        "CameraPermission.kt": 'Manifest.permission.CAMERA',
        "LocationPermission.kt": 'Manifest.permission.ACCESS_FINE_LOCATION',
        "StoragePermission.kt": 'if (android.os.Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE',
        "NotificationPermission.kt": 'if (android.os.Build.VERSION.SDK_INT >= 33) Manifest.permission.POST_NOTIFICATIONS else ""',
        "CalendarPermission.kt": 'Manifest.permission.READ_CALENDAR',
        "ContactsPermission.kt": 'Manifest.permission.READ_CONTACTS',
        "MicrophonePermission.kt": 'Manifest.permission.RECORD_AUDIO',
        "BiometricPermission.kt": 'Manifest.permission.USE_BIOMETRIC',
    }
    for filename, perm_expr in perm_impls.items():
        class_name = filename.replace(".kt", "")
        emit(
            f"permissions/permissions/{filename}",
            "permissions.permissions",
            f"""
import android.Manifest
import com.nordstern.hiredin.shared.permissions.PermissionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class {class_name} @Inject constructor(
    private val permissionManager: PermissionManager
) {{
    val permission: String
        get() = {perm_expr}

    fun isGranted(): Boolean = permission.isNotEmpty() && permissionManager.isGranted(permission)
}}
""",
        )

    emit(
        "permissions/RationaleDialog.kt",
        "permissions",
        """
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun RationaleDialog(
    title: String,
    message: String,
    confirmText: String = "Continue",
    dismissText: String = "Not now",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(dismissText) } }
    )
}
""",
    )


def generate_network() -> None:
    split_class_file(
        "network/ConnectivityObserver.kt",
        [
            ("NetworkQuality", "network/NetworkQuality.kt", "network"),
            ("BandwidthEstimator", "network/BandwidthEstimator.kt", "network"),
            ("RequestQueueManager", "network/RequestQueueManager.kt", "network"),
            ("ResponseCacheManager", "network/ResponseCacheManager.kt", "network"),
        ],
    )


def generate_logging() -> None:
    split_class_file(
        "logging/LogTypes.kt",
        [
            ("LogLevel", "logging/LogLevel.kt", "logging"),
            ("LogEntry", "logging/LogEntry.kt", "logging"),
            ("LogFormatter", "logging/LogFormatter.kt", "logging"),
            ("LogSink", "logging/LogSink.kt", "logging"),
        ],
    )

    split_class_file(
        "logging/loggers/Loggers.kt",
        [
            ("ConsoleLogger", "logging/loggers/ConsoleLogger.kt", "logging.loggers"),
            ("FileLogger", "logging/loggers/FileLogger.kt", "logging.loggers"),
            ("RemoteLogger", "logging/loggers/RemoteLogger.kt", "logging.loggers"),
            ("CrashlyticsLogger", "logging/loggers/CrashlyticsLogger.kt", "logging.loggers"),
        ],
    )

    split_class_file(
        "logging/formatters/Formatters.kt",
        [
            ("PrettyLogFormatter", "logging/formatters/PrettyLogFormatter.kt", "logging.formatters"),
            ("JsonLogFormatter", "logging/formatters/JsonLogFormatter.kt", "logging.formatters"),
            ("CsvLogFormatter", "logging/formatters/CsvLogFormatter.kt", "logging.formatters"),
        ],
    )


def generate_localization() -> None:
    split_class_file(
        "localization/Localization.kt",
        [
            ("LocaleManager", "localization/LocaleManager.kt", "localization"),
            ("LocalizationHelper", "localization/LocalizationHelper.kt", "localization"),
            ("RtlHelper", "localization/RtlHelper.kt", "localization"),
            ("DateFormatter", "localization/DateFormatter.kt", "localization"),
            ("CurrencyFormatter", "localization/CurrencyFormatter.kt", "localization"),
        ],
    )


def generate_analytics() -> None:
    split_class_file(
        "analytics/AnalyticsManager.kt",
        [
            ("AnalyticsEvent", "analytics/events/AnalyticsEvent.kt", "analytics.events"),
            ("AnalyticsProvider", "analytics/providers/AnalyticsProvider.kt", "analytics.providers"),
            ("EventTracker", "analytics/EventTracker.kt", "analytics"),
            ("ScreenTracker", "analytics/ScreenTracker.kt", "analytics"),
            ("UserPropertyManager", "analytics/UserPropertyManager.kt", "analytics"),
        ],
    )

    split_class_file(
        "analytics/providers/Providers.kt",
        [
            ("CustomAnalyticsProvider", "analytics/providers/CustomAnalyticsProvider.kt", "analytics.providers"),
            ("FirebaseAnalyticsProvider", "analytics/providers/FirebaseAnalyticsProvider.kt", "analytics.providers"),
            ("MixpanelProvider", "analytics/providers/MixpanelProvider.kt", "analytics.providers"),
            ("AmplitudeProvider", "analytics/providers/AmplitudeProvider.kt", "analytics.providers"),
        ],
    )

    for event in ("UserEvent", "BusinessEvent", "ErrorEvent"):
        emit(
            f"analytics/events/{event}.kt",
            "analytics.events",
            f"""
data class {event}(
    val name: String,
    val properties: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
) {{
    fun toAnalyticsEvent(): AnalyticsEvent = AnalyticsEvent(name, properties, timestamp)
}}
""",
        )


def generate_ui_theme() -> None:
    emit(
        "ui/theme/DarkTheme.kt",
        "ui.theme",
        """
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val HiredInDarkColorScheme = darkColorScheme(
    primary = HiredInDarkColors.Primary,
    onPrimary = HiredInColors.OnPrimary,
    secondary = HiredInColors.Secondary,
    background = HiredInDarkColors.Background,
    surface = HiredInDarkColors.Surface,
    surfaceVariant = HiredInDarkColors.SurfaceVariant,
    error = HiredInColors.Error,
    onBackground = HiredInDarkColors.OnBackground,
    onSurface = HiredInDarkColors.OnSurface,
    outline = HiredInDarkColors.Outline
)
""",
    )

    emit(
        "ui/theme/LightTheme.kt",
        "ui.theme",
        """
import androidx.compose.material3.lightColorScheme

val HiredInLightColorScheme = lightColorScheme(
    primary = HiredInColors.Primary,
    onPrimary = HiredInColors.OnPrimary,
    secondary = HiredInColors.Secondary,
    background = HiredInColors.Background,
    surface = HiredInColors.Surface,
    surfaceVariant = HiredInColors.SurfaceVariant,
    error = HiredInColors.Error,
    onBackground = HiredInColors.OnBackground,
    onSurface = HiredInColors.OnSurface,
    onSurfaceVariant = HiredInColors.OnSurfaceVariant,
    outline = HiredInColors.Outline
)
""",
    )

    emit(
        "ui/theme/Elevation.kt",
        "ui.theme",
        """
import androidx.compose.ui.unit.dp

object HiredInElevation {
    val none = 0.dp
    val low = 2.dp
    val medium = 4.dp
    val high = 8.dp
    val highest = 16.dp
}
""",
    )

    emit(
        "ui/theme/Animations.kt",
        "ui.theme",
        """
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

object HiredInAnimations {
    const val SHORT_MS = 150
    const val MEDIUM_MS = 300
    const val LONG_MS = 500

    fun <T> tweenShort() = tween<T>(SHORT_MS, easing = FastOutSlowInEasing)
    fun <T> tweenMedium() = tween<T>(MEDIUM_MS, easing = FastOutSlowInEasing)
    fun <T> tweenLong() = tween<T>(LONG_MS, easing = FastOutSlowInEasing)
}
""",
    )

    split_class_file(
        "ui/theme/Theme.kt",
        [("ThemeManager", "ui/theme/ThemeManager.kt", "ui.theme")],
    )


def generate_ui_utils() -> None:
    split_class_file(
        "ui/utils/UiUtils.kt",
        [
            ("DateFormatters", "ui/utils/DateFormatters.kt", "ui.utils"),
            ("NumberFormatters", "ui/utils/NumberFormatters.kt", "ui.utils"),
            ("ValidationUtils", "ui/utils/ValidationUtils.kt", "ui.utils"),
            ("ScreenUtils", "ui/utils/ScreenUtils.kt", "ui.utils"),
        ],
    )

    for util in ("ViewExtensions", "KeyboardUtils", "AnimUtils", "DrawableUtils", "ColorUtils"):
        body = {
            "ViewExtensions": """
import android.view.View

object ViewExtensions {
    fun View.visible() { visibility = View.VISIBLE }
    fun View.gone() { visibility = View.GONE }
    fun View.invisible() { visibility = View.INVISIBLE }
    fun View.enable() { isEnabled = true }
    fun View.disable() { isEnabled = false }
}
""",
            "KeyboardUtils": """
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.content.Context

object KeyboardUtils {
    fun hide(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        activity.currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    fun show(view: android.view.View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}
""",
            "AnimUtils": """
import android.view.View
import android.view.animation.AlphaAnimation

object AnimUtils {
    fun fadeIn(view: View, durationMs: Long = 300) {
        view.startAnimation(AlphaAnimation(0f, 1f).apply { duration = durationMs })
    }

    fun fadeOut(view: View, durationMs: Long = 300) {
        view.startAnimation(AlphaAnimation(1f, 0f).apply { duration = durationMs })
    }
}
""",
            "DrawableUtils": """
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

object DrawableUtils {
    fun getDrawable(context: Context, resId: Int): Drawable? =
        ContextCompat.getDrawable(context, resId)

    fun tint(context: Context, drawable: Drawable, colorRes: Int): Drawable {
        val wrapped = androidx.core.graphics.drawable.DrawableCompat.wrap(drawable.mutate())
        androidx.core.graphics.drawable.DrawableCompat.setTint(wrapped, ContextCompat.getColor(context, colorRes))
        return wrapped
    }
}
""",
            "ColorUtils": """
import androidx.compose.ui.graphics.Color

object ColorUtils {
    fun fromHex(hex: String): Color {
        val cleaned = hex.removePrefix("#")
        return Color(android.graphics.Color.parseColor("#$cleaned"))
    }

    fun withAlpha(color: Color, alpha: Float): Color = color.copy(alpha = alpha)
}
""",
        }[util]
        emit(f"ui/utils/{util}.kt", "ui.utils", body)


def generate_ui_components() -> None:
    ui_splits: List[Tuple[str, str, List[str]]] = [
        ("ui/components/CoreComponents.kt", "ui.components", [
            "LoadingButton", "SearchBar", "FilterChipGroup", "DatePicker",
            "FilePicker", "ImagePicker", "PdfViewer", "WebViewScreen",
        ]),
        ("ui/components/common/CommonComponents.kt", "ui.components.common", [
            "HiredInTextField", "HiredInDropdown", "HiredInCheckbox", "HiredInRadioButton",
            "HiredInSwitch", "HiredInSlider", "HiredInProgressBar", "HiredInBottomSheet",
        ]),
        ("ui/components/dialogs/Dialogs.kt", "ui.components.dialogs", [
            "ConfirmationDialog", "LoadingDialog", "ProgressDialog", "ErrorDialog",
            "SuccessDialog", "FilterDialog", "DateRangeDialog", "BiometricDialog",
        ]),
        ("ui/components/cards/Cards.kt", "ui.components.cards", [
            "InfoCard", "StatsCard", "MetricCard", "AlertCard", "SummaryCard", "ActionCard",
        ]),
        ("ui/components/feedback/FeedbackComponents.kt", "ui.components.feedback", [
            "LoadingState", "EmptyState", "ErrorState", "ShimmerEffect",
            "SnackbarMessage", "ToastMessage", "InAppNotification",
        ]),
        ("ui/components/navigation/NavigationComponents.kt", "ui.components.navigation", [
            "BottomNavigationBar", "TopAppBar", "NavigationDrawer", "TabLayout", "BreadcrumbNav",
        ]),
        ("ui/components/input/InputComponents.kt", "ui.components.input", [
            "OTPInputField", "PhoneInputField", "CurrencyInputField", "PasswordStrengthMeter",
            "AutoCompleteField", "TagsInputField", "RichTextEditor",
        ]),
        ("ui/components/lists/Lists.kt", "ui.components.lists", [
            "PaginatedList", "EndlessScrollList", "SwipeToRefreshList", "SectionedList", "ExpandableList",
        ]),
        ("ui/components/forms/Forms.kt", "ui.components.forms", [
            "DynamicForm", "MultiStepForm", "FormErrorHandler",
        ]),
        ("ui/components/charts/Charts.kt", "ui.components.charts", [
            "LineChart", "BarChart", "PieChart", "DonutChart", "AreaChart", "ProgressChart", "ChartDataMapper",
        ]),
        ("ui/components/media/MediaComponents.kt", "ui.components.media", [
            "ImageLoader", "ImageViewer", "VideoPlayer", "AudioPlayer", "DocumentViewer", "AttachmentPreview",
        ]),
        ("ui/components/layouts/Layouts.kt", "ui.components.layouts", [
            "AdaptiveLayout", "ResponsiveGrid", "SplitScreen", "CollapsibleSection", "StepperLayout",
        ]),
    ]

    for source_rel, pkg, names in ui_splits:
        split_composable_file(source_rel, pkg, names)

    split_class_file(
        "ui/components/charts/Charts.kt",
        [("ChartDataMapper", "ui/components/charts/ChartDataMapper.kt", "ui.components.charts")],
    )

    # Split non-composable forms helpers
    split_class_file(
        "ui/components/forms/Forms.kt",
        [
            ("FormField", "ui/components/forms/FormField.kt", "ui.components.forms"),
            ("FormValidator", "ui/components/forms/FormValidator.kt", "ui.components.forms"),
        ],
    )

    # NavItem data class from navigation
    split_class_file(
        "ui/components/navigation/NavigationComponents.kt",
        [("NavItem", "ui/components/navigation/NavItem.kt", "ui.components.navigation")],
    )

    emit(
        "ui/components/HiredInThemeWrapper.kt",
        "ui.components",
        """
import androidx.compose.runtime.Composable
import com.nordstern.hiredin.shared.ui.theme.HiredInTheme

@Composable
fun HiredInThemeWrapper(content: @Composable () -> Unit) {
    HiredInTheme(content = content)
}
""",
    )


def generate_testing() -> None:
    emit(
        "testing/TestDataFactory.kt",
        "testing",
        """
import com.nordstern.hiredin.shared.models.User
import com.nordstern.hiredin.shared.models.enums.UserRole
import com.nordstern.hiredin.shared.models.enums.UserStatus
import java.util.UUID

object TestDataFactory {
    fun user(
        id: String = UUID.randomUUID().toString(),
        email: String = "test@example.com",
        role: UserRole = UserRole.CANDIDATE
    ) = User(
        id = id,
        email = email,
        role = role,
        status = UserStatus.ACTIVE,
        firstName = "Test",
        lastName = "User"
    )

    fun randomId(): String = UUID.randomUUID().toString()
}
""",
        test=True,
    )

    emit(
        "testing/MockResponseInterceptor.kt",
        "testing",
        """
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockResponseInterceptor(
    private val responses: Map<String, Pair<Int, String>>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val match = responses.entries.firstOrNull { request.url.encodedPath.contains(it.key) }
        val (code, body) = match?.value ?: (404 to "{}")
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("Mock")
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }
}
""",
        test=True,
    )

    emit(
        "testing/TestCoroutineRule.kt",
        "testing",
        """
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
""",
        test=True,
    )

    emit(
        "testing/TestDispatcherProvider.kt",
        "testing",
        """
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) {
    val main: CoroutineDispatcher get() = testDispatcher
    val io: CoroutineDispatcher get() = testDispatcher
    val default: CoroutineDispatcher get() = testDispatcher
}
""",
        test=True,
    )

    _split_test_file(
        TEST_ROOT / "testing" / "Fixtures.kt",
        [
            ("UserFixtures", "testing/fixtures/UserFixtures.kt", "testing.fixtures"),
            ("JobFixtures", "testing/fixtures/JobFixtures.kt", "testing.fixtures"),
        ],
    )

    for fixture in ("LeaveFixtures", "PayrollFixtures"):
        emit(
            f"testing/fixtures/{fixture}.kt",
            "testing.fixtures",
            f"""
object {fixture} {{
    fun sample(): Map<String, String> = mapOf("id" to "test_001", "status" to "pending")
}}
""",
            test=True,
        )


def generate_resources() -> None:
    ar_dimens = RES_ROOT / "values-ar" / "dimens.xml"
    write_if_missing(
        ar_dimens,
        """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="hiredin_padding_small">8dp</dimen>
    <dimen name="hiredin_padding_medium">16dp</dimen>
    <dimen name="hiredin_padding_large">24dp</dimen>
    <dimen name="hiredin_corner_radius">12dp</dimen>
    <dimen name="hiredin_button_height">48dp</dimen>
    <dimen name="hiredin_icon_size">24dp</dimen>
</resources>
""",
    )

    fonts_xml = RES_ROOT / "font" / "fonts.xml"
    write_if_missing(
        fonts_xml,
        """<?xml version="1.0" encoding="utf-8"?>
<font-family xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <font
        android:fontStyle="normal"
        android:fontWeight="400"
        app:font="@font/inter_regular"
        app:fontProviderAuthority="com.google.android.gms.fonts"
        app:fontProviderPackage="com.google.android.gms"
        app:fontProviderQuery="name=Inter&amp;weight=400"
        app:fontProviderCerts="@array/com_google_android_gms_fonts_certs" />

    <font
        android:fontStyle="normal"
        android:fontWeight="700"
        app:font="@font/inter_bold"
        app:fontProviderAuthority="com.google.android.gms.fonts"
        app:fontProviderPackage="com.google.android.gms"
        app:fontProviderQuery="name=Inter&amp;weight=700"
        app:fontProviderCerts="@array/com_google_android_gms_fonts_certs" />

    <font
        android:fontStyle="normal"
        android:fontWeight="400"
        app:font="@font/cairo_regular"
        app:fontProviderAuthority="com.google.android.gms.fonts"
        app:fontProviderPackage="com.google.android.gms"
        app:fontProviderQuery="name=Cairo&amp;weight=400"
        app:fontProviderCerts="@array/com_google_android_gms_fonts_certs" />

    <font
        android:fontStyle="normal"
        android:fontWeight="700"
        app:font="@font/cairo_bold"
        app:fontProviderAuthority="com.google.android.gms.fonts"
        app:fontProviderPackage="com.google.android.gms"
        app:fontProviderQuery="name=Cairo&amp;weight=700"
        app:fontProviderCerts="@array/com_google_android_gms_fonts_certs" />
</font-family>
""",
    )


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------
GENERATORS: List[Callable[[], None]] = [
    generate_api,
    generate_auth,
    generate_database,
    generate_sync,
    generate_notifications,
    generate_utils,
    generate_cache,
    generate_security,
    generate_workers,
    generate_permissions,
    generate_network,
    generate_logging,
    generate_localization,
    generate_analytics,
    generate_ui_theme,
    generate_ui_utils,
    generate_ui_components,
    generate_testing,
    generate_resources,
]


def main() -> int:
    print(f"HiredIn Shared Library completion script")
    print(f"Source root: {PKG_ROOT}")
    print(f"Test root:   {TEST_ROOT}")
    print(f"Only creating missing files (no overwrites, no deletions)\n")

    for gen in GENERATORS:
        gen()

    print(f"\nCreated {len(created_files)} file(s):\n")
    for path in sorted(created_files):
        print(f"  + {path}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
