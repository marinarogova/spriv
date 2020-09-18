package com.spriv.data;

public class DeviceInfo {

	private String m_oSVersion;
	private String m_appVersion;
	private String m_oSName;
	private String m_oSModified;
	private String fingerprint;
	private String manufacturer;
	
	public String getOSVersion() {
		return m_oSVersion;
	}
	public void setOSVersion(String osVersion) {
		this.m_oSVersion = osVersion;
	}
	public String getOSName() {
		return m_oSName;
	}
	public void setOSName(String osName) {
		this.m_oSName = osName;
	}
	public String getOSModified() {
		return m_oSModified;
	}
	public void setOSModified(String osModified) {
		this.m_oSModified = osModified;
	}
	public String getFingerprint() {
		return fingerprint;
	}
	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public void setAppVersion(String appVersion) {
		m_appVersion = appVersion;
	}
	
	public String getAppVersion() {
		return m_appVersion;
	}
	
}
