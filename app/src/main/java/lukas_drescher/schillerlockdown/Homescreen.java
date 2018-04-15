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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Homescreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), R.string.lockscreen_undisableable, Toast.LENGTH_LONG).show();
        }
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        findViewById(R.id.apps_list).setBackground(wallpaperDrawable);
        loadWhiteList();
        loadApps();
        loadListView();
        addClickListener();
        try {
            preventStatusBarExpansion(getApplicationContext(), this, false);
        } catch (RuntimeException e) {
            try {
                preventStatusBarExpansion(getApplicationContext(), this, true);
            } catch (RuntimeException e2) {
                Toast.makeText(getApplicationContext(), R.string.status_bar_unblockable, Toast.LENGTH_LONG).show();
            }
        }
        if (getDefaultSharedPreferences(getApplicationContext()).getInt(getString(R.string.PIN), -1) == -1) {
            kioskMode = false;
            startActivity(new Intent(getApplicationContext(), ChangePIN.class));
        }
    }

    public void loadWhiteList() {
        whitelist = new ArrayList<>(getDefaultSharedPreferences(getApplicationContext()).getStringSet(getString(R.string.whitelist), new HashSet<String>()));
    }

    ArrayList<String> whitelist;

    public boolean isAllowed(String packageName) {
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
                Intent intent = new Intent(getApplicationContext(), AboutPage.class);
                startActivity(intent);
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
                if (pinIsRight(pin)) {
                    Intent intent = new Intent(getApplicationContext(), Settings.class);
                    startActivity(intent);
                } else {
                    kioskMode = true;
                    Toast.makeText(getApplicationContext(), R.string.wrong_pin, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        etPINrequest = builder.show().findViewById(R.id.PINfield);
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

    private void addClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                kioskMode = false;
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).name.toString());
                startActivity(i);
            }
        });
    }

    ListView list;

    private void loadListView() {
        list = findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.support_simple_spinner_dropdown_item,
                apps) {
            @Override
            @NonNull
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);
                appIcon.setAdjustViewBounds(true);
                appIcon.setMaxHeight(96);
                appIcon.setMaxWidth(96);

                TextView appLabel = convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).label);

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }

    List<AppDetail> apps;
    PackageManager manager;

    public void loadApps() {
        manager = getPackageManager();
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
            if (isAllowed(app.name.toString()))
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

    boolean kioskMode = true;

    @Override
    protected void onDestroy() {
        if (kioskMode) {
            startActivity(this.getIntent());
        }
        super.onDestroy();
        try {
            viewGroup.setVisibility(customViewGroup.INVISIBLE);
        } catch (Exception e) {
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
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
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

        customViewGroup view = new customViewGroup(context);
        assert manager != null;
        manager.addView(view, localLayoutParams);
        viewGroup = view;
    }
}