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

	/**
	 * @param action to request to the backend
	 */
	public BackendRequest(String action) {
		super();
		this.action=action;
		init();
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
				this.connection.setConnectTimeout(1000);
				this.connection.setReadTimeout(1000);
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
