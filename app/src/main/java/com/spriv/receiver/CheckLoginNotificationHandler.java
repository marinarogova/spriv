package com.spriv.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.activity.BootstrapActivity;
import com.spriv.data.CheckLoginResultInfo;
import com.spriv.firebase.MyFirebaseMessagingService;
import com.spriv.json.Tags;
import com.spriv.model.ServerAPI;

public class CheckLoginNotificationHandler extends PushNotificationHandler {

    public static final int NOTIFICATION_ID = 1;
    private static int m_intentCounter = 1;

    @Override
    public void handle() {
        try {
            CheckLoginResultInfo checkLoginResultInfo = ServerAPI.checkLogin(getTransactionId());
            if (checkLoginResultInfo != null) {
                Intent checkLoginIntent = new Intent(context, BootstrapActivity.class);
                checkLoginIntent.putExtra(Tags.NotificationType, Tags.CheckLogin);
                checkLoginIntent.putExtra(Tags.Id, getTransactionId());
                checkLoginIntent.putExtra(Tags.NotificationContent, checkLoginResultInfo);
                checkLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //Don't show up in recent applications list
                checkLoginIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                checkLoginIntent.setAction("Check login - " + getTransactionId());
                //Foreground - open activity
                if (SprivApp.InForground) {
                    context.startActivity(checkLoginIntent);
                }
                //Background - show notification
                {
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent checkLoginPendingIntent = PendingIntent.getActivity(context, ++m_intentCounter,
                            checkLoginIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    CharSequence title = getTitle();
                    CharSequence text = getText();
                    Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    NotificationCompat.Builder mBuilder;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel notificationChannel = new NotificationChannel(MyFirebaseMessagingService.NOTIFICATION_CHANNEL, MyFirebaseMessagingService.NOTIFICATION_CHANNEL, importance);
                        if (mNotificationManager != null)
                            mNotificationManager.createNotificationChannel(notificationChannel);
                        mBuilder = new NotificationCompat.Builder(context, notificationChannel.getId());
                    } else {
                        mBuilder = new NotificationCompat.Builder(context);
                    }

                    //NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MyFirebaseMessagingService.NOTIFICATION_CHANNEL)
                    mBuilder.setSmallIcon(R.drawable.ic_launcher)
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(text))
                            .setContentText(text)
                            .setSound(sound);

                    //mBuilder.setContentIntent(contentIntent);
                    mBuilder.setContentIntent(checkLoginPendingIntent);
                    if (mNotificationManager != null)
                        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CharSequence getText() {
        return context.getText(R.string.check_login_notif_text);
    }

    private CharSequence getTitle() {
        // TODO Auto-generated method stub
        return context.getText(R.string.app_name);
    }

}
