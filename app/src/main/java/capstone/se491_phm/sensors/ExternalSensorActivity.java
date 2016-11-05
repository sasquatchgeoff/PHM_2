package capstone.se491_phm.sensors;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;

/**
 * Created by Acer on 11/4/2016.
 */

public class ExternalSensorActivity extends Activity {
    BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getBaseContext();
        //cancel notification
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

        setContentView(R.layout.ext_monitoring_session);

        //TODO Remove after have screen for external sensors
        (findViewById(R.id.textViewSuccess)).setVisibility(View.INVISIBLE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String broadcastMessage = intent.getStringExtra(ExternalSensorClient.externalSensorClientMessage);
                if("error".equals(broadcastMessage)){
                    connectionIssueNotification();
                } else if("success".equals(broadcastMessage)){
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(3);
                    (findViewById(R.id.textViewSuccess)).setVisibility(View.VISIBLE);
                }
                hideLoading(null);
            }
        };

        PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(Constants.SERVER_VERIFIED,true).commit();

        startSensorClient();
    }

    private void startSensorClient(){
        showLoading(null);
        (findViewById(R.id.connectionErrorMessage)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.btnRetry)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.btnClose)).setVisibility(View.INVISIBLE);
        //start external sensor client
        Intent intent = new Intent(this, ExternalSensorClient.class);
        startService(intent);

    }

    public void showLoading(View view){
        (findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
    }

    public void hideLoading(View view){
        (findViewById(R.id.progressBar1)).setVisibility(View.INVISIBLE);
    }

    public void connectionIssueNotification(){
        (findViewById(R.id.connectionErrorMessage)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnRetry)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnClose)).setVisibility(View.VISIBLE);
    }

    public void backToMain(View view) {
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void retryConnection(View view) {
        startSensorClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(ExternalSensorClient.externalSensorBroadcastIntent)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }
}
