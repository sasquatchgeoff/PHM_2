package capstone.se491_phm.jobs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import capstone.se491_phm.common.Constants;
import capstone.se491_phm.common.util.FileManager;
import capstone.se491_phm.sensors.StepCounter;

/**
 * Created by Acer on 10/21/2016.
 */

public class DailyActivityMonitorJob extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DailyActivityMonitorJob","daily alarm fired");
        long totalStep = StepCounter.getActivityDataGroup(Constants.ActivityGroup.TOTAL.toString());

        StepCounter stepCounter = new StepCounter();
        if(!stepCounter.isInitialized()) {
            stepCounter.populateActivityMap(context);
        }
        stepCounter.updateActivityGroup(Constants.ActivityGroup.DAILY.toString(),0L);
        stepCounter.saveData(context);
    }
}
