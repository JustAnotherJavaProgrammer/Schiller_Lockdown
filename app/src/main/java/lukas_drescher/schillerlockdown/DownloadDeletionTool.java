package lukas_drescher.schillerlockdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

public class DownloadDeletionTool extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DownloadDeletionTool", "Received Broadcast... Starting deletion");
        deleteDownloads();
        return START_NOT_STICKY;
    }

    public static void deleteDownloads() {
        File[] downloads = new File(Environment.getExternalStorageDirectory().getPath() + "/Download").listFiles();
        if (downloads != null) {
            for (File download : downloads) {
                download.delete();
            }
            Log.d("DownloadDeletionTool", "Downloads deleted");
        } else {
            Log.d("DownloadDeletionTool", "No downloads found!");
        }
    }

    public static void setAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent deleter = PendingIntent.getService(context, 0, new Intent(context, DownloadDeletionTool.class), 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, deleter);
        Log.d("DownloadDeletionTool", "Alarm set");
    }

    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, DownloadDeletionTool.class), 0));
            Log.d("DownloadDeletionTool", "Alarm canceled");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
