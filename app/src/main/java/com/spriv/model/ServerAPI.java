package com.spriv.model;

import android.content.Context;
import android.database.DefaultDatabaseErrorHandler;
import android.os.AsyncTask;
import android.os.FileUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.media.AudioAttributesCompat;

import com.android.internal.http.multipart.MultipartEntity;
import com.spriv.SprivApp;
import com.spriv.activity.AboutActivity;
import com.spriv.activity.AccountsActivity;
import com.spriv.data.AuthInfo;
import com.spriv.data.CheckLoginResultInfo;
import com.spriv.data.CheckVerificationResultInfo;
import com.spriv.data.SprivAccount;
import com.spriv.json.JsonConverter;
import com.spriv.json.Tags;
import com.spriv.service.GpsService;
import com.spriv.utils.RestClient;
import com.spriv.utils.WebRequestMethod;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class ServerAPI {

    private static final int CANCEL_ALWAYS_ALLOE_DECISION_ID = 4;

    public AccountsActivity accountsActivity;
    public static SprivAccount pairAccount(String key, String phoneNumber, String pushToken) throws Exception {
        String serverBaseAdress = AppSettingsModel.getInstance().getServerBaseAddress();
        String url = serverBaseAdress + "wsM5.asmx/UpdatePair";
        RestClient client = new RestClient(url);
        client.AddParam("nKey", key);
        //client.AddParam("strPhoneNumber", phoneNumber);
        client.AddParam("strPushingID", pushToken);

        client.Execute(WebRequestMethod.POST);
        String resposeJson = client.getResponse();

        //Error
        if (client.hasException()) {
            throw new Exception("pairAccount internal Error - bad response from server");
        } else {
            JSONObject jsonResponseObj = new JSONObject(resposeJson);
            String codeStr = jsonResponseObj.getString(Tags.Code);
            int code = Integer.parseInt(codeStr);
            if (code == 200) {

                Log.d("Response", "Got Json");
                Log.d("StrResponse","200");
                String username = jsonResponseObj.getString(Tags.EndUserName);
                String company = jsonResponseObj.getString(Tags.Company);
                String tOTPSecret = jsonResponseObj.getString(Tags.TOTP);
                SprivAccount sprivAccount = new SprivAccount(username, company, tOTPSecret);
                return sprivAccount;
            } else {
                String message = jsonResponseObj.getString("Text");
                String str_code = jsonResponseObj.getString("Code");
                String str_res = jsonResponseObj.getString("strResponse");
                Log.d("StrResponse","404");
                Toast.makeText(SprivApp.getAppContext(),str_code,Toast.LENGTH_LONG).show();
                throw new Exception();
            }
        }
    }

	/*public static void identify(AuthInfo authInfo, String transactionId) throws Exception
	{
		String url = serverBaseAdress + "Listener/Identify.aspx";
	    RestClient client = new RestClient(url);
	    JSONObject identifyJsonObj = JsonConverter.toIdentifyJson(authInfo, transactionId);
	    String identifyJsonStr = identifyJsonObj.toString();
	    client.SetJsonStr(identifyJsonStr);
	    client.Execute(WebRequestMethod.POST);
		String resposeJson = client.getResponse();
		//Error
		if(client.hasException())
		{
			throw new Exception("Identify internal Error - bad response from server");
		}
		JSONObject jsonResponseObj = new JSONObject(resposeJson);
		String codeStr = jsonResponseObj.getString(Tags.Code);
		int code = Integer.parseInt(codeStr);
		if(code != 200)
		{
			String message = jsonResponseObj.getString(Tags.Message);
			StringBuffer strBuffer = new StringBuffer();
			if(jsonResponseObj.has(Tags.Errors))
			{
				JSONArray errorsJsonArr = jsonResponseObj.getJSONArray(Tags.Errors);
				for (int i = 0; i < errorsJsonArr.length(); i++) {
					strBuffer.append(errorsJsonArr.get(i).toString());
				}
				message += ", Errors: " + strBuffer.toString();
			}
			throw new Exception(message);
		}
	}*/

    public static void identify(AuthInfo authInfo, String connectedWifiBID, String transactionId) throws Exception {
        String serverBaseAdress = AppSettingsModel.getInstance().getServerBaseAddress();
        String url = serverBaseAdress + "Listener/Identify.aspx";
        RestClient client = new RestClient(url);

        JSONObject identifyJsonObj = new JSONObject();
        if (connectedWifiBID == null || !"".equals(connectedWifiBID)) {
            identifyJsonObj.put(Tags.BID, connectedWifiBID);
        } else {
            identifyJsonObj.put(Tags.BID, Tags.NONE);
        }
        identifyJsonObj.put(Tags.TID, transactionId);

        // Added

        identifyJsonObj.put(Tags.SS, authInfo.getCellTower().getSS());
        identifyJsonObj.put(Tags.SSID, authInfo.getCellTower().getSSID());
        identifyJsonObj.put(Tags.AlTITUDE, GpsService.w_faltitude);
        identifyJsonObj.put(Tags.LON, GpsService.w_fLongitude);
        identifyJsonObj.put(Tags.LAT, GpsService.w_fLatitude);
        identifyJsonObj.put(Tags.ACCURACY, GpsService.w_accurcry);
        identifyJsonObj.put(Tags.TIME_STAMP, GpsService.time_stamp);




        String identifyJsonStr = identifyJsonObj.toString();
        client.SetJsonStr(identifyJsonStr);
        client.Execute(WebRequestMethod.POST);
        //client.Execute(WebRequestMethod.POST, false);
        String resposeJson = client.getResponse();
        Log.d("usm_url","url= "+url);
        Log.d("usm_identifyJsonStr","identifyJsonStr= "+identifyJsonStr);
        Log.d("usm_response","responseJson= "+resposeJson);

        //Error
        if (client.hasException()) {
            throw new Exception("Identify internal Error - bad response from server");
        }
        JSONObject jsonResponseObj = new JSONObject(resposeJson);
        String codeStr = jsonResponseObj.getString(Tags.Code);
        int code = Integer.parseInt(codeStr);
        if (code != 200) {

            Log.d("identifyResponse","404");
            String message = jsonResponseObj.getString(Tags.Message);
            StringBuffer strBuffer = new StringBuffer();
            if (jsonResponseObj.has(Tags.Errors)) {
                JSONArray errorsJsonArr = jsonResponseObj.getJSONArray(Tags.Errors);
                for (int i = 0; i < errorsJsonArr.length(); i++) {
                    strBuffer.append(errorsJsonArr.get(i).toString());
                }
                message += ", Errors: " + strBuffer.toString();
            }
            throw new Exception(message);
        }
    }

    public static String getFileUploadUrl(File file) throws Exception {
        String fileUrl;
        String url = "https://qa.spriv.com/presigned-url";
        RestClient client = new RestClient(url);
        client.AddParam("device_id","nishant-test");
        client.Execute(WebRequestMethod.POST);
        String resposeJson = client.getResponse();
        Log.d("usm_url","url= "+url);
        Log.d("FileURL","checkUrl= "+resposeJson);
        if (client.hasException()) {
            throw new Exception("checkLogin internal Error - bad response from server");
        } else {
            JSONObject jsonResponseObj = new JSONObject(resposeJson);
            String codeStr = jsonResponseObj.getString("status");
            int code = Integer.parseInt(codeStr);
            //Error
            if (code != 200) {
                String message = jsonResponseObj.getString(Tags.Message);
                StringBuffer strBuffer = new StringBuffer();
                if (jsonResponseObj.has(Tags.Errors)) {
                    JSONArray errorsJsonArr = jsonResponseObj.getJSONArray(Tags.Errors);
                    for (int i = 0; i < errorsJsonArr.length(); i++) {
                        strBuffer.append(errorsJsonArr.get(i).toString());
                    }
                    message += ", Errors: " + strBuffer.toString();
                }
                throw new Exception(message);
            } else {

                JSONObject dataObject = jsonResponseObj.getJSONObject("data");
                fileUrl = dataObject.getString("presigned_url");

                uploadFile(fileUrl, file);

            }
        }

        return fileUrl;
    }

    public static String uploadFile(String uploadURL,File f) throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpPut request = new HttpPut(uploadURL);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        FileBody fileBody = new FileBody(f);
        builder.addPart("file",fileBody);

        HttpEntity multipart = builder.build();
        request.setEntity(multipart);

        if(uploadURL!= ""){
            HttpResponse response =  client.execute(request);

            if(response.getStatusLine().getStatusCode() == 200){

                notifyAdmin();
            }
            Log.d("Response", response.toString());
        }


        return "";
    }

    public static void notifyAdmin() throws Exception {
        String fileUrl;
        String url = "https://qa.spriv.com/notify_new_log_entry";
        RestClient client = new RestClient(url);
        client.AddParam("device_id", "logcat.txt");
        client.Execute(WebRequestMethod.POST);
        String resposeJson = client.getResponse();
        Log.d("usm_url","url= "+url);
        Log.d("NotifyAdmin","checkUrl= "+resposeJson);
        if (client.hasException()) {
            throw new Exception("checkLogin internal Error - bad response from server");
        } else {
            JSONObject jsonResponseObj = new JSONObject(resposeJson);
            String codeStr = jsonResponseObj.getString("status");
            int code = Integer.parseInt(codeStr);
            //Error
            if (code != 200) {
                String message = jsonResponseObj.getString(Tags.Message);
                StringBuffer strBuffer = new StringBuffer();
                if (jsonResponseObj.has(Tags.Errors)) {
                    JSONArray errorsJsonArr = jsonResponseObj.getJSONArray(Tags.Errors);
                    for (int i = 0; i < errorsJsonArr.length(); i++) {
                        strBuffer.append(errorsJsonArr.get(i).toString());
                    }
                    message += ", Errors: " + strBuffer.toString();
                }
                throw new Exception(message);
            } else {

                Log.d("NotifyAdmin", "success");
            }
        }
    }


    public static CheckLoginResultInfo checkLogin(String transactionId) throws Exception {
        String serverBaseAdress = AppSettingsModel.getInstance().getServerBaseAddress();
        String url = serverBaseAdress + "wsM5.asmx/CheckLogin";
        RestClient client = new RestClient(url);
        client.AddParam("strID", transactionId);

        client.Execute(WebRequestMethod.POST);
        String resposeJson = client.getResponse();
        Log.d("usm_url","url= "+url);
        Log.d("usm_responseJson","checkLogin= "+resposeJson);
        //Error
        if (client.hasException()) {
            throw new Exception("checkLogin internal Error - bad response from server");
        } else {
            JSONObject jsonResponseObj = new JSONObject(resposeJson);
            String codeStr = jsonResponseObj.getString(Tags.Code);
            int code = Integer.parseInt(codeStr);
            //Error
            if (code != 200) {
                String message = jsonResponseObj.getString(Tags.Message);
                StringBuffer strBuffer = new StringBuffer();
                if (jsonResponseObj.has(Tags.Errors)) {
                    JSONArray errorsJsonArr = jsonResponseObj.getJSONArray(Tags.Errors);
                    for (int i = 0; i < errorsJsonArr.length(); i++) {
                        strBuffer.append(errorsJsonArr.get(i).toString());
                    }
                    message += ", Errors: " + strBuffer.toString();
                }
                throw new Exception(message);
            } else {
                JSONObject loginResponseObj = jsonResponseObj.getJSONObject(Tags.Login);
                CheckLoginResultInfo checkLoginResultInfo = new CheckLoginResultInfo();
                checkLoginResultInfo.setCompany(loginResponseObj.getString(Tags.Company));
                checkLoginResultInfo.setService(loginResponseObj.getString(Tags.Service));
                checkLoginResultInfo.setEndUsername(loginResponseObj.getString(Tags.UserName));
                checkLoginResultInfo.setOS(loginResponseObj.getString(Tags.OS));
                checkLoginResultInfo.setBrowser(loginResponseObj.getString(Tags.Browser));
                checkLoginResultInfo.setIPAddress(loginResponseObj.getString(Tags.IPAddress));
                checkLoginResultInfo.setDate(loginResponseObj.getLong(Tags.Date));
                return checkLoginResultInfo;
            }
        }
    }

    public static CheckVerificationResultInfo checkVerification(String transactionId) throws Exception {
        String serverBaseAdress = AppSettingsModel.getInstance().getServerBaseAddress();
        String url = serverBaseAdress + "wsM5.asmx/CheckVerification";
        Log.d("usm_url", url);
        RestClient client = new RestClient(url);
        client.AddParam("strID", transactionId);

        client.Execute(WebRequestMethod.POST);
        String resposeJson = client.getResponse();
        Log.d("usm_resposeJson", "resposeJson= " + resposeJson);
        //Error
        if (client.hasException()) {
            throw new Exception("checkVerification internal Error - bad response from server");
        } else {
            JSONObject jsonResponseObj = new JSONObject(resposeJson);
            Log.d("usm_jsonResponseObj", "jsonResponseObj= " + jsonResponseObj.toString());
            String codeStr = jsonResponseObj.getString(Tags.Code);
            int code = Integer.parseInt(codeStr);
            if (code == 200) {
                CheckVerificationResultInfo checkVerificationResultInfo = new CheckVerificationResultInfo();
                checkVerificationResultInfo.setQuestion(jsonResponseObj.getString(Tags.Question));
                checkVerificationResultInfo.setCompany(jsonResponseObj.getString(Tags.Company));
                checkVerificationResultInfo.setEndUsername(jsonResponseObj.getString(Tags.EndUserName));
                checkVerificationResultInfo.setDate(jsonResponseObj.getLong(Tags.Date));
                return checkVerificationResultInfo;
            } else {
                String message = jsonResponseObj.getString(Tags.Message);
                throw new Exception(message);
            }
        }
    }
	
	/*public static boolean cancelAllwaysAllow(String transactionId) throws Exception
	{
		String serverBaseAdress = AppSettingsModel.getInstance().getServerBaseAddress();
		String url = serverBaseAdress + "wsM5.asmx/AddReportDecision"; 
	    RestClient client = new RestClient(url);
	    client.AddParam("nKey", "0");
	    client.AddParam("strID", transactionId);
	    client.AddParam("nDecisionID", String.valueOf(CANCEL_ALWAYS_ALLOE_DECISION_ID));
	    client.Execute(WebRequestMethod.POST);
		String resposeJson = client.getResponse();
		
		//Error
		if(client.hasException())
		{
			throw new Exception("addReportDecision internal Error - bad response from server");
		}
		else
		{
			JSONObject jsonResponseObj = new JSONObject(resposeJson);
			String codeStr = jsonResponseObj.getString(Tags.Code);
			int code = Integer.parseInt(codeStr);
			if(code != 200)
			{
				String message = jsonResponseObj.getString(Tags.Message);
				throw new Exception(message);
			}
			else
			{
				return true;
			}
		}
	}*/

    public static boolean cancelAllwaysAllow(String transactionId) throws Exception {
        String serverBaseAdress = AppSettingsModel.getInstance().getServerBaseAddress();
        String url = serverBaseAdress + "wsM5.asmx/AddReportDecision";
        RestClient client = new RestClient(url);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put(Tags.TransactionID, transactionId);
        jsonObj.put(Tags.Decision, CANCEL_ALWAYS_ALLOE_DECISION_ID);
        String identifyJsonStr = jsonObj.toString();
        client.SetJsonStr(identifyJsonStr);
        //client.Execute(WebRequestMethod.POST);
        client.Execute(WebRequestMethod.POST, false);
        String resposeJson = client.getResponse();

        //Error
        if (client.hasException()) {
            throw new Exception("addReportDecision internal Error - bad response from server");
        } else {
            JSONObject jsonResponseObj = new JSONObject(resposeJson);
            String codeStr = jsonResponseObj.getString(Tags.Code);
            int code = Integer.parseInt(codeStr);
            if (code != 200) {
                String message = jsonResponseObj.getString(Tags.Message);
                throw new Exception(message);
            } else {
                return true;
            }
        }
    }

    public static boolean addReportDecision(AuthInfo authInfo, String transactionId, int desicion) throws Exception {
        String serverBaseAdress = AppSettingsModel.getInstance().getServerBaseAddress();
        String url = serverBaseAdress + "wsM5.asmx/AddReportDecision";
        RestClient client = new RestClient(url);

        JSONObject identifyJsonObj = JsonConverter.toIdentifyJson(authInfo, transactionId);
        identifyJsonObj.put(Tags.TransactionID, transactionId);
        identifyJsonObj.put(Tags.Decision, desicion);
        String identifyJsonStr = identifyJsonObj.toString();
        //client.SetJsonStr(identifyJsonStr);
        addParamsToClient(client, identifyJsonObj);
        //client.Execute(WebRequestMethod.POST);
        client.Execute(WebRequestMethod.POST, false);
        String resposeJson = client.getResponse();
        Log.d("usm_url", "url= " + url);
        Log.d("usm_identifyJsonStr", "identifyJsonStr= " + identifyJsonStr);
        Log.d("usm_resposeJson", "addReportDecision= " + resposeJson);
        //Error
        if (client.hasException()) {
            throw new Exception("addReportDecision internal Error - bad response from server");
        } else {
            JSONObject jsonResponseObj = new JSONObject(resposeJson);
            String codeStr = jsonResponseObj.getString(Tags.Code);
            int code = Integer.parseInt(codeStr);
            if (code != 200) {
                String message = jsonResponseObj.getString(Tags.Message);
                throw new Exception(message);
            } else {
                return true;
            }
        }
    }

    private static void addParamsToClient(RestClient client, JSONObject identifyJsonObj) {
        Iterator<String> iter = identifyJsonObj.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Object value = identifyJsonObj.get(key);
                client.AddParam(key, value.toString());
            } catch (JSONException e) {
                // Something went wrong!
                e.printStackTrace();
            }
        }
    }

}
