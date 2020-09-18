package com.spriv.receiver;

import android.util.Log;
import com.spriv.data.AuthInfo;
import com.spriv.data.CellTowerInfo;
import com.spriv.json.Tags;
import com.spriv.model.AuthInfoCollector;
import com.spriv.model.ServerAPI;

public class IdentifyNotificationHandler extends PushNotificationHandler {

	
	@Override
	public void handle() {
		
		//OLD implementation
		AuthInfoCollector authInfoCollector = new AuthInfoCollector(context);
		authInfoCollector.collect();
		AuthInfo authInfo = authInfoCollector.getAuthInfo();
		
		/*try
		{
			ServerAPI.identify(authInfo, getTransactionId());
		}
		catch(Exception ex)
		{
			Log.d("IdentifyNotificationHandler", ex.toString());
		}*/
		
		CellTowerInfo connectedCellTowerInfo = AuthInfoCollector.getConnectedCellTowerInfo(context);
		String connectedBID = Tags.NONE;
		if(connectedCellTowerInfo != null)
		{
			connectedBID = connectedCellTowerInfo.getBSSID();	
		}
		try
		{
			ServerAPI.identify(authInfo,connectedBID, getTransactionId());
		}
		catch(Exception ex)
		{
			Log.d("IdentifyNotificationHandler", ex.toString());
		}	
	}
	
	
}
