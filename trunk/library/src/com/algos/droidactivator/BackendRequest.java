package com.algos.droidactivator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * An object representing a request to the backend.
 * <p>Encapsulates an HttpURLConnection.
 */
public class BackendRequest {

	private HttpURLConnection connection;
	private String action;
	private int connTimeout;	// connection timeout in ms
	private int readTimeout;	// read timeout in ms

	private static final int DEFAULT_CONN_TIMEOUT=5000;	// default connection timeout in ms
	
	// default read timeout in ms
	// specifies the timeout when reading from Input stream when a connection is established
	// in this application we don't read from InputStreams so it should not be meaningful
	// set it to a short value (but not 0 because 0 = infinity)
	private static final int DEFAULT_READ_TIMEOUT=100;

	/**
	 * @param action to request to the backend
	 * @param connTimeout the connection timeout in ms
	 */
	public BackendRequest(String action, int connTimeout) {
		super();
		this.action=action;
		this.connTimeout=connTimeout;
		this.readTimeout=DEFAULT_READ_TIMEOUT;
		init();
	}

	/**
	 * @param action to request to the backend
	 */
	public BackendRequest(String action) {
		this(action, DEFAULT_CONN_TIMEOUT);
	}
	


	private void init(){
		
		URL url = DroidActivator.getBackendURL();
		
		if (url != null) {
			try {
				this.connection = (HttpURLConnection) url.openConnection();
			}
			catch (IOException e) {
			}
			if (this.connection!=null) {

				this.connection.setReadTimeout(readTimeout);
				setRequestProperty("action", this.action);
				setRequestProperty("appname", DroidActivator.getAppName());
			}
		}

	}
	
	/**
	 * Sets a Request Property of the connection
	 */
	void setRequestProperty(String key, String value){
		if (this.connection!=null) {
			this.connection.setRequestProperty(key, value);
		}
	}
	
	void disconnect(){
		if (this.connection!=null) {
			this.connection.disconnect();
		}
	}
	
	HttpURLConnection getConnection(){
		return this.connection;
	}
	

}
