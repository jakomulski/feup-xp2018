package com.asso.conference.backgroundServices;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.asso.conference.HomeActivity;
import com.asso.conference.R;
import com.asso.conference.webClient.WebClientService;
import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.LoginDataModel;
import com.asso.conference.webClient.models.ResponseModel;
import com.asso.conference.webClient.models.UserModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends IntentService {

        private NotificationManager mNotificationManager;
        public static final int NOTIFICATION_ID = 1;
        WebClientService service;

        public NotificationService() {
                super("Notification");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
                Toast.makeText(this, "Notification service starting", Toast.LENGTH_SHORT).show();
                return super.onStartCommand(intent,flags,startId);
        }

        @Override
        protected void onHandleIntent(Intent workIntent) {
                mNotificationManager = (NotificationManager)
                        this.getSystemService(Context.NOTIFICATION_SERVICE);

                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, HomeActivity.class), 0);

                //TODO long polling asking for next event here, upon success show notification
                try {
                        Thread.sleep(5000);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(this)
                                        .setContentTitle("Session #1")
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText("Whassssup"))
                                        .setContentText("Whassssup")
                                        .setSmallIcon(R.drawable.xp2018_logo)
                                        .setVibrate(new long[]{300,300});
                        mBuilder.setContentIntent(contentIntent);
                        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                        Thread.sleep(5000);
                        NotificationCompat.Builder mBuilder2 =
                                new NotificationCompat.Builder(this)
                                        .setContentTitle("Session #2")
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText("Whassssup2"))
                                        .setContentText("Whassssup2")
                                        .setSmallIcon(R.drawable.xp2018_logo)
                                        .setVibrate(new long[]{300,300});
                        mBuilder2.setContentIntent(contentIntent);
                        mNotificationManager.notify(NOTIFICATION_ID+1, mBuilder2.build());
                } catch (InterruptedException e) {
                        NotificationCompat.Builder mBuilder2 =
                                new NotificationCompat.Builder(this)
                                        .setContentTitle("GONE")
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText("GONE"))
                                        .setContentText("GONE")
                                        .setSmallIcon(R.drawable.xp2018_logo)
                                        .setVibrate(new long[]{300,300});
                        mBuilder2.setContentIntent(contentIntent);
                        mNotificationManager.notify(NOTIFICATION_ID+2, mBuilder2.build());
                        // Restore interrupt status.
                        Thread.currentThread().interrupt();
                }
        }
}
