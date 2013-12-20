package com.service.asynctask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.service.activities.SyncToDeviceActivity;

public class GetDpFolderListAsyncTask 
	extends AsyncTask<Void, Void, HashMap<String, List<Entry>>>{
	
	private DropboxAPI<?> mApi;
	
	private Context context;
	
	private String childPath = "";
	
	private HashMap<String, List<Entry>> dpDirectories = new HashMap<String, List<Entry>>();
	
	ProgressDialog dialog;
	
	public GetDpFolderListAsyncTask( Context context, DropboxAPI<?> mApi, ProgressDialog dialog){
		this.context = context;
		this.mApi = mApi;
		this.dialog = dialog;
	}
	
	public GetDpFolderListAsyncTask( Context context, DropboxAPI<?> mApi, ProgressDialog dialog, String childPath){
		this.context = context;
		this.mApi = mApi;
		this.childPath = childPath;
		this.dialog = dialog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.dialog.show();
	}
	
	@Override
	protected HashMap<String, List<Entry>> doInBackground(Void... params) {
		
		Entry metadata_entry;
		List<Entry> root_entries = new ArrayList<DropboxAPI.Entry>();
		try {
			if ( this.childPath.equals( "" ) ){
				metadata_entry = this.mApi.metadata( "/", 100, null, true, null );
				root_entries = metadata_entry.contents;
				this.dpDirectories.put( "root", root_entries );
			}else{
				metadata_entry = this.mApi.metadata( "/" + this.childPath + "/", 100, null, true, null );
				root_entries = metadata_entry.contents;
				this.dpDirectories.put( this.childPath, root_entries );
			}
		}catch (DropboxException e) {
			e.printStackTrace();
		}
		
		
//		recursivelyDropBoxFolderSearch( root_entries );
		return this.dpDirectories;
	}
	
//	private void recursivelyDropBoxFolderSearch( List<Entry> root_entries ){
//		for ( Entry temp : root_entries ){
//			if ( temp.isDir ){
//				Entry lvl1_entry;
//				List<Entry> lvl1_entries = new ArrayList<DropboxAPI.Entry>();
//				try {
//					lvl1_entry = this.mApi.metadata( temp.path + "/", 100, null, true, null );
//					lvl1_entries = lvl1_entry.contents;
//					this.dpDirectories.put( temp.fileName(), lvl1_entries );
//					recursivelyDropBoxFolderSearch( lvl1_entries );
//				} catch (DropboxException e) {
//					e.printStackTrace();
//				}
//			}
//			else{
//				Entry lvl1_entry;
//				List<Entry> lvl1_entries = new ArrayList<DropboxAPI.Entry>();
//				try {
//					lvl1_entry = this.mApi.metadata( temp.path + "/", 100, null, true, null );
//					lvl1_entries = lvl1_entry.contents;
//					this.dpDirectories.put( temp.fileName(), lvl1_entries );
//				} catch (DropboxException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	
	@Override
	protected void onPostExecute(HashMap<String, List<Entry>> result) {
		super.onPostExecute(result);
		
		((SyncToDeviceActivity) this.context).getDPFoldersSucceeded( result );
	}

}
