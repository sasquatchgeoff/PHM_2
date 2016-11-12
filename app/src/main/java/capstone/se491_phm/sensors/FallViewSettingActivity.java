package capstone.se491_phm.sensors;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;

/**
 * Created by Acer on 11/9/2016.
 */

public class FallViewSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fallview);
        EditText phoneField = ((EditText) findViewById(R.id.emergencyPhoneField));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        phoneField.setText(sharedPreferences.getString(Constants.EMERGENCY_CONTACT,""));
    }

    public void saveEmergencyPhone(View view) {
        String phoneNumber = ((EditText) findViewById(R.id.emergencyPhoneField)).getText().toString();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.edit().putString(Constants.EMERGENCY_CONTACT,phoneNumber).commit();

        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
