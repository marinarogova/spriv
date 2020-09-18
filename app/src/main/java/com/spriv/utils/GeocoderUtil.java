package com.spriv.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;


public class GeocoderUtil {
	
	public static String getAddress(Context context, double lat, double lon)
	{
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
		    List<Address> listAddresses = geocoder.getFromLocation(lat, lon, 1);
		    if(null!=listAddresses&&listAddresses.size()>0)
		        	return listAddresses.get(0).getAddressLine(0);
		        else
		        	return null;
		    }
		catch (IOException e) {
			    e.printStackTrace();
			    return null;
			}
	}

}
