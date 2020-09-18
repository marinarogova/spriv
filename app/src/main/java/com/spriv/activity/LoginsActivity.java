package com.spriv.activity;

import java.util.ArrayList;
import java.util.List;

import com.spriv.R;
import com.spriv.SprivApp;
import com.spriv.data.SprivAccount;
import com.spriv.data.SprivAccountId;
import com.spriv.data.SprivLogin;
import com.spriv.list.LoginsListListener;
import com.spriv.list.LoginstListAdapter;
import com.spriv.list.MenuListItemData;
import com.spriv.model.AccountsModel;
import com.spriv.task.CancelAllowedLoginTask;
import com.spriv.task.CancelAllowedLoginTaskHandler;
import com.spriv.utils.FontsManager;

import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LoginsActivity extends AppCompatActivity implements OnItemClickListener, LoginsListListener, CancelAllowedLoginTaskHandler {

	private ArrayList<MenuListItemData> m_menuItems;
	private ActionBarDrawerToggle m_DrawerToggle;
	private String m_Title;
	private List<SprivLogin> m_logins;
	private ListView m_loginsList;
	private TextView m_loginTitleTextView;
	private TextView m_noLoginTextView;
	//private View m_accountsEmptyLayout;
	private LoginstListAdapter m_loginsListAdapter;
	private SprivAccountId m_accountId;
	private CancelAllowedLoginTask m_cancelAllowedLoginTask;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_logins);
        m_accountId = getIntent().getExtras().getParcelable(TOTPActivity.ACCOUNT_ID_KEY);
        
        m_Title = getString(R.string.title_accounts);
        m_loginTitleTextView = (TextView)findViewById(R.id.logins_account_title);
        SprivAccount account = AccountsModel.getInstance().getAccount(m_accountId);
        m_loginTitleTextView.setText(account.toString());
        m_noLoginTextView = (TextView)findViewById(R.id.no_logins_text);
        
        //Apply data
        m_loginsList =  (ListView)findViewById(R.id.logins_list);
        m_loginsList.setVisibility(View.VISIBLE);
        
        applyFonts();
  	  	SprivApp.updateActionbarText(this);   
  	  
    }

    private void applyFonts() {
		Typeface normalFont = FontsManager.getInstance().getNormalFont();
		((TextView)findViewById(R.id.logins_account_title)).setTypeface(normalFont);
		((TextView)findViewById(R.id.no_logins_text)).setTypeface(normalFont);
		
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
		if(m_cancelAllowedLoginTask != null)
		{
			m_cancelAllowedLoginTask.cancel(false);
		}
		super.onStop();
	}
    
    
	
    
	

	@Override
	protected void onStart() {
		m_logins = AccountsModel.getInstance().getAccount(m_accountId).getLogins();
		m_loginsListAdapter = new LoginstListAdapter(m_logins, this, this);
		m_loginsList.setAdapter(m_loginsListAdapter);
		if(m_logins.size() > 0)
		{
	  	    m_loginsList.setVisibility(View.VISIBLE);
			m_noLoginTextView.setVisibility(View.GONE);
		}
		else
		{
			m_loginsList.setVisibility(View.GONE);
			m_noLoginTextView.setVisibility(View.VISIBLE);
		}
		// TODO Auto-generated method stub
		super.onStart();
	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.auth_logins, menu);
        return super.onCreateOptionsMenu(menu);
        
    }
    
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean hasLogins = m_logins != null && m_logins.size() > 0;
        boolean editMode = m_loginsListAdapter.isEditMode();
        menu.findItem(R.id.action_edit_logins).setVisible(hasLogins && !editMode);
        menu.findItem(R.id.action_done).setVisible(editMode);
        
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
        if (id == R.id.action_edit_logins) 
        {
        	editLogins();
            return true;
        }
        else if (id == R.id.action_done) 
        {
        	doneEditLogins();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	private void editLogins() {
		m_loginsListAdapter.setEditMode(true);
		supportInvalidateOptionsMenu();
		
	}
	
	private void doneEditLogins() {
		m_loginsListAdapter.setEditMode(false);
		supportInvalidateOptionsMenu();
		
	}

	@Override
	public void onRemoveLoginClick(final SprivLogin sprivLogin) {
		try 
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Add the buttons
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   
			        	    m_cancelAllowedLoginTask = new CancelAllowedLoginTask(sprivLogin, m_accountId, LoginsActivity.this);
			        	    m_cancelAllowedLoginTask.execute();
			        	    
			           }
			       });
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User cancelled the dialog
			           }
			       });
			builder.setTitle(R.string.remove_login);
			builder.setMessage(R.string.are_you_sure);
			AlertDialog dialog = builder.create();
			dialog.show();
			
			 
			
		} 
		catch (Exception e) {
			Toast.makeText(this, "Remove login failed. Please try again", Toast.LENGTH_LONG).show();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancelAllowedLoginPerformed() {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
		    @Override
		    public void run() {
		    	m_loginsListAdapter.notifyDataSetChanged();
		    }
		});
		
		
	}

	@Override
	public void onCancelAllowedLoginException(final Exception exception) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
		    @Override
		    public void run() {
		    	Toast.makeText(LoginsActivity.this, "Remove allowed login failed. Please try again", Toast.LENGTH_LONG).show();
				exception.printStackTrace();
		    }
		});
		
		
	}
}
