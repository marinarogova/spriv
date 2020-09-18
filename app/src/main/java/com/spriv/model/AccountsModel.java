package com.spriv.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.webkit.GeolocationPermissions;

import com.spriv.SprivApp;
import com.spriv.data.CheckLoginResultInfo;
import com.spriv.data.SprivAccount;
import com.spriv.data.SprivAccountId;
import com.spriv.data.SprivLogin;
import com.spriv.json.Tags;
import com.spriv.utils.CriptoUtil;
import com.spriv.utils.GeocoderUtil;

public class AccountsModel implements AccountsDataProvider 
{
	public static final String ACCOUNTS_FILE_NAME = "Accounts.json";
	public static final String ALLOED_LOGINS_FILE_NAME = "AllowdLogins.json";
	private static final String AES_CRIPTO_KEY = "45775678ABCSFFRT";
	
	private List<SprivAccount> m_accounts = new ArrayList<SprivAccount>();
	private Map<String, CheckLoginResultInfo> m_accountsToAllowedLogins = new HashMap<String, CheckLoginResultInfo>();
	private static AccountsModel s_instance;
	private static final Object s_singletoneLock = new Object();
	private static final Object s_accountsLock = new Object();
	
	public static AccountsModel getInstance()
	{
		if(s_instance == null)
		{
			synchronized (s_singletoneLock) 
			{
				if(s_instance == null)
				{
					s_instance = new AccountsModel();
				}
			}
		}
		return s_instance;
	}
	
	private AccountsModel()
	{
		try 
		{
			loadAccounts();
		} 
		catch (IOException e) {
			e.printStackTrace();
			Log.e("Account model", e.toString());
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Account model", e.toString());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Account model", e.toString());
		}
		//addDefaultAccounts();
		
	}
	
	
	/*private void addDefaultAccounts() {
		SprivAccount account = new SprivAccount();
		account.setUserName("ofir@yahoo.com");
		account.setCompany("Spriv.com");
		account.setTOTPSecret("1111111");
		m_accounts.add(account);
		
		account = new SprivAccount();
		account.setUserName("ofir@apply.com");
		account.setCompany("Spriv.com");
		account.setTOTPSecret("222222");
		m_accounts.add(account);
		
		account = new SprivAccount();
		account.setUserName("ofir@gmail.com");
		account.setCompany("Spriv.com");
		account.setTOTPSecret("333333");
		m_accounts.add(account);
		try 
		{
			saveAccountsToFile();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}*/

	public List<SprivAccount> getAccounts()
	{
		return m_accounts;
	}
	
	public boolean hasAccounts()
	{
		return m_accounts != null && m_accounts.size() > 0;
	}

	public void addAccount(SprivAccount account) throws Exception {
		synchronized (s_accountsLock) 
		{
			SprivAccount accountToReplace = null;
			//Look for account with the same identifier.
			for(SprivAccount existAccount : m_accounts)
			{
				if(existAccount.getId().equals(account))
				{
					accountToReplace = existAccount;
					break;
				}
			}
			//Replace account
			if(accountToReplace != null)
			{
				//Id is the same, we want to save allowed logins so we only need to update TOTP secret.
				accountToReplace.setTOTPSecret(account.getTOTPSecret());
			}
			//Add account
			else
			{
				m_accounts.add(account);
			}
			saveAccountsToFile();
		}
	}
	
	public void addAllowedLogin(SprivLogin sprivLogin)
	{
		synchronized (s_accountsLock) 
		{
	
		}
	}
	
	public void removeAllowedLogin(SprivLogin sprivLogin)
	{
		synchronized (s_accountsLock) 
		{
		
		}
	}

	public void removeAccountAt(int index) throws Exception {
		synchronized (s_accountsLock) 
		{
			m_accounts.remove(index);
			saveAccountsToFile();
		}
	}
	
	public void removeAccount(SprivAccount account) throws Exception {
		synchronized (s_accountsLock) 
		{
			m_accounts.remove(account);
			saveAccountsToFile();
		}
	}
	
	private void saveAccountsToFile() throws Exception
	{
		//Create JSON
		JSONArray jsonArray = new JSONArray();
		for(int i=0 ; i < m_accounts.size() ;i++)
		{
			SprivAccount sprivAccount =  m_accounts.get(i);
			JSONObject accoutJson = new JSONObject();
			accoutJson.put(Tags.EndUserNameStore, sprivAccount.getId().getUserName());
			accoutJson.put(Tags.CompanyStore, sprivAccount.getId().getCompany());
			//Encrypt TOTP
			String totpEncrypt =  new CriptoUtil().encrypt(AES_CRIPTO_KEY, sprivAccount.getTOTPSecret()); 
			accoutJson.put(Tags.TOTPStore, totpEncrypt);
			JSONArray loginsJsonArr = new JSONArray();
			accoutJson.put(Tags.Login, loginsJsonArr);
			for(SprivLogin sprivLogin : sprivAccount.getLogins())
			{
				JSONObject loginJson = new JSONObject();
				loginJson.put(Tags.TransactionID, sprivLogin.getTransactionId());
				loginJson.put(Tags.Address, sprivLogin.getAddress());
				loginJson.put(Tags.Browser, sprivLogin.getCheckLoginResultInfo().getBrowser());
				loginJson.put(Tags.Company, sprivLogin.getCheckLoginResultInfo().getCompany());
				loginJson.put(Tags.Date, sprivLogin.getCheckLoginResultInfo().getDate());
				loginJson.put(Tags.Service, sprivLogin.getCheckLoginResultInfo().getService());
				loginJson.put(Tags.IPAddress, sprivLogin.getCheckLoginResultInfo().getIPAddress());
				loginJson.put(Tags.OS, sprivLogin.getCheckLoginResultInfo().getOS());
				loginsJsonArr.put(loginJson);
			}
			jsonArray.put(accoutJson);
		}
		
		//Save JSON to file
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(SprivApp.getAppContext().openFileOutput(ACCOUNTS_FILE_NAME ,Activity.MODE_PRIVATE));
        outputStreamWriter.write(jsonArray.toString());
        outputStreamWriter.close();
		
	}
	
	private void loadAccounts() throws Exception 
	{
		//Load JSON from file
		String accountsStr = "";
		File file = SprivApp.getAppContext().getFileStreamPath(ACCOUNTS_FILE_NAME);
		if(file.exists())
		{
			InputStream inputStream = SprivApp.getAppContext().openFileInput(ACCOUNTS_FILE_NAME);
	
	        if ( inputStream != null ) 
	        {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();
	
	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }
	            inputStream.close();
	            accountsStr = stringBuilder.toString();
	            //Convert JSON to accounts list
	            JSONArray jsonArray = new JSONArray(accountsStr);
	            ArrayList<SprivAccount> accounts = new ArrayList<SprivAccount>(); 
	            for(int i=0; i < jsonArray.length() ;i++)
	            {
	            	JSONObject jsonObj = jsonArray.getJSONObject(i);
	            	SprivAccount sprivAccount = new SprivAccount();
	            	SprivAccountId sprivAccountId = new SprivAccountId(jsonObj.getString(Tags.CompanyStore), jsonObj.getString(Tags.EndUserNameStore));
	            	sprivAccount.setId(sprivAccountId);
	            	//Decrypt TOTP
	            	String totpEncrypt =  jsonObj.getString(Tags.TOTPStore);
	            	String totpDecrypt = new CriptoUtil().decrypt(AES_CRIPTO_KEY, totpEncrypt);
	            	sprivAccount.setTOTPSecret(totpDecrypt);
	            	if(jsonObj.has(Tags.Login))
	            	{
		            	JSONArray loginsJsonArr = jsonObj.getJSONArray(Tags.Login);
		            	if(loginsJsonArr != null)
	            		{
			            	for(int j=0 ; j < loginsJsonArr.length() ; j++)
			            	{
			            		JSONObject loginJson = loginsJsonArr.getJSONObject(j);
			            		CheckLoginResultInfo checkLoginResultInfo = new CheckLoginResultInfo();
			            		checkLoginResultInfo.setBrowser(loginJson.getString(Tags.Browser));
			            		checkLoginResultInfo.setCompany(loginJson.getString(Tags.Company));
			            		checkLoginResultInfo.setDate(loginJson.getLong(Tags.Date));
			            		checkLoginResultInfo.setIPAddress(loginJson.getString(Tags.IPAddress));
			            		checkLoginResultInfo.setOS(loginJson.getString(Tags.OS));
			            		checkLoginResultInfo.setService(loginJson.getString(Tags.Service));
			            		String address = loginJson.getString(Tags.Address);
			            		String transactionId = loginJson.getString(Tags.TransactionID);
			            		SprivLogin sprivLogin = new SprivLogin(checkLoginResultInfo, address, transactionId);
			            		sprivAccount.getLogins().add(sprivLogin);
			            	}
	            		}
	            	}
	            	accounts.add(sprivAccount);
	            }
	            m_accounts = accounts;
	        }
		}
	}
	
	
	
	public SprivAccount getAccount(SprivAccountId id)
	{
		for (SprivAccount account : m_accounts) 
		{
			if(account.getId().equals(id))
			{
				return account;
			}
		}
		return null;
	}
	
	public void addLogin(SprivLogin login, SprivAccountId accountId)throws Exception {
		synchronized (s_accountsLock) 
		{
			getAccount(accountId).getLogins().add(login);
			saveAccountsToFile();
		} 
	}
	
	public void removeLogin(SprivLogin login, SprivAccountId accountId)throws Exception {
		synchronized (s_accountsLock) 
		{
			SprivAccount sprivAccount = getAccount(accountId);
			boolean removed = sprivAccount.getLogins().remove(login);
			if(removed)
			{
				saveAccountsToFile();
			}
		}
	}
	
	
	@Override
	public String getSecret(SprivAccountId accountId) {
		for (SprivAccount account : m_accounts) 
		{
			if(account.getId().equals(accountId))
			{
				return account.getTOTPSecret();
			}
		}
		return null;
	}
	

}
