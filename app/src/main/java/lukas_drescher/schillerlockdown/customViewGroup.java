package lukas_drescher.schillerlockdown;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class customViewGroup extends ViewGroup {

    public customViewGroup(Context context) {
        super(context);
    }

    //Context context;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("Status bar blocker", "BLOCKED!");
        if (getDefaultSharedPreferences(getContext()).getBoolean("show_Statusbar_blocked_message", true)) {
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
