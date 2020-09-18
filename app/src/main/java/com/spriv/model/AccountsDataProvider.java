package com.spriv.model;

import com.spriv.data.SprivAccountId;

public interface AccountsDataProvider {

	String getSecret(SprivAccountId accountId);
	
}
