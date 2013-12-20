package com.service.asynctask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.service.activities.SyncToDeviceActivity;

public class SyncToDeviceAsyncTask extends AsyncTask<String, Void, Void> {
	
	private HashMap<String, List<Entry>> dp_folders_list;
	
	private Set<String> folders;
	
	private FileOutputStream mFos;
	
	private DropboxAPI<?> mApi;
	
	private Context context;
	
	private List<String> file_path = new ArrayList<String>();
	
	private List<String> folder_path = new ArrayList<String>();

	public SyncToDeviceAsyncTask(Context context, DropboxAPI<?> api, HashMap<String, List<Entry>> dp_folders_list, Set<String> folders){
		this.context = context;
		this.mApi = api;
		this.dp_folders_list = dp_folders_list;
		this.folders = folders;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		for ( String s_temp : folders ){
			int times = 1;
			for ( int i = 0; i < this.dp_folders_list.size(); i++ ){
				if ( this.dp_folders_list.containsKey( s_temp ) && !( times > folders.size() ) ){
					for ( int n = 0; n < this.dp_folders_list.get( s_temp ).size(); n++ ){
						this.recursivelyDropBoxFolderSearch( this.dp_folders_list.get( s_temp ) );
					}
					times++;
				}
				else if ( this.dp_folders_list.containsKey( "root" ) ){
					this.recursivelyDropBoxFolderSearch( this.dp_folders_list.get( "root" ) );
				}
				else
					break;
			} 
		}
		try{
			for ( int i = 0; i < file_path.size(); i++ ){
				File file = new File(Environment.getExternalStorageDirectory() 
						+ folder_path.get( i ) );
				if(!file.exists())
	            {
	            	if(file.mkdirs()){
            			if(file.exists()) {
            				mFos = new FileOutputStream( Environment.getExternalStorageDirectory()
	                										+ "/" + file_path.get( i ) );
            				mApi.getFile( file_path.get( i ), null, mFos, null );
            			}
	            	}
	            }
				else{
					mFos = new FileOutputStream( Environment.getExternalStorageDirectory()
							+ "/" + file_path.get( i ) );
					mApi.getFile( file_path.get( i ), null, mFos, null );
				}
					
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private void recursivelyDropBoxFolderSearch( List<Entry> root_entries ){
		for ( Entry temp : root_entries ){
			if ( temp.isDir ){
				Entry lvl1_entry;
				List<Entry> lvl1_entries = new ArrayList<DropboxAPI.Entry>();
				try {
					lvl1_entry = this.mApi.metadata( temp.path + "/", 100, null, true, null );
					lvl1_entries = lvl1_entry.contents;
					recursivelyDropBoxFolderSearch( lvl1_entries );
				} catch (DropboxException e) {
					e.printStackTrace();
				}
			}
			else{
				folder_path.add( temp.parentPath() );
				file_path.add( temp.path );
			}
		}
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		( (SyncToDeviceActivity)this.context ).SyncToDeviceAsyncTaskSucceed();
	}

}
