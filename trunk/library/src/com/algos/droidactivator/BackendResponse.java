package com.algos.droidactivator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Object representing a response from the Backend.
 */
public class BackendResponse {

	private BackendRequest request;
	private HashMap<String, Object> responseMap = new HashMap<String, Object>();
	private int httpResultCode = 0;


	public BackendResponse(BackendRequest request) {
		super();
		this.request = request;
		init();
	}


	private void init() {

		// send the request and retrieve http response code
		try {
			if (this.getConnection() != null) {
				this.httpResultCode = this.getConnection().getResponseCode();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// if the call succeeded, put all response headers in the map
		if (isHttpSuccess()) {

			Map<String, List<String>> responseMap = this.getConnection().getHeaderFields();
			if (responseMap != null) {

				String key = "";
				String valueStr = "";

				for (Iterator<String> iterator = responseMap.keySet().iterator(); iterator.hasNext();) {
					key = (String) iterator.next();

					List values = (List) responseMap.get(key);
					if (values.size() > 0) {
						Object value = values.get(0);
						valueStr = Lib.getString(value);
					}

					this.responseMap.put(key, valueStr);

				}

			}

		}

	}


	private HttpURLConnection getConnection() {
		return this.request.getConnection();
	}


	/**
	 * @return true if the Http call succeeded (at protocol level)
	 */
	boolean isHttpSuccess() {
		return ((this.httpResultCode >= 200) && (this.httpResultCode < 300));
	}


	/**
	 * @return true if the backend call succeeded (at backend logical)
	 */
	boolean isResponseSuccess() {
		boolean success = false;
		if (isHttpSuccess()) {
			String string = getString("success");
			if (Lib.getBool(string)) {
				success = true;
			}
		}

		return success;
	}


	/**
	 * @return true if the app is activated
	 */
	boolean isActivated() {
		return getBool(DroidActivator.KEY_ACTIVATED);
	}


	
	/**
	 * @return the expiration date
	 */
	long getExpirationTime() {
		return getLong(DroidActivator.KEY_EXPIRATION);
	}



	/**
	 * @return the app level
	 */
	int getLevel() {
		return getInt(DroidActivator.KEY_LEVEL);
	}


	/**
	 * Retuns a int from the response map.
	 * 
	 * @param key the key to search
	 * @return the int
	 */
	int getInt(String key) {
		int num=0;
		String string = Lib.getString(this.responseMap.get(key));
		try {
			num=Integer.parseInt(string);
		}
		catch (Exception e) {
		}
		
		//return Lib.getInt(this.responseMap.get(key));
		return num;
	}


	/**
	 * Retuns a long from the response map.
	 * 
	 * @param key the key to search
	 * @return the long
	 */
	long getLong(String key) {
		long num=0;
		String string = Lib.getString(this.responseMap.get(key));
		try {
			num=Long.parseLong(string);
		}
		catch (Exception e) {
		}
		
		//return Lib.getInt(this.responseMap.get(key));
		return num;
	}


	/**
	 * Retuns a boolean from the response map.
	 * 
	 * @param key the key to search
	 * @return the boolean
	 */
	boolean getBool(String key) {
		return Lib.getBool(this.responseMap.get(key));
	}


	/**
	 * Retuns a string from the response map.
	 * 
	 * @param key the key to search
	 * @return the string
	 */
	String getString(String key) {
		return Lib.getString(this.responseMap.get(key));
	}
}
