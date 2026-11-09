# Interveen iOS

Hybrid Guard: Screen Time for Apple’s adult catalog, plus a Safari content blocker that actually stops listed sites.

Open `Interveen.xcodeproj` in Xcode 16 or later. Run the **Interveen** scheme (not Shield).

| Piece | Role |
| --- | --- |
| App | Onboarding, dashboard, goal text, full-screen reminder (`interveen://goal`) |
| `InterveenBlocker` | Safari Content Blocker — this is what stops `wow.xxx` in Safari |
| `InterveenGuard` | Optional Safari Web Extension for an in-page overlay |
| `InterveenShield` + `InterveenShieldAction` | Screen Time shield: **Open Goal Reminder** deep-links into the app |
| App Group `group.com.interveen.ios.shared` | Shared Guard toggle, goal, and pending overlay flag |

**Safari:** Settings → Apps → Safari → Extensions → turn on **Interveen Blocker**. Screen Time Allow does not do this.

**Chrome:** only Apple’s Screen Time adult catalog. The **Google app cannot be paused.**

Enable **Family Controls** on App ID `com.interveen.appios` (and the two shield extensions) in the Apple Developer portal. Add App ID `com.interveen.appios.Blocker` if automatic signing does not create it.
