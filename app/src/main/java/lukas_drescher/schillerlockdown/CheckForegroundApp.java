package lukas_drescher.schillerlockdown;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.HashSet;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CheckForegroundApp extends AccessibilityService {

    ArrayList<String> whitelist;
    AppLockscreen lockscreen;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v("checker", "Connected!");
    }

    public void loadWhiteList() {
        whitelist = new ArrayList<>(getDefaultSharedPreferences(getApplicationContext()).getStringSet("Whitelist", new HashSet<String>()));
    }

//    AccessibilityEvent foregroundActivity;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            Log.d("checker", AccessibilityEvent.eventTypeToString(event.getEventType()) + " (" + event.getPackageName() + ")");
            try {
                Log.d("checker", event.getClassName().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadWhiteList();
            //if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//        if (foregroundActivity == null || !event.getPackageName().equals(foregroundActivity.getPackageName())) {
            boolean isRecentAppsScreen = event.getClassName().equals("com.android.systemui.recents.RecentsActivity") || event.getClassName().equals("com.android.systemui.recents.SeparatedRecentsActivity") || event.getClassName().equals("com.android.internal.app.ChooserActivity") || event.getClassName().equals("com.android.internal.app.ResolverActivity");
            boolean isAllowed = isAllowed(event.getPackageName().toString());
            if (!(isRecentAppsScreen || isAllowed)) {
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeDialog);
                Log.w("checker", "start Lockscreen (" + event.getPackageName() + "; " + AccessibilityEvent.eventTypeToString(event.getEventType()) + ";");
                if (lockscreen == null) {
                    // Close every kind of system dialog
                    try {
                        startLockscreen(event, true);
                    } catch (RuntimeException e) {
                        try {
                            startLockscreen(event, false);
                        } catch (RuntimeException e2) {
                            legacyLockscreen(event);
                        }
                    }
                } else {
                    lockscreen.show();
                }
                if (event.getPackageName().equals("com.android.systemui") && event.getClassName().equals("android.widget.FrameLayout")) {
                    legacyLockscreen(event);
                }
            } else if (lockscreen != null) {
                lockscreen.hide();
            }
//            foregroundActivity = event;
//        }
            //}
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("checker", "This error was catched and the service will work normally.");
        }
    }

    @Override
    public void onInterrupt() {
    }

    private void startLockscreen(AccessibilityEvent event, boolean systemOverlay) {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = systemOverlay ? WindowManager.LayoutParams.TYPE_SYSTEM_ERROR : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        lockscreen = new AppLockscreen(getApplicationContext(), wm, event);
        wm.addView(lockscreen, localLayoutParams);
    }

    private void legacyLockscreen(AccessibilityEvent event) {
        Intent i = new Intent(getApplicationContext(), Homescreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("EXIT", true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        ((ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).killBackgroundProcesses(event.getPackageName().toString());
    }

    public boolean isAllowed(String packageName) {
        if (System.currentTimeMillis() < getDefaultSharedPreferences(getApplicationContext()).getLong("disabled until", 0) || (packageName.contains("android") && packageName.contains("settings")) || packageName.equals(Homescreen.class.getPackage().getName()))
            return true;
        for (int i = 0; i < whitelist.size(); i++) {
            if (whitelist.get(i).equals(packageName))
                return true;
        }
        return false;
        //|| packageName.equals("com.android.systemui")
    }
}
