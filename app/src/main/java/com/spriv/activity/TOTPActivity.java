package com.spriv.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.data.SprivAccount;
import com.spriv.data.SprivAccountId;
import com.spriv.model.AccountsModel;
import com.spriv.totp.TotpManager;
import com.spriv.utils.FontsManager;

public class TOTPActivity extends AppCompatActivity {

	public final static String ACCOUNT_ID_KEY = "AccountId";
	
	private SprivAccount m_account;
	private TextView m_accountTextView;
	private TextView m_codeTextView;
	private TextView m_timeTextView;
	Handler m_timerHandler = new Handler();
	
	int m_validTimeDisplayInSecs = 0;
	int m_validTimeCalculateInSecs = 2;
	
	long m_startDisplayTime = 0;
	long m_startCalculateTime = 0;
	private TotpManager m_totpManager;
	private String m_lastTotp = "";
	private boolean m_started = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_totp);
		SprivAccountId accountId = getIntent().getExtras().getParcelable(ACCOUNT_ID_KEY);
		m_totpManager = TotpManager.getInstance();
		m_totpManager.init();
		m_validTimeDisplayInSecs = (int)m_totpManager.getOtpProvider().getTotpCounter().getTimeStep();
		m_account = AccountsModel.getInstance().getAccount(accountId);
		m_accountTextView = (TextView)findViewById(R.id.totp_account);
		m_codeTextView = (TextView)findViewById(R.id.totp_code);
		m_timeTextView = (TextView)findViewById(R.id.totp_time);
		m_accountTextView.setText(m_account.toString());
		applyFonts();
		m_lastTotp = calulateTOTP();
		applyTotp(m_lastTotp);
		long currentTime = getCurrentTimeMills();
		long currentTimeSecs = currentTime/1000;
		long passSecs = currentTimeSecs % m_validTimeDisplayInSecs;
		m_startDisplayTime = currentTime - passSecs*1000;
		m_startCalculateTime = currentTime  - passSecs*1000;
		applyTimeLeft((int)(m_validTimeDisplayInSecs - passSecs));
		SprivApp.updateActionbarText(this);
	}

	
    Runnable timerRunnable = new Runnable() 
    {
        @Override
        public void run() {
        	//Activity not in foreground
        	if(!m_started)
        	{
        		return;
        	}
        	//display time
        	boolean forceTotpUpdate = false;
        	long currentTimeMills = getCurrentTimeMills();
            long millis =  currentTimeMills - m_startDisplayTime;
            int seconds = (int) (millis / 1000);
            int timeLeftForDisplay = m_validTimeDisplayInSecs - seconds;
            if(timeLeftForDisplay > 0)
            {
            	applyTimeLeft(timeLeftForDisplay);
            }
            else
            {
            	m_startDisplayTime = currentTimeMills;
            	forceTotpUpdate = true;
            }
            //Calculate time
            millis = currentTimeMills - m_startCalculateTime;
            seconds = (int) (millis / 1000);
            int timeLeftForCalculate = m_validTimeCalculateInSecs - seconds;
            if(forceTotpUpdate || timeLeftForCalculate <= 0)
            {
            	String totp = calulateTOTP();
            	if(!totp.equals(m_lastTotp))
            	{
            		m_startDisplayTime = currentTimeMills;
            		m_lastTotp = totp;
            		applyTimeLeft(m_validTimeDisplayInSecs);
            		applyTotp(totp);
            	}   
            	m_startCalculateTime = currentTimeMills;
            }
            m_timerHandler.postDelayed(this, 500);
        }

		
    };
    
    
    
    private void applyTimeLeft(int timeLeftForDisplay) 
	{
		m_timeTextView.setText(String.valueOf(timeLeftForDisplay) + " seconds left");
	}
	
	
	private long getCurrentTimeMills() {
		return m_totpManager.getOtpProvider().getTotpClock().currentTimeMillis();
	}

	private void applyFonts() {
		Typeface normalFont = FontsManager.getInstance().getNormalFont();
		((TextView)findViewById(R.id.totp_account)).setTypeface(normalFont);
		((TextView)findViewById(R.id.totp_code)).setTypeface(normalFont);
		((TextView)findViewById(R.id.totp_time)).setTypeface(normalFont);
	}
	
	private void applyTotp(String totp) 
	{
		m_codeTextView.setText(totp);
	}

	private String calulateTOTP() 
	{
		try 
		{
			return m_totpManager.getOtpProvider().getNextCode(m_account.getId());
			//return m_totpManager.getTotp(m_account.getTOTPSecret());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return "??????";
		}
	}
	
	@Override
	protected void onPause() 
	{
		SprivApp.InForground = false;
		super.onPause();
	}
	
	@Override
	protected void onResume() 
	{
		SprivApp.InForground = true;
		super.onResume();
	}
	
	@Override
	protected void onStop() 
	{
		m_started = false;
		super.onStop();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		m_started = true;
		m_timerHandler.postDelayed(timerRunnable, 0);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.totp, menu);
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
