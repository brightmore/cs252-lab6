package edu.purdue.cs252.lab6.userapp;

import org.apache.http.auth.AuthenticationException;

import edu.purdue.cs252.lab6.DirectoryCommand;
import edu.purdue.cs252.lab6.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityHome extends Activity {
	public static final String PREFS_NAME = "mySettingsFile";

	VoipApp appState = null;
	
    public void myClickHandler(View view) {
		switch (view.getId()) {
		case R.id.Quit_Button:
			finish();
    		break;
		case R.id.Settings:
			Intent help = new Intent(ActivityHome.this, ActivitySettings.class);
			startActivity(help);
			break;
		case R.id.Login:
			Login(view);
			break;
		}
    }
	
    public void Login(View v) {
    	// Set directory server
    	String server = ActivitySettings.serverName;
		final String username = ActivitySettings.userName;
		
		User user = new User(username);
		appState.setUser(user);
			
	    final ProgressDialog connectDialog = new ProgressDialog(ActivityHome.this);
	    connectDialog.setMessage("Connecting...");
	    connectDialog.setCancelable(true);
	    connectDialog.show();
	    Log.i("AH","Connect dialog.show");
	       	
	    final View clickView = v;
	    final DirectoryClient dc;
			
	    Handler loginHandler = new Handler() {
	    	public void handleMessage(Message msg) {
	       		Log.i("AH","loginHandler");
	       		connectDialog.dismiss();
	       		
   	       		if(msg.what == DirectoryCommand.S_STATUS_OK.getCode() && msg.obj.equals(DirectoryCommand.C_LOGIN)) {
   	       			Intent directoryIntent = new Intent(clickView.getContext(), ActivityDirectory.class);
   	       			startActivity(directoryIntent);
   	       		}
   	       		else {
   					CharSequence text = "Login failed";
   					Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
   					toast.show();
   	       		}
	       	}
	    };
	    
	    try {
    		dc = new DirectoryClient(server,user,loginHandler);
    		appState.setDirectoryClient(dc);
    		connectDialog.setMessage("Logging in...");
   	       	Log.i("AH","DirectoryClient constructed");
    		dc.login();
    	}
    	catch(Exception e) {
    		Log.e("AH",e.toString());
    		connectDialog.dismiss();
    		CharSequence text = "Could not connect to server";
    		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
    		toast.show();
    	}

    	
       
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        appState = (VoipApp) getApplicationContext();
        // set call state to idle
        Call.setState(Call.State.IDLE);
       
        SharedPreferences settings = getSharedPreferences(ActivityHome.PREFS_NAME, 0);
        ActivitySettings.userName = settings.getString("userName", "");
        ActivitySettings.serverName = settings.getString("serverName", "");

    }
    
    
}