package com.spriv.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.spriv.BuildConfig;
import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.model.AppSettingsModel;
import com.spriv.utils.FontsManager;

public class AboutActivity extends AppCompatActivity {

    private static final int EDIT_ENABLED_CLICK_COUNTER = 7;

    private EditText serverBasePathEditText;
    private ImageView poweredBySprivImageView;
    private TextView versionValueText;
    private int poweredBySprivImageViewClickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        versionValueText = (TextView) findViewById(R.id.version_value_text);
        serverBasePathEditText = (EditText) findViewById(R.id.serverBasePathEditText);
        poweredBySprivImageView = (ImageView) findViewById(R.id.poweredBySprivImageView);
        poweredBySprivImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                poweredBySprivImageViewClickCounter++;
                if (poweredBySprivImageViewClickCounter == EDIT_ENABLED_CLICK_COUNTER) {
                    poweredBySprivImageViewClickCounter = 0;
                    enableServerBasePathEditing(true);
                }
            }
        });
        serverBasePathEditText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                    AppSettingsModel.getInstance().setServerBaseAddress(serverBasePathEditText.getText().toString());
                    Toast.makeText(AboutActivity.this, "Server Base path updated to: " + serverBasePathEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    handled = true;
                    enableServerBasePathEditing(false);
                }
                return handled;
            }
        });

        enableServerBasePathEditing(false);
        applyFonts();
        SprivApp.updateActionbarText(this);


        versionValueText.setText(BuildConfig.VERSION_NAME);
    }

    protected void enableServerBasePathEditing(boolean enabled) {
        if (enabled) {
            serverBasePathEditText.setVisibility(View.VISIBLE);
            serverBasePathEditText.setText(AppSettingsModel.getInstance().getServerBaseAddress());
        } else {
            serverBasePathEditText.setVisibility(View.GONE);
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

    private void applyFonts() {
        Typeface normalFont = FontsManager.getInstance().getNormalFont();
        ((TextView) findViewById(R.id.content_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.version_title_text)).setTypeface(normalFont);
        ((TextView) findViewById(R.id.version_value_text)).setTypeface(normalFont);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about, menu);
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
		/*else if(id == android.R.id.home)
		{
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
		}*/

        return super.onOptionsItemSelected(item);
    }
}
