package lukas_drescher.schillerlockdown;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.Calendar;

public class AprilFool {

    public static View.OnClickListener adOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder ad_buyPro_dialog = new AlertDialog.Builder(view.getContext());
                ad_buyPro_dialog.setMessage(R.string.question_wanna_buy_pro);
                setYesNoAprilButtons(ad_buyPro_dialog, view.getContext(), null);
                ad_buyPro_dialog.show();
            }
        };
    }

    public static void showStatusBarMessageApril(Context activity) {
        AlertDialog.Builder statusBar_buyPro_dialog = new AlertDialog.Builder(activity);
        statusBar_buyPro_dialog.setMessage(activity.getString(R.string.statusbar_only_pros) + activity.getString(R.string.question_wanna_buy_pro));
        setYesNoAprilButtons(statusBar_buyPro_dialog, activity, null);
        statusBar_buyPro_dialog.show();
    }

    public static void showForbiddenAppDialogApril(View view) {
        AlertDialog.Builder forbiddenApp_dialog = new AlertDialog.Builder(view.getContext());
        forbiddenApp_dialog.setMessage(view.getContext().getString(R.string.app_only_pro) + view.getContext().getString(R.string.question_wanna_buy_pro));
        setYesNoAprilButtons(forbiddenApp_dialog, view.getContext(), null);
        forbiddenApp_dialog.show();
    }

    public static void showAllowedAppDialogApril(View view, Intent intent) {
        AlertDialog.Builder allowedApp_dialog = new AlertDialog.Builder(view.getContext());
        allowedApp_dialog.setMessage(view.getContext().getString(R.string.open_app) + view.getContext().getString(R.string.question_wanna_buy_pro));
        setYesNoAprilButtons(allowedApp_dialog, view.getContext(), intent);
        allowedApp_dialog.show();
    }

    private static void setYesNoAprilButtons(AlertDialog.Builder alertDialogBuilder, final Context context, @Nullable final Intent intent) {
        alertDialogBuilder.setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                if (intent != null) {
                    context.startActivity(intent);
                }
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.yes_i_want, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                showAprilFoolDialog(context, intent);
            }
        });
    }


    public static void showAprilFoolDialog(final Context activity, @Nullable final Intent intent) {
        AlertDialog.Builder aprilFool = new AlertDialog.Builder(activity);
        aprilFool.setTitle(R.string.april_fool);
        aprilFool.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (intent != null) {
                    activity.startActivity(intent);
                }
            }
        });
        aprilFool.show();
    }

    public static boolean isFirstOfApril() {
        Calendar currentTime = Calendar.getInstance();
        return currentTime.get(Calendar.MONTH) == Calendar.APRIL && currentTime.get(Calendar.DAY_OF_MONTH) == 1;
    }
}
