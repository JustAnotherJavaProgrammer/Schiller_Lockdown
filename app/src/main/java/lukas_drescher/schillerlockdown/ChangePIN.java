package lukas_drescher.schillerlockdown;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePIN extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Util.getStatusBarColor(getApplicationContext(), getResources())));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Settings.class));
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

    public void setPIN(View view) {
        EditText newPIN = findViewById(R.id.enterNewPIN);
        EditText PIN2 = findViewById(R.id.PIN2);
        if (newPIN.getText().length() > 3) {
            if (newPIN.getText().toString().equals(PIN2.getText().toString())) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                try {
                    editor.putInt(getString(R.string.PIN), Integer.valueOf(newPIN.getText().toString()));
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), Settings.class));
                    finish();
                    Toast.makeText(getApplicationContext(), R.string.PIN_changed_successfully, Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), R.string.pin_too_large, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.PIN_not_identical, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.PIN_too_short, Toast.LENGTH_SHORT).show();
        }
    }
}
