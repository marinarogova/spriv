package com.spriv.data;

import java.util.ArrayList;
import java.util.List;

public class SprivAccount {

	private SprivAccountId m_id;
	private String m_tOTPSecret;
	private List<SprivLogin> m_logins = new ArrayList<SprivLogin>();
	
	public SprivAccount()
	{
		
	}
	
	public SprivAccount(String userName, String company, String tOTPSecret)
	{
		this.m_id = new SprivAccountId(company, userName);
		this.m_tOTPSecret = tOTPSecret;
	}

	public SprivAccountId getId() {
		return m_id;
	}
	
	public void setId(SprivAccountId id) {
		this.m_id = id;
	}
	
	public String getTOTPSecret() {
		return m_tOTPSecret;
	}
	
	public void setTOTPSecret(String tOTPSecret) {
		m_tOTPSecret = tOTPSecret;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return m_id.getUserName() + "@" + m_id.getCompany();
	}
	
	public List<SprivLogin> getLogins()
	{
		return m_logins;
	}
}
