package com.spriv.data;

public class LocationInfo {
	
	private boolean m_hasAccuracy;
	private float m_accuracy;
	private double m_latitude;
	private double m_longitude;
	
	private boolean m_hasAltitude;
	private double m_altitude;
	
	private float m_elevation;
	private float m_azimuth;
	
	public float getAccuracy() {
		return m_accuracy;
	}
	
	public void setAccuracy(float accuracy) {
		this.m_accuracy = accuracy;
	}
	
	public double getLatitude() {
		return m_latitude;
	}
	
	public void setLatitude(double latitude) {
		this.m_latitude = latitude;
	}

	public double getLongitude() {
		return m_longitude;
	}

	public void setLongitude(double longitude) {
		this.m_longitude = longitude;
	}

	public double getAltitude() {
		return m_altitude;
	}

	public void setAltitude(double altitude) {
		this.m_altitude = altitude;
	}
	
	public boolean hasAltitude() {
		return this.m_hasAltitude;
	}
	
	public void setHasAltitude(boolean hasAltitude) {
		this.m_hasAltitude = hasAltitude;
	}
	
	public boolean hasAccuracy() {
		return this.m_hasAccuracy;
	}
	
	public void setHasAccuracy(boolean hasAccuracy) {
		this.m_hasAccuracy = hasAccuracy;
	}

	public float getElevation() {
		return m_elevation;
	}

	public void setElevation(float elevation) {
		this.m_elevation = elevation;
	}

	public float getAzimuth() {
		return m_azimuth;
	}

	public void setAzimuth(float azimuth) {
		this.m_azimuth = azimuth;
	}
	
}
