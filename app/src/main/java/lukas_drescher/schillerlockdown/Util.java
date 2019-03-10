package lukas_drescher.schillerlockdown;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Util {
    public static int getStatusBarColor(Context context, Resources resources) {
        if (AprilFool.isFirstOfApril())
            return ResourcesCompat.getColor(resources, R.color.colorPrimary, null);
        if (getDefaultSharedPreferences(context).getBoolean("custom_color_active", false)) {
            return getDefaultSharedPreferences(context).getInt("custom_color", ResourcesCompat.getColor(resources, R.color.colorPrimary, null));
        }
        return getConstantColor(context, resources);
    }

    private static int getConstantColor(Context context, Resources resources) {
        switch (getDefaultSharedPreferences(context).getInt("custom_statusbar_color", 0)) {
            case 0:
                return ResourcesCompat.getColor(resources, R.color.colorPrimary, null);
            case 1:
                return ResourcesCompat.getColor(resources, R.color.colorAccent, null);
            default:
                return Color.BLACK;
        }
    }
}
