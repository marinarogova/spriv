package com.spriv.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SprivAccountId implements Parcelable{

	private final String m_userName;
	private final String m_company;
	
	public SprivAccountId(String company, String userName)
	{
		m_userName = userName;
		m_company = company;
	}
	
	public SprivAccountId(Parcel in){
    	String[] data = new String[2];
        in.readStringArray(data);
        this.m_company = data[0];
        this.m_userName = data[1];
    }
	
	public String getUserName() {
		return m_userName;
	}
	
	public String getCompany() {
		return m_company;
	}
	
	@Override
    public int hashCode() {
        return m_company.hashCode() + m_userName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof SprivAccountId))
            return false;
        if (obj == this)
            return true;
        SprivAccountId sAI = (SprivAccountId) obj;
        return sAI.m_company.equals(m_company) && sAI.m_userName.equals(m_userName);
    }

    @Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] 
				{
				this.m_company,
                this.m_userName});}
	
	
	
	public static final Parcelable.Creator<SprivAccountId> CREATOR = new Parcelable.Creator<SprivAccountId>() 
			{
        		public SprivAccountId createFromParcel(Parcel in) 
        		{
        				return new SprivAccountId(in); 
        		}

        public SprivAccountId[] newArray(int size) {
            return new SprivAccountId[size];
        }
    };
}
