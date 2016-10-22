/*
Project Name: PHM
Author Name: Advait, Artem, Geoff, Tahani & Yatin.
*/

package capstone.se491_phm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import capstone.se491_phm.jobs.DailyActivityMonitorJob;
import capstone.se491_phm.jobs.WeeklyActivityMonitorJob;
import capstone.se491_phm.sensors.ISensors;
import capstone.se491_phm.sensors.StepCounter;

public class MainActivity extends Activity {
    static Context mContext;
    Map<String, ISensors> sensors;
    private AlarmManager alarmMgr;
    private Map<String,PendingIntent> mAlarmIntents = new HashMap<String,PendingIntent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getBaseContext();
        initSensors();
        createScheduleJobs();
    }

    /**
     * Called when app is killed
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //need to save any collected data before exit
        for(ISensors sensor : sensors.values()){
            sensor.saveData(mContext);
        }
    }

    private void initSensors(){
        sensors = new HashMap<>();
        //do not need to save reference for fall detector
        //Detector.initiate(getContextMain());
        StepCounter stepCounter = new StepCounter();
        stepCounter.initialize(mContext);

        //add all initialized sensors
        sensors.put("stepCounter",stepCounter);
    }

    /**
     * delete all schedules by looping through the list with schedules and canceling them one by one
     * @param view
     */
    public void deleteSchedules(View view) {
        if (alarmMgr!= null) {
            for(PendingIntent intent : mAlarmIntents.values()) {
                alarmMgr.cancel(intent);
            }
        }
    }

    private void createScheduleJobs(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //daily job to clear daily activity group
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentDailyActivity = new Intent(mContext, DailyActivityMonitorJob.class);
        PendingIntent alarmIntentDailyActivity = PendingIntent.getBroadcast(mContext, 0, intentDailyActivity, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC,
                calendar.getTimeInMillis(),
                1000*60*60*24, alarmIntentDailyActivity);
        mAlarmIntents.put("dailyActivityMonitorJob",alarmIntentDailyActivity);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 0);
        calendar2.set(Calendar.MINUTE, 30);
        calendar2.set(Calendar.SECOND, 0);
        //weekly job to clear weekly activity group
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentWeeklyActivity = new Intent(mContext, WeeklyActivityMonitorJob.class);
        PendingIntent alarmIntentWeeklyActivity = PendingIntent.getBroadcast(mContext, 0, intentWeeklyActivity, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC,
                calendar2.getTimeInMillis(),
                1000*60*60*24*7, alarmIntentWeeklyActivity);
        mAlarmIntents.put("weeklyActivityMonitorJob",alarmIntentWeeklyActivity);

    }

    public Context getContextMain(){
        if(mContext == null){
            mContext = getBaseContext();
        }
        return mContext;
    }
}
