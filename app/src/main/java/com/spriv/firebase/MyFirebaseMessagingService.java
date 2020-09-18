package com.spriv.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.spriv.R;
import com.spriv.activity.BootstrapActivity;
import com.spriv.json.Tags;
import com.spriv.receiver.PushNotificationHandler;
import com.spriv.receiver.PushNotificationParser;
import com.spriv.utils.MySharedPreferences;

/**
 * Created by android4 on 9/20/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String NOTIFICATION_CHANNEL = "SprivChannel";

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        //Getting registration token
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        MySharedPreferences mySharedPreferences = new MySharedPreferences(getApplicationContext());
        mySharedPreferences.setFcmToken(refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "Data: " + remoteMessage.getData());
        // sendNotification(remoteMessage.getData().get("alert"));
        //Calling method to generate notification
//        sendNotification(remoteMessage.getData());

        try {
            String notificationType = remoteMessage.getData().get(Tags.Type);
            String transactionId = remoteMessage.getData().get(Tags.Id);
            PushNotificationHandler pushNotification = PushNotificationParser.toPushNotification(notificationType, transactionId);
            if (pushNotification == null) {
                Log.d("usm_pushNotification", "Notification is empty");
                return;
            }
            pushNotification.setContext(getApplicationContext());
            pushNotification.handle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, BootstrapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(MyFirebaseMessagingService.NOTIFICATION_CHANNEL, MyFirebaseMessagingService.NOTIFICATION_CHANNEL, importance);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
            mBuilder = new NotificationCompat.Builder(this, notificationChannel.getId());
        } else {
            mBuilder = new NotificationCompat.Builder(this);
        }

        //NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MyFirebaseMessagingService.NOTIFICATION_CHANNEL)
        mBuilder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        if (notificationManager != null)
            notificationManager.notify(0, mBuilder.build());
    }

}
