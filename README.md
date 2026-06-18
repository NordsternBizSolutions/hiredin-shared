# HiredIn Shared Library

Enterprise-grade Android shared library for HiredIn mobile apps (Candidate, Employer, HRMS, ESS).

**Package:** `com.nordstern.hiredin.shared` | **Latest version:** `1.1.2`

GitHub: [NordsternBizSolutions/hiredin-shared](https://github.com/NordsternBizSolutions/hiredin-shared)

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

Use a **tag with the `v` prefix** (recommended):

```kotlin
dependencies {
    implementation("com.github.NordsternBizSolutions:hiredin-shared:v1.1.2")
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

### Step 4: Initialize with API base URL

JitPack artifacts are built with a placeholder base URL. **Always pass your mobile API base URL at runtime** (must end with `/api/mobile/v1/` or equivalent):

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        HiredInSharedLibrary.init(
            context = this,
            debug = BuildConfig.DEBUG,
            apiBaseUrl = "https://your-domain.com/api/mobile/v1/"
        )
    }
}
```

You can also set the URL before Retrofit initializes:

```kotlin
ApiConfig.setBaseUrl("https://your-domain.com/api/mobile/v1/")
```

### Local development (Candidate app)

The HiredIn Candidate app includes this module locally via `settings.gradle.kts`:

```kotlin
include(":shared")
project(":shared").projectDir = file("../hiredin-shared-temp/shared")
```

When built as a local module, `API_BASE_URL` is read from the consuming app's `local.properties` at compile time.

## Candidate mobile API highlights

| Area | Endpoints / DTOs |
|------|------------------|
| **Profile & Career Hub** | `CandidateApi.getProfile`, work experience, education, skills, languages, certifications, social links, career hub |
| **Dashboard** | Stats, journey, application trend, job sources, upcoming interviews |
| **Jobs & applications** | Saved jobs, applications tracker, quick apply |
| **Interviews** | `GET candidate/interviews` → `CandidateInterviewsListDto` (`upcoming`, `history`, `pending_count`); respond, Daily.co join |
| **Companies** | Recommended, following, trending, detail, reviews |
| **Messages & offers** | Conversations, offers respond |
| **Settings & subscription** | Password, 2FA, privacy, Stripe subscription |

Interview list parsing tolerates array or `{ upcoming, history }` JSON shapes via Gson flexible deserializers.

## Usage

```kotlin
// Auth
@Inject lateinit var authenticator: Authenticator
authenticator.login("user@example.com", "password")

// API
@Inject lateinit var apiClient: BaseApiClient
val response = apiClient.safeApiCall {
    apiClient.createAuthenticatedService<CandidateApi>().getInterviews()
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

## Version history

| Version | Notes |
|---------|--------|
| **1.1.2** | Mobile interview list API (`CandidateInterviewsListDto`, `InterviewDto` helpers), Gson flex parsing for interview arrays, `CompanyBriefDto` logo aliases, `SocialLinkDto` platform/url aliases, Gradle/Kotlin 2.4 toolchain alignment, `gradle/hiredin-env.gradle.kts` for standalone builds |
| **1.1.1** | Runtime `ApiConfig.setBaseUrl()` / `HiredInSharedLibrary.init(apiBaseUrl)` for JitPack consumers |
| **1.1.0** | Build constants fix, auth API updates, login JSON parsing |
| **1.0.0** | Enterprise initial release |

## Repository & distribution

- **GitHub:** https://github.com/NordsternBizSolutions/hiredin-shared
- **JitPack:** `com.github.NordsternBizSolutions:hiredin-shared:v1.1.2`
