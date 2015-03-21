package com.polymtl.shoppingsolver.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.polymtl.shoppingsolver.R;

public class DrawerItemAdapter extends ArrayAdapter<NavDrawerItem> {
	Context mContext;
	int layoutResourceId;
	NavDrawerItem data[] = null;
	
	public DrawerItemAdapter(Context mContext, int layoutResourceId, NavDrawerItem[] data){
		super(mContext, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View listItem = convertView;
		
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        listItem = inflater.inflate(layoutResourceId, parent, false);
 
        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);
        
        NavDrawerItem folder = data[position];
 
        
        imageViewIcon.setImageResource(folder.icon);
        textViewName.setText(folder.name);
        
        return listItem;
	}

}
