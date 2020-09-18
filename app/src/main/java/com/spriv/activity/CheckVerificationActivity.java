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
import com.spriv.data.CheckVerificationResultInfo;
import com.spriv.json.Tags;
import com.spriv.receiver.CheckVerificationNotificationHandler;
import com.spriv.task.AddReportDesicionTask;
import com.spriv.task.AddReportDesicionTaskHandler;
import com.spriv.utils.FontsManager;

import java.util.Date;

public class CheckVerificationActivity extends AppCompatActivity implements OnClickListener, AddReportDesicionTaskHandler {

    Context context;
    Button allowButton, denyButton;
    private CheckVerificationResultInfo m_checkVerificationResultInfo;
    private String m_transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_check_verification_new);

        Bundle data = getIntent().getExtras();
        m_transactionId = data.getString(Tags.Id);
        m_checkVerificationResultInfo = (CheckVerificationResultInfo) data.getParcelable(Tags.NotificationContent);
        allowButton = (Button) findViewById(R.id.allow_button);
        denyButton = (Button) findViewById(R.id.deny_button);
        if (m_checkVerificationResultInfo != null) {
            TextView companyText = (TextView) findViewById(R.id.company_text);
            TextView endUserNameText = (TextView) findViewById(R.id.end_user_name_text);
            TextView dateText = (TextView) findViewById(R.id.date_text);
            TextView questionText = (TextView) findViewById(R.id.question_text);

            //Set data
            companyText.setText(m_checkVerificationResultInfo.getCompany());
            String name = m_checkVerificationResultInfo.getEndUsername();
            name = name.substring(0,name.indexOf("@"));
            endUserNameText.setText(name);
            dateText.setText(DateFormat.format("EEE MMM dd HH:mm", new Date(m_checkVerificationResultInfo.getDate())));
            Log.d("usm_verification","question= "+m_checkVerificationResultInfo.getQuestion());
            questionText.setText(m_checkVerificationResultInfo.getQuestion());
            //Register on buttons click
            allowButton.setOnClickListener(this);
            denyButton.setOnClickListener(this);
            //Cancel notification.
            NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancel(CheckVerificationNotificationHandler.NOTIFICATION_ID);
        }
        setButtonIcons();
        applyFonts();
      //  SprivApp.updateActionbarText(this);
    }

    private void setButtonIcons() {
        int size = (int) getResources().getDimension(R.dimen.s_icon_size);
        Drawable ic_tick = ContextCompat.getDrawable(context, R.drawable.ticj);
        Drawable ic_cross = ContextCompat.getDrawable(context, R.drawable.cross);
        ic_tick.setBounds(0, 0, size, size);
        ic_cross.setBounds(0, 0, size, size);
        allowButton.setCompoundDrawables(ic_tick, null, null, null);
        denyButton.setCompoundDrawables(ic_cross, null, null, null);

        allowButton.getCompoundDrawables()[0].mutate().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);
        denyButton.getCompoundDrawables()[0].mutate().setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);

    }

    private void applyFonts() {
        Typeface normalFont = FontsManager.getInstance().getNormalFont();
        Typeface boldFont = FontsManager.getInstance().getBoldFont();
        ((TextView) findViewById(R.id.company_text)).setTypeface(boldFont);
        ((TextView) findViewById(R.id.end_user_name_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.date_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.question_text)).setTypeface(normalFont);
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
            allow();
            finish();
        } else if (v.getId() == R.id.deny_button) {
            deny();
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

    private void allow() {
        AddReportDesicionTask addReportDesicionTask = new AddReportDesicionTask(this, m_transactionId, 1, this, null);
        addReportDesicionTask.execute();
        ActivityNavigator.navigateToMain(this);
    }

    private void deny() {
        AddReportDesicionTask addReportDesicionTask = new AddReportDesicionTask(this, m_transactionId, 3, this, null);
        addReportDesicionTask.execute();
        ActivityNavigator.navigateToMain(this);
    }

    @Override
    public void onBackPressed() {
        deny();
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
