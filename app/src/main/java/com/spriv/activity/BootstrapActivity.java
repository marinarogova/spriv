package com.spriv.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.data.CheckLoginResultInfo;
import com.spriv.data.CheckVerificationResultInfo;
import com.spriv.json.Tags;
import com.spriv.model.AppSettingsModel;
import com.spriv.receiver.PushNotificationType;
import com.spriv.service.GpsService;
import com.spriv.service.ServiceNoDelay;
import com.spriv.utils.InstallationManager;

public class BootstrapActivity extends AppCompatActivity {

    private String SPRIV_SCHEMA_PREFIX = "spriv://";

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d ("MyServiceRun_make_log", true+"");
                return true;
            }
        }
        Log.d ("MyServiceRun_make_log", false+"");
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerUserIfNeeded();
        String uriContent = null;
        Uri uri = getIntent().getData();
        if (uri != null) {
            String uriStr = uri.toString();
            if (uriStr != null && uriStr.startsWith(SPRIV_SCHEMA_PREFIX)) {
                uriContent = uriStr.substring(SPRIV_SCHEMA_PREFIX.length());
            }
        }

        // GPS Service Start
        Intent intent = new Intent(this, GpsService.class);
        startService(intent);

        // play referrer API

        final InstallReferrerClient referrerClient;

        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.

                        try {
                            ReferrerDetails response = referrerClient.getInstallReferrer();
                            String referrer = response.getInstallReferrer();
                            referrerClient.endConnection();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.

                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


//        ReferrerDetails response = null;
//        try {
//            response = referrerClient.getInstallReferrer();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        String referrerUrl = response.getInstallReferrer();
//        long referrerClickTime = response.getReferrerClickTimestampSeconds();
//        long appInstallTime = response.getInstallBeginTimestampSeconds();
//        boolean instantExperienceLaunched = response.getGooglePlayInstantParam();



        ServiceNoDelay mSensorService = new ServiceNoDelay(getApplicationContext());
        Intent mServiceIntent = new Intent(getApplicationContext(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }



        boolean navigated = tryNavigateToNextPage();
        if (!navigated) {
            if (uriContent != null) {
                ActivityNavigator.navigateToAddAccount(this, uriContent);
            } else {
                //Toast.makeText(this, "WELCOME_CHECK + " + AppSettingsModel.getInstance().isShowWelcome(), Toast.LENGTH_LONG).show();
                if (AppSettingsModel.getInstance().isShowWelcome()) {
                    ActivityNavigator.navigateToWelcome(this);
                } else {
                    ActivityNavigator.navigateToMain(this);
                }
            }
        }
        finish();
        //setContentView(R.layout.activity_bootstrap);
    }

    private boolean tryNavigateToNextPage() {
        String notificationType = getIntent().getStringExtra(Tags.NotificationType);
        String type = getIntent().getStringExtra(Tags.Type);
        Log.d("usm_notification_val", "notifType= " + notificationType + " ,type= " + type);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("usm_extra", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        if (Tags.CheckLogin.equals(notificationType) || String.valueOf(PushNotificationType.CHECK_LOGIN).equals(type)) {
            Intent checkLoginIntent = new Intent(this, CheckLoginActivity.class);
            Bundle data = getIntent().getExtras();
            String transactionId = data.getString(Tags.Id);
            CheckLoginResultInfo checkLoginResultInfo = (CheckLoginResultInfo) data.getParcelable(Tags.NotificationContent);
            checkLoginIntent.putExtra(Tags.Id, transactionId);
            checkLoginIntent.putExtra(Tags.NotificationContent, checkLoginResultInfo);
            checkLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            checkLoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(checkLoginIntent);
            return true;
        } else if (Tags.CheckVerification.equals(notificationType) || String.valueOf(PushNotificationType.CHECK_VERIFICATION).equals(type)) {
            Intent checkVerificationIntent = new Intent(this, CheckVerificationActivity.class);
            Bundle data = getIntent().getExtras();
            String transactionId = data.getString(Tags.Id);
            CheckVerificationResultInfo checkLoginResultInfo = (CheckVerificationResultInfo) data.getParcelable(Tags.NotificationContent);
            checkVerificationIntent.putExtra(Tags.Id, transactionId);
            checkVerificationIntent.putExtra(Tags.NotificationContent, checkLoginResultInfo);
            checkVerificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            checkVerificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(checkVerificationIntent);
            return true;
        }

        return false;
    }

    @Override
    protected void onPause() {
        SprivApp.InForground = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        SprivApp.InForground = true;
        super.onResume();
    }

    private void registerUserIfNeeded() {
        //boolean userAlreadyRegistered = InstallationManager.isRegistered(this);
        InstallationManager.id(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bootstrap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
