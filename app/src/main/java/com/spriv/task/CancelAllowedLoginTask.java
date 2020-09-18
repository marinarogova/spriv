package com.spriv.task;
import android.os.AsyncTask;

import com.spriv.data.SprivAccount;
import com.spriv.data.SprivAccountId;
import com.spriv.data.SprivLogin;
import com.spriv.model.AccountsModel;
import com.spriv.model.ServerAPI;

public class CancelAllowedLoginTask extends AsyncTask<String, Void, Boolean> {

    private Exception m_exception;
    private CancelAllowedLoginTaskHandler m_taskHandler;
    private SprivLogin m_login;
    private SprivAccountId m_accountId;
    
    public CancelAllowedLoginTask(SprivLogin login, SprivAccountId accountId, CancelAllowedLoginTaskHandler taskHandler)
    {
    	m_login = login;
    	m_accountId = accountId;
    	m_taskHandler = taskHandler;
    }

    @Override
    protected Boolean doInBackground(String... urls) {
    	try 
        {
    		boolean success = ServerAPI.cancelAllwaysAllow(m_login.getTransactionId());
    		if(success)
    		{
    			AccountsModel.getInstance().removeLogin(m_login, m_accountId);
    		}
    		return success;
        } 
    	catch (Exception ex) 
    	{
    		m_exception = ex;
    		return null;
    	}
    }

    @Override
    protected void onPostExecute(Boolean success) 
    {
    	if(!isCancelled())
    	{
	    	if(this.m_exception != null)
	    	{
	    		m_taskHandler.onCancelAllowedLoginException(m_exception);
	    	}
	    	else
	    	{
	    		m_taskHandler.onCancelAllowedLoginPerformed();
	    	}
    	}
    }
 }