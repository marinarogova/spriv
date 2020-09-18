package com.spriv.activity;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.model.AppSettingsModel;
import com.spriv.utils.FontsManager;
import com.spriv.utils.MySharedPreferences;


public class WelcomeActivity extends AppCompatActivity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		MySharedPreferences mySharedPreferences = new MySharedPreferences(this);
		Log.d("usm_device_token","token = "+mySharedPreferences.getFCMToken());
		View getStartedLayout = findViewById(R.id.get_started_layout);
		AppSettingsModel.getInstance().setShowWelcome(false);
		//Toast.makeText(this, "WELCOME + " + AppSettingsModel.getInstance().isShowWelcome(), Toast.LENGTH_LONG).show();
		getStartedLayout.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				Intent accountsIntent = new Intent(WelcomeActivity.this, TipsActivity.class);
				WelcomeActivity.this.startActivity(accountsIntent);
				AppSettingsModel.getInstance().setShowWelcome(false);
				finish();
			}
		});
		getSupportActionBar().hide();
		applyFonts();
		
		TextView contentText = ((TextView)findViewById(R.id.welcome_marketing_content_text));
		/*contentText.setText(Html.fromHtml(
				"Using Spriv\'s patented technology, users simply login as they normally do and Spriv authenticates the information in the background multiple times and in multiple ways without any additional action by the user. To test and create a new free ten-user account go to " +
	            "<a href=\"http://www.spriv.com\">www.spriv.com</a>" +
				" and click on "  +  
				"<a href=\"https://m.spriv.com/two-factor-authentication/automatic-2fa/free-signup\">signup</a>"));*/
		contentText.setText(R.string.welcome_marketing_content);
		contentText.setMovementMethod(LinkMovementMethod.getInstance());
		
		SprivApp.updateActionbarText(this);
	}
		
	@Override
	public void onBackPressed() 
	{
		ActivityNavigator.navigateToMain(this);
		AppSettingsModel.getInstance().setShowWelcome(false);
		finish();
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
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
	
	private void applyFonts() {
		Typeface normalFont = FontsManager.getInstance().getNormalFont();
		//((TextView)findViewById(R.id.welcome_title_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.welcome_marketing_title_text)).setTypeface(normalFont);
		((TextView)findViewById(R.id.welcome_marketing_content_text)).setTypeface(normalFont);
		((TextView)findViewById(R.id.get_started_text)).setTypeface(normalFont);
		
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
