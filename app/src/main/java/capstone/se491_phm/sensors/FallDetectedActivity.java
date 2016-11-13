package capstone.se491_phm.sensors;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import capstone.se491_phm.Alarm;
import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;

/**
 * Created by Acer on 11/12/2016.
 */

public class FallDetectedActivity extends Activity {
    public static TextView mAlertTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detected);
        mAlertTimer = ((TextView) findViewById(R.id.countDownText));
    }

    public void userAckFall(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.edit().putBoolean(Constants.WAITING_FOR_FALL_ACK,false).commit();

        Alarm.cancelTimer();
        Alarm.cancelSmsThread();

        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static void setCountDownTextValue(String value){
        if(mAlertTimer!=null){
            mAlertTimer.setText(value);
        }
    }
}
