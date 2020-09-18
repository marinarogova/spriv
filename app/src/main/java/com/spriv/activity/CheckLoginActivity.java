package com.spriv.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.data.CheckLoginResultInfo;
import com.spriv.json.Tags;
import com.spriv.receiver.CheckLoginNotificationHandler;
import com.spriv.task.AddReportDesicionTask;
import com.spriv.task.AddReportDesicionTaskHandler;
import com.spriv.utils.Connectivity;
import com.spriv.utils.FontsManager;

import java.util.Date;


public class CheckLoginActivity extends AppCompatActivity implements OnClickListener, AddReportDesicionTaskHandler {

    Context context;
    Button allowButton, denyButton;
    private CheckLoginResultInfo m_checkLoginResultInfo;
    private String m_transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_check_login_new);

        Bundle data = getIntent().getExtras();
        m_transactionId = data.getString(Tags.Id);
        m_checkLoginResultInfo = (CheckLoginResultInfo) data.getParcelable(Tags.NotificationContent);
        if (m_checkLoginResultInfo != null) {
            TextView companyText = (TextView) findViewById(R.id.company_text);
            TextView serviceText = (TextView) findViewById(R.id.service_text);
            TextView endUserNameText = (TextView) findViewById(R.id.end_user_name_text);
            TextView dateText = (TextView) findViewById(R.id.date_text);
            TextView ipText = (TextView) findViewById(R.id.ip_content_text);
            TextView osText = (TextView) findViewById(R.id.os_content_text);
            TextView browserText = (TextView) findViewById(R.id.browser_content_text);
            allowButton = (Button) findViewById(R.id.allow_button);
            denyButton = (Button) findViewById(R.id.deny_button);

            //Set data
            String companyName = m_checkLoginResultInfo.getCompany();
            if(companyName!=null && !companyName.isEmpty())
            companyName = companyName.replace(".com","");
            companyText.setText(companyName);
            String name = m_checkLoginResultInfo.getEndUsername();
            name = name.substring(0, name.indexOf("@"));
            endUserNameText.setText(name);
            dateText.setText(DateFormat.format("EEE MMM dd HH:mm", new Date(m_checkLoginResultInfo.getDate())));
            ipText.setText(m_checkLoginResultInfo.getIPAddress());
            osText.setText(m_checkLoginResultInfo.getOS());
            browserText.setText(m_checkLoginResultInfo.getBrowser());
            serviceText.setText(m_checkLoginResultInfo.getService());
            //Register on buttons click
            allowButton.setOnClickListener(this);
            denyButton.setOnClickListener(this);
            NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(CheckLoginNotificationHandler.NOTIFICATION_ID);
        }
        applyFonts();
        // SprivApp.updateActionbarText(this);

        boolean isOnWifi = Connectivity.isConnectedWifi(this);
        boolean isOnMobile = Connectivity.isConnectedMobile(this);
        boolean isOnConnection = Connectivity.isConnected(this);

        TextView tv1 = (TextView) findViewById(R.id.networkTextView);

        if (isOnConnection) {
            Log.d("CONNECTIVITY", "Is Connected!");

            if (isOnWifi) {
                Log.d("CONNECTIVITY", "Is on Wifi!");
                tv1.setText(R.string.network_message_wifi);
                //   tv1.setTextColor(Color.WHITE);
                //     tv1.setBackgroundColor(Color.rgb(147, 223, 117));        //Spriv Green RGB Values 147, 223, 117
            } else {

                Log.d("CONNECTIVITY", "Is not on wifi!");

            }
            if (isOnMobile) {
                Log.d("CONNECTIVITY", "Is on Mobile!");
                tv1.setText(R.string.network_message_cellular);
                // tv1.setTextColor(Color.WHITE);
                //  tv1.setBackgroundColor(Color.rgb(223, 144, 113));        //Spriv Red RGB Values 223, 144, 113

            } else {
                Log.d("CONNECTIVITY", "Is not on Mobile!");

            }

        } else {
            Log.d("CONNECTIVITY", "Is not Connected!");
            tv1.setText(R.string.network_message_blank);
        }

        setIcons();
    }

    private void setIcons() {

        int size = (int) getResources().getDimension(R.dimen.s_icon_size);
        Drawable ic_tick = ContextCompat.getDrawable(context, R.drawable.ticj);
        Drawable ic_cross = ContextCompat.getDrawable(context, R.drawable.cross);
        ic_tick.setBounds(0, 0, size, size);
        ic_cross.setBounds(0, 0, size, size);
        allowButton.setCompoundDrawables(ic_tick, null, null, null);
        denyButton.setCompoundDrawables(ic_cross, null, null, null);

        allowButton.getCompoundDrawables()[0].mutate().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);
        denyButton.getCompoundDrawables()[0].mutate().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);

        TextView ip_title_text = (TextView) findViewById(R.id.ip_title_text);
        TextView os_title_text = (TextView) findViewById(R.id.os_title_text);
        TextView browser_title_text = (TextView) findViewById(R.id.browser_title_text);
        TextView service_title_text = (TextView) findViewById(R.id.service_title_text);

        Drawable ic_server = ContextCompat.getDrawable(context, R.drawable.server);
        Drawable ic_mac = ContextCompat.getDrawable(context, R.drawable.mac);
        Drawable ic_speedo = ContextCompat.getDrawable(context, R.drawable.speedo);
        Drawable ic_startup = ContextCompat.getDrawable(context, R.drawable.startup);

        size *= 1.2;

        ic_server.setBounds(0, 0, size, size);
        ic_mac.setBounds(0, 0, size, size);
        ic_speedo.setBounds(0, 0, size, size);
        ic_startup.setBounds(0, 0, size, size);

        ip_title_text.setCompoundDrawables(ic_server, null, null, null);
        os_title_text.setCompoundDrawables(ic_mac, null, null, null);
        browser_title_text.setCompoundDrawables(ic_speedo, null, null, null);
        service_title_text.setCompoundDrawables(ic_startup, null, null, null);


        ip_title_text.getCompoundDrawables()[0].mutate().setColorFilter(ContextCompat.getColor(context, R.color.link_foreground), PorterDuff.Mode.SRC_ATOP);
        os_title_text.getCompoundDrawables()[0].mutate().setColorFilter(ContextCompat.getColor(context, R.color.link_foreground), PorterDuff.Mode.SRC_ATOP);
        browser_title_text.getCompoundDrawables()[0].mutate().setColorFilter(ContextCompat.getColor(context, R.color.link_foreground), PorterDuff.Mode.SRC_ATOP);
        service_title_text.getCompoundDrawables()[0].mutate().setColorFilter(ContextCompat.getColor(context, R.color.link_foreground), PorterDuff.Mode.SRC_ATOP);


    }

    private void applyFonts() {
        Typeface normalFont = FontsManager.getInstance().getNormalFont();
        Typeface boldFont = FontsManager.getInstance().getBoldFont();
        ((TextView) findViewById(R.id.company_text)).setTypeface(boldFont);
        ((TextView) findViewById(R.id.service_text)).setTypeface(boldFont);
        ((TextView) findViewById(R.id.end_user_name_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.date_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.ip_content_text)).setTypeface(boldFont);
        ((TextView) findViewById(R.id.ip_title_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.os_content_text)).setTypeface(boldFont);
        ((TextView) findViewById(R.id.os_title_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.browser_content_text)).setTypeface(boldFont);
        ((TextView) findViewById(R.id.browser_title_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.allow_button)).setTypeface(boldFont);
        ((TextView) findViewById(R.id.deny_button)).setTypeface(boldFont);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_login, menu);
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

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.allow_button) {
            alaysAllow();
            ActivityNavigator.navigateToMain(this);
            finish();
        } else if (v.getId() == R.id.deny_button) {
            deny();
            ActivityNavigator.navigateToMain(this);
            finish();
        }

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

    private void alaysAllow() {

        AddReportDesicionTask addReportDesicionTask = new AddReportDesicionTask(this, m_transactionId, AddReportDesicionTask.ALWAYS_ALLOW_INDEX, this, m_checkLoginResultInfo);
        addReportDesicionTask.execute();
    }

    private void allow() {
        AddReportDesicionTask addReportDesicionTask = new AddReportDesicionTask(this, m_transactionId, AddReportDesicionTask.ALLOW_INDEX, this, m_checkLoginResultInfo);
        addReportDesicionTask.execute();
    }

    private void deny() {
        AddReportDesicionTask addReportDesicionTask = new AddReportDesicionTask(this, m_transactionId, AddReportDesicionTask.DENY_INDEX, this, m_checkLoginResultInfo);
        addReportDesicionTask.execute();
    }

    @Override
    public void onBackPressed() {
        deny();
        ActivityNavigator.navigateToMain(this);
        finish();
    }

    ;

    @Override
    public void onAddReportDesicionPerformed(boolean success) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAddReportDesicionException(Exception exception) {
        // TODO Auto-generated method stub

    }
}
