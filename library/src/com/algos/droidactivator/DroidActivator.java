package com.algos.droidactivator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.algos.droidactivator.dialog.InfoDialog;

/**
 * Main DroidActivator's class.
 * <p>This is istantiated by calling the newInstance() method.
 * <p>DroidActivator uses the Singleton pattern, so after instantiation you don't 
 * have to keep track of an instance and can make only static calls.
 * Usage example:
 * <code>
 * DroidActivator.newInstance(this, "http://151.10.128.32:8080",new Runnable() {   
 *         public void run() {startMyApp();}
 * });
 * </code>
 */
public class DroidActivator {

	// a variable holding the Singleton instance of the Activator
	private static DroidActivator ACTIVATOR = null;

	// the global Application context, used to obtain Resources, SharedPreferences etc
	private Context context;
	
	// the backend address as supplied in the constructor
	private String address;
	
	// the runnable to run to start the app when an Activation Cycle is completed
	private Runnable runnable;

	// flag to control if the Temporary Activation mechanism is available
	private boolean temporaryActivationAvailable;
	
	// maximum number of days activation can be delayed pressing "Later"
	private int maxActivationDelay;
	
	// Number of seconds the user must wait before "Later" becomes enabled
	private int temporaryWaitTime;
	
	// flag turned on when a Temporary Activation is issued.
	// if this flag is on, the isActivated() method returns always true
	private boolean temporarilyActivated;
	
	// a view to display in the message area of the dialog instead of the default message view
	// if a custom view is specified, the custom view takes precedence over the custom text
	private View dialogCustomView;
	
	// a text to display in the message view instead of the standard message
	// if a custom view is specified, the custom view takes precedence over the custom text
	private String dialogCustomText;

	// the shared preferences file name
	private static String SHARED_PREFS_FILE_NAME = "droidActivatorData";

	// keys to identify values
	// used both in bundles sent to the backend and in shared preferences
	static String KEY_ACTIVATED = "activated";
	static String KEY_LEVEL = "level";
	static String KEY_EXPIRATION = "expiration";
	static String KEY_INSTALLATION_UUID = "installation_UUID";
	static String KEY_UNIQUEID = "uniqueid";
	static String KEY_TS_FIRST_TEMP_ACTIVATION = "first_temp_activation";	// the timestamp of the first Temporary Activation
	//static String KEY_BACKEND_URL = "backend_url";
	//static String KEY_MAX_ACTIVATION_DELAY_DAYS = "max_activation_delay";

	// the app name passed to the backend, defaults to the current App Name
	private String appName = "";


	// private ArrayList<OnActivationDoneListener> activationDoneListeners = new ArrayList<OnActivationDoneListener>();

	/**
	 * Constructor
	 */
	private DroidActivator(Context context, String address, Runnable runnable) {
		super();
		this.context = context.getApplicationContext(); // this survives for all the app lifetime!
		this.address = address;
		this.runnable = runnable;
	}


	/**
	 * init() is called by newInstance() after the ACTIVATOR variable is set!
	 */
	private void init() {
		
		// default for temporary activation mechanism availability
		setTemporaryActivationAvailable(true);

		// default number of days for maximum activation delay
		setMaximumActivationDelay(7);
		
		// default number of seconds user must wait before "Later" button becomes enabled
		setTemporaryWaitTime(10);
		
		// set the temporary activation flag to off
		setTemporarilyActivated(false);

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
		
		// Device change detection: if device chenges, activation data is wiped out.
		// If the cached UniqueId is present (activation already done), but it doesn't 
		// correspond to the calculated Unique Id anymore, then device has changed.
		// The app has to be re-activated. Revoke activation and delete Unique Id.
		String cachedUid = getUniqueId();
		if (!cachedUid.equals("")) {
			String currUid = calcUniqueId();
			if (!currUid.equals(cachedUid)) {
				setActivated(false);
				setUniqueId("");
			}
		}

		
	}


	/**
	 * Performs the Activation Cycle (update and check). 
	 * When completed, your Runnable is run. 
	 * 
	 * @param context the current context
	 */
	public static void doActivationCycle(Context context) {

		// performs an Activation Update
		// after the Activation Update the cached variables are updated
		doUpdate();

		// performs a Check (even if the update vas unsuccessful)
		doCheck(context);

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

				success = requestUpdate();

			}

		}

		return success;

	}


	/**
	 * Performs the Check operation.
	 * 
	 * @param context the current context
	 */
	private static void doCheck(Context context) {

		if (!isActivatedInCache()) {
			openDialog(context, isUseridRequested());
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
	 * Presents the Activation dialog.
	 * 
	 * @param context the current context
	 * @param context useridRequested true if userid must be requested by the dialog
	 */
	private static void openDialog(Context context, boolean useridRequested) {
		ActivationDialog dialog = new ActivationDialog(context, useridRequested, isTemporaryActivationAvailable(), createDialogView(context));
		dialog.show();
	}
	
	/**
	 * Creates the View shown in the message area the dialog.
	 * 
	 * @param the context for the view if created here
	 * @return the message view
	 */
	private static View createDialogView(Context context){
		View view;
		if (getInstance().dialogCustomView!=null) {
			view=getInstance().dialogCustomView;
		}
		else {
			
			TextView tv = new TextView(context);
			String cText = getInstance().dialogCustomText;
			if (!Lib.getString(cText).equals("")) {
				tv.setText(cText);
			}
			else {
				tv.setText(R.string.dialog_message);
			}
			view=tv;

		}
		return view;
	}


	/**
	 * Requests an Activation to the backend.
	 * <p>Called by the Activation Dialog when Activate button is pressed.
	 * @param context the context for displaying the final message
	 * @param userId the activation userid to use.
	 * @param activationCode the activation code to use.
	 * @return true if the ativation succeeded
	 */
	static boolean requestActivation(Context context, String userId, String activationCode) {

		// retrieve the Unique Id (the cached one if present, otherwise calculated on the fly)
		String uniqueId = getUniqueId();
		if (uniqueId.equals("")) {
			uniqueId = calcUniqueId();
		}

		// create the task
		RequestActivationTask task = getInstance().new RequestActivationTask(uniqueId, userId, activationCode);

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

		// final message
		InfoDialog dialog = new InfoDialog(context);
		if (task.isSuccessful()) {
			dialog.setTitle(R.string.congratulations);
			dialog.setMessage(getContext().getString(R.string.app_successfully_activated));
		}
		else {
			int failureCode = task.getFailureCode();
			String failureString = getFailureString(failureCode);
			dialog.setTitle(R.string.activation_error);
			dialog.setMessage(failureString);
		}
		dialog.show();

		return task.isSuccessful();

	}


	/**
	 * @param failureCode a failure code
	 * @return a failure string in the current language
	 */
	private static String getFailureString(int failureCode) {
		return "generic failure";
	}

	/**
	 * An AsyncTask to request the activation in a background process. 
	 * This is needed to comply with Honeycomb Strict Mode wich doesn't allow 
	 * networking operations in the UI thread.
	 */
	private class RequestActivationTask extends AsyncTask<Void, Void, Void> {

		private boolean finished = false;
		private String uniqueId;
		private String userId;
		private String activationCode;
		private boolean successful;
		private int failureCode;


		public RequestActivationTask(String uniqueId, String userId, String activationCode) {
			super();
			this.uniqueId = uniqueId;
			this.userId = userId;
			this.activationCode = activationCode;
		}


		@Override
		protected Void doInBackground(Void... params) {

			BackendRequest request = new BackendRequest("activate");
			request.setRequestProperty(KEY_UNIQUEID, this.uniqueId);
			request.setRequestProperty("userid", this.userId);
			request.setRequestProperty("activationcode", this.activationCode);
			BackendResponse response = new BackendResponse(request);

			// write returned data in shared preferences
			if (response.isResponseSuccess()) {
				setActivated(true);
				setExpiration(response.getExpirationTime());
				setLevel(response.getLevel());
				setUniqueId(this.uniqueId);// this can be new, write it back!
				this.successful = true;
			}
			else {
				setActivated(false);
				this.failureCode = response.getInt("failurecode");
				this.successful = false;
			}

			request.disconnect();
			this.finished = true;

			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			cancel(true);
		}


		private boolean isFinished() {
			return this.finished;
		}


		private boolean isSuccessful() {
			return this.successful;
		}


		private int getFailureCode() {
			return this.failureCode;
		}

	}


	/**
	 * Performs a Temporary Activation.
	 * <p>Called by the Activation Dialog when Later button is pressed.
	 */
	static void doTemporaryActivation() {
		
		// turns the temporary activation flag on
		setTemporarilyActivated(true);
		
		// save the time of the first temporary activation
		long ts = getFirstTempActivationTS();
		if (ts==0) {
			Calendar c = Calendar.getInstance(); 
			ts = c.getTimeInMillis();
			setFirstTempActivationTS(ts);
		}

	}


	/**
	 * Requests an Update to the backend.
	 * @return true if the update succeeded
	 */
	private static boolean requestUpdate() {

		RequestUpdateTask task = null;
		boolean successful = false;

		// retrieve the cached Unique Id
		String uniqueId = getUniqueId();

		if (!uniqueId.equals("")) {

			// create the task
			task = getInstance().new RequestUpdateTask(uniqueId);

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

			// retrieve result
			successful = task.isSuccessful();

		}

		return successful;

	}

	/**
	 * An AsyncTask to request an Activation Update in a background process. 
	 * This is needed to comply with Honeycomb Strict Mode wich doesn't allow 
	 * networking operations in the UI thread.
	 * <p>When finished, call isSuccessul() for the result.
	 */
	private class RequestUpdateTask extends AsyncTask<Void, Void, Void> {

		private boolean finished = false;
		private String uniqueId;
		private boolean successful;


		public RequestUpdateTask(String uniqueId) {
			super();
			this.uniqueId = uniqueId;
		}


		@Override
		protected Void doInBackground(Void... params) {

			BackendRequest request = new BackendRequest("update");
			request.setRequestProperty(KEY_UNIQUEID, this.uniqueId);
			BackendResponse response = new BackendResponse(request);

			// write returned data in shared preferences
			if (response.isResponseSuccess()) {
				setActivated(response.isActivated());
				setExpiration(response.getExpirationTime());
				setLevel(response.getLevel());
				this.successful = true;
			}
			else {
				this.successful = false;
			}

			request.disconnect();
			this.finished = true;

			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			cancel(true);
		}


		private boolean isFinished() {
			return this.finished;
		}


		private boolean isSuccessful() {
			return this.successful;
		}

	}


	/**
	 * Check if network is configured and connected.
	 * 
	 * @return true if network is connected
	 */
	static boolean isNetworkAvailable() {
		boolean available = false;
		Object service = getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
			this.finished = true;

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
			request.setRequestProperty(KEY_UNIQUEID, getUniqueId());
			BackendResponse response = new BackendResponse(request);

			if (response.isResponseSuccess()) {
				this.present = true;
			}

			request.disconnect();
			this.finished = true;

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


	/**
	 * Retrieve the SharedPreferences object. The preferences file is 
	 * created now if it doesn't exist.
	 * 
	 * @return the SharedPreferences object
	 */
	private static SharedPreferences getPrefs() {
		SharedPreferences prefs = getContext().getSharedPreferences(SHARED_PREFS_FILE_NAME,
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
		URL url=null;
		
		String urlString = getInstance().address+"/activator/activation/check";
		try {
			url = new URL(urlString);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return url;
	}
	
	static void runRunnable(){
		getRunnable().run();
	}


//	/**
//	 * Sets the URL of the backend.
//	 * 
//	 * @param the backend URL
//	 */
//	private static void setBackendURL(URL url) {
//		if (url != null) {
//			getPrefs().edit().putString(KEY_BACKEND_URL, url.toString()).commit();
//		}
//	}

	
//	/**
//	 * Sets the backend address and port.
//	 * <p>Valid address are in the form "http://123.123.123.123:8080" or "http://mydomain.com:8081".
//	 * <p>The default backend port is 8080
//	 * 
//	 * @param the backend address 
//	 */
//	private static void setBackendAddress(String address) {
//
//		String urlString = address+"/activator/activation/check";
//		setBackendURL(urlString);
//		
//	}


//	/**
//	 * Sets the URL of the backend.
//	 * 
//	 * @param the backend URL as a string
//	 */
//	private static void setBackendURL(String urlString) {
//
//		URL url;
//		try {
//			url = new URL(urlString);
//			setBackendURL(url);
//		}
//		catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//
//	}


	/**
	 * Checks if the app is currently activated from cached data.
	 * @return whether the app is activated
	 */
	private static boolean isActivatedInCache() {
		return getPrefs().getBoolean(KEY_ACTIVATED, false);
	}

	/**
	 * Checks if the app is currently activated.
	 * <p>Uses a combination of cached data and temporary activation.
	 * <p>If Temporary Activation is on, always returns true. If off, lloks in cached data.
	 * @return whether the app is activated
	 */
	public static boolean isActivated() {
		boolean activated;
		if (isTemporarilyActivated()) {
			activated=true;
		}
		else {
			activated=isActivatedInCache();
		}
		return activated;
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
	static void setExpiration(long expirationTs) {
		getPrefs().edit().putLong(KEY_EXPIRATION, expirationTs).commit();
	}
	
	/**
	 * Saves the timestamp of the first Temporary Activation in shared preferences.
	 * 
	 * @param timestamp of the first Temporary Activation
	 */
	private static void setFirstTempActivationTS(long timestamp) {
		getPrefs().edit().putLong(KEY_TS_FIRST_TEMP_ACTIVATION, timestamp).commit();
	}
	
	/**
	 * Returns the timestamp of the first Temporary Activation from shared preferences.
	 * 
	 * @return the timestamp of the first Temporary Activation
	 */
	static long getFirstTempActivationTS() {
		return getPrefs().getLong(KEY_TS_FIRST_TEMP_ACTIVATION, 0);
	}

	
	/**
	 * Sets the temporary activation flag.
	 * 
	 * @param flag the temporary activation flag
	 */
	public static void setTemporarilyActivated(boolean flag) {
		getInstance().temporarilyActivated=flag;
	}

	
	/**
	 * Checks if the activation is temporary.
	 * 
	 * @return true if the activation is temporary
	 */
	public static boolean isTemporarilyActivated() {
		return getInstance().temporarilyActivated;
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
	private static String calcUniqueId() {
		String installUUID = getPrefs().getString(KEY_INSTALLATION_UUID, "");
		String deviceUUID = new DeviceUuidFactory(getContext()).getDeviceUuid().toString();

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


	private static Context getContext() {
		return getInstance().context;
	}

	private static Runnable getRunnable() {
		return getInstance().runnable;
	}

	/**
	 * Control the availability of the Temporary Activation mechanism.
	 * 
	 * @param true if you want it to be available to the user, false otherwise
	 */
	public static void setTemporaryActivationAvailable(boolean available) {
		getInstance().temporaryActivationAvailable=available;
	}
	
	private static boolean isTemporaryActivationAvailable(){
		return getInstance().temporaryActivationAvailable;
	}
	
	/**
	 * Maximum number of days the user can delay activation by pressing "Later".
	 * 
	 * @param days number of days
	 */
	public static void setMaximumActivationDelay(int days){
		getInstance().maxActivationDelay=days;
	}
	
	/**
	 * Maximum number of days the user can delay activation by pressing "Later".
	 * 
	 * @return max number of days
	 */
	static int getMaximumActivationDelay(){
		return getInstance().maxActivationDelay;
	}
	
	
	/**
	 * Number of seconds the user must wait before "Later" becomes enabled
	 * 
	 * @param days number of seconds
	 */
	public static void setTemporaryWaitTime(int seconds){
		getInstance().temporaryWaitTime=seconds;
	}
	
	/**
	 * Retrieve the number of seconds the user must wait before "Later" becomes enabled.
	 * 
	 * @return number of seconds
	 */
	static int getTemporaryWaitTime(){
		return getInstance().temporaryWaitTime;
	}


	// a view to display in the message area of the dialog instead of the default message view
	// if a custom view is specified, the custom view takes precedence over the custom text

	/**
	 * Sets a view to be displayed in the message area of the 
	 * dialog instead of the default message view.
	 * <p>If a custom view is specified, the custom view takes precedence 
	 * over the custom text.
	 * 
	 * @param view the custom view
	 */
	public static void setDialogCustomView(View view){
		getInstance().dialogCustomView=view;
	}
	
	
	/**
	 * Sets a text to display in the message view instead of the standard message.
	 * <p>If a custom view is specified, the custom view takes precedence 
	 * over the custom text.
	 * 
	 * @param text the custom text
	 */
	public static void setDialogCustomText(String text){
		getInstance().dialogCustomText=text;
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
	 * @param address the backend address
	 * @param runnable the runnable to run to start your app when an Activation Cycle is completed.
	 * <p>Valid backend addresses are in the form "http://123.123.123.123:8080" or 
	 * "http://mydomain.com:8081".
	 * <p>The default backend port is 8080.
	 */
	public static void newInstance(Context ctx, String address, Runnable runnable) {
		if (ACTIVATOR == null) {
			ACTIVATOR = new DroidActivator(ctx, address, runnable);
		}
		ACTIVATOR.init();

	}// end of method


	/**
	 * Return the Singleton instance of this class.
	 * @return the DroidActivator object<br>
	 */
	private static DroidActivator getInstance() {
		return ACTIVATOR;
	}// end of method


}
