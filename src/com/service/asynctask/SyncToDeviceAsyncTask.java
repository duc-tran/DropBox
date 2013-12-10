package com.service.asynctask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;

import android.os.AsyncTask;
import android.os.Environment;

public class SyncToDeviceAsyncTask extends AsyncTask<String, Void, Void> {
	
	private HashMap<String, List<Entry>> dp_folders_list;
	
	private Set<String> folders;
	
	private FileOutputStream mFos;
	
	private DropboxAPI<?> mApi;

	public SyncToDeviceAsyncTask(DropboxAPI<?> api, HashMap<String, List<Entry>> dp_folders_list, Set<String> folders){
		this.mApi = api;
		this.dp_folders_list = dp_folders_list;
		this.folders = folders;
	}
	
	@Override
	protected Void doInBackground(String... params) {
		
//		Entry entries = new Entry();
		try {
//			entries = this.mApi.metadata( "/", 100, null, true, null );
//		
//		
//		List<Entry> entry1 = new ArrayList<Entry>();
//		if ( entries != null )
//			entry1 = entries.contents; 
		
		for ( int i = 0; i < this.dp_folders_list.size(); i++ ){
			for ( String s_temp : folders ){
				if ( this.dp_folders_list.containsKey( s_temp ) ){
					for ( int n = 0; n < this.dp_folders_list.get( s_temp ).size(); n++ ){
						if ( this.dp_folders_list.get( s_temp ).get( n ).isDir ){
							File direct = new File(Environment.getExternalStorageDirectory()
									+ "/" + s_temp + "/" + this.dp_folders_list.get( s_temp ).get( n ).fileName() );
							
				            String cachePath="";
			            	String alt_file_name="";
			            	String dropbox_path = "";
			            	
				            if(!direct.exists())
				            {
				            	if(direct.mkdirs()){
			            			if(direct.exists()) {
			            				List<Entry> files =  new ArrayList<Entry>();
						            	if ( this.dp_folders_list.get( s_temp ).get( n ).isDir ){
						            		String folder_name = this.dp_folders_list.get( s_temp ).get( n ).fileName();
						            		if ( this.dp_folders_list.get( folder_name ).get( n ).contents != null )
						            			files = this.dp_folders_list.get( folder_name ).get( n ).contents;
						            		else
						            			alt_file_name = this.dp_folders_list.get( folder_name ).get( n ).fileName();
						            	}
						            	else{
						            		files = this.dp_folders_list.get( s_temp ).get( n ).contents;
						            	}
				                		if ( files != null && !files.isEmpty() ){
					                		for ( int j = 0; j < files.size() ; j++ )
						                		cachePath = Environment.getExternalStorageDirectory()
						                				+ "/" + s_temp
						                				+ "/" + this.dp_folders_list.get( s_temp ).get( n ).fileName() 
						                				+ "/" + files.get( j ).fileName();
					                		dropbox_path = this.dp_folders_list.get( s_temp ).get( n ).path;
				                		}
				                		else{
						            		cachePath = Environment.getExternalStorageDirectory()
				                				+ "/" + s_temp
				                				+ "/" + this.dp_folders_list.get( s_temp ).get( n ).fileName() 
				                				+ "/" + alt_file_name;
						            		dropbox_path = this.dp_folders_list.get( s_temp ).get( n ).path;
				                		}
			            			}
			                	}
				            }
				            else{
				            	List<Entry> files = new ArrayList<Entry>();
				            	if ( this.dp_folders_list.get( s_temp ).get( n ).isDir ){
				            		String folder_name = this.dp_folders_list.get( s_temp ).get( n ).fileName();
				            		if ( this.dp_folders_list.get( folder_name ).get( n ).contents != null )
				            			files = this.dp_folders_list.get( folder_name ).get( n ).contents;
				            		else
				            			alt_file_name = this.dp_folders_list.get( folder_name ).get( n ).fileName();
				            	}
				            	else{
				            		files = this.dp_folders_list.get( s_temp ).get( n ).contents;
				            	}
				            	if ( files != null && !files.isEmpty() ){
			                		for ( int j = 0; j < files.size() ; j++ )
				                		cachePath = Environment.getExternalStorageDirectory()
				                				+ "/" + s_temp
				                				+ "/" + this.dp_folders_list.get( s_temp ).get( n ).fileName() 
				                				+ "/" + files.get( j ).fileName();
			                		dropbox_path = this.dp_folders_list.get( s_temp ).get( n ).path;
				            	}
				            	else{
				            		cachePath = Environment.getExternalStorageDirectory()
		                				+ "/" + s_temp
		                				+ "/" + this.dp_folders_list.get( s_temp ).get( n ).fileName() 
		                				+ "/" + alt_file_name;
				            		dropbox_path = this.dp_folders_list.get( s_temp ).get( n ).path;
				            	}
				            }
				            
				            
			                mFos = new FileOutputStream(cachePath);
				            
				            
				            mApi.getFile(dropbox_path, null, mFos, null);
				            break;
						}
					}
				}
				else
					break;
			}
		}
		} catch ( Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		return null;
	}

}
