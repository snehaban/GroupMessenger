package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class OnSendClickListener implements OnClickListener {

	private static final String TAG = OnPTestClickListener.class.getName();
    private String portNo;
    private Context context;
    EditText editText;
    
	public OnSendClickListener(String _port, Context _context) {        
		 portNo = _port;
         context = _context;         
         editText = (EditText) ((GroupMessengerActivity) context).findViewById(R.id.editText1);
	}

	@Override
	public void onClick(View v) {		
        // Create an AsyncTask that sends the string to the remote AVD                   
        String msg = editText.getText().toString() + "\n";
        editText.setText("");         
        
        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, portNo, null);
    }

}
