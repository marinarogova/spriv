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
import com.spriv.activity.BootstrapActivity;
import com.spriv.firebase.MyFirebaseMessagingService;

public abstract class PushNotificationHandler {

    private static int intentCounter = 0;
    protected Context context;
    private int notificationType;
    private String m_transactionId;

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getTransactionId() {
        return m_transactionId;
    }

    public void setTransactionId(String transactionId) {
        m_transactionId = transactionId;
    }

    public void showSimpleNotification(Context context, int notificationId, String text, String title) {
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mainIntent = new Intent(context, BootstrapActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, ++intentCounter,
                mainIntent, 0);

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
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public abstract void handle();

    public void setContext(Context context2) {
        this.context = context2;

    }

}
