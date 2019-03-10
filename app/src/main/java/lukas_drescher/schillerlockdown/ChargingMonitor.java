package lukas_drescher.schillerlockdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ChargingMonitor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED")) {
            resetColors(context);
            DownloadDeletionTool.deleteDownloads();
            Toast.makeText(context, R.string.DownloadsDeleted, Toast.LENGTH_SHORT);
        }
        if (Homescreen.viewGroup != null) {
            Homescreen.viewGroup.setBatteryStateTextViewAndCorrespondingDrawable();
        }
    }

    public static void resetColors(Context context) {
        getDefaultSharedPreferences(context).edit().putBoolean("custom_color_active", false).remove("custom_color").apply();
        int bgColor = Util.getStatusBarColor(context, context.getResources());
        if (Homescreen.viewGroup != null) {
            Homescreen.viewGroup.findViewById(R.id.linearLayoutStatusbar).setBackgroundColor(bgColor);
        }
        if (Homescreen.titleBar != null) {
            Homescreen.titleBar.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
        if (AboutPage.titleBar != null) {
            AboutPage.titleBar.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
        if (ChangePIN.titleBar != null) {
            ChangePIN.titleBar.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
        if (Settings.titleBar != null) {
            Settings.titleBar.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
    }
}
