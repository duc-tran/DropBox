package com.service.research;


//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.ListView;
//import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
//import com.service.asynctask.DownloadRandomPicture;
//import com.service.asynctask.UploadPicture;
//import com.service.extend.FolderListAdapter;
//import com.dropbox.sync.android.DbxAccountManager;
//import com.dropbox.sync.android.DbxException.Unauthorized;
//import com.dropbox.sync.android.DbxFile;
//import com.dropbox.sync.android.DbxFileSystem;


public class MainActivity extends Activity 
	implements OnClickListener{
	
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			Bundle bundle = intent.getExtras();
//			if (bundle != null) {
//				String string = bundle.getString(BackUpService.FILEPATH);
//				int resultCode = bundle.getInt(BackUpService.RESULT);
//				if (resultCode == RESULT_OK) {
//				Toast.makeText(MainActivity.this,
//				"Download complete. Download URI: " + string,
//				Toast.LENGTH_LONG).show();
//				} else {
//					Toast.makeText(MainActivity.this, "Download failed",
//					Toast.LENGTH_LONG).show();
//				}
//			}
//		}
//	};
	
	private static final String TAG = "DBRoulette";
	
	private Button btn_Sign_In, btn_Back_Up, btn_Sync;
	
//	private DbxAccountManager dbxAccountManager;
	
//	DbxFileSystem dbxFs;
	 
	final static private String APP_KEY = "lurzefwf3wsgtr5";
    final static private String APP_SECRET = "zll1qoyggk6h5mr";
    final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
    
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    
    public static DropboxAPI<AndroidAuthSession> mApi;
    
    static final int REQUEST_LINK_TO_DBX = 0;
    
    private boolean mLoggedIn;
    
	@SuppressWarnings("static-access")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView( R.layout.home_screen );
		
//		this.dbxAccountManager = DbxAccountManager.getInstance( this.getApplicationContext(), APP_KEY, APP_SECRET );
		
		AndroidAuthSession session = buildSession();
        this.mApi = new DropboxAPI<AndroidAuthSession>(session);
        
        this.checkAppKeySetup();
        
        this.btn_Sign_In = ( (Button) this.findViewById( R.id.btn_sign_in ) );
        this.btn_Sign_In.setOnClickListener( this );
        
        this.btn_Back_Up = ( (Button) this.findViewById( R.id.btn_backup ) );
        this.btn_Back_Up.setOnClickListener( this );
        
        this.btn_Sync = ( (Button) this.findViewById( R.id.btn_sync ) );
        this.btn_Sync.setOnClickListener( this );
        
        setLoggedIn(mApi.getSession().isLinked());
        
        
        
//        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);
        
//        this.doDropboxTest();
	}
	
//	private void doDropboxTest() {
//        try {
//            final String TEST_DATA = "Hello Dropbox";
//            final String TEST_FILE_NAME = "hello_dropbox.txt";
//            DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);
//
//            // Create DbxFileSystem for synchronized file access.
//            DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
//
//            // Print the contents of the root folder.  This will block until we can
//            // sync metadata the first time.
//            List<DbxFileInfo> infos = dbxFs.listFolder(DbxPath.ROOT);
//            mTestOutput.setText("\nContents of app folder:\n");
//            for (DbxFileInfo info : infos) {
//                mTestOutput.append("    " + info.path + ", " + info.modifiedTime + '\n');
//            }
//
//            // Create a test file only if it doesn't already exist.
//            if (!dbxFs.exists(testPath)) {
//                DbxFile testFile = dbxFs.create(testPath);
//                try {
//                    testFile.writeString(TEST_DATA);
//                } finally {
//                    testFile.close();
//                }
//                mTestOutput.append("\nCreated new file '" + testPath + "'.\n");
//            }
//
//            // Read and print the contents of test file.  Since we're not making
//            // any attempt to wait for the latest version, this may print an
//            // older cached version.  Use getSyncStatus() and/or a listener to
//            // check for a new version.
//            if (dbxFs.isFile(testPath)) {
//                String resultData;
//                DbxFile testFile = dbxFs.open(testPath);
//                try {
//                    resultData = testFile.readString();
//                } finally {
//                    testFile.close();
//                }
//                mTestOutput.append("\nRead file '" + testPath + "' and got data:\n    " + resultData);
//            } else if (dbxFs.isFolder(testPath)) {
//                mTestOutput.append("'" + testPath.toString() + "' is a folder.\n");
//            }
//        } catch (IOException e) {
//            mTestOutput.setText("Dropbox test failed: " + e);
//        }
//    }

	@Override
	protected void onResume() {
		super.onResume();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
//		registerReceiver(receiver, new IntentFilter(BackUpService.NOTIFICATION));
		
		AndroidAuthSession session = mApi.getSession();
		
		 if (session.authenticationSuccessful()) {
	            try {
	                // Mandatory call to complete the auth
	                session.finishAuthentication();

	                // Store it locally in our app for later use
	                TokenPair tokens = session.getAccessTokenPair();
	                storeKeys(tokens.key, tokens.secret);
	                setLoggedIn(true);
	            } catch (IllegalStateException e) {
	                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
	                Log.i(TAG, "Error authenticating", e);
	            }
	        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		unregisterReceiver(receiver);
	}

	  public void onClick(View view) {
//	    Intent intent = new Intent(this, BackUpService.class);
//	    // add infos for the service which file to download and where to store
//	    intent.putExtra(BackUpService.FILENAME, "index.html");
//	    intent.putExtra(BackUpService.URL,
//	        "http://www.vogella.com/index.html");
//	    intent.setData( Uri.parse( BackUpService.URL ) );
//	    intent.setAction( "Toast" );
//	    this.startService( intent );
//	    textView.setText("Service started");
		switch ( view.getId() ){
			case R.id.btn_sign_in:{
				if (mLoggedIn) {
                    logOut();
                } else {
                    // Start the remote authentication
                    mApi.getSession().startAuthentication( this );
                }
//				 this.dbxAccountManager.startLink( this, REQUEST_LINK_TO_DBX );
			}
			break;
			case R.id.btn_backup:{
				this.startActivity( new Intent ( this, UpLoadToCloudActivity.class ) );
			}
			break;
			
			case R.id.btn_sync:{
//				DownloadRandomPicture download = new DownloadRandomPicture( this, mApi, "ZingMp3");
//                download.execute();
				this.startActivity( new Intent ( this, SyncToDeviceActivity.class ) );
			}
			break;
		}
	}
	 
//	public void onClickLinkToDropbox(View view) {
//	    this.dbxAccountManager.startLink( this, REQUEST_LINK_TO_DBX );
//	}
	  private AndroidAuthSession buildSession() {
	        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
	        AndroidAuthSession session;

	        String[] stored = getKeys();
	        if (stored != null) {
	            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
	            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
	        } else {
	            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
	        }

	        return session;
	    }
	
	private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }
	
	private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }
	
	private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
	
	private void logOut() {
        // Remove credentials from the session

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }
	
	private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
	
	private void setLoggedIn(boolean loggedIn) {
    	mLoggedIn = loggedIn;
    	if (loggedIn) {
    		this.btn_Sign_In.setText("Unlink from Dropbox");
//            mDisplay.setVisibility(View.VISIBLE);
    	} else{
    		this.btn_Sign_In.setText("Link with Dropbox");
//            mDisplay.setVisibility(View.GONE);
//            mImage.setImageDrawable(null);
    	}
    }
	
	 private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }
	 
	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	     if (requestCode == REQUEST_LINK_TO_DBX) {
	         if (resultCode == Activity.RESULT_OK) {
	             this.mLoggedIn = true;
	             Toast.makeText( this, "Logged In Successfully", Toast.LENGTH_SHORT ).show();
	         } else {
	        	 this.mLoggedIn = false;
	        	 Toast.makeText( this, "Logged In Failed", Toast.LENGTH_SHORT ).show();
	         }
	     } else {
	         super.onActivityResult(requestCode, resultCode, data);
	     }
	 }

}
