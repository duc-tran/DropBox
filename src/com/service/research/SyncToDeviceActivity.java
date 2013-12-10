package com.service.research;

import java.io.File;
import java.io.FileNotFoundException;
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
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI.Entry;
import com.service.asynctask.GetDpFolderListAsyncTask;
import com.service.asynctask.SyncToDeviceAsyncTask;
import com.service.extend.FolderListAdapter;

public class SyncToDeviceActivity
	extends Activity
		implements OnClickListener{
	
	private Button btn_sync_to_device;
	
	private ListView dp_list;
	
	private HashMap<String, List<Entry>> dp_folders_list;
	
	private ProgressDialog mDialog;
	private boolean mCanceled;
	private String mErrorMsg;
	
	private FileOutputStream mFos;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView( R.layout.sync_to_phone_screen );
		
		this.dp_list = (ListView) this.findViewById( R.id.listView2 );
		
		this.btn_sync_to_device = (Button) this.findViewById( R.id.btn_sync_to_device );
		
		this.btn_sync_to_device.setOnClickListener( this );
		
		GetDpFolderListAsyncTask getfolderslist = new GetDpFolderListAsyncTask( this , MainActivity.mApi );
		getfolderslist.execute();
		
		mDialog = new ProgressDialog( this );
        mDialog.setMessage("Loading Files List");
        mDialog.setButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mCanceled = true;
                mErrorMsg = "Canceled";

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
	}
	
	public void getDPFoldersSucceeded( HashMap<String, List<Entry>> directories ){
		ArrayList<String> dp_folders_list = new ArrayList<String>();
		
		this.dp_folders_list = directories;
		
		for ( Entry temp : directories.get( "root" ) ){
			dp_folders_list.add( temp.fileName() );
		}
		FolderListAdapter adapter = new FolderListAdapter( this, dp_folders_list );
        	
		this.dp_list.setAdapter( adapter );
		
		this.mDialog.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		switch ( v.getId() ){
			case R.id.btn_sync_to_device:{
				Set<String> folders;
				folders = ( (FolderListAdapter)this.dp_list.getAdapter() ).getChecked();
				
				this.SyncToDevice( folders );
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
		new SyncToDeviceAsyncTask( MainActivity.mApi, this.dp_folders_list, folders ).execute();
	}
	
}
