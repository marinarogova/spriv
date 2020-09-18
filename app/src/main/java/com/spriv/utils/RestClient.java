package com.spriv.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class RestClient {

    private ArrayList <NameValuePair> params;
    private ArrayList <NameValuePair> headers;
    private String jsonStrParam;
    private String url;

    private int responseCode;
    private String message;

    private String response;

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url)
    {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public void AddParam(String name, String value)
    {
        params.add(new BasicNameValuePair(name, value));
    }

    public void AddHeader(String name, String value)
    {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void Execute(WebRequestMethod method) throws Exception
    {
    	Execute(method, true);
    }
    
    public void Execute(WebRequestMethod method, boolean autoSetContentHeader) throws Exception
    {
    	if(method == WebRequestMethod.GET)
        {
            //add parameters
            String combinedParams = "";
            if(!params.isEmpty()){
                combinedParams += "?";
                for(NameValuePair p : params)
                {
                    String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                    if(combinedParams.length() > 1)
                    {
                        combinedParams  +=  "&" + paramString;
                    }
                    else
                    {
                        combinedParams += paramString;
                    }
                }
            }

            HttpGet httpGet = new HttpGet(url + combinedParams);

            //add headers
            for(NameValuePair h : headers)
            {
                httpGet.addHeader(h.getName(), h.getValue());
            }

            executeRequest(httpGet);
        }
            
    	if(method == WebRequestMethod.POST)
        {
    		HttpPost httpPost = new HttpPost(url);
                
            //add headers
            for(NameValuePair h : headers)
            {
                httpPost.addHeader(h.getName(), h.getValue());
            }
            if(!params.isEmpty())
            {
            	httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            }
            else
            {	
            	StringEntity se = new StringEntity( jsonStrParam, HTTP.UTF_8);  
                if(autoSetContentHeader)
                {
                	httpPost.setHeader("content-type", "application/json");
                }
                httpPost.setEntity(se);
            }
            executeRequest(httpPost);
        }
    }

    private void executeRequest(HttpUriRequest request)
    {
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

	public void SetJsonStr(String jsonStrParam) {
		this.jsonStrParam = jsonStrParam;
	}
	
	public boolean hasException()
	{
		return responseCode >= 400;
	}
	
	
}