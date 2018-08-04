package lukas_drescher.schillerlockdown;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class customViewGroup extends LinearLayout {

    private boolean stillNotDestroyed = true;

    public customViewGroup(Context context) {
        super(context);
        if (getDefaultSharedPreferences(getContext()).getBoolean("cover_statusbar_completely", false)) {
            Log.d("LayoutInflater", "starting...");
            LayoutInflater.from(getContext()).inflate(R.layout.statusbar_pro, this, true).setVisibility(VISIBLE);
            makeHandlerForClock();
        }
    }

    public void makeHandlerForClock() {
        if (stillNotDestroyed) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    makeHandlerForClock();
                }
            }, setCurrentTimeTextView());
        }
    }

    public long setCurrentTimeTextView() {
        Calendar currentTime = Calendar.getInstance();
        ((TextView) findViewById(R.id.txtviewCurrentTime)).setText(addZeroIfNeeded(currentTime.get(Calendar.HOUR_OF_DAY)) + ":" + addZeroIfNeeded(currentTime.get(Calendar.MINUTE)));
        currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(currentTime.getTimeInMillis() + 60000);
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);
        long result = currentTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        if (result > 0) {
//          Log.d("setCurrentTimeResult", "Wait for "+ result + " millis");
            return result;
        }
        return setCurrentTimeTextView();
    }

    public String addZeroIfNeeded(int i) {
        if (i < 10) {
            return "0" + i;
        }
        return String.valueOf(i);
    }

    //Context context;

    //@Override
    //protected void onLayout(boolean changed, int l, int t, int r, int b) {
    //}

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("Status bar blocker", "BLOCKED!");
        if (getDefaultSharedPreferences(getContext()).getBoolean("show_Statusbar_blocked_message", false)) {
            Toast.makeText(getContext(), R.string.status_bar_blocked, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void finalize() throws Throwable {
        stillNotDestroyed = false;
        super.finalize();
    }
    // @Override
    // public void onWindowFocusChanged(boolean hasWindowFocus) {
    //    super.onWindowFocusChanged(hasWindowFocus);
    //    if (!hasWindowFocus) {
    //      // Close every kind of system dialog
    //    Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    //  getContext().sendBroadcast(closeDialog);
    //}
    //}
}
