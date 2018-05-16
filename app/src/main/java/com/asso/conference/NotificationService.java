package com.asso.conference;

import android.app.Notification;
import android.content.Context;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationService {
    public static void showNotification(Context context, String title, String message){
        PugNotification.with(context)
                .load()
                .title(title)
                .message(message)
                .bigTextStyle("style")
                .smallIcon(R.drawable.xp2018_logo)
                .largeIcon(R.drawable.xp2018_logo)
                .flags(Notification.DEFAULT_ALL)
                .simple()
                .build();
    }
}
