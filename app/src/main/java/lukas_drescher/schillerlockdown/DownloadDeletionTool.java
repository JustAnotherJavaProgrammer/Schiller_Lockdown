package lukas_drescher.schillerlockdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.util.Calendar;

public class DownloadDeletionTool {

    public void onCreate() {
        deleteDownloads();
    }

    public static void deleteDownloads() {
        File[] downloads = new File(Environment.getExternalStorageDirectory().getPath() + "/Download").listFiles();
        for (File download : downloads) {
            download.delete();
        }
    }

    public static void setAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent deleter = PendingIntent.getBroadcast(context, 0, new Intent(context, DownloadDeletionTool.class), 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, deleter);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, DownloadDeletionTool.class), 0));
        }
    }
}
