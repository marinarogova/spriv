package com.spriv.activity;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.model.AppSettingsModel;
import com.spriv.utils.FontsManager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TipsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tips);
		View getStartedLayout = findViewById(R.id.get_started_layout);
		getStartedLayout.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				Intent accountsIntent = new Intent(TipsActivity.this, NextStepsActivity.class);
				TipsActivity.this.startActivity(accountsIntent);
			}
		});
		
		
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
		getMenuInflater().inflate(R.menu.tips, menu);
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
//		((TextView)findViewById(R.id.tip1_content_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.tip2_content_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.tip3_content_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.tip4_content_text)).setTypeface(normalFont);
		((TextView)findViewById(R.id.fyi_title_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.fyi1_content_text)).setTypeface(normalFont);
//		((TextView)findViewById(R.id.fyi2_content_text)).setTypeface(normalFont);
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

