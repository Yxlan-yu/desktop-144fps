# Desktop 144 (LSPosed)

Fix Xiaomi HyperOS launcher (Flutter/Impeller desktop) frame-rate-category stutter on high-refresh panels.

## Problem
SurfaceFlinger puts the launcher into `frameRateCategoryRate {normal=60, high=90}`.
The launcher presents at <=90fps while the panel runs 120Hz, producing periodic
dropped-frame troughs (every ~4th vsync) that read as ~60Hz visually.

Apps that explicitly request a frame rate (e.g. 144Hz Exact) are not affected.

## Fix
An Xposed/LSPosed module for `com.miui.home` that calls `Transaction.setFrameRate(120Hz, DEFAULT, ALWAYS)`
on the launcher root surface each second so SurfaceFlinger presents the launcher at a uniform 120Hz.

## Install
1. Build with the GitHub Actions workflow (or `gradle :app:assembleDebug`).
2. `adb install app-debug.apk`
3. Enable the module in LSPosed, scope = `com.miui.home`.
4. Reboot / restart System UI.

## Notes
- Change `120.0f` to `144.0f` in `MainHook.java` for max refresh on capable panels.
- Safe to disable anytime in LSPosed; no system files touched.