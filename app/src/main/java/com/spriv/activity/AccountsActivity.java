package com.spriv.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.camera2.TotalCaptureResult;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.legacy.app.ActionBarDrawerToggle;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.spriv.BuildConfig;
import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.data.SprivAccount;
import com.spriv.list.AccountListAdapter;
import com.spriv.list.AccountListListener;
import com.spriv.list.MenuListAdapter;
import com.spriv.list.MenuListItemData;
import com.spriv.model.AccountsModel;
import com.spriv.model.ServerAPI;
import com.spriv.service.GpsService;
import com.spriv.utils.Connectivity;
import com.spriv.utils.InstallationManager;
import com.spriv.utils.PhoneInfoUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AccountsActivity extends AppCompatActivity implements OnItemClickListener, AccountListListener {

    File logFile;
    public static boolean flag = false;
    public final static short REQUEST_CODE_QR_SCAN = 11;
    private static String SUPPORT_EMAIL_ADDRESS = "support@spriv.com";
    private ListView m_DrawerList;
    private DrawerLayout m_DrawerLayout;
    private ArrayList<MenuListItemData> m_menuItems;
    private ActionBarDrawerToggle m_DrawerToggle;
    private String m_Title;
    private String m_DrawerTitle;
    private List<SprivAccount> m_accounts;
    private ListView m_accountsList;
    //private View m_accountsEmptyLayout;
    private AccountListAdapter m_accountListAdapter;
    private static final int REQUEST = 112;

    public LocationManager myLocationManager;
    public boolean w_bGpsEnabled, w_bNetworkEnabled;

    public  static double w_fLatitude = 0;
    public  static double w_fLongitude = 0;

    public final static int MY_PERMISSION_LOCATION = 123;
    boolean GpsStatus = false ;
    Location location;
    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_accounts);

      //  Toast.makeText(this, " here", Toast.LENGTH_LONG).show();
//        m_Title = getString(R.string.title_accounts);
        m_DrawerTitle = getString(R.string.drawer_open);

        //Navigation drawer
        m_DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        m_DrawerList = (ListView) findViewById(R.id.left_drawer);

        m_DrawerToggle = new ActionBarDrawerToggle(this, m_DrawerLayout, R.drawable.ic_menu,
                R.string.drawer_open, R.string.title_none) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("");
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(m_DrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        m_DrawerLayout.addDrawerListener(m_DrawerToggle);

        m_menuItems = new ArrayList<MenuListItemData>();
      //  m_menuItems.add(new MenuListItemData(getString(R.string.automatic_2fa_title), R.drawable.lock_icon_large));
        m_menuItems.add(new MenuListItemData(getString(R.string.feedback), R.drawable.ic_action_email));
        m_menuItems.add(new MenuListItemData(getString(R.string.get_started_title), R.drawable.ic_action_secure));
        m_menuItems.add(new MenuListItemData(getString(R.string.patents_title), R.drawable.ic_action_secure));
        m_menuItems.add(new MenuListItemData(getString(R.string.about_title), R.drawable.ic_action_about));
        m_menuItems.add(new MenuListItemData(getString(R.string.title_privacy_policy), R.drawable.ic_action_about));
        m_menuItems.add(new MenuListItemData(getString(R.string.title_terms_of_use), R.drawable.ic_action_about));
        m_menuItems.add(new MenuListItemData(getString(R.string.title_legal_notice), R.drawable.ic_action_about));
        m_menuItems.add(new MenuListItemData(getString(R.string.title_send_log), R.drawable.ic_action_log));

        MenuListAdapter menuListAdapter = new MenuListAdapter(m_menuItems, this);

        // Set the adapter for the list view
        m_DrawerList.setAdapter(menuListAdapter);
        // Set the list's click listener
        m_DrawerList.setOnItemClickListener(this);

        //Set drawer listener for the drawer layout
        m_DrawerLayout.addDrawerListener(m_DrawerToggle);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().show();

        //Apply data
        m_accountsList = (ListView) findViewById(R.id.accounts_list);
        m_accountsList.setVisibility(View.VISIBLE);


        SprivApp.updateActionbarText(this);

        boolean isOnWifi = Connectivity.isConnectedWifi(this);
        boolean isOnMobile = Connectivity.isConnectedMobile(this);
        boolean isOnConnection = Connectivity.isConnected(this);

        if (isOnConnection) {
            Log.d("CONNECTIVITY", "Is Connected!");

            if (isOnWifi)
                Log.d("CONNECTIVITY", "Is on Wifi!");
            else
                Log.d("CONNECTIVITY", "Is not on wifi!");

            if (isOnMobile)
                Log.d("CONNECTIVITY", "Is on Mobile!");
            else
                Log.d("CONNECTIVITY", "Is not on Mobile!");
        } else {
            Log.d("CONNECTIVITY", "Is not Connected!");
        }

        checkGpsStatus();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        if (GpsStatus){


        }else {

            showAlert();
        }

//      Toast.makeText(this, String.valueOf(GpsService.time_stamp), Toast.LENGTH_LONG).show();
//        final Handler mHandler = new Handler();
//        Timer mTimer = new Timer();
//        TimerTask doAsynchronousTask = new TimerTask() {
//
//            @Override
//            public void run() {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        Toast.makeText(getApplicationContext(), String.valueOf(GpsService.w_fLatitude), Toast.LENGTH_LONG).show();
//
//                    }
//                });
//            }
//        };
//
//
//        mTimer.schedule(doAsynchronousTask, 0, 300);
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

    @Override
    public void onBackPressed() {
        boolean drawerOpen = m_DrawerLayout.isDrawerOpen(m_DrawerList);
        if (drawerOpen) {
            m_DrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        if (position == 0) {
//            Log.d("NAVIGATION", "Tapped Automatic 2FA!");
////            Intent automatic2FAIntent = new Intent(this, Automatic2FAActivity.class);
////            startActivity(automatic2FAIntent);
//            m_DrawerLayout.closeDrawers();
//        } else
        if (position == 0) {

        String timeString = (String) DateFormat.format("EEE MMM dd HH:mm", new Date());
        String timeLine = "Approximate Time Sent: " + timeString;
        String versionLine = "Application Version: " + BuildConfig.VERSION_NAME;
        String phoneModelLine = "Phone Model: " + PhoneInfoUtil.getPhoneModel();
        String osLine = "Android Version: " + android.os.Build.VERSION.RELEASE;
        String memoryLine = "Available Memory: " + PhoneInfoUtil.getAvailMemory();
        String userLine = "";
        if(m_accounts != null && m_accounts.size() > 0) {
            for (int i = 0 ; i < m_accounts.size() ; i ++) {
                userLine = String.format("%s%s", userLine, "User: " + m_accounts.get(i).getId().getUserName() + "\n");
            }
        }

        String supportEmailContent = getString(R.string.support_email_content);
        supportEmailContent += "\n\n";
        supportEmailContent += timeLine;
        supportEmailContent += "\n\n";
        supportEmailContent += versionLine;
        supportEmailContent += "\n\n";
        supportEmailContent += phoneModelLine;
        supportEmailContent += "\n\n";
        supportEmailContent += osLine;
        supportEmailContent += "\n\n";
        supportEmailContent += memoryLine;
        supportEmailContent += "\n\n";
        supportEmailContent += userLine;
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{SUPPORT_EMAIL_ADDRESS});
        emailIntent.putExtra(Intent.EXTRA_CC, SUPPORT_EMAIL_ADDRESS);
        emailIntent.putExtra(Intent.EXTRA_BCC, SUPPORT_EMAIL_ADDRESS);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, supportEmailContent);
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
        } else if (position == 1) {
            Intent aboutIntent = new Intent(this, WelcomeActivity.class);
            startActivity(aboutIntent);
        }
        if (position == 2) {
            Intent patentIntent = new Intent(this, PatentsActivity.class);
            startActivity(patentIntent);
        } else if (position == 3) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        } else if (position == 4) {
            Intent visitWebIntent = new Intent(this, VisitWebActivity.class);
            visitWebIntent.putExtra("title", "Privacy");
            visitWebIntent.putExtra("url", VisitWebActivity.PRIVACY_POLICY);
            startActivity(visitWebIntent);
        } else if (position == 5) {
            Intent visitWebIntent = new Intent(this, VisitWebActivity.class);
            visitWebIntent.putExtra("title", "Terms of use");
            visitWebIntent.putExtra("url", VisitWebActivity.TERMS_OF_USE);
            startActivity(visitWebIntent);
        } else if (position == 6) {
            Intent visitWebIntent = new Intent(this, VisitWebActivity.class);
            visitWebIntent.putExtra("title", "Legal Notice");
            visitWebIntent.putExtra("url", VisitWebActivity.LEGAL_NOTICE);
            startActivity(visitWebIntent);
        }else if (position == 7){


            //do here
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        writeLogToFile(AccountsActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();




            m_DrawerLayout.closeDrawers();
        }

    }

    public void checkGpsStatus(){

        myLocationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
//    private void marshmallowGPSPremissionCheck() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && checkSelfPermission(
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && checkSelfPermission(
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSION_LOCATION);
//        } else {
//            //   gps functions.
//        }
//    }

    public static String getDateTime(){

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateToStr = format.format(today);

        return dateToStr;

    }
    public String writeLogToFile(Context context) throws IOException {
        String fileName = "logcat.txt";
        final File file= new File(context.getExternalCacheDir(),fileName);

        if (file.exists()){
            Log.d("Check_make_log", file.length() / 1024 + " KB");
            if (file.length()/1024 <= 480){
                String command = "logcat -f " + file.getAbsolutePath();
                Process p = Runtime.getRuntime().exec(command);
                try {
                    Thread.sleep(4000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p.destroy();
            }
        } else {
            file.createNewFile();
            String command = "logcat -f "+file.getAbsolutePath();
            Process p = Runtime.getRuntime().exec(command);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            p.destroy();
        }
        final String[] uploadUrl = {""};
        Log.d("After_make_log", file.length() / 1024 + " KB");

        if (file!=null){

            try  {
                //Your code goes here
                uploadUrl[0] = ServerAPI.getFileUploadUrl(file);
                Log.d("FileUrl=", uploadUrl[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.runOnUiThread(new Runnable() {
                               public void run() {
                                   Toast.makeText(AccountsActivity.this, "Debug log " + file.length() / 1024 + " KB", Toast.LENGTH_SHORT).show();
                               }
                           }
        );

        logFile = file;
        if(file.length() / 1024 > 490) {
            file.delete();
        }
        return uploadUrl[0];
    }



    public void sendLogfileViaEmail(Context context, File file){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", SUPPORT_EMAIL_ADDRESS, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        Uri uri = Uri.fromFile(file);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send via"));
    }

//    private static boolean hasPermissions(Context context, String... permissions) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
//            for (String permission : permissions) {
//                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here


                } else {
                    Toast.makeText(this, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
                }
            }
            case MY_PERMISSION_LOCATION: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {

                    showAlert();
                }

            }
        }
    }

    @Override
    protected void onStart() {
        m_accounts = AccountsModel.getInstance().getAccounts();
        m_accountListAdapter = new AccountListAdapter(m_accounts, this, this);
        m_accountsList.setAdapter(m_accountListAdapter);

        // TODO Auto-generated method stub
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.accounts, menu);
        return super.onCreateOptionsMenu(menu);

    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = m_DrawerLayout.isDrawerOpen(m_DrawerList);
        boolean hasAccounts = m_accounts != null && m_accounts.size() > 0;
        boolean editMode = m_accountListAdapter.isEditMode();
        //menu.findItem(R.id.action_add_account).setVisible(!drawerOpen && hasAccounts && !editMode);
        menu.findItem(R.id.action_edit_accounts).setVisible(!drawerOpen && hasAccounts && !editMode);
        menu.findItem(R.id.action_done).setVisible(editMode);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        m_DrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        m_DrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (m_DrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        /*if (id == R.id.action_add_account) 
        {
        	addAccount();
            return true;
        }*/
        if (id == R.id.action_edit_accounts) {
            editAccounts();
            return true;
        } else if (id == R.id.action_done) {
            doneEditAccounts();
            return true;
        } else if (id == R.id.action_qr_scan) {
            Intent qrScannerIntent = new Intent(this, QRScannerActivity.class);
            startActivityForResult(qrScannerIntent, REQUEST_CODE_QR_SCAN);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editAccounts() {
        m_accountListAdapter.setEditMode(true);
        supportInvalidateOptionsMenu();

    }

    private void doneEditAccounts() {
        m_accountListAdapter.setEditMode(false);
        supportInvalidateOptionsMenu();

    }

    private void addAccount(String pairId) {

        Intent intent = new Intent(this, AddAccountActivity.class);
        intent.putExtra(AddAccountActivity.PAIRING_CODE_PARAM, pairId);
        startActivity(intent);

    }

    @Override
    public void onRemoveAccountClick(final SprivAccount sprivAccount) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Add the buttons
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    m_accountListAdapter.removeAccount(sprivAccount);
                    try {
                        AccountsModel.getInstance().removeAccount(sprivAccount);
                    } catch (Exception e) {
                        Toast.makeText(AccountsActivity.this, "Remove account failed. Please try again", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        return;
                    }
                    m_accountListAdapter.notifyDataSetChanged();
                    /*if (!AccountsModel.getInstance().hasAccounts()) {
                        ActivityNavigator.navigateToAddAccount(AccountsActivity.this);//aaaaaaaaaaaaaa
                        finish();
                    }*/
                    doneEditAccounts();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            builder.setTitle(R.string.remove_account);
            builder.setMessage(R.string.are_you_sure);
            AlertDialog dialog = builder.create();
            dialog.show();


        } catch (Exception e) {
            Toast.makeText(this, "Remove account failed. Please try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTOTPClick(SprivAccount sprivAccount) {
        Intent totpIntent = new Intent(this, TOTPActivity.class);
        totpIntent.putExtra(TOTPActivity.ACCOUNT_ID_KEY, sprivAccount.getId());
        startActivity(totpIntent);

    }

    @Override
    public void onAccountClick(SprivAccount sprivAccount) {
        Intent loginsIntent = new Intent(this, LoginsActivity.class);
        loginsIntent.putExtra(TOTPActivity.ACCOUNT_ID_KEY, sprivAccount.getId());
        startActivity(loginsIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_QR_SCAN) {
            if (data != null && data.hasExtra(AddAccountActivity.PAIRING_CODE_PARAM)) {
                addAccount(data.getStringExtra(AddAccountActivity.PAIRING_CODE_PARAM));
            }
        }
    }

    private void showAlert(){

        new MaterialDialog.Builder(this)
                .title("Warning")
                .content("Will you turn on the location service?")
                .positiveText("Yes")
                .positiveColorRes(R.color.actionbar_background)
                .negativeText("No")
                .negativeColorRes(R.color.red)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                    }
                })
                .show();
    }

    LocationListener m_myLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }


        @Override
        public void onLocationChanged(Location location) {

        }
    };
}
