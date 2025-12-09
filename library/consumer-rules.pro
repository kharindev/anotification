# Не обфусцировать классы библиотеки (важно для reflection)
-keep class com.kharin.anotification.** { *; }

# WorkManager должен находить Worker по имени
-keep class com.kharin.anotification.work.** extends androidx.work.Worker { *; }

# Keep annotation info
-keepattributes *Annotation*

# Не трогать класс Activity, если он приходит строкой
-keepnames class * extends android.app.Activity
