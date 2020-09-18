package com.spriv.data;

public class CellTowerInfo {

	private boolean m_inUse = false;
	private String m_bSSID;
	private String m_sSID;
	private int m_sS;
	
	public boolean isInUse() {
		return m_inUse;
	}
	public void setInUse(boolean m_inUse) {
		this.m_inUse = m_inUse;
	}
	public String getBSSID() {
		return m_bSSID;
	}
	public void setBSSID(String BSSID) {
		this.m_bSSID = BSSID;
	}
	public String getSSID() {
		return m_sSID;
	}
	public void setSSID(String SSID) {
		this.m_sSID = SSID;
	}
	public int getSS() {
		return m_sS;
	}
	public void setSS(int ss) {
		this.m_sS = ss;
	}
}
