package com.spriv.totp;

import org.apache.http.impl.client.DefaultHttpClient;

import com.spriv.SprivApp;
import com.spriv.model.AccountsModel;

public class TotpManager {

	private static OtpProvider m_otpProvider;
	private static TotpManager s_instance = null;
	private static final Object s_singletoneLock = new Object();
	private TotpClock m_totpClock;
	private boolean _initialized = false;
	private SyncNowController m_syncNowController;
	
	private TotpManager()
	{
		 
	}
	
	public void init()
	{
		if(!_initialized)
		{
			m_totpClock = new TotpClock(SprivApp.getAppContext());
			m_syncNowController = new SyncNowController(m_totpClock, new NetworkTimeProvider(new DefaultHttpClient()));
			m_syncNowController.syncTime();
			_initialized = true;
		}
	}
	
	public static TotpManager getInstance()
	{
		if(s_instance == null)
		{
			synchronized (s_singletoneLock) 
			{
				if(s_instance == null)
				{
					s_instance = new TotpManager();
				}
			}
		}
		return s_instance;
	}
	
	
	
	public OtpProvider getOtpProvider()
	{
		if(!_initialized)
		{
			throw new IllegalStateException("TotpManager not initialized yet");
		}
		if(m_otpProvider == null)
		{
			m_otpProvider = OtpProvider.CreteOtpProvider(AccountsModel.getInstance(), m_totpClock);
		}
		return m_otpProvider;
		
	}
	
}
