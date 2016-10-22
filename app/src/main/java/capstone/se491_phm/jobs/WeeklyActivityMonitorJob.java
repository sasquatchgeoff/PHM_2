package capstone.se491_phm.jobs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import capstone.se491_phm.common.Constants;
import capstone.se491_phm.sensors.StepCounter;

/**
 * Created by Acer on 10/22/2016.
 */

public class WeeklyActivityMonitorJob extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("WeekActivityMonitorJob","weekly alarm fired");
        long totalStep = StepCounter.getActivityDataGroup(Constants.ActivityGroup.TOTAL.toString());

        StepCounter stepCounter = new StepCounter();
        if(!stepCounter.isInitialized()) {
            stepCounter.populateActivityMap(context);
        }
        stepCounter.updateActivityGroup(Constants.ActivityGroup.WEEKLY.toString(),0L);
        stepCounter.saveData(context);
    }
}
