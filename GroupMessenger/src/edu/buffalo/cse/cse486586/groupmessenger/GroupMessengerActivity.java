package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.net.ServerSocket;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

	static final String TAG = GroupMessengerActivity.class.getSimpleName();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        final TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
              
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
         findViewById(R.id.button1).setOnClickListener(
               new OnPTestClickListener(tv, getContentResolver()));
        
        // Calculate the port number that this AVD listens on
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        try {
             ServerSocket serverSocket = new ServerSocket(Utility.SERVER_PORT);
             new ServerTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
             return;
        }                
         
        findViewById(R.id.button4).setOnClickListener(
                new OnSendClickListener(myPort, this));
        
        final EditText editText = (EditText) findViewById(R.id.editText1);
        
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Create an AsyncTask that sends the string to the remote AVD                     
                    String msg = editText.getText().toString() + "\n";
                    editText.setText(""); 
                    
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort, null);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
