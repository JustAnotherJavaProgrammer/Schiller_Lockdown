package lukas_drescher.schillerlockdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class customViewGroup extends LinearLayout {

    @SuppressLint("ResourceAsColor")
    public customViewGroup(Context context) {
        super(context);
        if (getDefaultSharedPreferences(getContext()).getBoolean("cover_statusbar_completely", false)) {
            setBackgroundColor(Color.rgb(111, 131, 192));
        }
    }

    //Context context;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

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
