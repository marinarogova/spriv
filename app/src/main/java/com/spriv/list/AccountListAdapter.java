package com.spriv.list;

import java.util.List;

import com.spriv.R;
import com.spriv.data.SprivAccount;
import com.spriv.utils.FontsManager;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountListAdapter extends BaseAdapter {

	private LayoutInflater m_Inflater;
	private List<SprivAccount> m_accounts;
	private boolean m_editMode = false;
	private AccountListListener m_listener;
	private Typeface _normalFont;
	
	public AccountListAdapter(List<SprivAccount> accounts, Context context, AccountListListener listener)
	{
		m_accounts = accounts;
		m_Inflater = LayoutInflater.from(context);
		m_listener = listener;
		_normalFont = FontsManager.getInstance().getNormalFont();
	}
	
	public void addAccount(SprivAccount account) 
	{
		m_accounts.add(account);
		 notifyDataSetChanged();
	}
	
	public void removeAccountAt(int position) 
	{
		m_accounts.remove(position);
		 notifyDataSetChanged();
	}
	
	public void removeAccount(SprivAccount account) 
	{
		m_accounts.remove(account);
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
		return m_accounts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_accounts.get(position);
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
		  SprivAccount account = m_accounts.get(position);
		  //ChatHeader chatHeader = (ChatHeader)getItem(position);
		  if (convertView == null) 
		  {
			   convertView = m_Inflater.inflate(R.layout.account_list_item, null);
			   holder = createViewHolder(convertView);
			   convertView.setTag(holder);
			   holder.account_remove_image.setOnClickListener(OnRemoveClickListener);	   
			   holder.account_totp_button.setOnClickListener(OnTotpClickListener);
			//   holder.account_name_text.setOnClickListener(OnNameClickListener);
			   holder.account_name_text.setTypeface(_normalFont);
		  }
		  else 
		  {
			  holder = (ViewHolder) convertView.getTag();
		  }
		  holder.account_remove_image.setTag(Integer.valueOf(position));
		  holder.account_totp_button.setTag(Integer.valueOf(position));
		  holder.account_name_text.setTag(Integer.valueOf(position));
		  holder.account_name_text.setText(account.getId().getUserName());
		  if(m_editMode)
		  {
			  holder.account_totp_button.setVisibility(View.GONE);
			  holder.account_remove_image.setVisibility(View.VISIBLE);
		  }
		  else
		  {
			  holder.account_totp_button.setVisibility(View.VISIBLE);
			  holder.account_remove_image.setVisibility(View.GONE);
		  }
		  return convertView;
	}

	private ViewHolder createViewHolder(View convertView) {
		ViewHolder holder;
		holder = new ViewHolder();
		   holder.view = convertView;
		   holder.account_name_text = (TextView) convertView.findViewById(R.id.account_name_text);
		   holder.account_totp_button =  convertView.findViewById(R.id.account_totp_button);
		   holder.account_remove_image = convertView.findViewById(R.id.account_remove_image);
		return holder;
	}
	
	static class ViewHolder {
		  View view;
		  TextView account_name_text;
		  View account_totp_button;
		  View account_remove_image;
		 }
	public OnClickListener OnRemoveClickListener = new OnClickListener() {
	   
		public void onClick(View v) 
	    {
			int position = ((Integer)v.getTag()).intValue();
			m_listener.onRemoveAccountClick(m_accounts.get(position));
	    }
	};
	
	public OnClickListener OnTotpClickListener = new OnClickListener() {
	    public void onClick(View v) 
	    {
	    	int position = ((Integer)v.getTag()).intValue();
	    	m_listener.onTOTPClick(m_accounts.get(position));
	    }

		
	};
	
	public OnClickListener OnNameClickListener = new OnClickListener() {
	    public void onClick(View v) 
	    {
	    	int position = ((Integer)v.getTag()).intValue();
	    	m_listener.onAccountClick(m_accounts.get(position));
	    }

		
	};
	
	public boolean isEditMode() {
		
		return m_editMode;
	}
	
 }
	
