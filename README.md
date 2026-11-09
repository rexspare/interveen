# Interveen

A native focus and habit-interruption app. Interveen detects adult-content browser access, collapses the browsing session, and immediately presents a full-screen personal goal reminder — a recorded video, image, or written intention — so impulse loops break before they start.

This is a multi-platform monorepo:

- `android/` — Kotlin, Jetpack Compose, Clean Architecture + MVVM, Hilt, Coroutines/Flow
- `ios/` — Swift, SwiftUI, Swift Concurrency
- `web/` — Next.js marketing site (landing, privacy, terms, support)

## Design language

| Token | Value | Role |
| --- | --- | --- |
| Background | `#0F172A` | Deep slate charcoal |
| Surface | `#1E293B` | Muted cool gray |
| Typography | `#F8FAFC` | Crisp white |
| Accent | `#10B981` | Emerald — active / complete states |

UI is borderless, 20dp rounded, and intentionally calm. Notifications stay non-judgmental: *“Interveen Guard Active — Staying aligned with your goals.”*

## Android

Open the `android/` directory in Android Studio, or from the repo root:

```bash
cd android
./gradlew :app:assembleDebug
```

Minimum SDK 26. Target / compile SDK 36.

### Required system permissions

1. **Accessibility Service** — reads address-bar text in Chrome, Firefox, Brave, and Edge so the matcher can run locally.
2. **Display over other apps** (`SYSTEM_ALERT_WINDOW`) — presents the full-screen goal overlay.
3. **Notifications** — keeps the guard as a foreground service so the OS does not kill it.

All matching is on-device. No browsing history leaves the phone.

### Module map

```
com.interveen.app
├── core/           Accessibility, overlay, foreground guard
├── data/           DataStore, blocklist, matcher, repository impls
├── di/             Hilt bindings
├── domain/         Models, repository contracts, use cases
└── ui/             Theme, navigation, onboarding, dashboard, media, overlay
```

## iOS

Open `ios/Interveen.xcodeproj` in Xcode. The SwiftUI shell mirrors the Android information architecture (onboarding, dashboard, media setup) and the same color system.

## Web

```bash
cd web
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000). Canonical host: `https://interveen.soltoapps.top`. Routes: `/`, `/privacy`, `/terms`, `/support`.
