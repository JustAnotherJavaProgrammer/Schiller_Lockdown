package lukas_drescher.schillerlockdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class customViewGroup extends LinearLayout {

    @SuppressLint("ResourceAsColor")
    public customViewGroup(Context context) {
        super(context);
        if (getDefaultSharedPreferences(getContext()).getBoolean("cover_statusbar_completely", false)) {
            setBackgroundColor(Color.rgb(111, 131, 192));
            Log.d("LayoutInflater", "starting...");
            LayoutInflater.from(getContext()).inflate(R.layout.statusbar_pro, this, true).setVisibility(VISIBLE);
            setCurrentTimeTextView();
        }
    }

    public void setCurrentTimeTextView() {
        Calendar currentTime = Calendar.getInstance();
        ((TextView) findViewById(R.id.txtviewCurrentTime)).setText(currentTime.get(Calendar.HOUR_OF_DAY) + ":" + currentTime.get(Calendar.MINUTE));
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
