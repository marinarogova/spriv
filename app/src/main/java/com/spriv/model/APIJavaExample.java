package com.spriv.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class APIJavaExample {

	private String url;
	
	/*public void javaex()
	{
		String url = "https://selfsolve.apple.com/wcResults.do";

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("User-Agent", USER_AGENT);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
		urlParameters.add(new BasicNameValuePair("cn", ""));
		urlParameters.add(new BasicNameValuePair("locale", ""));
		urlParameters.add(new BasicNameValuePair("caller", ""));
		urlParameters.add(new BasicNameValuePair("num", "12345"));

		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + 
                                    response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

	}*/
	
	public String addUser(String companyUserName, String companyPassword, String endUserName, int 
			clientID, String firstName, String lastName, String email, String personID, String mobileNumber) throws ClientProtocolException, IOException
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "AddUserToCompany");
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("strUsername", companyUserName));
		urlParameters.add(new BasicNameValuePair("strPassword", companyPassword));
		urlParameters.add(new BasicNameValuePair("strAccount", endUserName));
		urlParameters.add(new BasicNameValuePair("nClientID", String.valueOf(clientID)));
		urlParameters.add(new BasicNameValuePair("strFirstName", firstName));
		urlParameters.add(new BasicNameValuePair("strLastName", lastName));
		urlParameters.add(new BasicNameValuePair("strEmail", email));
		urlParameters.add(new BasicNameValuePair("strPersonID", personID));
		urlParameters.add(new BasicNameValuePair("strMobilePhone", mobileNumber));
		urlParameters.add(new BasicNameValuePair("nStatusID", "1"));
		urlParameters.add(new BasicNameValuePair("nStatusTimeout", "0"));
		urlParameters.add(new BasicNameValuePair("bAsHTML", "false"));
		
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		
		//Debug
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
	
	public String sendInvitation(String companyUserName, String companyPassword, String endUserName) throws ClientProtocolException, IOException
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "SendInvitation");
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("strUsername", companyUserName));
		urlParameters.add(new BasicNameValuePair("strPassword", companyPassword));
		urlParameters.add(new BasicNameValuePair("strEndUsers", endUserName));
		
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		
		//Debug
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
	
	public String login(String companyUserName, String companyPassword, String endUserName) throws ClientProtocolException, IOException
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "Login");
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("strUsername", companyUserName));
		urlParameters.add(new BasicNameValuePair("strPassword", companyPassword));
		urlParameters.add(new BasicNameValuePair("strEndUsername", endUserName));
		urlParameters.add(new BasicNameValuePair("strPCFingerprint", "Your PC Identifier: Ex: MAC address or browser user agent"));
		urlParameters.add(new BasicNameValuePair("strIPAddress", "Your IP Address"));
		urlParameters.add(new BasicNameValuePair("strService", "Your service description. Ex: Web Access"));
		urlParameters.add(new BasicNameValuePair("nMethod", "1"));
		urlParameters.add(new BasicNameValuePair("bAsHTML", "false"));
		
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
	
		//Debug
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
	
	public String verify(String companyUserName, String companyPassword, String endUserName, String msg) 
			throws ClientProtocolException, IOException
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "AddVerification");
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("strUsername", companyUserName));
		urlParameters.add(new BasicNameValuePair("strPassword", companyPassword));
		urlParameters.add(new BasicNameValuePair("strEndUsername", endUserName));
		urlParameters.add(new BasicNameValuePair("strMessage", msg));
		urlParameters.add(new BasicNameValuePair("strService", "Your service description. Ex: Web Access"));
		urlParameters.add(new BasicNameValuePair("nMethod", "1"));
		urlParameters.add(new BasicNameValuePair("bAsHTML", "false"));
		
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		
		//Debug
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
		
	}
	
	public String verifyTotp(String companyUserName, String companyPassword, String endUserName, String key) 
			throws ClientProtocolException, IOException
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url + "AddTotp");
		
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("strUsername", companyUserName));
		urlParameters.add(new BasicNameValuePair("strPassword", companyPassword));
		urlParameters.add(new BasicNameValuePair("strEndUsername", endUserName));
		urlParameters.add(new BasicNameValuePair("strKey", key));
		urlParameters.add(new BasicNameValuePair("strService", "Your service description. Ex: Web Access"));
		
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		
		//Debug
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + post.getEntity());
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
		
	}
}
