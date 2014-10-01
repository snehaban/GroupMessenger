package edu.buffalo.cse.cse486586.groupmessenger;

import android.net.Uri;

public class Utility {

	private static int globalSequence = -1;  
	private final static Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
	
	// Accessed by other classes
	public static final String[] REMOTE_PORTS = {"11108","11112","11116","11120","11124"};
	public static final int SERVER_PORT = 10000;
	public static final int SEQUENCER_PORT = 11108;
	public static final int COUNT_AVDS = 5;
	
    public static String createGlobalSequenceNumber() {
    	globalSequence++;
    	return Integer.toString(globalSequence);
    }
    
    private static Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }
    
    public static Uri getUri() {
    	return mUri;
    }
}
