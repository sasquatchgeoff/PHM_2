package capstone.se491_phm.sensors;


/**
 * https://developers.google.com/cloud-messaging/android/client
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import capstone.se491_phm.R;

public class ExternalSensorClient extends IntentService {

    private static final String TAG = "ExternalSensorClient";

    public ExternalSensorClient() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String messageResponse;
        Socket s = null;
        String sendM = "Start Monitoring";

        try {
            s = new Socket("192.168.1.109", 12345);

            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
            osw.write(sendM + "\n");
            Log.i(TAG, "Success");
            osw.flush();

            InputStreamReader isR = new InputStreamReader(s.getInputStream());
            BufferedReader bfr = new BufferedReader(isR);
            messageResponse = bfr.readLine();
            Log.i(TAG, messageResponse + "\n");
        } catch (Exception ex) {
            Log.i(TAG, "Could not be sent");
        } finally {
            if(s != null){
                try {
                    s.close();
                } catch (IOException e) {
                    Log.i(TAG, "Trying to close socket but it is already closed");
                }
            }
        }
    }
}

