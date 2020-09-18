package com.spriv.task;

import com.spriv.data.SprivAccount;

public interface UpdatePairTaskHandler {

	public void onUpdatePairPerformed(SprivAccount sprivAccount);
	public void onUpdatePairException(Exception exception, String key);
}
