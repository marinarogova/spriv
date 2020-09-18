package com.spriv.data;

import android.os.Parcel;
import android.os.Parcelable;

public class CheckVerificationResultInfo implements Parcelable {

	private String m_question;
	private String m_company;
	private String m_endUsername;
	private long m_date;
	
	public CheckVerificationResultInfo()
	{
		
	}
	
    public CheckVerificationResultInfo(Parcel in){
    	String[] data = new String[4];
        in.readStringArray(data);
        this.m_company = data[0];
        this.m_endUsername = data[1];
        this.m_question = data[2];
        if(data[3] != null && !data[3].equals("") )
        {
        	this.m_date = Long.parseLong(data[3]);
        }
    }
    
    public String getCompany() {
		return m_company;
	}
	public void setCompany(String m_company) {
		this.m_company = m_company;
	}
	
	public String getEndUsername() {
		return m_endUsername;
	}
	public void setEndUsername(String m_endUsername) {
		this.m_endUsername = m_endUsername;
	}

	public long getDate() {
		return m_date;
	}
	public void setDate(long m_date) {
		this.m_date = m_date;
	}
	
	public String getQuestion() {
		return m_question;
	}
	public void setQuestion(String question) {
		this.m_question = question;
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
                this.m_endUsername,
                this.m_question,
                Long.valueOf(this.m_date).toString()});}
	
	
	
	public static final Parcelable.Creator<CheckVerificationResultInfo> CREATOR = new Parcelable.Creator<CheckVerificationResultInfo>() 
			{
        		public CheckVerificationResultInfo createFromParcel(Parcel in) 
        		{
        				return new CheckVerificationResultInfo(in); 
        		}

        public CheckVerificationResultInfo[] newArray(int size) {
            return new CheckVerificationResultInfo[size];
        }
    };
	
	
}
