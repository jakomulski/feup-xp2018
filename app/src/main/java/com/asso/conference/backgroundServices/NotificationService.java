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
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.WebClientService;
import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.EventModel;
import com.asso.conference.webClient.models.LoginDataModel;
import com.asso.conference.webClient.models.ResponseModel;
import com.asso.conference.webClient.models.UserModel;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends IntentService {

        UserService userService = UserService.INSTANCE;
        private NotificationManager mNotificationManager;
        PendingIntent contentIntent;
        WebClientService service;

        private boolean isSending = false;
        public int THREAD_SLEEP = 60000;
        private int notificationCounter = 0;

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

                contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, HomeActivity.class), 0);


                while(true) {
                        try {
                                Thread.sleep(THREAD_SLEEP);
                                if (!isSending) {
                                        isSending = true;
                                        userService.getNextEvent( new BookmarkCallback<EventModel>() {
                                                @Override
                                                public void onSuccess(EventModel nextEvent) {
                                                        popupNotification(nextEvent.name,nextEvent.description,nextEvent.minsToHappen);
                                                        isSending = false;
                                                }

                                                @Override
                                                public void onError(String message) {
                                                        isSending = false;
                                                }
                                        });
                                }
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                }
        }

        private void popupNotification(String name, String description, int minsToHappen) {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(name)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(description + " (" + minsToHappen + " min.)"))
                                .setContentText(description + " (" + minsToHappen + " min.)")
                                .setSmallIcon(R.drawable.xp2018_logo)
                                .setVibrate(new long[]{300,300});
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(notificationCounter, mBuilder.build());
                notificationCounter++;
        }
}
