package com.service.extend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.service.libs.ViewHolder;
import com.service.research.R;

public class FolderListAdapter 
	extends BaseAdapter{

	private Context context;
	
	private ArrayList<String> folders_list;
	
	private Set<String> checked = new HashSet<String>();

	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Boolean> checkbox_status_list = new HashMap<Integer, Boolean>(); 
	
	public boolean unchecked;
	
	public FolderListAdapter( Context context, ArrayList<String> folders_list ){
		this.context = context;
		this.folders_list = folders_list;
		for ( int i = 0; i < this.folders_list.size(); i++ ){
			checkbox_status_list.put( i , false );
		}
	}
	
	@Override
	public int getCount() {
		return this.folders_list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.folders_list.get( position );
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		LayoutInflater layoutinflater= ( (Activity) context).getLayoutInflater();
		if( convertView == null )
		{  
			viewHolder = new ViewHolder();
			convertView = layoutinflater.inflate( R.layout.folder_list_item, null );  
			viewHolder.folder_name = (TextView) convertView.findViewById( R.id.folder_name );
			viewHolder.checked_folder = (CheckBox) convertView.findViewById( R.id.checked_folder );
			viewHolder.checked_folder.setOnCheckedChangeListener( new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int position = (Integer) buttonView.getTag();
					if ( isChecked ){
						checked.add( folders_list.get( position ).toString() );
					}
					else
					{
						checked.remove( buttonView.getTag().toString() );
					}  
					checkbox_status_list.put( position, isChecked );
				}
			});
			convertView.setTag(viewHolder);
			convertView.setTag( R.id.folder_name, viewHolder.folder_name );
			convertView.setTag( R.id.checked_folder, viewHolder.checked_folder );
		}  
		else{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		viewHolder.checked_folder.setTag( position );
		viewHolder.folder_name.setText( this.folders_list.get( position ) );
		viewHolder.checked_folder.setChecked( checkbox_status_list.get( position ) );
		
//		viewHolder.checked_folder.setTag( position );
		
		return convertView;
	}
	
	public Set<String> getChecked()
    {
        return checked;
    }
	
	public boolean getType(){
		
		return false;
	}

}
