package lukas_drescher.schillerlockdown;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AppLockscreen extends ConstraintLayout {

    String passwd = "";
    TextView passwdField;
    WindowManager wm;
    AppLockscreen here = this;
    AccessibilityEvent event;
    int currentColor = Util.getStatusBarColor(getContext(), getResources());
    boolean aprilFoolsMode = AprilFool.isFirstOfApril();

    public AppLockscreen(Context context, WindowManager wm, AccessibilityEvent event) {
        super(context);
        this.wm = wm;
        this.event = event;
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.app_lockscreen, this, true);
        passwdField = findViewById(R.id.codeView);
        changeColor();
        findViewById(R.id.zero).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "0";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.one).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "1";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.two).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "2";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.three).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "3";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.four).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "4";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.five).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "5";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.six).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "6";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.seven).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "7";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.eight).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "8";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.nine).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                passwd = passwd + "9";
                passwdField.setText(passwd);
            }
        });
        findViewById(R.id.backspace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwd.length() > 0) {
                    passwd = passwd.substring(0, passwd.length() - 1);
                    passwdField.setText(passwd);
                }
            }
        });
        findViewById(R.id.backspace).setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (passwd.length() > 0) {
                    passwd = "";
                    passwdField.setText(passwd);
                }
                return true;
            }
        });
        findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.toString(getDefaultSharedPreferences(getContext().getApplicationContext()).getInt(getContext().getString(R.string.PIN), 0)).equals(passwd)) {
                    getDefaultSharedPreferences(getContext()).edit().putLong("disabled until", System.currentTimeMillis() + 300000).apply();
                    hide();
                } else {
                    passwd = "";
                    passwdField.setBackgroundColor(Color.RED);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            passwdField.setBackgroundColor(currentColor);
                            passwdField.setText(passwd);
                        }
                    }, 500);
                }
            }
        });
        findViewById(R.id.backToHome).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext().getApplicationContext(), Homescreen.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("EXIT", true);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
                try {
                    ((ActivityManager) view.getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).killBackgroundProcesses(event.getPackageName().toString());
                } catch (NullPointerException e) {
                    Log.d("AppLockscreen", "No background processes to kill");
                }
//                wm.removeView(here);
            }
        });
        changeAprilFoolsMode();
    }

    public void changeColor() {
        ConstraintLayout me = findViewById(R.id.constraintLayoutLockscreen);
        me.setBackgroundColor(currentColor);
        int childCount = me.getChildCount();
        for (int i = 0; i < childCount; i++) {
            me.getChildAt(i).setBackgroundColor(currentColor);
        }
    }

    public void changeAprilFoolsMode() {
        if (aprilFoolsMode) {
            ((TextView) findViewById(R.id.lockscreen_title)).setText(R.string.april_app_locked);
            ((TextView) findViewById(R.id.lockscreen_instructions)).setText(R.string.enter_code_to_unlock_pro);
        } else {
            ((TextView) findViewById(R.id.lockscreen_title)).setText(R.string.app_locked);
            ((TextView) findViewById(R.id.lockscreen_instructions)).setText(R.string.enter_code_to_unlock);
        }
    }

//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//            Toast.makeText(getContext(), "TouchEvent intercepted!", Toast.LENGTH_SHORT).show();
//        return true;
//    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            // Close every kind of system dialog
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getContext().sendBroadcast(closeDialog);
        }
    }

    public void show() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
            passwd = "";
            passwdField.setText(passwd);
            if (currentColor != Util.getStatusBarColor(getContext(), getResources())) {
                currentColor = Util.getStatusBarColor(getContext(), getResources());
                changeColor();
            }
            if (aprilFoolsMode != AprilFool.isFirstOfApril()) {
                aprilFoolsMode = AprilFool.isFirstOfApril();
                changeAprilFoolsMode();
            }
            this.bringToFront();
        }
    }

    public void hide() {
        if (getVisibility() != GONE) {
            setVisibility(GONE);
        }
    }
}
