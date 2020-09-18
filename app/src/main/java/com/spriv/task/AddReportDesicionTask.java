package com.spriv.task;

import com.spriv.data.AuthInfo;
import com.spriv.data.CheckLoginResultInfo;
import com.spriv.data.LocationInfo;
import com.spriv.data.SprivAccountId;
import com.spriv.data.SprivLogin;
import com.spriv.model.AccountsModel;
import com.spriv.model.AuthInfoCollector;
import com.spriv.model.ServerAPI;
import com.spriv.utils.GPSTracker;
import com.spriv.utils.GeocoderUtil;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

public class AddReportDesicionTask  extends AsyncTask<String, Void, Boolean> {
	
	public static int ALWAYS_ALLOW_INDEX = 1;
	public static int ALLOW_INDEX = 2;
	public static int DENY_INDEX = 3;
	
    private Exception m_exception;
    private AddReportDesicionTaskHandler m_taskHandler;
    private Context m_context;
    private String m_transactionId;
    private int m_decision;
    private CheckLoginResultInfo m_checkLoginResultInfo;
        
    public AddReportDesicionTask(Context context, String transactionId, int decision, AddReportDesicionTaskHandler taskHandler, CheckLoginResultInfo checkLoginResultInfo)
    {
    	m_context = context;
    	m_transactionId = transactionId;
    	m_decision = decision;
    	m_taskHandler = taskHandler;
    	m_checkLoginResultInfo = checkLoginResultInfo;
    }
    

    @Override
    protected Boolean doInBackground(String... urls) {
    	try 
        {
    		/*boolean result = ServerAPI.addReportDecision(m_transactionId, m_decision);
    		if(result)
    		{
    			//Login with always allow decision.
    			if(m_checkLoginResultInfo != null && m_decision == ALWAYS_ALLOW_INDEX)
    			{
    				GPSTracker gPSTracker = new GPSTracker(m_context);
    				Location location = gPSTracker.getLocation();
    				if(gPSTracker.canGetLocation());
    				{
    					SprivLogin sprivLogin;
    					if(location != null)
    					{
    						String address = GeocoderUtil.getAddress(m_context, location.getLatitude(), location.getLongitude());
    						sprivLogin = new SprivLogin(m_checkLoginResultInfo, location.getLatitude(), location.getLongitude(), address, m_transactionId);
    					}
    					//No location - set an empty location and address.
    					else
    					{
    						sprivLogin = new SprivLogin(m_checkLoginResultInfo, 0, 0, "", m_transactionId);
    					}
    					SprivAccountId accountId = new SprivAccountId(m_checkLoginResultInfo.getCompany(), m_checkLoginResultInfo.getEndUsername());
						AccountsModel.getInstance().addLogin(sprivLogin, accountId);
    				}
    				
    				
    			}
    		}*/
    		AuthInfoCollector authInfoCollector = new AuthInfoCollector(m_context);
    		authInfoCollector.collect();
    		AuthInfo authInfo = authInfoCollector.getAuthInfo();
    		boolean result = ServerAPI.addReportDecision(authInfo, m_transactionId, m_decision);
    		if(result)
    		{
    			//Login with always allow decision.
    			if(m_checkLoginResultInfo != null && m_decision == ALWAYS_ALLOW_INDEX)
    			{
    				SprivLogin sprivLogin = new SprivLogin(m_checkLoginResultInfo, authInfo.GetConnectedWifiName(), m_transactionId);
    				SprivAccountId accountId = new SprivAccountId(m_checkLoginResultInfo.getCompany(), m_checkLoginResultInfo.getEndUsername());
					AccountsModel.getInstance().addLogin(sprivLogin, accountId);
    			}
    		}
    		return result;
        } 
    	catch (Exception ex) 
    	{
    		m_exception = ex;
    		return false;
    	}
    }

    @Override
    protected void onPostExecute(Boolean success) 
    {
    	if(!isCancelled())
    	{
	    	if(this.m_exception != null)
	    	{
	    		m_taskHandler.onAddReportDesicionException(m_exception);
	    		
	    	}
	    	else
	    	{
	    		m_taskHandler.onAddReportDesicionPerformed(success);
	    	}
    	}
    }
 }