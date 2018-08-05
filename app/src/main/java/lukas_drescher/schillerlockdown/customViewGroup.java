package lukas_drescher.schillerlockdown;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
            setWifiStateDrawableTextView();
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

    public void setWifiStateDrawableTextView() {
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        int drawableID = R.drawable.ic_baseline_wifi;
        if (wifiManager.isWifiEnabled()) {
            if (isConnected(WifiInfo.getDetailedStateOf(wifiManager.getConnectionInfo().getSupplicantState()))) {
                switch (WifiManager.calculateSignalLevel(connectionInfo.getRssi(), 5)) {
                    case 0:
                        drawableID = R.drawable.ic_baseline_signal_wifi_0_bar;
                        break;
                    case 1:
                        drawableID = R.drawable.ic_baseline_signal_wifi_1_bar;
                        break;
                    case 2:
                        drawableID = R.drawable.ic_baseline_signal_wifi_2_bar;
                        break;
                    case 3:
                        drawableID = R.drawable.ic_baseline_signal_wifi_3_bar;
                        break;
                    case 4:
                        drawableID = R.drawable.ic_baseline_signal_wifi_4_bar;
                        break;
                    default:
                        drawableID = R.drawable.ic_baseline_network_wifi;
                }
            } else {
                drawableID = R.drawable.ic_baseline_signal_wifi_off;
            }
        } else {
            drawableID = R.drawable.ic_baseline_wifi_off;
        }
        ((TextView) findViewById(R.id.txtviewWifiState)).setCompoundDrawablesWithIntrinsicBounds(null, null, getContext().getDrawable(drawableID), null);
    }

    public boolean isConnected(NetworkInfo.DetailedState detailedState) {
        return detailedState.equals(NetworkInfo.DetailedState.CONNECTED) || detailedState.equals(NetworkInfo.DetailedState.AUTHENTICATING) || detailedState.equals(NetworkInfo.DetailedState.CONNECTING) || detailedState.equals(NetworkInfo.DetailedState.OBTAINING_IPADDR);
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
