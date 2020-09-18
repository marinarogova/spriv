package com.spriv.receiver;

import org.json.JSONException;

import com.spriv.json.Tags;

import android.content.Intent;
import android.os.Bundle;

public class PushNotificationParser {
	public static PushNotificationHandler toPushNotification(Intent intent) throws JSONException {
			
			//Defensive... 
			if(intent == null)
			{
				return null;
			}
			Bundle bundle = intent.getExtras();
			//Defensive...
			if(bundle == null)
			{
				return null;
			}
	    	String notificationTypeStr = bundle.getString(Tags.Type);
	    	String transactionId = bundle.getString(Tags.Id);
			PushNotificationHandler pushNotificationHandler = toPushNotification(notificationTypeStr,transactionId);
			return pushNotificationHandler;
		}


	public static PushNotificationHandler toPushNotification(String notificationTypeStr,String transactionId) throws JSONException
	{
		PushNotificationHandler pushNotificationHandler = null;
		//JSONObject jsonObject = new JSONObject(jsonString);

		if(notificationTypeStr == null || "".equals(notificationTypeStr))
		{
			return null;
		}
		int notificationType = Integer.valueOf(notificationTypeStr);
		if(notificationType == PushNotificationType.IDENTIFY)
		{
			pushNotificationHandler = toIdentifyNotification(transactionId);
		}
		else if(notificationType == PushNotificationType.CHECK_LOGIN)
		{
			pushNotificationHandler = toCheckLoginNotification(transactionId);
		}
		else if(notificationType == PushNotificationType.CHECK_VERIFICATION)
		{
			pushNotificationHandler = toCheckVerificationNotification(transactionId);
		}
		pushNotificationHandler.setNotificationType(notificationType);
		return pushNotificationHandler;
	}

	private static IdentifyNotificationHandler toIdentifyNotification(String transactionId)
	{
		IdentifyNotificationHandler notifictionHandler = new IdentifyNotificationHandler();
		notifictionHandler.setTransactionId(transactionId);
		return notifictionHandler;
	}
	
	private static CheckLoginNotificationHandler toCheckLoginNotification(String transactionId)
	{
		CheckLoginNotificationHandler notifictionHandler = new CheckLoginNotificationHandler();
		notifictionHandler.setTransactionId(transactionId);
		return notifictionHandler;
	}
	
	private static CheckVerificationNotificationHandler toCheckVerificationNotification(String transactionId)
	{
		CheckVerificationNotificationHandler notifictionHandler = new CheckVerificationNotificationHandler();
		notifictionHandler.setTransactionId(transactionId);
		return notifictionHandler;
	}
}
