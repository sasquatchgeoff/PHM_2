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
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class Alarm {
    private static SoundPool pool = null;
    private static int id = -1;
    private static boolean mSmsEscalation;
    public static boolean mUserAckAlarm;
    private static CountDownTimer mTimer;

    @SuppressWarnings("deprecation")
    public static void siren(Context context) {
        if (null == pool) {
            pool = new SoundPool(5, AudioManager.STREAM_ALARM, 0);
        }
        if (-1 == id) {
            id = pool.load(context.getApplicationContext(), R.raw.alarm, 1);
        }
        loudest(context, AudioManager.STREAM_ALARM);
        pool.play(id, 1.0f, 1.0f, 1, 3, 1.0f);
    }

    public static void loudest(Context context, int stream) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int loudest = manager.getStreamMaxVolume(stream);
        manager.setStreamVolume(stream, loudest, 0);
    }

    public static void sendSMS(Context context){
        //TODO need to get number from user
        String phoneNumber="";
        String message="Hello World!";
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        // Show the toast
        Toast.makeText(context, "SMS Sent", Toast.LENGTH_LONG).show();
    }

    public static void call(final Context context) {
        mUserAckAlarm = false;
        mSmsEscalation = false;

        siren(context);

        //Code commented temporary until full integration
//        mTimer = showTimer(context, 1000*60*2);
//
//        Thread smsThread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    sleep(1000*60*2);//sleep for 2 minute
//                    int tried = 1;
//                    do {
//                        if(mSmsEscalation) {
//                            sendSMS(context);
//                        }
//                        sleep(1000 * 10);//sleep ten sec and check again
//                    }while(tried != 2);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        smsThread.start();
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
                    //TODO Set timer on activity
                    //MainActivity.setAlertTimer(display);
                } else {
                    mTimer.cancel();
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
}
