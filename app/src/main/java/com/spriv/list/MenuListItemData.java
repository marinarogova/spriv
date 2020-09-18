package com.spriv.list;

public class MenuListItemData {

	private String m_text;
	private int m_icon;
	
	public MenuListItemData(String text, int icon)
	{
		setText(text);
		setIicon(icon);
	}

	public String getText() {
		return m_text;
	}

	public void setText(String m_text) {
		this.m_text = m_text;
	}

	public int getIcon() {
		return m_icon;
	}

	public void setIicon(int m_icon) {
		this.m_icon = m_icon;
	}
}
