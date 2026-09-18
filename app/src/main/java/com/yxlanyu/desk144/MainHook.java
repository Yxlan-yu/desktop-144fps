package com.yxlanyu.desk144;

import android.view.Surface;
import android.view.SurfaceControl;
import android.view.ViewRootImpl;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private static volatile long lastApply = 0L;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) {
        if (!"com.miui.home".equals(lpp.packageName)) {
            return;
        }
        XposedBridge.log("[desk144] attach com.miui.home");
        XposedHelpers.findAndHookMethod(ViewRootImpl.class, "performTraversals",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        long now = System.currentTimeMillis();
                        if (now - lastApply < 1000) {
                            return;
                        }
                        try {
                            Object sc = XposedHelpers.callMethod(param.thisObject, "getSurfaceControl");
                            if (sc instanceof SurfaceControl) {
                                SurfaceControl.Transaction t = new SurfaceControl.Transaction();
                                // 120Hz: matches SurfaceFlinger primary mode; removes category-90 stutter.
                                // Change to 144.0f if you prefer max refresh on capable panels.
                                t.setFrameRate((SurfaceControl) sc, 120.0f,
                                        Surface.FRAME_RATE_COMPATIBILITY_DEFAULT,
                                        Surface.CHANGE_FRAME_RATE_ALWAYS);
                                t.apply();
                                lastApply = now;
                            }
                        } catch (Throwable ignore) {
                        }
                    }
                });
    }
}