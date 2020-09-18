package com.spriv.list;

import java.util.Date;
import java.util.List;

import com.spriv.R;
import com.spriv.data.SprivAccount;
import com.spriv.data.SprivLogin;
import com.spriv.utils.FontsManager;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginstListAdapter extends BaseAdapter {

	private LayoutInflater m_Inflater;
	private List<SprivLogin> m_logins;
	private boolean m_editMode = false;
	private LoginsListListener m_listener;
	private Typeface _normalFont;
	
	public LoginstListAdapter(List<SprivLogin> logins, Context context, LoginsListListener listener)
	{
		m_logins = logins;
		m_Inflater = LayoutInflater.from(context);
		m_listener = listener;
		_normalFont = FontsManager.getInstance().getNormalFont();
	}
	
	public void addLogin(SprivLogin login) 
	{
		m_logins.add(login);
		 notifyDataSetChanged();
	}
	
	public void removeLoginAt(int position) 
	{
		m_logins.remove(position);
		 notifyDataSetChanged();
	}
	
	public void removeLogin(SprivLogin login) 
	{
		m_logins.remove(login);
		 notifyDataSetChanged();
	}
	
	public void setEditMode(boolean editMode)
	{
		m_editMode = editMode;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_logins.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_logins.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		
		  final ViewHolder holder;
		  SprivLogin login = m_logins.get(position);
		  //ChatHeader chatHeader = (ChatHeader)getItem(position);
		  if (convertView == null) 
		  {
			   convertView = m_Inflater.inflate(R.layout.login_list_item, null);
			   holder = createViewHolder(convertView);
			   convertView.setTag(holder);
			   holder.login_remove_image.setOnClickListener(OnRemoveClickListener);	   
			   holder.service_text.setTypeface(_normalFont);
			   holder.address_text.setTypeface(_normalFont);
			   holder.time_text.setTypeface(_normalFont);
		  }
		  else 
		  {
			  holder = (ViewHolder) convertView.getTag();
		  }
		  holder.login_remove_image.setTag(Integer.valueOf(position));
		  holder.service_text.setText(login.getCheckLoginResultInfo().getService());
		  String address = login.getAddress();
		  if(address == null || "".equals(address))
		  {
			  address = login.getCheckLoginResultInfo().getIPAddress();
		  }
		  holder.address_text.setText(address);
		  holder.time_text.setText(DateFormat.format("EEE MMM dd HH:mm", new Date(login.getCheckLoginResultInfo().getDate())));
		  if(m_editMode)
		  {
			  holder.login_remove_image.setVisibility(View.VISIBLE);
		  }
		  else
		  {
			  holder.login_remove_image.setVisibility(View.GONE);
		  }
		  return convertView;
	}

	private ViewHolder createViewHolder(View convertView) {
		ViewHolder holder;
		holder = new ViewHolder();
		   holder.view = convertView;
		   holder.address_text = (TextView) convertView.findViewById(R.id.address_text);
		   holder.time_text =  (TextView) convertView.findViewById(R.id.time_text);
		   holder.service_text = (TextView) convertView.findViewById(R.id.service_name_text);
		   holder.login_remove_image = (ImageView) convertView.findViewById(R.id.login_remove_image);
		return holder;
	}
	
	static class ViewHolder {
		  View view;
		  TextView address_text;
		  TextView time_text;
		  TextView service_text;
		  ImageView login_remove_image;
		 }
	public OnClickListener OnRemoveClickListener = new OnClickListener() {
	   
		public void onClick(View v) 
	    {
			int position = ((Integer)v.getTag()).intValue();
			m_listener.onRemoveLoginClick(m_logins.get(position));
	    }
	};
	
	
	public boolean isEditMode() {
		
		return m_editMode;
	}
	
 }
	
