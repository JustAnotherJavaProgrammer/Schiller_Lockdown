package lukas_drescher.schillerlockdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent myIntent = new Intent(context, Homescreen.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Startup", true);
            context.startActivity(myIntent);
        }
    }
}
