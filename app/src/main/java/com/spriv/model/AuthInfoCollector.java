package com.spriv.model;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.spriv.BuildConfig;
import com.spriv.R;
import com.spriv.data.AuthInfo;
import com.spriv.data.CellTowerInfo;
import com.spriv.data.DeviceInfo;

public class AuthInfoCollector 
{
	private final static int SIGNAL_STRENGTH_LEVELS = 100;
	
	private AuthInfo m_authInfo = new AuthInfo();
	Context m_context;
	
	public AuthInfo getAuthInfo()
	{
		return m_authInfo;
	}
	
	public AuthInfoCollector(Context context)
	{
		m_context = context;
	}
	
	public void collect()
	{
		collectCellTowerInfo();
		collectCellDeviceInfo();
	}
	
	private void collectCellDeviceInfo() {
		
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setOSName("Android");
		deviceInfo.setOSVersion(System.getProperty("os.version"));
		deviceInfo.setOSModified(android.os.Build.VERSION.INCREMENTAL);
		deviceInfo.setFingerprint(android.os.Build.SERIAL);
		deviceInfo.setManufacturer(android.os.Build.MANUFACTURER);
		deviceInfo.setAppVersion(BuildConfig.VERSION_NAME);
		m_authInfo.setDevice(deviceInfo);
	}

	private void collectCellTowerInfo() {
		WifiManager wifiManager = (WifiManager) m_context.getSystemService(Context.WIFI_SERVICE);
		//Connected WIFI   
		WifiInfo connectedWifiInfo = wifiManager.getConnectionInfo();
		if(wifiManager.isWifiEnabled() && connectedWifiInfo != null)
		{
			   CellTowerInfo cellTowerInfo = new CellTowerInfo();
			   cellTowerInfo.setInUse(true);
			   String SSID = connectedWifiInfo.getSSID();
			   //Remove redundant ""
			   SSID = SSID.replace("\"", "");
			   String BSSID = connectedWifiInfo.getBSSID();
			   //TODO: check numbers
			   int signalStrength = WifiManager.calculateSignalLevel(connectedWifiInfo.getRssi(), SIGNAL_STRENGTH_LEVELS);
			   cellTowerInfo.setSS(signalStrength);
			   if(hasValue(BSSID))
			   {
				   BSSID = BSSID.replace("\"", "");
				   //Remove redundant :
				   BSSID = BSSID.replace(":", "");
				   cellTowerInfo.setBSSID(BSSID);
			   }
			   if(hasValue(SSID))
				   cellTowerInfo.setSSID(SSID);
			   m_authInfo.setCellTower(cellTowerInfo);
		} 
	}
	
	public static CellTowerInfo getConnectedCellTowerInfo(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		//Connected WIFI   
		WifiInfo connectedWifiInfo = wifiManager.getConnectionInfo();
		CellTowerInfo connectedCellTowerInfo = null;
		if(wifiManager.isWifiEnabled() && connectedWifiInfo != null)
		{
			   connectedCellTowerInfo = new CellTowerInfo();
			   connectedCellTowerInfo.setInUse(true);
			   String SSID = connectedWifiInfo.getSSID();
			   //Remove redundant ""
			   SSID = SSID.replace("\"", "");
			   String BSSID = connectedWifiInfo.getBSSID();
			   //TODO: check numbers
			   int signalStrength = WifiManager.calculateSignalLevel(connectedWifiInfo.getRssi(), SIGNAL_STRENGTH_LEVELS);
			   connectedCellTowerInfo.setSS(signalStrength);
			   if(hasValue(BSSID))
			   {
				   BSSID = BSSID.replace("\"", "");
				   //Remove redundant :
				   BSSID = BSSID.replace(":", "");
				   connectedCellTowerInfo.setBSSID(BSSID);
			   }
			   if(hasValue(SSID))
				   connectedCellTowerInfo.setSSID(SSID);
		}
		return connectedCellTowerInfo;
	}

	private static boolean hasValue(String str) {
		return str != null && !"".equals(str.trim());
	}

	/*private void collectIPAddressInfo() {
		
		String ipv4Address = NetworkAddressUtils.getIPAddress(true);
		if(hasValue(ipv4Address))
		{
			IPAddressInfo iPAddressInfo = new IPAddressInfo();
			iPAddressInfo.setAddress(ipv4Address);
			iPAddressInfo.setIsPublic(false);
			iPAddressInfo.setInUse(true);
			try 
			{
				InetAddress inetAddress = InetAddress.getByName(ipv4Address);
				String hostName = inetAddress.getHostAddress();
				iPAddressInfo.setHost(hostName);
			} 
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			m_authInfo.getIPAddress().add(iPAddressInfo);
		}
		String ipv6Address = NetworkAddressUtils.getIPAddress(false);
		if(hasValue(ipv6Address))
		{
			IPAddressInfo iPAddressInfo = new IPAddressInfo();
			iPAddressInfo.setAddress(ipv6Address);
			iPAddressInfo.setIsPublic(false);
			iPAddressInfo.setInUse(true);
			try 
			{
				InetAddress inetAddress = InetAddress.getByName(ipv6Address);
				String hostName = inetAddress.getHostAddress();
				iPAddressInfo.setHost(hostName);
			} 
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_authInfo.getIPAddress().add(iPAddressInfo);
		}
	}*/

	/*private void collectLocationInfo() 
	{
		GPSTracker gPSTracker = null;
		try
		{
			gPSTracker = new GPSTracker(m_context);
			if(gPSTracker.canGetLocation());
			{
				Location location = gPSTracker.getLocation();
				if(location != null)
				{
					LocationInfo locationInfo = new LocationInfo();
					locationInfo.setLatitude(location.getLatitude());
					locationInfo.setLongitude(location.getLongitude());
					locationInfo.setHasAltitude(location.hasAltitude());
					if(location.hasAltitude())
					{
						locationInfo.setAltitude(location.getAltitude());
					}
					locationInfo.setHasAccuracy(location.hasAccuracy());
					if(location.hasAccuracy())
					{
						locationInfo.setAccuracy(location.getAccuracy());
					}
					locationInfo.setElevation(0);
					locationInfo.setAzimuth(0);
					m_authInfo.setLocation(locationInfo);
				}
				//Dummy code
				else
				{
					LocationInfo locationInfo = new LocationInfo();
					locationInfo.setLatitude(0);
					locationInfo.setLongitude(0);
					locationInfo.setHasAltitude(true);
					locationInfo.setHasAccuracy(true);
					locationInfo.setElevation(0);
					locationInfo.setAzimuth(0);
					m_authInfo.setLocation(locationInfo);
				}
			}
		}
		finally
		{
			if(gPSTracker != null)
			{
				gPSTracker.stopUsingGPS();
			}
		}
	}*/
	
}
