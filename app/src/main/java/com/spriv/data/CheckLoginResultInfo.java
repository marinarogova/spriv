package com.spriv.data;

import android.os.Parcel;
import android.os.Parcelable;

public class CheckLoginResultInfo implements Parcelable {

	private String m_company;
	private String m_service;
	private String m_endUsername;
	private String m_oS;
	private String m_browser;
	private String m_iPAddress;
	private long m_date;
	
	public CheckLoginResultInfo()
	{
		
	}
	
	
    public CheckLoginResultInfo(Parcel in){
        String[] data = new String[7];
        in.readStringArray(data);
        this.m_company = data[0];
        this.m_service = data[1];
        this.m_endUsername = data[2];
        this.m_oS = data[3];
        this.m_browser = data[4];
        this.m_iPAddress = data[5];
        if(data[6] != null && !data[6].equals("") )
        {
        	this.m_date = Long.parseLong(data[6]);
        }
    }
	
	public String getCompany() {
		return m_company;
	}
	public void setCompany(String m_company) {
		this.m_company = m_company;
	}
	
	public String getService() {
		return m_service!=null?m_service:"";
	}
	public void setService(String service) {
		this.m_service = service;
	}
	public String getEndUsername() {
		return m_endUsername;
	}
	public void setEndUsername(String m_endUsername) {
		this.m_endUsername = m_endUsername;
	}
	public String getOS() {
		return m_oS;
	}
	public void setOS(String os) {
		this.m_oS = os;
	}
	public String getBrowser() {
		return m_browser;
	}
	public void setBrowser(String browser) {
		this.m_browser = browser;
	}
	public String getIPAddress() {
		return m_iPAddress;
	}
	public void setIPAddress(String iPAddress) {
		this.m_iPAddress = iPAddress;
	}
	public long getDate() {
		return m_date;
	}
	public void setDate(long m_date) {
		this.m_date = m_date;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {
				this.m_company,
				this.m_service,
                this.m_endUsername,
                this.m_oS,
                this.m_browser,
                this.m_iPAddress,
                Long.valueOf(this.m_date).toString(),
                });
		
	}
	
	
	
	public static final Parcelable.Creator<CheckLoginResultInfo> CREATOR = new Parcelable.Creator<CheckLoginResultInfo>() 
			{
        		public CheckLoginResultInfo createFromParcel(Parcel in) 
        		{
        				return new CheckLoginResultInfo(in); 
        		}

        public CheckLoginResultInfo[] newArray(int size) {
            return new CheckLoginResultInfo[size];
        }
    };
}
