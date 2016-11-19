/*
The MIT License (MIT)

Copyright (c) 2016

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package capstone.se491_phm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import capstone.se491_phm.common.Constants;
import capstone.se491_phm.location.LocationMgr;
import capstone.se491_phm.sensors.FallDetectedActivity;

public class Alarm {
    private static SoundPool pool = null;
    private static int id = -1;
    private static boolean mSmsEscalation;
    public static boolean mUserAckAlarm;
    private static CountDownTimer mTimer;
    private static Thread mSmsThread;
    public static boolean fallMonitoringOn = true;

    private static LocalBroadcastManager alertbroadcaster = null;
    static final public String alertBroadcastIntent = "capstone.se491_phm.MainActivity";//to
    static final public String broadcasterMessage = "capstone.se491_phm.Alarm";//from

    private static final int ESCALATION_TIME = 1000*60*2;

    @SuppressWarnings("deprecation")
    public static void siren(Context context) {
        if (null == pool) {
            pool = new SoundPool(5, AudioManager.STREAM_ALARM, 0);
        }
        if (-1 == id) {
            id = pool.load(context.getApplicationContext(), R.raw.alarm, 1);
            loudest(context, AudioManager.STREAM_ALARM);
            pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId,
                                           int status) {
                    soundPool.play(id, 1.0f, 1.0f, 1, 3, 1.0f);
                }
            });
        } else {
            loudest(context, AudioManager.STREAM_ALARM);
            pool.play(id, 1.0f, 1.0f, 1, 3, 1.0f);
        }
    }

    public static void loudest(Context context, int stream) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int loudest = manager.getStreamMaxVolume(stream);
        manager.setStreamVolume(stream, loudest, 0);
    }

    public static void sendSMS(Context context){
        String phoneNumber= PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.EMERGENCY_CONTACT, "");
        if(!"".equals(phoneNumber)){
            String message="Help I have fallen! Last know location: ";

            LocationMgr locMgr = new LocationMgr(context);
            message += locMgr.getLocationUrl();

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);

            broadcast(alertBroadcastIntent, "SMS Sent");
        } else {
            broadcast(alertBroadcastIntent, "Unable to send SMS. Missing number.");
        }
    }

    public static void call(final Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (fallMonitoringOn && !sharedPreferences.getBoolean(Constants.WAITING_FOR_FALL_ACK,false)){
            sharedPreferences.edit().putBoolean(Constants.WAITING_FOR_FALL_ACK,true).commit();

            mUserAckAlarm = false;
            mSmsEscalation = false;

            alertbroadcaster = LocalBroadcastManager.getInstance(context);
            broadcast(alertBroadcastIntent, "fallDetected");
            siren(context);

            mTimer = showTimer(context, ESCALATION_TIME);

            mSmsThread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(ESCALATION_TIME);
                        int tried = 1;
                        do {
                            if(mSmsEscalation) {
                                sendSMS(context);
                                this.interrupt();
                            }
                            sleep(1000 * 10);//sleep ten sec and check again
                        }while(tried != 2);

                    } catch (InterruptedException e) {
                        Log.i("Alarm","sms thread interrupted");
                    }
                }
            };
            mSmsThread.start();
        }
    }

    public static CountDownTimer showTimer(final Context context, long countDownFrom){
        return new CountDownTimer(countDownFrom, 1000) {
            public void onTick(long millisUntilFinished) {
                if(!mUserAckAlarm) {
                    String display = String.format("%02d min, %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                    );
                    FallDetectedActivity.setCountDownTextValue(display);
                } else {
                    cancelTimer();
                }
            }

            public void onFinish() {
                if(!mUserAckAlarm) {
                    mSmsEscalation = true;
                }
                this.cancel();
            }
        }.start();
    }

    public static void cancelTimer(){
        if(mTimer!=null){
            mTimer.cancel();
        }
    }

    public static void cancelSmsThread(){
        if(mSmsThread!=null){
            mSmsThread.interrupt();
        }
    }

    private static void broadcast(String broadcastIntent, String message) {
        Intent intent = new Intent(broadcastIntent);
        if(message != null)
            intent.putExtra(broadcasterMessage, message);
        if(alertbroadcaster != null) {
            alertbroadcaster.sendBroadcast(intent);
        }
    }
}
