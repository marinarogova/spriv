package com.spriv.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.spriv.R;
import com.spriv.R.id;
import com.spriv.R.layout;
import com.spriv.SprivApp;
import com.spriv.model.AppSettingsModel;
import com.spriv.utils.FontsManager;

public class Automatic2FAActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.automatic_2fa);
		View getStartedLayout = findViewById(id.get_started_layout);
		getStartedLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				ActivityNavigator.navigateToMain(Automatic2FAActivity.this);
				finish();
			}
		});
		Log.d("NAVIGATION", "Created Automatic 2FA!");

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
//		((TextView)findViewById(id.user_next1_content_text)).setTypeface(normalFont);
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

