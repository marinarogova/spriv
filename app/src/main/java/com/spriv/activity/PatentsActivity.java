package com.spriv.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.utils.FontsManager;

public class PatentsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patents);
		applyFonts();
		SprivApp.updateActionbarText(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patents, menu);
		return true;
	}
	
	private void applyFonts() {
		Typeface normalFont = FontsManager.getInstance().getNormalFont();
		((TextView)findViewById(R.id.content_text)).setTypeface(normalFont);
		
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
