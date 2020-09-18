package com.spriv.data;

public class SprivLogin {

	private CheckLoginResultInfo checkLoginResultInfo;
	private String address;
	private String transactionId;
	
	public SprivLogin(CheckLoginResultInfo checkLoginResultInfo, String address, String transactionId)
	{
		this.checkLoginResultInfo = checkLoginResultInfo;
		this.setAddress(address);
		this.setTransactionId(transactionId);
	}

	public CheckLoginResultInfo getCheckLoginResultInfo() {
		return checkLoginResultInfo;
	}

	public void setCheckLoginResultInfo(CheckLoginResultInfo checkLoginResultInfo) {
		this.checkLoginResultInfo = checkLoginResultInfo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
}
