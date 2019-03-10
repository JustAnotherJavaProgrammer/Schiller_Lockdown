package lukas_drescher.schillerlockdown;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutPage extends AppCompatActivity {

    public static ActionBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        titleBar = getSupportActionBar();
        titleBar.setDisplayHomeAsUpEnabled(true);
        titleBar.setBackgroundDrawable(new ColorDrawable(Util.getStatusBarColor(getApplicationContext(), getResources())));
        ((TextView) findViewById(R.id.txtviewSchillerQuote)).setText(chooseRandom(R.array.schiller_quotes));
        if (AprilFool.isFirstOfApril()) {
            findViewById(R.id.ad_about_page).setVisibility(View.VISIBLE);
            findViewById(R.id.ad_about_page).setOnClickListener(AprilFool.adOnClick());
        }
    }

    private String chooseRandom(int id) {
        String[] array = getResources().getStringArray(id);
        return array[(int) (Math.random() * array.length)];
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
