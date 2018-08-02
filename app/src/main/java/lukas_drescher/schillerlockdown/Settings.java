package lukas_drescher.schillerlockdown;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSwitches();
        loadWhiteList();
        loadApps();
        loadListView();
        addClickListener();
    }

    public void loadWhiteList() {
        whitelist = new ArrayList<>(getDefaultSharedPreferences(getApplicationContext()).getStringSet(getString(R.string.whitelist), new HashSet<String>()));
    }

    public void saveWhiteList() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putStringSet(getString(R.string.whitelist), new HashSet<>(whitelist)).apply();
        String whitelist = "";
        for (int i = 0; i < this.whitelist.size(); i++) {
            if (!whitelist.equals(""))
                whitelist = whitelist + "\n";
            whitelist = whitelist + "- " + this.whitelist.get(i);
        }
        Log.v("whitelist_save", whitelist);
    }

    public void allowApp(String app) {
        whitelist.add(app);
        saveWhiteList();
    }

    public void forbidApp(String app) {
        Iterator i = whitelist.iterator();
        while (i.hasNext()) {
            if (i.next().equals(app))
                i.remove();
        }
        saveWhiteList();
    }

    ArrayList<String> whitelist;

    public boolean isAllowed(String packageName) {
        for (int i = 0; i < whitelist.size(); i++) {
            if (whitelist.get(i).equals(packageName))
                return true;
        }
        return false;
    }

    private void addClickListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                if (apps.get(pos).isAllowed) {
                    forbidApp(apps.get(pos).name.toString());
                    v.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    allowApp(apps.get(pos).name.toString());
                    v.setBackgroundColor(Color.GREEN);
                }
                apps.get(pos).isAllowed = !apps.get(pos).isAllowed;
            }
        });
    }

    ListView list;

    private void loadListView() {
        list = findViewById(R.id.appList);

        ArrayAdapter<AppDetailPlus> adapter = new ArrayAdapter<AppDetailPlus>(this,
                R.layout.support_simple_spinner_dropdown_item,
                apps) {
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.settings_list_item, null);
                }

                if (apps.get(position).isAllowed) {
                    convertView.setBackgroundColor(Color.GREEN);
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                }

                ImageView appIcon = convertView.findViewById(R.id.settings_item_app_icon);
                appIcon.setImageDrawable(apps.get(position).icon);
                appIcon.setAdjustViewBounds(true);
                appIcon.setMaxHeight(96);
                appIcon.setMaxWidth(96);

                TextView appLabel = convertView.findViewById(R.id.settings_item_app_label);
                appLabel.setText(apps.get(position).label + " (" + apps.get(position).name + ")");

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }

    List<AppDetailPlus> apps;
    PackageManager manager;

    public void loadApps() {
        manager = getPackageManager();
        apps = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            AppDetailPlus app = new AppDetailPlus();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            if (app.label == null) {
                app.label = app.name;
            }
            app.icon = ri.activityInfo.loadIcon(manager);
            app.isAllowed = isAllowed(app.name.toString());
            apps.add(app);
        }
        sortApps(apps);
    }

    public static void sortApps(List<AppDetailPlus> apps) {
        Collections.sort(apps, new Comparator<AppDetail>() {
            @Override
            public int compare(AppDetail a1, AppDetail a2) {
                return a1.label.toString().compareToIgnoreCase(a2.label.toString());
            }
        });
    }

    public void setSwitches() {
        Switch showStatusBarBlockedMessage = findViewById(R.id.showStatusBarBlockedMessage);
        showStatusBarBlockedMessage.setChecked(getDefaultSharedPreferences(getApplicationContext()).getBoolean("show_Statusbar_blocked_message", false));
        showStatusBarBlockedMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("show_Statusbar_blocked_message", isChecked).apply();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            finish();
        }
    }

    public void changeLauncher(View view) {
        startActivity(new Intent(android.provider.Settings.ACTION_HOME_SETTINGS));
    }

    public void openSystemSettings(View view) {
        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
    }

    public void changePIN(View view) {
        startActivity(new Intent(getApplicationContext(), ChangePIN.class));
    }

    public void activateBackgroundService(View view) {
        startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }
}
