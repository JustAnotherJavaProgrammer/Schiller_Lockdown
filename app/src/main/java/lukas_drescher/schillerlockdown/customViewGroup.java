package lukas_drescher.schillerlockdown;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
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
            setBatteryStateTextViewAndCorrespondingDrawable();
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
            return result;
        }
        return setCurrentTimeTextView();
    }

    public void setBatteryStateTextViewAndCorrespondingDrawable() {
        Intent batteryStatus = getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        TextView txtviewBatteryState = findViewById(R.id.txtviewBatteryState);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (int) (Math.round((level / (double) scale) * 100));
        txtviewBatteryState.setCompoundDrawablesRelativeWithIntrinsicBounds(getContext().getDrawable(getBatteryImage(isCharging, batteryPct)), null, null, null);
        txtviewBatteryState.setText(batteryPct + "%");
    }

    public int getBatteryImage(boolean charging, int percentage) {
        if (charging) {
            if (percentage <= 20) {
                return R.drawable.ic_baseline_battery_charging_20;
            } else if (percentage <= 30) {
                return R.drawable.ic_baseline_battery_charging_30;
            } else if (percentage <= 30) {
                return R.drawable.ic_baseline_battery_charging_30;
            } else if (percentage <= 50) {
                return R.drawable.ic_baseline_battery_charging_50;
            } else if (percentage <= 60) {
                return R.drawable.ic_baseline_battery_charging_60;
            } else if (percentage <= 80) {
                return R.drawable.ic_baseline_battery_charging_80;
            } else if (percentage <= 90) {
                return R.drawable.ic_baseline_battery_charging_90;
            } else if (percentage <= 100) {
                return R.drawable.ic_baseline_battery_charging_full;
            }
        } else {
            if (percentage <= 20) {
                return R.drawable.ic_baseline_battery_20;
            } else if (percentage <= 30) {
                return R.drawable.ic_baseline_battery_30;
            } else if (percentage <= 30) {
                return R.drawable.ic_baseline_battery_30;
            } else if (percentage <= 50) {
                return R.drawable.ic_baseline_battery_50;
            } else if (percentage <= 60) {
                return R.drawable.ic_baseline_battery_60;
            } else if (percentage <= 80) {
                return R.drawable.ic_baseline_battery_80;
            } else if (percentage <= 90) {
                return R.drawable.ic_baseline_battery_90;
            } else if (percentage <= 100) {
                return R.drawable.ic_baseline_battery_full;
            }
        }
        return R.drawable.ic_baseline_battery_unknown;
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
        Log.d("customViewGroup", "finalized");
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
