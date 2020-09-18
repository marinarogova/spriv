package com.spriv.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spriv.data.AuthInfo;
import com.spriv.data.CellTowerInfo;
import com.spriv.data.DeviceInfo;
import com.spriv.data.IPAddressInfo;
import com.spriv.data.LocationInfo;
import com.spriv.data.PhoneNumberInfo;
import com.spriv.service.GpsService;

public class JsonConverter {

	public static JSONObject toIdentifyJson(AuthInfo authInfo, String transactionId) throws JSONException
	{
		JSONObject jSONObject = new JSONObject();
		
		//Transaction id
		jSONObject.put(Tags.TransactionID, transactionId);
		
		//Device
		DeviceInfo deviceInfo = authInfo.getDevice();
		JSONObject deviceInfoJsonObj = new JSONObject(); 
		jSONObject.put(Tags.Device, deviceInfoJsonObj);
		if(deviceInfo.getFingerprint() != null)
		{
			deviceInfoJsonObj.put(Tags.Fingerprint, deviceInfo.getFingerprint());
		}
		if(deviceInfo.getManufacturer() != null)
		{
			deviceInfoJsonObj.put(Tags.Manufacturer, deviceInfo.getManufacturer());
		}
		JSONObject osInfoJsonObj = new JSONObject();
		deviceInfoJsonObj.put(Tags.OS, osInfoJsonObj);
		if(deviceInfo.getOSName() != null)
		{
			osInfoJsonObj.put(Tags.Name, deviceInfo.getOSName());
		}
		if(deviceInfo.getOSVersion() != null)
		{
			osInfoJsonObj.put(Tags.Version, deviceInfo.getOSVersion());
		}
		//TODO: check this
		osInfoJsonObj.put(Tags.Modified, 1);
		
		//Application
		JSONObject appJson = new JSONObject();
		jSONObject.put(Tags.App, appJson);
		appJson.put(Tags.Version, authInfo.getDevice().getAppVersion());
		
		//Cell tower
		JSONObject cellTowerJson = new JSONObject();
		jSONObject.put(Tags.CellTower, cellTowerJson);
		if(authInfo.getCellTower() != null)
		{	
			cellTowerJson.put(Tags.BSSID, authInfo.getCellTower().getBSSID());
			cellTowerJson.put(Tags.SS, authInfo.getCellTower().getSS());
			cellTowerJson.put(Tags.SSID, authInfo.getCellTower().getSSID());

			// Added
			cellTowerJson.put(Tags.LAT, GpsService.w_fLatitude);
			cellTowerJson.put(Tags.ACCURACY, GpsService.w_accurcry);
			cellTowerJson.put(Tags.LAT, GpsService.w_fLatitude);
			cellTowerJson.put(Tags.TIME_STAMP, GpsService.time_stamp);
			cellTowerJson.put(Tags.LON, GpsService.w_fLongitude);


		}
		else
		{
			cellTowerJson.put(Tags.BSSID, Tags.NONE);
			cellTowerJson.put(Tags.SS, 0);
			cellTowerJson.put(Tags.SSID, Tags.NONE);
		}
		return jSONObject;
	}
}
