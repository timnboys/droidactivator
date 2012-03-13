package com.algos.droidactivator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

public class DroidActivator {

	// a variable holding the Singleton instance of the Activator
	private static DroidActivator ACTIVATOR = null;

	// the context to obtain the SharedPreferences
	private Context context;

	// a runnable to run when the Activation Cycle is finished
	private Runnable cycleFinishedRunnable;

	// the shared preferences file name
	private static String SHARED_PREFS_FILE_NAME = "droidActivatorData";

	// keys to identify values in bundles and preferences
	static String KEY_SUCCESS = "success";
	static String KEY_ACTIVATED = "activated";
	static String KEY_LEVEL = "level";
	static String KEY_EXPIRATION = "expiration";
	static String KEY_INSTALLATION_UUID = "installation_UUID";
	static String KEY_UNIQUEID = "unique_id";
	static String KEY_BACKEND_URL = "backend_url";

	// the app name passed to the backend, defaults to the current App Name
	private String appName = "";

	// listener notified when the Activation Cycle is finished
	OnActivationDoneListener activationDoneListener;


	// private ArrayList<OnActivationDoneListener> activationDoneListeners = new ArrayList<OnActivationDoneListener>();

	/**
	 * Constructor
	 */
	private DroidActivator(Context context, Runnable runnable) {
		super();
		this.context = context;
		this.cycleFinishedRunnable = runnable;
	}


	/**
	 * init() is called by newInstance() after the ACTIVATOR variable is set!
	 */
	private void init() {

		// retrieve default application name
		PackageManager packageManager = getContext().getPackageManager();
		String appname = packageManager.getApplicationLabel(getContext().getApplicationInfo()).toString();
		this.appName = appname;

		// create and save the installation UUID in Shared Preferences if not present (first time only)
		// this is the In stallation part of the UniqueId
		String uuidStr = getInstallationUuid();
		if (uuidStr.equals("")) {
			UUID uuid = UUID.randomUUID();
			setInstallationUuid(uuid.toString());
		}

		this.activationDoneListener = new OnActivationDoneListener() {

			@Override
			public void onActivationDone() {
				// TODO Auto-generated method stub

			}
		};
	}


	/**
	 * Retrieve the SharedPreferences object. The preferences file is 
	 * created now if it doesn't exist.
	 * 
	 * @return the SharedPreferences object
	 */
	public static void doActivationCycle() {

		// performs an Activation Update
		// after the Activation Update the cached variables are updated
		doUpdate();

		// performs a Check (even if the update vas unsuccessful)
		doCheck();

		// // notify the listener
		// getInstance().activationDoneListener.onActivationDone();

		// run the Cycle Finished runnable
		Runnable runnable = getInstance().cycleFinishedRunnable;
		if (runnable != null) {
			runnable.run();
		}

	}


	/**
	 * Performs the Activation Update operation. At the end, the UpdateDoneListener is notified.
	 * 
	 * @return true if the update was successful
	 */
	private static boolean doUpdate() {
		boolean success = false;

		if (isNetworkAvailable()) {

			if (isBackendResponding()) {

				Bundle bundle = readBackendData();

				// write backend values in local cache
				if (bundle.getBoolean(KEY_SUCCESS)) {
					setActivated(bundle.getBoolean(KEY_ACTIVATED));
					setExpiration(bundle.getLong(KEY_EXPIRATION));
					setLevel(bundle.getInt(KEY_LEVEL));
					success = true;
				}

			}

		}

		return success;

	}


	/**
	 * Performs the Check operation
	 */
	private static void doCheck() {

		if (!isActivated()) {
			openDialog(isUseridRequested());
		}

	}


	/**
	 * Determine if the activation process requires to supply the userid also.
	 * 
	 * @return true if userid is requested
	 */
	private static boolean isUseridRequested() {
		boolean requested = false;
		
		String uid = getUniqueId();
		
		if (uid.equals("")) { // no uniqueid in cached data
			requested = true;
		}
		else { // uniqueid present in cached data
			if (isUniqueidPresentInBackend()) {// also in the backend?
				requested = false;
			}
			else {
				requested = true;
			}
		}
		return requested;
	}


	/**
	 * Checks if the cached uniqueid is present in the backend.
	 * If the backend is not responding, it is assumed as present
	 * (will be checked again in the the effective activation process)
	 * 
	 * @return true if the uniqueid is present in the backend or the backend is not responding
	 */
	private static boolean isUniqueidPresentInBackend() {
		boolean present = true;
		if (isBackendResponding()) {

			// create the task
			CheckUniqueidPresentTask task = getInstance().new CheckUniqueidPresentTask();

			// start the background task
			task.execute();

			// wait until finished
			while (!task.isFinished()) {
				try {
					Thread.sleep(50);
				}
				catch (InterruptedException e) {
				}
			}

			// retrieve the result
			present = task.isPresent();

		}
		return present;
	}


	/**
	 * Presents the Activation dialog
	 * 
	 * @param context useridRequested true if userid must be requested by the dialog
	 */
	private static void openDialog(boolean useridRequested) {
		ActivationDialog dialog = new ActivationDialog(getInstance().getContext(), useridRequested);
		dialog.setOnActivationRequestedListener(new OnActivationRequestedListener() {

			@Override
			public void onActivationRequested(boolean temporary, String userid, String code) {
				if (temporary) {
					doTemporaryActivation();
				}
				else {
					requestActivation(userid, code);
				}
			}
		});

		dialog.show();
	}


	/**
	 * Requests an Activation to the backend.
	 * <p>Called by the Activation Dialog when Activate button is pressed.
	 * @param userId the activation userid to use.
	 * @param activationCode the activation code to use.
	 */
	private static void requestActivation(String userId, String activationCode) {

		
		// retrieve the Unique Id (the cached one if present, otherwise calculated on the fly)
		String uid = getUniqueId();
		if (uid.equals("")) {
			uid=createUniqueId();
		}
		
		
		// to be continued..

		int a = 87;
		int b = a;
	}


	/**
	 * Performs a Temporary Activation.
	 * <p>Called by the Activation Dialog when Later button is pressed.
	 */
	private static void doTemporaryActivation() {
		int a = 87;
		int b = a;
	}


	/**
	 * Reads activation data from the backend and returns it in a Bundle.
	 * 
	 * @return a Bundle containing backend activation data
	 */
	private static Bundle readBackendData() {

		// create the task
		ReadBackendDataTask task = getInstance().new ReadBackendDataTask();

		// start the background task
		task.execute();

		// wait until finished
		while (!task.isFinished()) {
			try {
				Thread.sleep(50);
			}
			catch (InterruptedException e) {
			}
		}

		// get the result
		return task.getBundle();

	}

	/**
	 * An AsyncTask to read backend data in a background process. 
	 * This is needed to comply with Honeycomb Strict Mode wich doesn't allow 
	 * networking operations in the UI thread.
	 */
	private class ReadBackendDataTask extends AsyncTask<Void, Void, Void> {

		private boolean finished = false;
		private Bundle bundle = null;


		@Override
		protected Void doInBackground(Void... params) {

			// create an empty bundle with success=false (it will be set to true later)
			Bundle bundle = new Bundle();
			bundle.putBoolean(KEY_SUCCESS, false);

			// create a parameter object for the http client
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
			HttpConnectionParams.setSoTimeout(httpParameters, 1000);

			HttpClient client = new DefaultHttpClient(httpParameters);
			client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "droid-activator");

			HttpGet request = new HttpGet(getBackendURL().toString());
			try {
				HttpResponse response = client.execute(request);
				if (response != null) {

					// get data from the response
					HttpEntity entity = response.getEntity();
					String string = EntityUtils.toString(entity);
					// ... todo...

					// retrieve the result code from the backend
					boolean success = true;

					if (success) {
						bundle.putBoolean(KEY_SUCCESS, true);
						bundle.putBoolean(KEY_ACTIVATED, false);
						bundle.putLong(KEY_EXPIRATION, 0);
						bundle.putInt(KEY_LEVEL, 0);
					}

				}
			}
			catch (ClientProtocolException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			this.bundle = bundle;
			this.finished = true;

			return null;
		}


		private boolean isFinished() {
			return finished;
		}


		private Bundle getBundle() {
			return this.bundle;
		}


		@Override
		protected void onPostExecute(Void result) {
			cancel(true);
		}

	}


	/**
	 * Check if network is configured and connected.
	 * 
	 * @return true if network is connected
	 */
	static boolean isNetworkAvailable() {
		boolean available = false;
		Object service = getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (service != null && service instanceof ConnectivityManager) {
			ConnectivityManager cm = (ConnectivityManager) service;

			NetworkInfo networkInfo = cm.getActiveNetworkInfo();

			// if no network is available networkInfo will be null, otherwise check if we are connected
			if (networkInfo != null && networkInfo.isConnected()) {
				available = true;
			}
		}
		return available;
	}


	/**
	 * Check if backend is responding.
	 * 
	 * @return true if backend is responding
	 */
	static boolean isBackendResponding() {

		// create the task
		CheckBackendRespondingTask task = getInstance().new CheckBackendRespondingTask();

		// start the background task
		task.execute();

		// wait until finished
		while (!task.isFinished()) {
			try {
				Thread.sleep(50);
			}
			catch (InterruptedException e) {
			}
		}

		// get the result
		return task.isResponding();

	}

	/**
	 * An AsyncTask to test if the backend is responding in a background process. 
	 * This is needed to comply with Honeycomb Strict Mode wich doesn't allow 
	 * networking operations in the UI thread.
	 * <p>Call isResponding() at the end for the response
	 */
	private class CheckBackendRespondingTask extends AsyncTask<Void, Void, Void> {

		private boolean finished = false;
		private boolean responding = false;


		@Override
		protected Void doInBackground(Void... params) {

			BackendRequest request = new BackendRequest("checkresponding");
			BackendResponse response = new BackendResponse(request);
			
			if (response.isResponseSuccess()) {
				this.responding = true;
			}
			
			request.disconnect();
			this.finished=true;

			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			cancel(true);
		}


		private boolean isFinished() {
			return finished;
		}


		private boolean isResponding() {
			return responding;
		}

	}

	/**
	 * An AsyncTask to check if the cached uniqueid is present in the backend. 
	 * This is needed to comply with Honeycomb Strict Mode wich doesn't allow 
	 * networking operations in the UI thread.
	 * <p>Call isPresent() at the end for the response
	 */
	private class CheckUniqueidPresentTask extends AsyncTask<Void, Void, Void> {

		private boolean finished = false;
		private boolean present = false;


		@Override
		protected Void doInBackground(Void... params) {

			
			BackendRequest request = new BackendRequest("checkidpresent");
			BackendResponse response = new BackendResponse(request);

			if (response.isResponseSuccess()) {
				this.present = response.getBool("present");
			}
			
			request.disconnect();
			this.finished=true;
			
			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			cancel(true);
		}


		private boolean isFinished() {
			return finished;
		}


		private boolean isPresent() {
			return this.present;
		}

	}



//	
//	/**
//	 * Object representing a response from the Backend.
//	 */
//	private class BackendResponse {
//
//		private HttpURLConnection connection;
//		private HashMap<String, Object> responseMap = new HashMap<String, Object>();
//		private int httpResultCode = 0;
//
//
//		public BackendResponse(HttpURLConnection conn) {
//			super();
//			this.connection = conn;
//			init();
//		}
//
//
//		private void init() {
//
//			// send the request and retrieve http response code
//			try {
//				if (this.connection!=null) {
//					this.httpResultCode = this.connection.getResponseCode();					
//				}
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			// if the call succeeded, put all response headers in the map
//			if (isHttpSuccess()) {
//
//				Map<String, List<String>> responseMap = this.connection.getHeaderFields();
//				if (responseMap != null) {
//
//					String key = "";
//					String valueStr = "";
//
//					for (Iterator<String> iterator = responseMap.keySet().iterator(); iterator.hasNext();) {
//						key = (String) iterator.next();
//
//						List values = (List) responseMap.get(key);
//						if (values.size() > 0) {
//							Object value = values.get(0);
//							valueStr = Lib.getString(value);
//						}
//
//						this.responseMap.put(key, valueStr);
//
//					}
//
//				}
//
//			}
//
//		}
//
//
//		/**
//		 * @return true if the Http call succeeded (at protocol level)
//		 */
//		boolean isHttpSuccess() {
//			return ((this.httpResultCode>=200) && (this.httpResultCode < 300));
//		}
//
//
//		/**
//		 * @return true if the backend call succeeded (at backend logical)
//		 */
//		boolean isResponseSuccess() {
//			boolean success=false;
//			if (isHttpSuccess()) {
//				String string = getString("success");
//				if (Lib.getBool(string)) {
//					success = true;
//				}
//			}
//			
//			return success;
//		}
//		
//		/**
//		 * @return true if the app is activated
//		 */
//		boolean isActivated() {
//			return getBool(KEY_ACTIVATED);
//		}
//		
//		/**
//		 * @return the expiration date
//		 */
//		Date getExpirationDate() {
//			return getDate(KEY_EXPIRATION);
//		}
//		
//		/**
//		 * @return the app level
//		 */
//		Date getAppLevel() {
//			return getDate(KEY_LEVEL);
//		}
//
//		/**
//		 * Retuns a Date from the response map.
//		 * 
//		 * @param key the key to search
//		 * @return the date
//		 */
//		Date getDate(String key) {
//			return Lib.getDate(this.responseMap.get(key));
//		}
//
//
//		/**
//		 * Retuns a int from the response map.
//		 * 
//		 * @param key the key to search
//		 * @return the int
//		 */
//		int getInt(String key) {
//			return Lib.getInt(this.responseMap.get(key));
//		}
//
//
//		/**
//		 * Retuns a long from the response map.
//		 * 
//		 * @param key the key to search
//		 * @return the long
//		 */
//		long getLong(String key) {
//			return Lib.getLong(this.responseMap.get(key));
//		}
//
//
//		/**
//		 * Retuns a boolean from the response map.
//		 * 
//		 * @param key the key to search
//		 * @return the boolean
//		 */
//		boolean getBool(String key) {
//			return Lib.getBool(this.responseMap.get(key));
//		}
//
//
//		/**
//		 * Retuns a string from the response map.
//		 * 
//		 * @param key the key to search
//		 * @return the string
//		 */
//		String getString(String key) {
//			return Lib.getString(this.responseMap.get(key));
//		}
//
//	}


	/**
	 * Retrieve the SharedPreferences object. The preferences file is 
	 * created now if it doesn't exist.
	 * 
	 * @return the SharedPreferences object
	 */
	private static SharedPreferences getPrefs() {
		SharedPreferences prefs = getInstance().getContext().getSharedPreferences(SHARED_PREFS_FILE_NAME,
				Context.MODE_PRIVATE);
		return prefs;
	}


	static String getAppName() {
		return getInstance().appName;
	}


	/**
	 * Returns the URL of the backend
	 * 
	 * @return the backend URL
	 */
	static URL getBackendURL() {
		String string;
		URL url = null;
		string = getPrefs().getString(KEY_BACKEND_URL, "");
		try {
			url = new URL(string);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}


	/**
	 * Sets the URL of the backend.
	 * 
	 * @param the backend URL
	 */
	private static void setBackendURL(URL url) {
		if (url != null) {
			getPrefs().edit().putString(KEY_BACKEND_URL, url.toString()).commit();
		}
	}


	/**
	 * Sets the URL of the backend.
	 * 
	 * @param the backend URL as a string
	 */
	public static void setBackendURL(String urlString) {

		URL url;
		try {
			url = new URL(urlString);
			setBackendURL(url);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}


	/**
	 * Checks if the app is currently activated from cached data.
	 * @return whether the app is activated
	 */
	public static boolean isActivated() {
		return getPrefs().getBoolean(KEY_ACTIVATED, false);
	}


	/**
	 * Sets the activation flag in cached data.
	 * 
	 * @param activated the activation flag
	 */
	public static void setActivated(boolean activated) {
		getPrefs().edit().putBoolean(KEY_ACTIVATED, activated).commit();
	}


	/**
	 * Returns the current activation level from cached data.
	 * @return the current activation level
	 */
	public static int getLevel() {
		return getPrefs().getInt(KEY_LEVEL, 0);
	}


	/**
	 * Sets the level value in cached data.
	 * 
	 * @param level the level value
	 */
	public static void setLevel(int level) {
		getPrefs().edit().putInt(KEY_LEVEL, level).commit();
	}


	/**
	 * Returns the current expiration timestamp from cached data.
	 * @return the current expiration timestamp
	 */
	public static long getExpiration() {
		return getPrefs().getLong(KEY_EXPIRATION, 0);
	}


	/**
	 * Sets the expiration timestamp value in cached data.
	 * 
	 * @param expirationTs the expiration timestamp
	 */
	public static void setExpiration(long expirationTs) {
		getPrefs().edit().putLong(KEY_EXPIRATION, expirationTs).commit();
	}


	/**
	 * Returns the installation uuid from shared preferences.
	 * @return the installation uuid
	 */
	public static String getInstallationUuid() {
		return getPrefs().getString(KEY_INSTALLATION_UUID, "");
	}


	/**
	 * Sets the installation uuid in shared preferences.
	 * 
	 * @param uuidString the installation uuid string
	 */
	public static void setInstallationUuid(String uuidString) {
		getPrefs().edit().putString(KEY_INSTALLATION_UUID, uuidString).commit();
	}
	
	/**
	 * Returns the Unique Id from shared preferences.
	 * @return the Unique Id
	 */
	public static String getUniqueId() {
		return getPrefs().getString(KEY_UNIQUEID, "");
	}


	/**
	 * Sets the Unique Id in shared preferences.
	 * 
	 * @param uidString the Unique Id string
	 */
	public static void setUniqueId(String uidString) {
		getPrefs().edit().putString(KEY_UNIQUEID, uidString).commit();
	}



	/**
	 * Calculates a unique id made with a combination of the Unique Installation 
	 * Id (type UUID) generated at startup and a Unique Device Id calculated on the fly from
	 * some characteristics of the device.
	 * 
	 * <p>The returned id is tied to the installation AND to the the device; hence if you reinstall
	 * the app on the same device or copy the app to another device you get different ids.
	 * 
	 * @return the combined id
	 */
	private static String createUniqueId() {
		String installUUID = getPrefs().getString(KEY_INSTALLATION_UUID, "");
		String deviceUUID = new DeviceUuidFactory(getInstance().getContext()).getDeviceUuid().toString();

		// create a long string
		String longString = installUUID + deviceUUID;

		// strip "-"
		longString = longString.replace("-", "");
		;

		// compressing-encoding results in a string longer than the original! it is useless.
		// // byte array from long string
		// byte[] originalBytes = longString.getBytes();
		//
		// // compress to another byte array with DEFLATE
		// Deflater deflater = new Deflater();
		// deflater.setInput(originalBytes);
		// deflater.finish();
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// byte[] buf = new byte[8192];
		// while (!deflater.finished()) {
		// int byteCount = deflater.deflate(buf);
		// baos.write(buf, 0, byteCount);
		// }
		// deflater.end();
		// byte[] compressedBytes = baos.toByteArray();
		//
		// // Convert a compressed byte array to base64 string
		// String encoded = Base64.encodeToString(compressedBytes, Base64.DEFAULT);

		return longString;
	}


	private Context getContext() {
		return context;
	}


	// /**
	// * Create the Singleton instance of this class.
	// *
	// * @param ctx the context
	// * @param runnable the runnable to run when an Activation Cycle is finished
	// */
	// public static void newInstance(Context ctx, Runnable runnable) {
	// if (ACTIVATOR == null)
	// ACTIVATOR = new DroidActivator(ctx, runnable);
	// }// end of method

	/**
	 * Create the Singleton instance of this class.
	 * 
	 * @param ctx the context
	 * @param runnable the runnable to run when an Activation Cycle is finished
	 */
	public static void newInstance(Context ctx, Runnable runnable) {
		if (ACTIVATOR == null) {
			ACTIVATOR = new DroidActivator(ctx, runnable);
			ACTIVATOR.init();
		}
	}// end of method


	/**
	 * Return the Singleton instance of this class.
	 * @return the DroidActivator object<br>
	 */
	private static DroidActivator getInstance() {
		return ACTIVATOR;
	}// end of method

	private interface OnUpdateDoneListener {

		/**
		 * Called when the update operation is finished.
		 */
		public abstract void OnUpdateDone();

	}

}
