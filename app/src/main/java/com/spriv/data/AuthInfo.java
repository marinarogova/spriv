package com.spriv.data;

public class AuthInfo {
	private DeviceInfo m_device;
	private CellTowerInfo m_cellTower;




	public DeviceInfo getDevice() {
		return m_device;
	}

	public void setDevice(DeviceInfo m_device) {
		this.m_device = m_device;
	}
	
	public CellTowerInfo getCellTower()
	{
		return m_cellTower;
	}
	
	public void setCellTower(CellTowerInfo cellTower)
	{
		m_cellTower = cellTower;
	}
	
	public String GetConnectedWifiName()
	{
		String name = null;
		if(m_cellTower != null && m_cellTower.isInUse())
		{
			name = m_cellTower.getSSID();
		}
		return name;
	}
	
}
