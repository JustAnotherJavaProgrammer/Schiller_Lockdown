package lukas_drescher.schillerlockdown;

import android.os.Environment;

import java.io.File;

public class DownloadDeletionTool {
    public static void deleteDownloads() {
        File[] downloads = new File(Environment.getExternalStorageDirectory().getPath() + "/Download").listFiles();
        for (File download : downloads) {
            download.delete();
        }
    }
}
