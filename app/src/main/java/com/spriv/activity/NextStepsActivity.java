package com.spriv.activity;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.model.AppSettingsModel;
import com.spriv.utils.FontsManager;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class NextStepsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next_steps);
		View getStartedLayout = findViewById(R.id.get_started_layout);
		getStartedLayout.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				ActivityNavigator.navigateToMain(NextStepsActivity.this);
				finish();
			}
		});
		
		TextView adminStep1Text = ((TextView)findViewById(R.id.admin_next1_text));
		adminStep1Text.setText(Html.fromHtml(
				"A. From your PC: Create a new company account at " +
						"<a href=\"" +
						"http://www.spriv.com\">www.spriv.com</a>" +
						" and click on \"Signup\". Spriv\'s management portal is compatible with the following browsers: Chrome, Explorer, Opera and Firefox."));
		
		adminStep1Text.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView adminStep3Text = ((TextView)findViewById(R.id.admin_next3_text));
		adminStep3Text.setText(Html.fromHtml(
				"C. Once pairing device is complete, from your PC: login to Spriv’s management portal to configure your 2FA platform and invite users to start using Spriv’s 2FA.   </br>"));
		adminStep3Text.setMovementMethod(LinkMovementMethod.getInstance());
		getSupportActionBar().hide();
		applyFonts();
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
		getMenuInflater().inflate(R.menu.next_steps, menu);
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
//		((TextView)findViewById(R.id.admin_title_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.admin_next1_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.admin_next2_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.admin_next3_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.admin_next4_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.user_next_title_text)).setTypeface(normalFont);
		((TextView)findViewById(R.id.user_next1_content_text)).setTypeface(normalFont);
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

