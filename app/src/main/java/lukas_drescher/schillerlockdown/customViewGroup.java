package lukas_drescher.schillerlockdown;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class customViewGroup extends ViewGroup {

    public customViewGroup(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.v("Status bar blocker", "BLOCKED!");
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
