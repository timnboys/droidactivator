package com.algos.droidactivator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.http.AndroidHttpClient;

/**
 * An object representing a request to the backend.
 * <p>Encapsulates an HttpClient.
 */
public class BackendRequest {

	private String action;
	private int connTimeout;	// connection timeout in ms
	private int socketTimeout;	// socket timeout in ms
	private HashMap<String, String> properties; // request properties


	/**
	 * @param action to request to the backend
	 * @param connTimeout the connection timeout in ms
	 */
	public BackendRequest(String action, int connTimeout) {
		super();
		this.action=action;
		this.connTimeout=connTimeout;
		this.socketTimeout=DroidActivator.getDefaultSocketTimeout();
		init();
	}

	/**
	 * @param action to request to the backend
	 */
	public BackendRequest(String action) {
		this(action, DroidActivator.getDefaultConnectionTimeout());
	}
	
	private void init(){
		
		properties = new HashMap<String,String>();

		// standard request properties (always present)
		setRequestProperty("action", this.action);
		setRequestProperty("producerid", ""+DroidActivator.getProducerId());
		setRequestProperty("appname", DroidActivator.getAppName());

	}
	
	/**
	 * Sets a Request Property of the connection
	 */
	public void setRequestProperty(String key, String value){
		this.properties.put(key, value);
	}

	

	public HttpResponse execute(){
		HttpResponse response=null;

		// create an HTTP Client
		AndroidHttpClient client = AndroidHttpClient.newInstance("droidactivator");
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, connTimeout);
		HttpConnectionParams.setSoTimeout(params, socketTimeout);        
		
		// create an HTTPGet request
		HttpGet request = new HttpGet(DroidActivator.getBackendURI());
		//HttpPost request = new HttpPost(DroidActivator.getBackendURI());
		
		// add request headers
		for (Entry<String, String> entry : this.properties.entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}
		
		// obtain a response
		try {
			response = client.execute(request);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		client.close();
		
		return response;
	}
	
	
	
}
