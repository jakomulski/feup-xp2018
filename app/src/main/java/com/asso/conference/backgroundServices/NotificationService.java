package com.asso.conference.backgroundServices;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.asso.conference.HomeActivity;
import com.asso.conference.R;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.GithubService;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.WebClientService;
import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.EventModel;
import com.asso.conference.webClient.models.LoginDataModel;
import com.asso.conference.webClient.models.ResponseModel;
import com.asso.conference.webClient.models.UserModel;
import com.asso.conference.webClient.models.XpEvent;

import java.io.IOException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.PriorityQueue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationService extends IntentService {

    UserService userService = UserService.INSTANCE;
    private NotificationManager mNotificationManager;
    PendingIntent contentIntent;
    GithubService githubService = GithubService.getClient();

    private boolean isSending = false;
    public int THREAD_SLEEP = 60000;
    private int notificationCounter = 0;

    public NotificationService() {
        super("Notification");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Notification service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

//    @Override
//    protected void onHandleIntent(Intent workIntent) {
//        mNotificationManager = (NotificationManager)
//                this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, HomeActivity.class), 0);
//
//
//        while (true) {
//            try {
//                Thread.sleep(THREAD_SLEEP);
//                if (!isSending) {
//                    isSending = true;
//                    userService.getNextEvent(new BookmarkCallback<EventModel>() {
//                        @Override
//                        public void onSuccess(EventModel nextEvent) {
//                            popupNotification(nextEvent.name, nextEvent.description, nextEvent.minsToHappen);
//                            isSending = false;
//                        }
//
//                        @Override
//                        public void onError(String message) {
//                            isSending = false;
//                        }
//                    });
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        PriorityQueue<XpEvent> queue = null;

        while (queue == null) {
                try {
                    Response<List<XpEvent>> response = githubService.getXpEvents().execute();
                    if(response.isSuccessful()){
                        queue = createQueue(response.body());
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            try {
                Thread.sleep(THREAD_SLEEP);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(!queue.isEmpty()){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 5);
            Date time = calendar.getTime();
            for(XpEvent event = queue.peek(); time.after(event.FIELD3); event = queue.peek()){
                popupNotification(event.FIELD2, event.FIELD8, 5);
                queue.poll();
            }

            try {
                Thread.sleep(THREAD_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private PriorityQueue createQueue(List<XpEvent> events){
        PriorityQueue<XpEvent> queue = new PriorityQueue(100, new Comparator<XpEvent>() {
            @Override
            public int compare(XpEvent e1, XpEvent e2) {
                return e1.FIELD3.compareTo(e2.FIELD3);
            }
        });
        Date currentTime = Calendar.getInstance().getTime();

        for(XpEvent e : events){
            if(e!= null && e.FIELD3 != null && e.FIELD3.after(currentTime))
                queue.add(e);
        }
        return queue;
    }


    private void popupNotification(String name, String description, int minsToHappen) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(name)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(description + " (" + minsToHappen + " min.)"))
                        .setContentText(description + " (" + minsToHappen + " min.)")
                        .setSmallIcon(R.drawable.xp2018_logo)
                        .setVibrate(new long[]{300, 300});
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notificationCounter, mBuilder.build());
        notificationCounter++;
    }
}

class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // show toast
        Toast.makeText(context, "Alarm running", Toast.LENGTH_SHORT).show();
    }
}
