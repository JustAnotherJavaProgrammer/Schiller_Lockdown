package lukas_drescher.schillerlockdown;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.HashSet;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CheckForegroundApp extends AccessibilityService {

    ArrayList<String> whitelist;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v("checker", "Connected!");
    }

    public void loadWhiteList() {
        whitelist = new ArrayList<>(getDefaultSharedPreferences(getApplicationContext()).getStringSet(getString(R.string.whitelist), new HashSet<String>()));
    }

    AccessibilityEvent foregroundActivity;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.w("checker", AccessibilityEvent.eventTypeToString(event.getEventType()) + " (" + event.getPackageName() + ")");
        Log.d("checker", event.getClassName().toString());
        loadWhiteList();
        //if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
        if (foregroundActivity == null || !event.getPackageName().equals(foregroundActivity.getPackageName())) {
            boolean isRecentAppsScreen = event.getClassName().equals("com.android.systemui.recents.RecentsActivity");
            boolean isAllowed = isAllowed(event.getPackageName().toString());
            if (!(isRecentAppsScreen || isAllowed)) {
                // Close every kind of system dialog
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeDialog);
                Log.w("checker", "start Lockscreen (" + event.getPackageName() + "; " + AccessibilityEvent.eventTypeToString(event.getEventType()) + ";");
                Intent i = new Intent(getApplicationContext(), Homescreen.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("EXIT", true);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
            foregroundActivity = event;
        }
        //}
    }

    @Override
    public void onInterrupt() {

    }

    public boolean isAllowed(String packageName) {
        if ((packageName.contains("android") && packageName.contains("settings")) || packageName.equals(Homescreen.class.getPackage().getName()))
            return true;
        for (int i = 0; i < whitelist.size(); i++) {
            if (whitelist.get(i).equals(packageName))
                return true;
        }
        return false;
        //|| packageName.equals("com.android.systemui")
    }
}
