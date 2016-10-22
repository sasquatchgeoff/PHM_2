package capstone.se491_phm.jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.questionnaire.MoodDaily;

/**
 * Created by Acer on 10/15/2016.
 */

public class MoodDailyJob extends BroadcastReceiver {
    public static int notificationId = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("Alarm","mood daily alarm");
        //Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle("PHM")
                        .setContentText("Enter your mood for today");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MoodDaily.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MoodDaily.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // mId allows you to update the notification later on.
        MainActivity.mNotificationManager.notify(notificationId, mBuilder.build());
    }
}
