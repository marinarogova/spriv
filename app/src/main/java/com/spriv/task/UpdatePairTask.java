package com.spriv.task;
import android.os.AsyncTask;

import com.spriv.data.SprivAccount;
import com.spriv.model.AccountsModel;
import com.spriv.model.ServerAPI;

public class UpdatePairTask extends AsyncTask<String, Void, SprivAccount> {

    private Exception m_exception;
    private UpdatePairTaskHandler m_taskHandler;
    private String m_key;
    private String m_phoneNumber;
    private String m_pushToken;
    
    
    public UpdatePairTask(String key, String phoneNumber, String pushToken, UpdatePairTaskHandler taskHandler)
    {
    	m_key = key;
    	m_phoneNumber = phoneNumber;
    	m_pushToken = pushToken;
    	m_taskHandler = taskHandler;
    }

    @Override
    protected SprivAccount doInBackground(String... urls) {
    	try 
        {
    		SprivAccount sprivAccount = ServerAPI.pairAccount(m_key, m_phoneNumber, m_pushToken);
    		AccountsModel.getInstance().addAccount(sprivAccount);
    		return sprivAccount;
        } 
    	catch (Exception ex) 
    	{
    		m_exception = ex;
    		return null;
    	}
    }

    @Override
    protected void onPostExecute(SprivAccount account) 
    {
    	if(!isCancelled())
    	{
	    	if(this.m_exception != null)
	    	{
	    		m_taskHandler.onUpdatePairException(m_exception, m_key);
	    	}
	    	else
	    	{
	    		m_taskHandler.onUpdatePairPerformed(account);
	    	}
    	}
    }
 }