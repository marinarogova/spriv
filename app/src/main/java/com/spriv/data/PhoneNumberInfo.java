package com.spriv.data;

public class PhoneNumberInfo {

	private boolean m_inUse = false;
	private int m_number;
	
	public boolean isInUse() {
		return m_inUse;
	}
	public void setInUse(boolean m_inUse) {
		this.m_inUse = m_inUse;
	}
	public int getNumber() {
		return m_number;
	}
	public void setNumber(int number) {
		this.m_number = number;
	}
}
