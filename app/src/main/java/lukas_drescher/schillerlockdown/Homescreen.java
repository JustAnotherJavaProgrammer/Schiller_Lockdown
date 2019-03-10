package lukas_drescher.schillerlockdown;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Homescreen extends AppCompatActivity {

    private static boolean isFirstOfApril = AprilFool.isFirstOfApril();
    public static ActionBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        if ((getIntent().getBooleanExtra("Startup", false) || SystemClock.elapsedRealtime() < 5 * 1000 * 60) && getDefaultSharedPreferences(getApplicationContext()).getBoolean("DeleteDownloads", false)) {
            Log.d("Homescreen, DDT", "Starting deleting downloads on startup...");
            DownloadDeletionTool.deleteDownloads();
            Toast.makeText(getApplicationContext(), R.string.DownloadsDeleted, Toast.LENGTH_SHORT).show();
            DownloadDeletionTool.setAlarm(getApplicationContext());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), R.string.lockscreen_undisableable, Toast.LENGTH_LONG).show();
        }
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        ((ImageView) findViewById(R.id.homescreenWallpaperBackground)).setImageDrawable(wallpaperDrawable);
        list = findViewById(R.id.apps_list);
        manager = getPackageManager();
        layoutInflater = getLayoutInflater();
        thisOne = this;
        loadHomescreen();
        titleBar = getSupportActionBar();
        titleBar.setBackgroundDrawable(new ColorDrawable(Util.getStatusBarColor(getApplicationContext(), getResources())));
        findViewById(R.id.ad_homescreen).setOnClickListener(AprilFool.adOnClick());
        try {
            preventStatusBarExpansion(getApplicationContext(), this, false);
        } catch (RuntimeException e) {
            Log.e("statusBarBlocker", "first error");
            e.printStackTrace();
            try {
                preventStatusBarExpansion(getApplicationContext(), this, true);
            } catch (RuntimeException e2) {
                Log.e("statusBarBlocker", "second error");
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.status_bar_unblockable, Toast.LENGTH_LONG).show();
            }
        }
        if (getDefaultSharedPreferences(getApplicationContext()).getInt(getString(R.string.PIN), -1) == -1) {
            kioskMode = false;
            startActivity(new Intent(getApplicationContext(), ChangePIN.class));
        }
        if (isFirstOfApril) {
            setTitle(R.string.title);
            findViewById(R.id.ad_homescreen).setVisibility(View.VISIBLE);
        }
    }

    public static void loadHomescreen() {
        loadWhiteList(thisOne.getApplicationContext());
        loadApps();
        loadListView();
        addClickListener();
    }

    public static void loadWhiteList(Context applicationContext) {
        whitelist = new ArrayList<>(getDefaultSharedPreferences(applicationContext).getStringSet("Whitelist", new HashSet<String>()));
    }

    static ArrayList<String> whitelist;

    public static boolean isAllowed(String packageName) {
        for (int i = 0; i < whitelist.size(); i++) {
            if (whitelist.get(i).equals(packageName))
                return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                if (getDefaultSharedPreferences(getApplicationContext()).getInt(getString(R.string.PIN), -1) == -1) {
                    kioskMode = false;
                    Intent intent = new Intent(getApplicationContext(), Settings.class);
                    startActivity(intent);
                } else {
                    requestPINDialogAndShowSettings(getDefaultSharedPreferences(getApplicationContext()).getInt(getString(R.string.PIN), 0));
                }
                return true;
            case R.id.about_page:
                kioskMode = false;
                startActivity(new Intent(getApplicationContext(), AboutPage.class));
                break;
            case R.id.change_color:
                kioskMode = false;
                startActivity(new Intent(getApplicationContext(), ColorChooser.class));
        }

        return super.onOptionsItemSelected(item);
    }

    EditText etPINrequest;

    public void requestPINDialogAndShowSettings(final int pin) {
        kioskMode = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enterPIN));
        builder.setView(R.layout.pinfield);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PINrequestDialogCheckPIN(pin);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        etPINrequest = builder.show().findViewById(R.id.PINfield);
        etPINrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PINrequestDialogCheckPIN(pin);
            }
        });
    }

    private void PINrequestDialogCheckPIN(final int pin) {
        if (pinIsRight(pin)) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        } else {
            kioskMode = true;
            Toast.makeText(getApplicationContext(), R.string.wrong_pin, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean pinIsRight(int pin) {
        try {
            return Integer.valueOf(etPINrequest.getText().toString()) == pin;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.app_bar, menu);
        return true;
    }

    public static void addClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                if (apps.get(pos).isAllowed) {
                    kioskMode = false;
                    Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
                    if (isFirstOfApril) {
                        AprilFool.showAllowedAppDialogApril(v, i);
                    } else {
                        thisOne.startActivity(i);
                    }
                } else if (isFirstOfApril) {
                    AprilFool.showForbiddenAppDialogApril(v);
                }
            }
        });
    }

    static GridView list;
    static LayoutInflater layoutInflater;
    static Context thisOne;

    public static void loadListView() {
        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(thisOne,
                R.layout.support_simple_spinner_dropdown_item,
                apps) {
            @Override
            @NonNull
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.list_item, null);
                }

                ImageView appIcon = convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);
                appIcon.setAdjustViewBounds(true);
                appIcon.setMaxHeight(96);
                appIcon.setMaxWidth(96);
                appIcon.setMinimumHeight(96);
                appIcon.setMinimumWidth(96);

                TextView appLabel = convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).label);

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }

    static List<AppDetail> apps;
    static PackageManager manager;

    public static void loadApps() {
        apps = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            if (app.label == null) {
                app.label = app.name;
            }
            app.icon = ri.activityInfo.loadIcon(manager);
            app.isAllowed = isAllowed(app.name.toString());
            if (app.isAllowed || isFirstOfApril)
                apps.add(app);
        }
        sortApps(apps);
    }

    public static void sortApps(List<AppDetail> apps) {
        Collections.sort(apps, new Comparator<AppDetail>() {
            @Override
            public int compare(AppDetail a1, AppDetail a2) {
                return a1.label.toString().compareToIgnoreCase(a2.label.toString());
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (kioskMode) {
//            Intent intent = new Intent(getApplicationContext(), Homescreen.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            //intent.putExtra("EXIT", true);
//            startActivity(intent);
//            kioskMode = false;
//        }
//    }

    @Override
    protected void onResume() {
        kioskMode = true;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    static boolean kioskMode = true;

    @Override
    protected void onDestroy() {
        list = null;
        thisOne = null;
        viewGroup = null;

        if (kioskMode) {
            startActivity(this.getIntent());
        }
        super.onDestroy();
        try {
            viewGroup.onFinalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static customViewGroup viewGroup;

    public static void preventStatusBarExpansion(Context context, Activity activity, boolean overlay) {
        WindowManager manager = ((WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        if (overlay) {
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        } else {
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //https://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }

        localLayoutParams.height = result;

        localLayoutParams.format = PixelFormat.TRANSPARENT;

        customViewGroup view = new customViewGroup(context, activity);
        assert manager != null;
        manager.addView(view, localLayoutParams);
        viewGroup = view;
        Log.d("status_bar_blocker", "ADDED");
    }
}