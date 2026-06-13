# HiredIn Shared Library

Android shared library for HiredIn mobile apps (Candidate, Employer, HRMS, ESS).

**Package:** `com.nordstern.hiredin.shared`  
**Version:** 1.0.0

## Features

- **API Client** — Retrofit wrapper with retry, offline cache, and error handling
- **Authentication** — Encrypted token storage, auto-refresh, biometric auth
- **Sync** — Incremental data sync with conflict resolution
- **Offline Queue** — Queue actions when offline with retry logic
- **Push Notifications** — Eclipse Paho MQTT (enterprise open-source, no Firebase)
- **UI Components** — Compose theme, buttons, dialogs, charts, forms
- **Utils** — Logger, CryptoUtils, NetworkUtils, PermissionHelper

## Adding to your project

### Step 1: Add JitPack repository

```kotlin
// settings.gradle.kts
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
// app/build.gradle.kts
dependencies {
    implementation("com.github.NordsternBizSolutions:hiredin-shared:1.0.0")
}
```

### Step 3: Enable Hilt in your app

```kotlin
// app/build.gradle.kts
plugins {
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}
dependencies {
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
}
```

### Step 4: Initialize in Application class

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        HiredInSharedLibrary.init(this)
    }
}
```

### Step 5: Configure API base URL (optional)

Override in your app's `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        buildConfigField("String", "API_BASE_URL", "\"https://your-domain.com/api/mobile/v1/\"")
    }
}
```

## Usage Examples

### Authentication

```kotlin
@Inject lateinit var authenticator: Authenticator

// Login
authenticator.login("user@example.com", "password")

// Logout
authenticator.logout()
```

### API Calls

```kotlin
@Inject lateinit var apiClient: BaseApiClient

val response = apiClient.safeApiCall {
    apiClient.createAuthenticatedService<AuthApi>().me()
}
```

### Sync

```kotlin
@Inject lateinit var syncManager: SyncManager

syncManager.schedulePeriodicSync()
syncManager.triggerImmediateSync()
```

### Push Notifications (MQTT)

```kotlin
@Inject lateinit var pushHandler: PushNotificationHandler

// Connect to your MQTT broker
pushHandler.connect(
    brokerUrl = "ssl://mqtt.your-domain.com:8883",
    topic = "hiredin/notifications/{userId}"
)
```

### UI Theme

```kotlin
HiredInTheme {
    // Your Compose content
}
```

## Architecture

```
Shared Library
├── API (Retrofit, interceptors, error handling)
├── Auth (tokens, biometric, session)
├── Database (Room, sync entities)
├── Sync (incremental sync, offline queue)
├── Notifications (MQTT push, channels)
├── UI (Compose components, theme)
└── Utils (logging, crypto, network, permissions)
```

## Version History

- **1.0.0** — Initial release

## License

MIT License

## Repository

- GitHub: https://github.com/NordsternBizSolutions/hiredin-shared
- JitPack: `com.github.NordsternBizSolutions:hiredin-shared:1.0.0`
