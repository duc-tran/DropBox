package com.service.research;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.service.asynctask.UploadPicture;
import com.service.extend.FolderListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

public class UpLoadToCloudActivity 
	extends Activity implements OnClickListener{
	
    private ListView folders_list;
    
    private CheckBox cb_auto_sync;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView( R.layout.upload_screen );
		
		this.folders_list = (ListView) this.findViewById( R.id.listView1 );

		this.cb_auto_sync = (CheckBox) this.findViewById( R.id.cb_auto_sync );
		
		ArrayList<String> folders = new ArrayList<String>();
		
		for ( File f : Environment.getExternalStorageDirectory().listFiles() )
		{
			folders.add( f.getName() );
		}
		
		FolderListAdapter adapter = new FolderListAdapter( this, folders );

        this.cb_auto_sync.setOnClickListener( this );
        	
		this.folders_list.setAdapter( adapter );
	}

	@Override
	public void onClick(View v) {
		switch ( v.getId() ) {
			case R.id.btn_sync_to_cloud:{
				Set<String> folders;
				folders = ( (FolderListAdapter)this.folders_list.getAdapter() ).getChecked();
	
				HashMap<String, File[]> outFiles = new HashMap<String, File[]>();
				for ( String selected_folder_name : folders ){
					outFiles.put( selected_folder_name, 
							Environment.getExternalStoragePublicDirectory( selected_folder_name )
							.listFiles() );
				}
				ArrayList<String> folders_name = new ArrayList<String>();
				for (Map.Entry<String, File[]> e : outFiles.entrySet()) {
					folders_name.add( e.getKey() );
				}
				for ( int i = 0; i< outFiles.size(); i++ ){
					for ( File f: outFiles.get( folders_name.get( i ) ) ){
						String storagePath = "";
						storagePath += "/" + folders_name.get( i ).toString() + "/";
						if ( f.isFile() ){
							UploadPicture upload = new UploadPicture( this, MainActivity.mApi, storagePath , f );
							upload.execute();
						}
						else if ( f.isDirectory() ){
							File[] innerFiles = f.listFiles();
							for ( File inner_file : innerFiles ){
								if ( inner_file.isFile() ){
									String innerStoragePath = storagePath + f.getName()+ "/";
									UploadPicture upload = new UploadPicture( this, MainActivity.mApi, innerStoragePath, inner_file );
									upload.execute();
								}
							}
						}
					}
				}
			}
			break;
			case R.id.cb_auto_sync:{
				if ( cb_auto_sync.isChecked() ){
	//				try {
	//					dbxFs = DbxFileSystem.forAccount( this.dbxAccountManager.getLinkedAccount());
	//				} catch (Unauthorized e) {
	//					// TODO Auto-generated catch block
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

}
