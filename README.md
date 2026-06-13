# HiredIn Shared Library

Enterprise-grade Android shared library for HiredIn mobile apps (Candidate, Employer, HRMS, ESS).

**Package:** `com.nordstern.hiredin.shared` | **Version:** `1.0.0`

## Architecture

| Layer | Components |
|-------|------------|
| **DI** | Hilt modules: Network, Database, Sync, Analytics, Notifications |
| **API** | Retrofit + OkHttp, certificate pinning, metrics, retry, offline cache |
| **Auth** | Encrypted tokens (AES-GCM), auto-refresh, biometric, session management |
| **Database** | Room with migrations, type converters, offline action queue |
| **Sync** | Incremental/full/smart strategies, conflict resolution, WorkManager |
| **Push** | Eclipse Paho MQTT (enterprise open-source), typed notification handlers |
| **UI** | Material3 Compose design system with 50+ production components |
| **Security** | KeyStore, secure storage, fail-closed encryption, cert pinning |

## Adding to your project

### Step 1: Add JitPack repository

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add dependency

```kotlin
dependencies {
    implementation("com.github.NordsternBizSolutions:hiredin-shared:1.0.0")
}
```

### Step 3: Enable Hilt

```kotlin
plugins {
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}
dependencies {
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
}
```

### Step 4: Initialize

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        HiredInSharedLibrary.init(this)
    }
}
```

## Usage

```kotlin
// Auth
@Inject lateinit var authenticator: Authenticator
authenticator.login("user@example.com", "password")

// API
@Inject lateinit var apiClient: BaseApiClient
val response = apiClient.safeApiCall {
    apiClient.createAuthenticatedService<AuthApi>().me()
}

// Sync
@Inject lateinit var syncManager: SyncManager
syncManager.schedulePeriodicSync()

// MQTT Push
@Inject lateinit var pushHandler: PushNotificationHandler
pushHandler.connect("ssl://mqtt.your-domain.com:8883", "hiredin/notifications/{userId}")

// UI
HiredInTheme {
    HiredInTextField(value, onValueChange, label = "Email")
    LoadingButton("Submit", isLoading, onClick = { })
    EmptyState(title = "No jobs", actionLabel = "Browse", onAction = { })
}
```

## Version History

- **1.0.0** — Enterprise initial release

## Repository

- GitHub: https://github.com/NordsternBizSolutions/hiredin-shared
- JitPack: `com.github.NordsternBizSolutions:hiredin-shared:1.0.0`
