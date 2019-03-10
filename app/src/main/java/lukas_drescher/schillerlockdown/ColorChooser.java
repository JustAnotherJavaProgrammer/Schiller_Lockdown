package lukas_drescher.schillerlockdown;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class ColorChooser extends AppCompatActivity {
    int[] colors = new int[]{R.color.colorPrimary, R.color.colorAccent, android.R.color.black, android.R.color.darker_gray, android.R.color.holo_blue_bright, android.R.color.holo_blue_light, android.R.color.holo_blue_dark, android.R.color.holo_green_dark, android.R.color.holo_green_light, android.R.color.holo_orange_dark, android.R.color.holo_orange_light, android.R.color.holo_purple, android.R.color.holo_red_dark, android.R.color.holo_red_light};

    public static ActionBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_chooser);
        int currentColor = Util.getStatusBarColor(getApplicationContext(), getResources());
        titleBar = getSupportActionBar();
        titleBar.setDisplayHomeAsUpEnabled(true);
        titleBar.setBackgroundDrawable(new ColorDrawable(currentColor));
        RadioGroup colorSelectList = findViewById(R.id.colorSelectList);
        for (int i = 0; i < colors.length; i++) {
            int realColorInt = ResourcesCompat.getColor(getResources(), colors[i], null);
            RadioButton option = (RadioButton) LayoutInflater.from(this).inflate(R.layout.selectable_radio_button, colorSelectList, false);
            option.setBackground(new ColorDrawable(realColorInt));
            option.setId(i);
            option.setChecked(getDefaultSharedPreferences(this).getBoolean("custom_color_active", false) && currentColor == realColorInt);
            colorSelectList.addView(option);
        }
        ((RadioGroup) findViewById(R.id.colorSelectList)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setCustomColor(ResourcesCompat.getColor(getResources(), colors[i], null));
            }
        });

        if (AprilFool.isFirstOfApril()) {
            findViewById(R.id.april_fools_function_blocker_clolor_chooser).setVisibility(View.VISIBLE);
            findViewById(R.id.april_fools_function_blocker_clolor_chooser).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AprilFool.showAllowedAppDialogApril(view, null);
                }
            });
        }
    }

    public void setCustomColor(int color) {
        getDefaultSharedPreferences(this).edit().putBoolean("custom_color_active", true).putInt("custom_color", color).apply();
        int bgColor = Util.getStatusBarColor(this, getResources());
        if (Homescreen.viewGroup != null) {
            Homescreen.viewGroup.findViewById(R.id.linearLayoutStatusbar).setBackgroundColor(bgColor);
        }
        if (Homescreen.titleBar != null) {
            Homescreen.titleBar.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(bgColor));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
