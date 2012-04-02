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

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.algos.droidactivator.dialog.InfoDialog;
import com.algos.droidactivator.dialog.WarningDialog;
import com.algos.droidactivator.resources.DroidActivatorIcon;
import com.algos.droidactivator.resources.GreenCheck48Icon;
import com.algos.droidactivator.resources.RedCross48Icon;
import com.algos.droidactivator.resources.Strings;
import com.algos.droidactivator.resources.WarningIcon;

class ActivationDialog extends Dialog {

	boolean useridRequested;
	boolean temporaryActivationAvailable;
	private Button activateButton;
	private Button laterButton;
	private boolean waitTimeElapsed = false; // turned on when the wait time to press Later has elapsed
	private EditText inputUseridField;
	private EditText inputCodeField;
	private View messageView;


	/**
	 * @param context the context
	 * @param useridRequested true if the userid field is requested
	 * @param temporaryActivationAvailable whether the Temporary Activation option should be available
	 * @param messageView the view to show in the message area of the dialog
	 */
	ActivationDialog(Context context, boolean useridRequested, boolean temporaryActivationAvailable, View messageView) {
		super(context);
		this.useridRequested = useridRequested;
		this.temporaryActivationAvailable = temporaryActivationAvailable;
		this.messageView = messageView;
		init();
	}


	private void init() {

		// never show the soft imput area (soft keyboard) when the window receives focus!
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// call this before setting content!
		requestWindowFeature(Window.FEATURE_LEFT_ICON);

		// Android bug: this line should go here but if i put it here the icon is invisibe!
		// setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.warning_icon);

		setTitle(DroidActivator.getDialogTitle());
		setContentView(createContentView());

		// Android bug: must be called after setContentView()!
		setFeatureDrawable(Window.FEATURE_LEFT_ICON, WarningIcon.getDrawable());
		// setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.droidactivator_warning_icon);

		// back is disabled in this dialog
		setCancelable(false);

		// can't cancel touching outside!
		setCanceledOnTouchOutside(false);

	}


	@Override
	protected void onStart() {
		super.onStart();
		sync();
	}


	/**
	 * Creates the ContentView for the dialog.
	 * @return the v
	 */
	private View createContentView() {

		// main container
		LinearLayout vLayout = new LinearLayout(getContext());
		vLayout.setOrientation(LinearLayout.VERTICAL);
		vLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		vLayout.setPadding(10, 10, 10, 10);

		// add message view
		vLayout.addView(this.messageView);

		// add input panel
		vLayout.addView(createInputPanel());

		// add button panel
		vLayout.addView(createButtonPanel());

		// add bottom panel
		vLayout.addView(createBottomPanel());

		return vLayout;

	}


	/**
	 * Creates the input panel
	 */
	private View createInputPanel() {
		TextView tv;
		EditText et;
		int wdp;
		int widthPx;
		Resources r = getContext().getResources();
		InputFilter[] filterArray;

		// input userid panel
		LinearLayout useridPanel = new LinearLayout(getContext());
		useridPanel.setOrientation(LinearLayout.VERTICAL);
		useridPanel
				.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		// input userid label
		tv = new TextView(getContext());
		tv.setText(Strings.input_userid_label.get());
		useridPanel.addView(tv);

		// input userid field
		wdp = 240;
		widthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wdp, r.getDisplayMetrics());
		// widthPx=LayoutParams.MATCH_PARENT;

		et = new EditText(getContext());
		et.setLayoutParams(new LinearLayout.LayoutParams(widthPx, LayoutParams.WRAP_CONTENT));
		et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}


			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}


			@Override
			public void afterTextChanged(Editable s) {
				sync();
			}
		});
		this.inputUseridField = et;
		useridPanel.addView(et);

		// input code panel
		LinearLayout codePanel = new LinearLayout(getContext());
		codePanel.setOrientation(LinearLayout.VERTICAL);
		codePanel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		// "input code" label
		tv = new TextView(getContext());
		tv.setText(Strings.input_code_label.get());
		codePanel.addView(tv);

		// input code field
		wdp = 240;
		widthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wdp, r.getDisplayMetrics());

		et = new EditText(getContext());
		et.setLayoutParams(new LinearLayout.LayoutParams(widthPx, LayoutParams.WRAP_CONTENT));
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(8);
		et.setFilters(filterArray);
		et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}


			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}


			@Override
			public void afterTextChanged(Editable s) {
				sync();
			}
		});
		this.inputCodeField = et;
		codePanel.addView(et);

		// horizontal panel with centered userid
		LinearLayout centeredUseridPanel = new LinearLayout(getContext());
		centeredUseridPanel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		centeredUseridPanel.setOrientation(LinearLayout.HORIZONTAL);
		centeredUseridPanel.addView(new ElasticSpacer(getContext()));
		centeredUseridPanel.addView(useridPanel);
		centeredUseridPanel.addView(new ElasticSpacer(getContext()));

		// horizontal panel with centered code
		LinearLayout centeredCodePanel = new LinearLayout(getContext());
		centeredCodePanel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		centeredCodePanel.setOrientation(LinearLayout.HORIZONTAL);
		centeredCodePanel.addView(new ElasticSpacer(getContext()));
		centeredCodePanel.addView(codePanel);
		centeredCodePanel.addView(new ElasticSpacer(getContext()));

		// main panel
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		layout.setPadding(0, 8, 0, 10);
		//layout.setPadding(0, 0, 0, 0);
		if (isUseridRequested()) {
			layout.addView(centeredUseridPanel);
			layout.addView(new Spacer(getContext(), 0, 6));
		}
		layout.addView(centeredCodePanel);

		return layout;

	}


	/**
	 * Creates the button panel
	 */
	private View createButtonPanel() {
		String text;
		
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		Button button;

		// cancel button
		button = new DialogButton(getContext(), DroidActivator.getCancelButtonText(), new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelButtonPressed();
			}
		});
		layout.addView(button);

		// later button
		if (this.temporaryActivationAvailable) {
			button = new DialogButton(getContext(),DroidActivator.getLaterButtonText(), new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					laterButtonPressed();
				}
			});
			this.laterButton = button;
			layout.addView(button);

			// if time isn't over, start a countdown task
			// to show remaining seconds in the Later button text
			if (calcDaysRemaining() > 0) {
				ButtonCountdownTask task = new ButtonCountdownTask();
				task.execute();
			}

		}

		// activate button
		button = new DialogButton(getContext(), Strings.confirm_button_text.get(), new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activateButtonPressed();
			}
		});
		this.activateButton = button;
		layout.addView(button);

		return layout;

	}

	
	


	/**
	 * Creates the bottom panel (time remaining & powered by)
	 */
	private View createBottomPanel() {
		TextView tv;

		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.BOTTOM);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// add time remaining text
		if (this.temporaryActivationAvailable) {
			tv = new TextView(getContext());
			tv.setTextSize(12);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv.setGravity(Gravity.LEFT);
			
			String text="";
			int days = calcDaysRemaining();
			if (days>0) {
				text = Strings.time_remaining.get() + ": " + calcDaysRemaining();
			}
			else {
				text = Strings.trial_period_expired.get();
			}
			tv.setText(text);
			layout.addView(tv);
		}

		// add elastic spacer
		layout.addView(new ElasticSpacer(getContext()));

		// add powered by panel
		layout.addView(createPoweredByPanel());

		return layout;

	}


	/**
	 * Creates the Powered By panel
	 */
	private View createPoweredByPanel() {
		TextView tv;

		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.setGravity(Gravity.BOTTOM);

		tv = new TextView(getContext());
		tv.setTextSize(12);
		tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.LEFT);
		tv.setText(Strings.powered_by.get());
		layout.addView(tv);

		layout.addView(new Spacer(getContext(), 4, 0));

		ImageView iv = new ImageView(getContext());
		iv.setImageDrawable(DroidActivatorIcon.getDrawable());
		layout.addView(iv);

		return layout;
	}


	/**
	 * Calculates the number of days remaining for the Temporary Activation.
	 * 
	 * @return the number of days remaining
	 */
	private int calcDaysRemaining() {
		int daysLeft = 0;

		if (temporaryActivationAvailable) {
			
			long firstSeconds = DroidActivator.getFirstTempActivationTS();
			int maxDays = DroidActivator.getMaximumActivationDelay();
			
			if (firstSeconds>0) {
				long todaySeconds = Calendar.getInstance().getTimeInMillis()/1000;
				long diff = todaySeconds - firstSeconds; // result in seconds

				int elapsedDays = (int) diff / (24 * 60 * 60);

				daysLeft = maxDays - elapsedDays;

				if (daysLeft < 0) {
					daysLeft = 0;
				}
			}
			else {
				daysLeft=maxDays;	// temporary activation never issued yet
			}

		}

		return daysLeft;
	}


	private void sync() {

		boolean enabled;

		// enable the Activate button:
		enabled = true;

		// if userid is requested, perform a preliminary check: it must be longer
		// than 6 chars and contain the @ char and a dot. A deeper check
		// will be performed when Activate button is pressed.
		if (isUseridRequested()) {
			String uString = getUseridString();
			if ((uString.length() < 6) || (!uString.contains("@")) || (!uString.contains("."))) {
				enabled = false;
			}
		}

		// code field length must be 8 digits
		if (getCodeString().length() != 8) {
			enabled = false;
		}

		this.activateButton.setEnabled(enabled);

		// enable the Later button (if present):
		// days must not be over
		// waiting time must be passed
		Button button = this.laterButton;
		if (button != null) {
			enabled = false;
			if (calcDaysRemaining() > 0) {
				if (this.waitTimeElapsed) {
					enabled = true;
				}
			}
			button.setEnabled(enabled);
		}

	}


	/**
	 * Cancel button has been pressed
	 */
	private void cancelButtonPressed() {

		dismiss();
		DroidActivator.runRunnable();

	}


	/**
	 * Later button has been pressed
	 */
	private void laterButtonPressed() {

		DroidActivator.doTemporaryActivation();
		dismiss();
		DroidActivator.runRunnable();

	}


	/** 
	 * Activate button has been pressed
	 */
	private void activateButtonPressed() {
		boolean cont = true;

		// if used, check if userid is a valid email address
		if (cont) {
			if (isUseridRequested()) {
				if (!Lib.checkEmail(getUseridString())) {
					WarningDialog dialog = new WarningDialog(getContext());
					dialog.setMessage(Strings.invalid_email_address.get());
					dialog.show();
					cont = false;
				}
			}
		}

		// check if network is available
		if (cont) {
			if (!DroidActivator.isNetworkAvailable()) {
				new WarningDialog(getContext(), Strings.network_unavailable.get()).show();
				cont = false;
			}
		}

		// check if backend is reachable
		if (cont) {
			if (!DroidActivator.isBackendResponding()) {
				new WarningDialog(getContext(), Strings.backend_not_responding.get()).show();
				cont = false;
			}
		}

		// perform the activation, 
		// if successful, dissmiss this dialog, show success message, run runnable when confirmed
		// if failed, show fail message
		if (cont) {
			
			// perform the activation and retrieve the result
			ActivationResult result = DroidActivator.requestActivation(getContext(), getUseridString(), getCodeString());
			boolean success = result.success;
			int failureCode= result.failureCode;
			
			// show the result dialog
			InfoDialog dialog;
			if (success) {
				
				dismiss(); // dismiss this dialog
				
				// show success message, run runnable when confirmed
				dialog = new InfoDialog(getContext(), DroidActivator.getRunnable());
				dialog.setIcon(GreenCheck48Icon.getDrawable());
				dialog.setTitle(Strings.congratulations.get());
				dialog.setMessage(DroidActivator.getAppName()+" "+Strings.app_successfully_activated.get());
				
			}
			else {
				
				// show fail dialog
				dialog = new InfoDialog(getContext());
				dialog.setIcon(RedCross48Icon.getDrawable());
				dialog.setTitle(Strings.activation_error.get());
				dialog.setMessage(getFailureString(failureCode));
				
			}
			dialog.show();
			
		}

	}

	
	/**
	 * @param failureCode a failure code
	 * @return a failure string in the current language
	 */
	private String getFailureString(int failureCode) {
		String failureString = "";
		switch (failureCode) {
		case 1:
			failureString = Strings.wrong_activation_code.get();
			break;
		case 2:
			failureString = Strings.wrong_app_name.get();
			break;
		case 3:
			failureString = Strings.userid_not_found.get();
			break;
		default:
			failureString = Strings.unrecognized_error.get() + ": " + failureCode;
			break;
		}
		return failureString;
	}


	/**
	 * Check if userid is requested
	 * @return true if requested
	 */
	private boolean isUseridRequested() {
		return this.useridRequested;
	}


	/**
	 * @return the current input userid string
	 */
	private String getUseridString() {
		return this.inputUseridField.getText().toString();
	}


	/**
	 * @return the current input code string
	 */
	private String getCodeString() {
		return this.inputCodeField.getText().toString();
	}

	// /**
	// * @return a string from the resources
	// */
	// private String getResourceString(int id) {
	// return getContext().getResources().getString(id);
	// }

	// public void setOnActivationRequestedListener(OnActivationRequestedListener l){
	// this.activationRequestedListener=l;
	// }

	private class DialogButton extends Button {

		public DialogButton(Context context, String text, View.OnClickListener listener) {
			super(context);
			setText(text);
			setOnClickListener(listener);
			init();
		}


		private void init() {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, LayoutParams.WRAP_CONTENT, 1);
			setLayoutParams(params);
		}

	}

	/**
	 * An AsyncTask to update the "Later" button text showing 
	 * a countdown for the remaining time. 
	 */
	private class ButtonCountdownTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			long startTime = Calendar.getInstance().getTimeInMillis();
			long currTime;
			boolean end = false;
			while (!end) {
				currTime = Calendar.getInstance().getTimeInMillis();
				long elapsedMillis = currTime - startTime;
				int elapsedSecs = (int) (elapsedMillis / 1000);

				publishProgress(elapsedSecs);

				if (elapsedSecs >= getMaxSec()) {
					end = true;
				}

				try {
					Thread.sleep(200);
				}
				catch (InterruptedException e) {
				}

			}

			return null;
		}


		@Override
		protected void onProgressUpdate(Integer... values) {
			int elapsedSecs = values[0];
			int remainingSecs = getMaxSec() - elapsedSecs;
			laterButton.setText("" + remainingSecs + "s");
		}


		@Override
		protected void onPostExecute(Void result) {
			laterButton.setText(DroidActivator.getLaterButtonText());
			waitTimeElapsed = true;
			sync(); // update the GUI
			cancel(true);
		}


		private int getMaxSec() {
			return DroidActivator.getTemporaryWaitTime();
		}

	}

	/**
	 * Spacer Layout<br>
	 * Can be used horizontally or vertically<br>
	 */
	private class Spacer extends LinearLayout {

		public Spacer(Context context) {
			this(context, 4, 4);
		}


		/**
		 * @param context the context
		 * @param size of the spacer in dp
		 */
		public Spacer(Context context, int dpWidth, int dpHeight) {
			super(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpWidth, dpHeight);
			setLayoutParams(params);
		}

	}

	/**
	 * A spacer with a default weigth of 1.<br>
	 * Can be used horizontally or vertically.<br>
	 * You can change the weight with setWeigth()<br>
	 */
	private class ElasticSpacer extends Spacer {

		public ElasticSpacer(Context context) {
			super(context);
			init();
		}


		private void init() {
			setWeight(1);
		}


		/**
		 * Sets the weigth of the spacer
		 * @param weight the weight
		 */
		public void setWeight(int weight) {
			ViewGroup.LayoutParams params = getLayoutParams();
			if ((params != null) & (params instanceof LinearLayout.LayoutParams)) {
				LinearLayout.LayoutParams lparams = (LinearLayout.LayoutParams) params;
				lparams.weight = 1;
			}
		}

	}

}
