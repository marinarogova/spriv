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

public class MenuListAdapter extends BaseAdapter {

	
	private LayoutInflater m_Inflater;
	private List<MenuListItemData> m_items;
	private Typeface _normalFont;
	
	public MenuListAdapter(List<MenuListItemData> items, Context context)
	{
		m_items = items;
		m_Inflater = LayoutInflater.from(context);
		_normalFont = FontsManager.getInstance().getNormalFont();
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		  ViewHolder holder;
		  MenuListItemData menuListItemData = m_items.get(position);
		  //ChatHeader chatHeader = (ChatHeader)getItem(position);
		  if (convertView == null) 
		  {
			   convertView = m_Inflater.inflate(R.layout.drawer_list_item, null);
			   holder = createViewHolder(convertView);
			   convertView.setTag(holder);
			   holder.text.setTypeface(_normalFont);
		  } 
		  else 
		  {
			  holder = (ViewHolder) convertView.getTag();
		  }
		  holder.text.setText(menuListItemData.getText());
		  holder.icon.setImageResource(menuListItemData.getIcon());
		  return convertView;
	}

	private ViewHolder createViewHolder(View convertView) {
		ViewHolder holder;
		holder = new ViewHolder();
		   holder.view = convertView;
		   holder.text = (TextView) convertView.findViewById(R.id.menu_text);
		   holder.icon =  (ImageView)convertView.findViewById(R.id.menu_image);
		return holder;
	}
	
	static class ViewHolder {
		  View view;
		  TextView text;
		  ImageView icon;
		 }
	
 }
	
