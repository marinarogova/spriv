package com.spriv.utils;

import com.spriv.SprivApp;

import android.graphics.Typeface;

public class FontsManager 
{
	private static FontsManager instance;
	private static Typeface normalFont;
	private static Typeface boldFont;
	
	private FontsManager()
	{
		normalFont = Typeface.createFromAsset(SprivApp.getAppContext().getAssets(),"fonts/Roboto_Thin.ttf");
		boldFont = Typeface.createFromAsset(SprivApp.getAppContext().getAssets(),"fonts/Roboto_Regular.ttf");
	}
	
	public static FontsManager getInstance()
	{
		if(instance == null)
		{
			instance = new FontsManager();
		}
		return instance;
	}
	
	public Typeface getBoldFont()
	{
		return boldFont;
	}
	
	public Typeface getNormalFont()
	{
		return normalFont;
	}
}

