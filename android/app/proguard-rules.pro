-keep class com.interveen.app.core.accessibility.InterveenAccessibilityService { *; }
-keep class com.interveen.app.core.overlay.OverlayService { *; }
-keep class com.interveen.app.core.service.GuardForegroundService { *; }

# Goal kinds are stored by name in DataStore. Renaming them would drop saved reminders.
-keepnames class com.interveen.app.domain.model.GoalKind
-keepclassmembers class com.interveen.app.domain.model.GoalKind {
    public static <fields>;
}
