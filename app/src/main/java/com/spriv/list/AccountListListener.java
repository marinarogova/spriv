package com.spriv.list;

import com.spriv.data.SprivAccount;

public interface AccountListListener 
{
	void onRemoveAccountClick(SprivAccount sprivAccount);
	void onTOTPClick(SprivAccount sprivAccount);
	void onAccountClick(SprivAccount sprivAccount);
}
