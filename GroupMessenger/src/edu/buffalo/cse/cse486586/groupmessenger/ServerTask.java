package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class ServerTask extends AsyncTask<ServerSocket, String, Void> {
	
	static final String TAG = ServerTask.class.getSimpleName();
	private Context context;
	TextView textView;
	private List<Message> list;
	private int expectedSequenceNo = 0;
	
	public ServerTask(Context _context) {		
		context =_context;
		textView = (TextView) ((GroupMessengerActivity) context).findViewById(R.id.textView1);
		list = new ArrayList<Message>();				
	}
	
	@Override
    protected Void doInBackground(ServerSocket... sockets) {		
        ServerSocket serverSocket = sockets[0];
        Message msgReceived = null;
        Socket socket = null;
        ObjectInputStream inputStream = null;
        
        try {            	
	        while(true) {
	        	socket = serverSocket.accept();
	        	inputStream = new ObjectInputStream(socket.getInputStream());
				msgReceived = (Message) inputStream.readObject();
				String msg = msgReceived.getMessage();
				String seqNo = msgReceived.getSequenceNumber();
				
				// if seqNo null, create seqNo and send to all (clients)
				if("".equals(seqNo) || seqNo == null) {	
					seqNo = Utility.createGlobalSequenceNumber(); 
					msgReceived.setSequenceNumber(seqNo);
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, null, seqNo);
				}
				else {					
					// display directly
					if(Integer.parseInt(seqNo) == expectedSequenceNo) {						
						publishProgress(msg);
						insertInContentProvider(msg, seqNo);
						expectedSequenceNo++; 
						
						// check in buffer and display if messages stored
						boolean displayed;
						do {
							displayed = false;								
							for(int i=0;i<list.size();i++) {
								
								Message m = list.get(i);
								if(Integer.parseInt(m.getSequenceNumber()) == expectedSequenceNo) {									
									publishProgress(m.getMessage());
									insertInContentProvider(m.getMessage(), m.getSequenceNumber());
									list.remove(i);
									expectedSequenceNo++;
									displayed = true;
								}
							}
						}while(list.size() > 0 && displayed == true);
					}
					else {
						list.add(msgReceived);
					}
				}
					
				inputStream.close();
				socket.close(); 
	        }
		} 
        catch (IOException e) {
			Log.e(TAG, "Unable to accept data on server");				
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "Class not found on server");		
		}            
        
        return null;
    }

    protected void onProgressUpdate(String...strings) {    	
        String strReceived = strings[0].trim();
        textView.append(strReceived + "\n");
        
        String filename = "GroupMessengerOutput";
        String string = strReceived + "\n";
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "File write failed");
        }

        return;
    }

	protected void insertInContentProvider(String msg, String seqNo) {		
		ContentValues cv = new ContentValues();
		cv.put(GroupMessengerProvider.KEY_FIELD, seqNo);
        cv.put(GroupMessengerProvider.VALUE_FIELD, msg);
        context.getContentResolver().insert(Utility.getUri(), cv);
	}
}