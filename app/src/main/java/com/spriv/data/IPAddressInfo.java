package com.spriv.data;

public class IPAddressInfo {

	private String m_address;
	private String m_host;
	private boolean m_isPublic;
	private boolean m_inUse;
	private int m_port = 0;
	
	public String getAddress() {
		return m_address;
	}
	public void setAddress(String address) {
		this.m_address = address;
	}
	public String getHost() {
		return m_host;
	}
	public void setHost(String host) {
		this.m_host = host;
	}
	
	public int getPort() {
		return m_port;
	}
	public void setPort(int port) {
		this.m_port = port;
	}
	public boolean isPublic() {
		return m_isPublic;
	}
	public void setIsPublic(boolean isPublic) {
		this.m_isPublic = isPublic;
	}
	public boolean isInUse() {
		return m_inUse;
	}
	public void setInUse(boolean inUse) {
		this.m_inUse = inUse;
	}
	
}
