package com.spriv.activity;

import android.content.Context;
import android.content.Intent;

import com.spriv.model.AccountsModel;

public class ActivityNavigator {

	public static void navigateToWelcome(Context context)
	{
		Intent welcomeIntent = new Intent(context, WelcomeActivity.class);
		welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(welcomeIntent);
	}
	
	public static void navigateToMain(Context context)
	{
		/*if(AccountsModel.getInstance().hasAccounts())
		{
			navigateToAccountList(context);
		}
		else
		{
			navigateToAddAccount(context);
		}*/
		navigateToAccountList(context);
	}
	
	public static void navigateToAddAccount(Context context)
	{
		Intent addAccountIntent = new Intent(context, AddAccountActivity.class);
		addAccountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		addAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(addAccountIntent);
	}
	
	public static void navigateToAddAccount(Context context, String accountPairingCode)
	{
		Intent addAccountIntent = new Intent(context, AddAccountActivity.class);
		addAccountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		addAccountIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		addAccountIntent.putExtra(AddAccountActivity.PAIRING_CODE_PARAM, accountPairingCode);
		context.startActivity(addAccountIntent);
	}
	
	public static void navigateToAccountList(Context context)
	{
		Intent accountsIntent = new Intent(context, AccountsActivity.class);
		accountsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		accountsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(accountsIntent);
	}

	
}
