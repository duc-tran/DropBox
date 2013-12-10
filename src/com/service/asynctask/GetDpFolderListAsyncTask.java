package com.service.asynctask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.service.research.SyncToDeviceActivity;

public class GetDpFolderListAsyncTask 
	extends AsyncTask<Void, Void, HashMap<String, List<Entry>>>{
	
	private DropboxAPI<?> mApi;
	
	private Context context;
	
	private HashMap<String, List<Entry>> dpDirectories = new HashMap<String, List<Entry>>();
	
	public GetDpFolderListAsyncTask( Context context, DropboxAPI<?> mApi){
		this.context = context;
		
		this.mApi = mApi;
	}

	@Override
	protected HashMap<String, List<Entry>> doInBackground(Void... params) {
		
		Entry metadata_entry;
		List<Entry> root_entries = new ArrayList<DropboxAPI.Entry>();
		
		try {
			metadata_entry = this.mApi.metadata( "/", 100, null, true, null );
			root_entries = metadata_entry.contents;
			dpDirectories.put( "root", root_entries );
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		
		recursivelyFolderSearch( root_entries );
		return dpDirectories;
	}
	
	private void recursivelyFolderSearch( List<Entry> root_entries ){
		for ( Entry temp : root_entries ){
			if ( temp.isDir ){
				Entry lvl1_entry;
				List<Entry> lvl1_entries = new ArrayList<DropboxAPI.Entry>();
				try {
					lvl1_entry = this.mApi.metadata( temp.path + "/", 100, null, true, null );
					lvl1_entries = lvl1_entry.contents;
					dpDirectories.put( temp.fileName(), lvl1_entries );
					recursivelyFolderSearch( lvl1_entries );
				} catch (DropboxException e) {
					e.printStackTrace();
				}
			}
			else{
				Entry lvl1_entry;
				List<Entry> lvl1_entries = new ArrayList<DropboxAPI.Entry>();
				try {
					lvl1_entry = this.mApi.metadata( temp.path + "/", 100, null, true, null );
					lvl1_entries = lvl1_entry.contents;
					dpDirectories.put( temp.fileName(), lvl1_entries );
				} catch (DropboxException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void onPostExecute(HashMap<String, List<Entry>> result) {
		super.onPostExecute(result);
		
		((SyncToDeviceActivity) this.context).getDPFoldersSucceeded( result );
	}

}
