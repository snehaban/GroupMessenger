package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class ClientTask extends AsyncTask<String, Void, Void> {	
	
	static final String TAG = ClientTask.class.getSimpleName();
	
    @Override
    protected Void doInBackground(String... msgs) {    	           	
    	String msgToSend = msgs[0];
     	int portNo = -1;
     	String seqNo = msgs[2];
     	String avd = "avd"; // find the avdNo sending message
     	
     	if(msgs[1] != null)
     		portNo = Integer.parseInt(msgs[1]);
     	
     	switch(portNo) {
     		case 11108:
     			avd = "avd0";
     			break;
     		case 11112:
     			avd = "avd1";
     			break;
     		case 11116:
     			avd = "avd2";
     			break;
     		case 11120:
     			avd = "avd3";
     			break;
     		case 11124:
     			avd = "avd4";
     			break;
     	}
     	         	
     	Message m = new Message(msgToSend, seqNo, avd);
     	
     	// Requesting sequenceNo from sequencer
     	if("".equals(seqNo) || seqNo == null ) {     		     		
     		sendToPort(m, Integer.toString(Utility.SEQUENCER_PORT));		   
     	}
     	// SequenceNo present - multicast to all
     	else {     		
     		 for(int i=0; i<Utility.COUNT_AVDS; i++) {         			     			 
     			sendToPort(m, Utility.REMOTE_PORTS[i]);
     		}
     	}
     	
        return null;
    }
    
    public void sendToPort(Message m, String portNo) {
    	try {
			Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), Integer.parseInt(portNo));	        				
	        
	        ObjectOutputStream outputStream = null;
	        try {
	        	outputStream = new ObjectOutputStream(socket.getOutputStream());
	        	outputStream.writeObject(m);
	        	outputStream.flush();
	            outputStream.close();
	        }
	        catch(IOException e) {
	        	 Log.e(TAG, "Error in writing to client socket");
	        }                         
	        socket.close();   
	    } 
	    catch (UnknownHostException e) {
	        Log.e(TAG, "ClientTask UnknownHostException");
	    } 
	    catch (IOException e) {
	        Log.e(TAG, "ClientTask socket IOException");
	    }     			 
    }
}