package com.service.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.service.asynctask.UploadPicture;
import com.service.extend.FolderListAdapter;
import com.service.research.R;

public class UpLoadToDropboxActivity 
	extends Activity implements OnClickListener, OnItemClickListener{
	
    private ListView folders_list;
    
    private CheckBox cb_auto_sync;
    
    private FolderListAdapter adapter;
    
    private boolean clicked = false;
    
    private Button btn_sync_to_cloud;

	private String selected_folder_name = "";
	
	private ArrayList<String> exploredDir = new ArrayList<String>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView( R.layout.upload_screen );
		
		this.folders_list = (ListView) this.findViewById( R.id.listView1 );

		this.cb_auto_sync = (CheckBox) this.findViewById( R.id.cb_auto_sync );
		
		this.btn_sync_to_cloud = (Button) this.findViewById( R.id.btn_sync_to_cloud );
		
		ArrayList<String> folders = new ArrayList<String>();
		
		for ( File f : Environment.getExternalStorageDirectory().listFiles() )
		{
			folders.add( f.getName() );
		}
		
		this.adapter = new FolderListAdapter( this, folders );

        this.cb_auto_sync.setOnClickListener( this );
        
        this.btn_sync_to_cloud.setOnClickListener( this );
        	
		this.folders_list.setAdapter( this.adapter );
		
		this.folders_list.setOnItemClickListener( this );
	}

	@Override
	public void onClick(View v) {
		switch ( v.getId() ) {
			case R.id.btn_sync_to_cloud:{
				Set<String> folders;
				folders = ( (FolderListAdapter)this.folders_list.getAdapter() ).getChecked();
	
//				HashMap<String, File[]> outFiles = new HashMap<String, File[]>();
				for ( String selected_folder_name : folders ){
//					outFiles.put( selected_folder_name, 
//							Environment.getExternalStoragePublicDirectory( selected_folder_name )
//							.listFiles() );
					this.recursivelyDeviceFolderSearch( selected_folder_name );
				}
//				ArrayList<String> folders_name = new ArrayList<String>();
//				for (Map.Entry<String, File[]> e : outFiles.entrySet()) {
//					folders_name.add( e.getKey() );
//				}
//				for ( int i = 0; i< outFiles.size(); i++ ){
//					for ( File f: outFiles.get( folders_name.get( i ) ) ){
//						String storagePath = "";
//						storagePath += "/" + folders_name.get( i ).toString() + "/";
//						if ( f.isFile() ){
//							UploadPicture upload = new UploadPicture( this, MainActivity.mApi, storagePath , f );
//							upload.execute();
//						}
//						else if ( f.isDirectory() ){
//							File[] innerFiles = f.listFiles();
//							for ( File inner_file : innerFiles ){
//								if ( inner_file.isFile() ){
//									String innerStoragePath = storagePath + f.getName()+ "/";
//									UploadPicture upload = new UploadPicture( this, MainActivity.mApi, innerStoragePath, inner_file );
//									upload.execute();
//								}
//							}
//						}
//					}
//				}
			}
			break;
			case R.id.cb_auto_sync:{
				if ( cb_auto_sync.isChecked() ){
	//				try {
	//					dbxFs = DbxFileSystem.forAccount( this.dbxAccountManager.getLinkedAccount());
	//				} catch (Unauthorized e) {
	//					e.printStackTrace();
	//				}
					Toast.makeText( this, "checked", Toast.LENGTH_SHORT ).show();
				}
				else if ( !cb_auto_sync.isChecked() ){
					Toast.makeText( this, "unchecked", Toast.LENGTH_SHORT ).show();
				}
			}
			break;
		}
		
	}
	
	private void recursivelyDeviceFolderSearch( String deviceDir ){
		File[] selectedDirs = Environment.getExternalStoragePublicDirectory( deviceDir ).listFiles();
		for ( File temp : selectedDirs ){
			String storagePath = "";
			if ( temp.isDirectory() ){
				storagePath += "/" + temp.getName() + "/";
				File[] childPath = Environment.getExternalStoragePublicDirectory( deviceDir + storagePath ).listFiles();
				for ( File grandChildDir : childPath )
					if ( !grandChildDir.isFile() )
						recursivelyDeviceFolderSearch( deviceDir + storagePath + grandChildDir.getName() );
					else{
						UploadPicture upload = new UploadPicture( this, HomeScreen.mApi, deviceDir + storagePath , grandChildDir );
						upload.execute();
					}
//				if ( f.isFile() ){
//					UploadPicture upload = new UploadPicture( this, MainActivity.mApi, storagePath , f );
//					upload.execute();
//				}
//				else if ( f.isDirectory() ){
//					File[] innerFiles = f.listFiles();
//					for ( File inner_file : innerFiles ){
//						if ( inner_file.isFile() ){
//							String innerStoragePath = storagePath + f.getName()+ "/";
//							UploadPicture upload = new UploadPicture( this, MainActivity.mApi, innerStoragePath, inner_file );
//							upload.execute();
//						}
//					}
//				}
			}
			else if ( temp.isFile() ){
				UploadPicture upload = new UploadPicture( this, HomeScreen.mApi, deviceDir + "/" , temp );
				upload.execute();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long arg3) {
		if ( !this.clicked ){
			if ( !Environment.getExternalStoragePublicDirectory( this.folders_list.getItemAtPosition( position ).toString() ).isFile() )
				this.selected_folder_name = this.folders_list.getItemAtPosition( position ).toString();
		}
		else{
			String temp = this.selected_folder_name + "/" + this.folders_list.getItemAtPosition( position ).toString();
			if ( !Environment.getExternalStoragePublicDirectory( temp ).isFile() )
				this.selected_folder_name += "/" + this.folders_list.getItemAtPosition( position ).toString();
		}

		this.exploredDir.add( this.selected_folder_name );
		
		ArrayList<String> folders = new ArrayList<String>();
		
		if ( Environment.getExternalStoragePublicDirectory( this.selected_folder_name )
				.listFiles() != null ){
			int i = 0;
			for ( File f : Environment.getExternalStoragePublicDirectory( this.selected_folder_name )
					.listFiles() )
			{
				if ( f.isFile() )
					folders.add( f.getName() );
				else
					folders.add( i++, f.getName() );
			}
			
			this.adapter = new FolderListAdapter( this, folders );
	
	        this.cb_auto_sync.setOnClickListener( this );
	        
	        this.adapter.notifyDataSetChanged();
			this.folders_list.setAdapter( adapter );
			this.clicked = true;
		}
	}
	
	@Override
	public void onBackPressed() {
		ArrayList<String> folders = new ArrayList<String>();
		if ( this.exploredDir.size() >= 2 ){
			this.selected_folder_name = this.exploredDir.get( this.exploredDir.size() - 2 );
			if ( Environment.getExternalStoragePublicDirectory( this.exploredDir.get( this.exploredDir.size() - 2 ) )
					.listFiles() != null ){
				int i = 0;
				for ( File f : Environment.getExternalStoragePublicDirectory( this.exploredDir.get( this.exploredDir.size() - 2 ) )
						.listFiles() )
				{
					if ( f.isFile() )
						folders.add( f.getName() );
					else
						folders.add( i++, f.getName() );
				}

				this.exploredDir.remove( this.exploredDir.size() - 1 );
				
				this.adapter = new FolderListAdapter( this, folders );
		
		        this.adapter.notifyDataSetChanged();
				this.folders_list.setAdapter( adapter );
			}
		}
		else if ( this.exploredDir.size() == 1 ){
			this.exploredDir.remove( this.exploredDir.size() - 1 );
			for ( File f : Environment.getExternalStorageDirectory().listFiles() )
			{
				folders.add( f.getName() );
			}
			
			this.adapter = new FolderListAdapter( this, folders );
			
			this.adapter.notifyDataSetChanged();
			this.folders_list.setAdapter( this.adapter );
			
			this.clicked = false;
		}
		else{
			super.onBackPressed();
		}
	}

}
