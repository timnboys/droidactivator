/*
 * This file is part of DroidActivator.
 * Copyright (C) 2012 algos.it
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.algos.droidactivator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.algos.droidactivator.resources.Strings;

/**
 * Main DroidActivator's class.
 * <p>This is instantiated by calling the newInstance() method.
 * <p>DroidActivator uses the Singleton pattern, so after instantiation you don't 
 * have to keep track of an instance and you make only static calls.
 * <p>Usage example:
 * <code>
 * <br>DroidActivator.newInstance(this, "http://151.10.128.32",new Runnable() {   
 * <br>	public void run() {startMyApp();}
 * <br>});
 * </code>
 */
public class DroidActivator {

	// a variable holding the Singleton instance of the Activator
	private static DroidActivator ACTIVATOR = null;

	// the global Application context, used to obtain Resources, SharedPreferences etc
	private Context context;

	// the backend address as supplied in the constructor
	private String address;
	
	// the application's Producer id
	private int producer_id;

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
	
	// the default connection timeout
	private int defaultConnectionTimeout;
	
	// the default socket timeout
	private int defaultSocketTimeout;

	// a view to display in the message area of the dialog instead of the default message view
	// if a custom view is specified, the custom view takes precedence over the custom text
	private View dialogCustomView;

	// a text to display in the message view instead of the standard message
	// if a custom view is specified, the custom view takes precedence over the custom text
	private String dialogCustomText;
	
	// a text for the Cancel button  in the dialog
	private String cancelButtonText;

	// a text for the Later button  in the dialog
	private String laterButtonText;
	
	// a text for the Activation Dialog title
	private String dialogTitle;


	
	// the shared preferences file name
	private static String SHARED_PREFS_FILE_NAME = "droidActivatorData";

	// keys to identify values
	// used both in bundles sent to the backend and in shared preferences
	static String KEY_ACTIVATED = "activated";
	static String KEY_LEVEL = "level";
	static String KEY_EXPIRATION = "expiration";
	static String KEY_INSTALLATION_UUID = "installation_UUID";
	static String KEY_UNIQUEID = "uniqueid";
	static String KEY_TS_FIRST_TEMP_ACTIVATION = "first_temp_activation"; // the unix timestamp of the first Temporary Activation
	static String KEY_USERID = "userid";// the user id entered in the last full activation
	static String KEY_EVENT_CODE = "eventcode";// the custom event code
	static String KEY_EVENT_DETAILS = "eventdetails";// the custom event details

	// the app name passed to the backend, defaults to the current App Name
	private String appName = "";


	// /**
	// * Constructor
	// */
	// private DroidActivator() {
	// super();
	// }

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
		
		// default connection timeout in millis
		setDefaultConnectionTimeout(2000);
		
		// default socket timeout in millis
		setDefaultSocketTimeout(5000);

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

		// Device change detection: if device changes, activation is revoked locally,
		// cached Unique Id is deleted, and the app has to be re-activated.
		String cachedUid = getUniqueId();
		if (!cachedUid.equals("")) {
			// If the cached Unique Id is present (activation already done), but it doesn't
			// correspond to the calculated Unique Id anymore, then device has changed.
			String currUid = calcUniqueId();
			if (!currUid.equals(cachedUid)) {
				setActivated(false);
				setUniqueId("");
			}
		}

	}


	/**
	 * Performs the Activation Cycle (update and check). 
	 * When completed, the Runnable you supplied at instantiation time is run. 
	 * 
	 * @param context the current context
	 */
	public static void doActivationCycle(Context context) {

		// performs an Activation Update
		// after the Activation Update the cached variables are updated
		doUpdate();

		// performs a Check (even if the update was unsuccessful)
		doCheck(context);

	}
	
	
	/**
	 * Sends a Custom Event. 
	 * 
	 * @param code custom event code
	 * @param details the event details
	 * @return true if the event has been acquired by the backend
	 */
	public static boolean sendCustomEvent(int code, String details) {
		boolean acquired = false;

		if (isBackendURLValid()) {

			if (isNetworkAvailable()) {

				if (isBackendResponding()) {

					acquired = sendEvent(code, details);

				}

			}

		}
		
		return acquired;

	}



	/**
	 * Performs the Activation Update operation. At the end, the UpdateDoneListener is notified.
	 * 
	 * @return true if the update was successful
	 */
	private static boolean doUpdate() {
		boolean success = false;

		if (isBackendURLValid()) {

			if (isNetworkAvailable()) {

				if (isBackendResponding()) {

					success = requestUpdate();

				}

			}

		}

		return success;

	}


	/**
	 * Performs the Check operation.
	 * After this operation the Runnable is always run.
	 * 
	 * @param context the current context
	 */
	private static void doCheck(Context context) {

		if (!isActivatedInCache()) {
			if (isBackendURLValid()) {
				// show the dialog, when dismissed the Runnable will ALWAYS be run
				openDialog(context, isUseridRequested());
			}
			else {
				runRunnable();
			}
		}
		else {
			runRunnable();
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
		else { // uniqueid is present in cached data
			if (isUniqueidPresentInBackend()) {// is uniqueid also present in the backend?
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
		ActivationDialog dialog = new ActivationDialog(context, useridRequested, isTemporaryActivationAvailable(),
				createDialogView(context));
		dialog.show();
	}


	/**
	 * Creates the View shown in the message area the dialog.
	 * 
	 * @param the context for the view if created here
	 * @return the message view
	 */
	private static View createDialogView(Context context) {
		View view;
		if (getInstance().dialogCustomView != null) {
			view = getInstance().dialogCustomView;
		}
		else {

			TextView tv = new TextView(context);
			String cText = getInstance().dialogCustomText;
			if (!Lib.getString(cText).equals("")) {
				tv.setText(cText);
			}
			else {
				tv.setText(Strings.dialog_message.get());
			}
			view = tv;

		}
		return view;
	}


	/**
	 * Requests an Activation to the backend.
	 * <p>Called by the Activation Dialog when Activate button is pressed.
	 * @param context the context for displaying the final message
	 * @param userId the activation userid to use.
	 * @param activationCode the activation code to use.
	 * @return a wrapper containin activation result information
	 */
	static ActivationResult requestActivation(Context context, String userId, String activationCode) {

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

		return new ActivationResult(task.isSuccessful(), task.getFailureCode());

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

			try {
				
//				BackendRequest request = new BackendRequest("activate");
//				request.setRequestProperty(KEY_UNIQUEID, this.uniqueId);
//				request.setRequestProperty("userid", this.userId);
//				request.setRequestProperty("activationcode", this.activationCode);
//				BackendResponse response = new BackendResponse(request);

				
				BackendRequest request = new BackendRequest("activate");
				request.setRequestProperty(KEY_UNIQUEID, this.uniqueId);
				request.setRequestProperty(KEY_USERID, this.userId);
				request.setRequestProperty("activationcode", this.activationCode);
				BackendResponse response = new BackendResponse(request);

				// write returned data in shared preferences
				if (response.isResponseSuccess()) {
					setActivated(true);
					setExpiration(response.getExpirationTime());
					setLevel(response.getLevel());
					setUniqueId(this.uniqueId);// cache the current Unique Id
					if (!this.userId.equals("")) {
						setActivationUserid(this.userId);// cache the user Id used for activation if present
					}
					this.successful = true;
				}
				else {
					String failureString = response.getString("failurecode");
					int code = 0;
					try {
						code = Integer.parseInt(failureString);
					}
					catch (Exception e) {
					}
					this.failureCode = code;
					this.successful = false;
				}

			}
			catch (Exception e) {
			}
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
		long seconds = getFirstTempActivationTS();
		if (seconds == 0) {
			Calendar c = Calendar.getInstance();
			seconds = c.getTimeInMillis()/1000;
			setFirstTempActivationTS(seconds);
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

			try {
				
//				BackendRequest request = new BackendRequest("update");
//				request.setRequestProperty(KEY_UNIQUEID, this.uniqueId);
//				request.setRequestProperty(KEY_USERID, getActivationUserId());
//				BackendResponse response = new BackendResponse(request);

				BackendRequest request = new BackendRequest("update");
				request.setRequestProperty(KEY_UNIQUEID, this.uniqueId);
				request.setRequestProperty(KEY_USERID, getActivationUserId());
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

			}
			catch (Exception e) {
			}
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
	 * Sends a Custom Event to the backend.
	 * @param code custom event code
	 * @param details the event details
	 * @return true if the event has been acquired by the backend
	 */
	private static boolean sendEvent(int code, String details) {
		SendEventTask task = null;

		// retrieve the cached Unique Id, create it now if not present
		String uniqueId = getUniqueId();
		if (uniqueId.equals("")) {
			uniqueId = calcUniqueId();
		}

		// create the task
		task = getInstance().new SendEventTask(uniqueId, code, details);

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

		return task.isSuccessful();

	}



	/**
	 * An AsyncTask to send a Custom Event in a background process. 
	 * This is needed to comply with Honeycomb Strict Mode wich doesn't allow 
	 * networking operations in the UI thread.
	 * <p>When finished, call isSuccessul() for the result.
	 */
	private class SendEventTask extends AsyncTask<Void, Void, Void> {

		private boolean finished = false;
		private String uniqueId;
		private int code;
		private String details;
		private boolean successful;


		public SendEventTask(String uniqueId, int code, String details) {
			super();
			this.uniqueId = uniqueId;
			this.code = code;
			this.details = details;
		}


		@Override
		protected Void doInBackground(Void... params) {

			try {

				BackendRequest request = new BackendRequest("event");
				request.setRequestProperty(KEY_UNIQUEID, this.uniqueId);
				request.setRequestProperty(KEY_EVENT_CODE, ""+this.code);
				request.setRequestProperty(KEY_EVENT_DETAILS, this.details);
				BackendResponse response = new BackendResponse(request);

				if (response.isResponseSuccess()) {
					this.successful = true;
				}
				else {
					this.successful = false;
				}

			}
			catch (Exception e) {
			}
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
		
		boolean finished = task.isFinished();

		// wait until finished
		while (!task.isFinished()) {
			try {
				Thread.sleep(50);
			}
			catch (InterruptedException e) {
			}
		}
		
		finished = task.isFinished();

		// get the result
		boolean responding = task.isResponding();
		return responding;

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

			try {
				BackendRequest request = new CheckRespondingRequest();
				BackendResponse response = new BackendResponse(request);

				if (response.isResponseSuccess()) {
					this.responding = true;
				}

			}
			catch (Exception e) {
				int a = 87;
			}
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

			try {
				
				BackendRequest request = new BackendRequest("checkuidpresent");
				request.setRequestProperty(KEY_UNIQUEID, getUniqueId());
				BackendResponse response = new BackendResponse(request);

				if (response.isResponseSuccess()) {
					this.present = true;
				}

			}
			catch (Exception e) {
			}
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
		SharedPreferences prefs = getContext().getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
		return prefs;
	}


	static String getAppName() {
		return getInstance().appName;
	}
	
	static int getProducerId() {
		return getInstance().producer_id;
	}



	/**
	 * Returns the URL of the backend.
	 * Performs some validity checking
	 * @return the backend URL
	 */
	static URL getBackendURL() {
		URL url = null;

		String address = getInstance().address;
		if (address != null) {
			if (address.length() >= 16) { // cant be less than http://x.xx:0000
				if (address.contains(".")) {// at least one dot is mandatory
					if (address.contains(":")) {// at least one : is mandatory
						
						// strip ending slashes
						while ((address.charAt(address.length()-1))=='/') {
							address = address.substring(0,address.length()-1);
						}
						
						String urlString = address + "/da_backend/check.php";
						try {
							url = new URL(urlString);
						}
						catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		return url;
	}
	
	/**
	 * Returns the URI of the backend.
	 * @return the backend URI
	 */
	static URI getBackendURI() {
		URI uri = null;
		try {
			uri = getBackendURL().toURI();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}



	static void runRunnable() {
		getRunnable().run();
	}


	/**
	 * Checks if the app is currently activated from cached data.
	 * @return whether the app is activated
	 */
	private static boolean isActivatedInCache() {
		return getPrefs().getBoolean(KEY_ACTIVATED, false);
	}


	/**
	 * Check if the registered Backend URL is valid
	 */
	private static boolean isBackendURLValid() {
		boolean valid = false;
		URL url = getBackendURL();
		if (url != null) {
			try {
				url.toURI();// performs some more validation...
				valid = true;// if we reach this line is valid.
			}
			catch (URISyntaxException e) {
			}
		}
		return valid;
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
			activated = true;
		}
		else {
			activated = isActivatedInCache();
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
	public static void setExpiration(long expirationTs) {
		getPrefs().edit().putLong(KEY_EXPIRATION, expirationTs).commit();
	}
	
	/**
	 * Returns the current expiration date from cached data.
	 * @return the current expiration date, null if not specified
	 */
	public static Date getExpirationDate() {
		Date date=null;
		long secs = getPrefs().getLong(KEY_EXPIRATION, 0);
		if (secs>0) {
			date = new Date(secs*1000);
		}
		return date;
	}


	/**
	 * Saves the timestamp of the first Temporary Activation in shared preferences.
	 * 
	 * @param unix timestamp of the first Temporary Activation
	 */
	private static void setFirstTempActivationTS(long timestamp) {
		getPrefs().edit().putLong(KEY_TS_FIRST_TEMP_ACTIVATION, timestamp).commit();
	}


	/**
	 * Returns the timestamp of the first Temporary Activation from shared preferences.
	 * 
	 * @return the unix timestamp of the first Temporary Activation
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
		getInstance().temporarilyActivated = flag;
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
	 * Returns the User Id used for the last activation from shared preferences.
	 * @return the User Id used for the last activation
	 */
	public static String getActivationUserId() {
		return getPrefs().getString(KEY_USERID, "");
	}


	/**
	 * Sets the User Id used for the last activation in shared preferences.
	 * 
	 * @param userId used for the last activation
	 */
	public static void setActivationUserid(String userId) {
		getPrefs().edit().putString(KEY_USERID, userId).commit();
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


	static Runnable getRunnable() {
		return getInstance().runnable;
	}


	/**
	 * Control the availability of the Temporary Activation mechanism.
	 * 
	 * @param true if you want it to be available to the user, false otherwise
	 */
	public static void setTemporaryActivationAvailable(boolean available) {
		getInstance().temporaryActivationAvailable = available;
	}


	private static boolean isTemporaryActivationAvailable() {
		return getInstance().temporaryActivationAvailable;
	}


	/**
	 * Maximum number of days the user can delay activation by pressing "Later".
	 * 
	 * @param days number of days
	 */
	public static void setMaximumActivationDelay(int days) {
		getInstance().maxActivationDelay = days;
	}


	/**
	 * Maximum number of days the user can delay activation by pressing "Later".
	 * 
	 * @return max number of days
	 */
	static int getMaximumActivationDelay() {
		return getInstance().maxActivationDelay;
	}


	/**
	 * Number of seconds the user must wait before "Later" becomes enabled
	 * 
	 * @param days number of seconds
	 */
	public static void setTemporaryWaitTime(int seconds) {
		getInstance().temporaryWaitTime = seconds;
	}


	/**
	 * Retrieve the number of seconds the user must wait before "Later" becomes enabled.
	 * 
	 * @return number of seconds
	 */
	static int getTemporaryWaitTime() {
		return getInstance().temporaryWaitTime;
	}

	/**
	 * Sets the default connection timeout in milliseconds.
	 * <p>A connection timeout can occurr if the host machine is active, 
	 * but no service is responding
	 * 
	 * @param days number of seconds
	 */
	public static void setDefaultConnectionTimeout(int millis) {
		getInstance().defaultConnectionTimeout = millis;
	}


	/**
	 * @return the default connection timeout in millis
	 */
	static int getDefaultConnectionTimeout() {
		return getInstance().defaultConnectionTimeout;
	}
	
	/**
	 * Sets the default socket timeout in milliseconds.
	 * <p>A socket timeout can occurr if the machine is down, 
	 * or if there is no route to that host. 
	 * @param days number of seconds
	 */
	public static void setDefaultSocketTimeout(int millis) {
		getInstance().defaultSocketTimeout = millis;
	}


	/**
	 * @return the default socket timeout in millis
	 */
	static int getDefaultSocketTimeout() {
		return getInstance().defaultSocketTimeout;
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
	public static void setDialogCustomView(View view) {
		getInstance().dialogCustomView = view;
	}
	
	/**
	 * Sets the text to use for the Cancel button in the Activation dialog 
	 * 
	 * @param text the text for the Cancel button
	 */
	public static void setCancelButtonText(String text) {
		getInstance().cancelButtonText = text;
	}

	/**
	 * @return the text for the Cancel button
	 */
	static String getCancelButtonText() {
		String text = getInstance().cancelButtonText;
		if ((text == null) || (text.equals(""))) {
			text = Strings.cancel_button_text.get();
		}
		return text;
	}
	
	/**
	 * Sets the text to use for the Later button in the Activation dialog 
	 * 
	 * @param text the text for the Later button
	 */
	public static void setLaterButtonText(String text) {
		getInstance().laterButtonText = text;
	}

	/**
	 * @return the text for the Later button
	 */
	static String getLaterButtonText() {
		String text = getInstance().laterButtonText;
		if ((text == null) || (text.equals(""))) {
			text = Strings.temporary_button_text.get();
		}
		return text;

	}
	
	/**
	 * Sets the text to use for the Activation dialog title
	 * 
	 * @param text the text for the Activation dialog title
	 */
	public static void setDialogTitle(String text) {
		getInstance().dialogTitle = text;
	}

	/**
	 * @return the text for the Activation dialog title
	 */
	static String getDialogTitle() {
		String text = getInstance().dialogTitle;
		if ((text == null) || (text.equals(""))) {
			text = Strings.dialog_title.get();
		}
		return text;

	}


	/**
	 * Sets a text to display in the message view instead of the standard message.
	 * <p>If a custom view is specified, the custom view takes precedence 
	 * over the custom text.
	 * 
	 * @param text the custom text
	 */
	public static void setDialogCustomText(String text) {
		getInstance().dialogCustomText = text;
	}


	/**
	 * Create the Singleton instance of this class.
	 * 
	 * @param ctx the context
	 * @param address the backend address
	 * @param producerId your producer Id
	 * @param runnable the runnable to run to start your app when an Activation Cycle is completed.
	 * The producer id identifies the application producer. Can be left to 0 if DroidAcrivator
	 * is used anly for applications of the same producer.
	 * <p>Valid backend addresses identify the Host and are in the form "http://123.123.123.123" 
	 * or "http://mydomain.com:12100". Don't add the path after the host, it's automatic.
	 */
	public static void newInstance(Context ctx, String address, int producerId, Runnable runnable) {
		if (ACTIVATOR == null) {
			ACTIVATOR = new DroidActivator();
		}
		ACTIVATOR.context = ctx.getApplicationContext(); // this survives for all the app lifetime!
		ACTIVATOR.address = address;
		ACTIVATOR.producer_id = producerId;
		ACTIVATOR.runnable = runnable;
		ACTIVATOR.init();

	}// end of method
	
	
	/**
	 * Create the Singleton instance of this class.
	 * <p>Uses a default value of 0 for the ProducerId
	 * @param ctx the context
	 * @param address the backend address
	 * @param runnable the runnable to run to start your app when an Activation Cycle is completed.
	 * The producer id identifies the application producer. Can be left to 0 if DroidAcrivator
	 * is used anly for applications of the same producer.
	 * <p>Valid backend addresses are in the form "http://123.123.123.123" or "http://mydomain.com:12100".
	 */
	public static void newInstance(Context ctx, String address, Runnable runnable) {
		newInstance(ctx, "droidactivator.algos.it", 0, runnable);
	}// end of method

	
	/**
	 * Create the Singleton instance of this class.
	 * <p>Uses the public DroidActivator's backend on the web
	 * @param ctx the context
	 * @param producerId your producer Id
	 * @param runnable the runnable to run to start your app when an Activation Cycle is completed.
	 */
	public static void newInstance(Context ctx, int producerId, Runnable runnable) {
		newInstance(ctx, "droidactivator.algos.it", producerId, runnable);
	}// end of method


	/**
	 * Return the Singleton instance of this class.
	 * @return the DroidActivator object<br>
	 */
	private static DroidActivator getInstance() {
		return ACTIVATOR;
	}// end of method

}
