/*
Project Name: PHM
Author Name: Advait, Artem, Geoff, Tahani & Yatin.
*/

package capstone.se491_phm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Switch;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import capstone.se491_phm.activities.LoginActivity;
import capstone.se491_phm.gcm.RegistrationIntentService;
import capstone.se491_phm.jobs.DailyActivityMonitorJob;
import capstone.se491_phm.jobs.MoodDailyJob;
import capstone.se491_phm.jobs.MoodSurvey;
import capstone.se491_phm.jobs.WeeklyActivityMonitorJob;
import capstone.se491_phm.sensors.ExternalSensorClient;
import capstone.se491_phm.sensors.ISensors;
import capstone.se491_phm.sensors.StepCounter;

public class MainActivity extends Activity {
    static Context mContext;
    Map<String, ISensors> sensors = new HashMap<>();
    private AlarmManager alarmMgr;
    private Map<String,PendingIntent> mAlarmIntents = new HashMap<String,PendingIntent>();
    public static NotificationManager mNotificationManager;
    public static final String PREFS_NAME = "PhmPrefsFile";
    public static SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getBaseContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        //cancel all notification created by the app
        mNotificationManager =(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        //start the external sensor client service
        //Intent intent = new Intent(this, ExternalSensorClient.class);
        //startService(intent);
        //register for gcm
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        //init sensors
        initSensors();
        //create all jobs
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
        //do not need to save reference for fall detector
        Detector.initiate(getContextMain());
        StepCounter stepCounter = new StepCounter();
        stepCounter.initialize(mContext);

        //add all initialized sensors
        sensors.put("stepCounter",stepCounter);

        if(sharedPreferences != null) {
            ((Switch) findViewById(R.id.activitySwitch)).setChecked(sharedPreferences.getBoolean("activitySwitch", true));
            ((Switch) findViewById(R.id.fallSwitch)).setChecked(sharedPreferences.getBoolean("fallSwitch", true));
            ((Switch) findViewById(R.id.externalSwitch)).setChecked(sharedPreferences.getBoolean("externalSwitch", true));
        }
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
        boolean alarmIntentDailyActivityActive = (PendingIntent.getBroadcast(mContext, 0,
                intentDailyActivity, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmIntentDailyActivityActive)
        {
            PendingIntent alarmIntentDailyActivity = PendingIntent.getBroadcast(mContext, 0, intentDailyActivity, 0);
            alarmMgr.setRepeating(AlarmManager.RTC,
                    calendar.getTimeInMillis(),
                    1000*60*60*24, alarmIntentDailyActivity);
            mAlarmIntents.put("dailyActivityMonitorJob",alarmIntentDailyActivity);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        //weekly job to clear weekly activity group
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentWeeklyActivity = new Intent(mContext, WeeklyActivityMonitorJob.class);
        boolean alarmIntentWeeklyActivityActive = (PendingIntent.getBroadcast(mContext, 1,
                intentWeeklyActivity, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmIntentWeeklyActivityActive)
        {
            PendingIntent alarmIntentWeeklyActivity = PendingIntent.getBroadcast(mContext, 1, intentWeeklyActivity, 0);
            alarmMgr.setRepeating(AlarmManager.RTC,
                    calendar.getTimeInMillis(),
                    1000*60*60*24*7, alarmIntentWeeklyActivity);
            mAlarmIntents.put("weeklyActivityMonitorJob",alarmIntentWeeklyActivity);
        }

        //mood daily job
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentMoodDaily = new Intent(mContext, MoodDailyJob.class);
        boolean alarmIntentMoodDailyActive = (PendingIntent.getBroadcast(mContext, 2,
                intentMoodDaily, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmIntentMoodDailyActive)
        {
            PendingIntent alarmIntentMoodDaily = PendingIntent.getBroadcast(mContext, 2, intentMoodDaily, 0);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_DAY, alarmIntentMoodDaily);
            mAlarmIntents.put("moodDaily",alarmIntentMoodDaily);
        }

        //start time for mood survey
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //start time for mood survey
        //weekly mood survey
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentWeeklySurvey = new Intent(mContext, MoodSurvey.class);
        boolean alarmIntentMoodSurveyActive = (PendingIntent.getBroadcast(mContext, 2,
                intentMoodDaily, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmIntentMoodSurveyActive)
        {
            PendingIntent alarmIntentMoodSurvey = PendingIntent.getBroadcast(mContext, 3, intentWeeklySurvey, 0);
            alarmMgr.setInexactRepeating(AlarmManager.RTC,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY*7, alarmIntentMoodSurvey);
            mAlarmIntents.put("moodSurvey",alarmIntentMoodSurvey);
        }
    }

    public Context getContextMain(){
        if(mContext == null){
            mContext = getBaseContext();
        }
        return mContext;
    }

    public void showWebPortal(View view) {
        setContentView(R.layout.activity_login);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void activitySwitch(View view) {
        Switch switch1 = (Switch) findViewById(R.id.activitySwitch);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(switch1.isChecked()){
            sensors.get("stepCounter").initialize(mContext);
            editor.putBoolean("activitySwitch", true);
        } else {
            sensors.get("stepCounter").stopMonitoring(view);
            editor.putBoolean("activitySwitch", false);
        }
        editor.commit();
    }
    public void fallSwitch(View view) {
        Switch switch1 = (Switch) findViewById(R.id.fallSwitch);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(switch1.isChecked()){
            Alarm.fallMonitoringOn = true;
            editor.putBoolean("fallSwitch", true);
        } else {
            Alarm.fallMonitoringOn = false;
            editor.putBoolean("fallSwitch", false);
        }
        editor.commit();
    }
    public void externalSwitch(View view) {
        Switch switch1 = (Switch) findViewById(R.id.externalSwitch);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(switch1.isChecked()){
            editor.putBoolean("externalSwitch", true);
        } else {
            editor.putBoolean("externalSwitch", false);
        }
        editor.commit();
    }
}
