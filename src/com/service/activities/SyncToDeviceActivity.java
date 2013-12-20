package com.service.activities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI.Entry;
import com.service.asynctask.GetDpFolderListAsyncTask;
import com.service.asynctask.SyncToDeviceAsyncTask;
import com.service.extend.FolderListAdapter;
import com.service.research.R;

public class SyncToDeviceActivity
	extends Activity
		implements OnClickListener, OnItemClickListener{
	
	private Button btn_sync_to_device;
	
	private ListView dp_list;
	
	private HashMap<String, List<Entry>> db_all_folders_list;
	
	private ProgressDialog mDialog;
	
	private FileOutputStream mFos;
	
	private FolderListAdapter adapter;
	
	private boolean clicked = false;
	
	private String selected_folder_name = "";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView( R.layout.sync_to_phone_screen );
		
		this.dp_list = (ListView) this.findViewById( R.id.listView2 );
		
		this.dp_list.setOnItemClickListener( this );
		
		this.btn_sync_to_device = (Button) this.findViewById( R.id.btn_sync_to_device );
		
		this.btn_sync_to_device.setOnClickListener( this );
		
		this.mDialog = new ProgressDialog( this );
		this.mDialog.setMessage( this.getString( R.string.get_dropbox_list ) );
		this.mDialog.setButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // This will cancel the getThumbnail operation by closing
                // its stream
                if (mFos != null) {
                    try {
                        mFos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });

        mDialog.show();
		
		GetDpFolderListAsyncTask getfolderslist = new GetDpFolderListAsyncTask( this , HomeScreen.mApi, this.mDialog );
		getfolderslist.execute();
		
	}
	
	public void getDPFoldersSucceeded( HashMap<String, List<Entry>> directories ){
		this.db_all_folders_list = directories;
		
		ArrayList<String> db_folders_list = new ArrayList<String>();
		if ( directories.get( "root" ) != null ){
			int i = 0;
			for ( Entry temp : directories.get( "root" ) ){
				if ( temp.isDir )
					db_folders_list.add( i++, temp.fileName() );
				else
					db_folders_list.add( temp.fileName() );
			}
		}else{
			if ( directories.get( this.selected_folder_name ) != null ){
				int i = 0;
				for ( Entry temp : directories.get( this.selected_folder_name ) ){
					if ( temp.isDir )
						db_folders_list.add( i++, temp.fileName() );
					else
						db_folders_list.add( temp.fileName() );
				}
			}
		}
		this.adapter = new FolderListAdapter( this, db_folders_list );
        this.adapter.notifyDataSetChanged();
		this.dp_list.setAdapter( this.adapter );
		
		this.mDialog.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		switch ( v.getId() ){
			case R.id.btn_sync_to_device:{
				Set<String> folders;
				folders = ( (FolderListAdapter)this.dp_list.getAdapter() ).getChecked();
				
				this.SyncToDevice( folders );
				mDialog = new ProgressDialog( this );
		        mDialog.setMessage( this.getString( R.string.download_file ) );
		        mDialog.show();
//				if (mFos != null) {
//                    try {
//                        mFos.close();
//                    } catch (IOException e) {
//                    }
//                }
				
			}
			break;
		}
	}

	private void SyncToDevice(Set<String> folders){
		if ( this.clicked ){
			folders.clear();
			folders.add( this.selected_folder_name );
			new SyncToDeviceAsyncTask( this, HomeScreen.mApi, this.db_all_folders_list, folders ).execute();
		} else {
			new SyncToDeviceAsyncTask( this, HomeScreen.mApi, this.db_all_folders_list, folders ).execute();
		}
	}

	public void SyncToDeviceAsyncTaskSucceed(){
		Toast.makeText( this, "succeeded", Toast.LENGTH_SHORT ).show();
		this.mDialog.dismiss();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		if ( !this.clicked ){
//			if ( !Environment.getExternalStoragePublicDirectory( this.folders_list.getItemAtPosition( position ).toString() ).isFile() )
				this.selected_folder_name = ( (TextView)view.findViewById( R.id.folder_name )).getText().toString();
			GetDpFolderListAsyncTask getfolderslist = new GetDpFolderListAsyncTask( this , HomeScreen.mApi, this.mDialog, selected_folder_name );
			getfolderslist.execute();
		}
		else{
			String temp = this.selected_folder_name + "/" + ( (TextView)view.findViewById( R.id.folder_name )).getText().toString();
			this.selected_folder_name += "/" + ( (TextView)view.findViewById( R.id.folder_name )).getText().toString();
			GetDpFolderListAsyncTask getfolderslist = new GetDpFolderListAsyncTask( this , HomeScreen.mApi, this.mDialog, temp );
			getfolderslist.execute();
//			if ( !Environment.getExternalStoragePublicDirectory( temp ).isFile() )
//				this.selected_folder_name += "/" + this.folders_list.getItemAtPosition( position ).toString();
		}
//
//		this.exploredDir.add( this.selected_folder_name );
		
//		ArrayList<String> folders = new ArrayList<String>();
		
//		if ( Environment.getExternalStoragePublicDirectory( this.selected_folder_name )
//				.listFiles() != null ){
//			for ( File f : Environment.getExternalStoragePublicDirectory( this.selected_folder_name )
//					.listFiles() )
//			{
//				folders.add( f.getName() );
//			}
			
//			this.adapter = new FolderListAdapter( this, folders );
	
//	        this.adapter.notifyDataSetChanged();
//			this.dp_list.setAdapter( adapter );
			this.clicked = true;
//		}
	}
	
}
