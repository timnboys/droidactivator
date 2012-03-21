package com.algos.droidactivator.test;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.algos.droidactivator.DroidActivator;

public class DroidActivatorTestActivity extends Activity {

	private EditText backendText; 
	private TextView uniqueIdText; 
	private CheckBox activatedCheckBox;
	private CheckBox tempActivatedCheckBox;
	private TextView userIdText; 
	private TextView levelText; 
	private TextView expirationText; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Instantiate the Activator (or reconfigure it if already instantiated)
		DroidActivator.newInstance(this, "http://"+loadBackendAddress(),new Runnable() {
			
			@Override
			public void run() {startMyApp();}
		});
		
		
		setContentView(new MainView(this));

		// perform the activation cycle, calls your runnable when finished
		DroidActivator.doActivationCycle(this);

	}


	@Override
	protected void onDestroy() {
		saveBackendAddress();
		super.onDestroy();
	}



	// called by your Runnable
	private void startMyApp() {
		
		//setContentView(new MainView(this));

		SyncGui();
		
        // test the activator variables and do whatever is appropriate  
        if (DroidActivator.isActivated()) {
                switch (DroidActivator.getLevel()) {
                case 0:
                        //startInDemoMode();
                        break;
                case 1:
                        //startInBasicMode();
                        break;
                case 2:
                        //startInProMode();
                        break;
                }
        }
        else {
                //startInDemoMode();
        }

		
	}
	
	// loads DroidActivator's cached values into Gui
	private void SyncGui(){
		uniqueIdText.setText(DroidActivator.getUniqueId());
		activatedCheckBox.setChecked(DroidActivator.isActivated());
		tempActivatedCheckBox.setChecked(DroidActivator.isTemporarilyActivated());
		userIdText.setText(DroidActivator.getActivationUserId());
		levelText.setText(""+DroidActivator.getLevel());
		
		String text="";
		Date date = DroidActivator.getExpirationDate();
		if (date!=null) {
			DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
			text=dateFormat.format(date);
		}
		expirationText.setText(text);
		
	}
	


	// For demonstrating purposes, activation is performed every time you click the button.
	// Normally, you will check activation only once, in the onCreate() method.
	private void buttonClicked(){
	
		
		// DroidActivator is already instantiated. With this call, we just reconfigure it.
		DroidActivator.newInstance(this, "http://"+getGuiBackendAddress(),new Runnable() {
			
			@Override
			public void run() {startMyApp();}
		});
		
		DroidActivator.setDefaultSocketTimeout(1000);
		DroidActivator.setDefaultConnectionTimeout(1000);

		// perform the activation cycle, calls your runnable when finished
		DroidActivator.doActivationCycle(this);

		// show activation data in the Gui
		SyncGui();

	}
	
	
	// Reset cached activation data.
	private void resetData(){
	
		DroidActivator.setActivated(false);
		DroidActivator.setTemporarilyActivated(false);
		DroidActivator.setActivationUserid("");
		DroidActivator.setLevel(0);
		DroidActivator.setExpiration(0);
		DroidActivator.setUniqueId("");
		SyncGui();
		
	}
	
	
	// Send a Custom Event.
	private void sendCustomEvent(){
		DroidActivator.sendCustomEvent(3,"text:ciao");
	}


	
	
	
	// save the current backend address in Shared Preferences
	private void saveBackendAddress(){
		SharedPreferences prefs = getPrefs();
		if (prefs!=null) {
			String address = getGuiBackendAddress();
			if (address!=null) {
				prefs.edit().putString("backend_address", address).commit();
			}
			
		}
	}
	
	
	// retrieve the backend address from Shared Preferences
	private String loadBackendAddress(){
		return getPrefs().getString("backend_address","");
	}
	
	// retrieve the current backend address from the Gui
	private String getGuiBackendAddress(){
		String text="";
		if (backendText!=null) {
			text= backendText.getText().toString();
		}
		return text;
	}


	/**
	 * Retrieve the SharedPreferences object. The preferences file is 
	 * created now if it doesn't exist.
	 * 
	 * @return the SharedPreferences object
	 */
	private SharedPreferences getPrefs() {
		SharedPreferences prefs = getSharedPreferences("DroidActivatorTestPrefs",Context.MODE_PRIVATE);
		return prefs;
	}

	// the View for the Activity
	private class MainView extends LinearLayout {

		public MainView(Context context) {
			super(context);
			init();
		}
		
		private void init(){
			LinearLayout layout;
			TextView label;
			
			setOrientation(VERTICAL);
			setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			setPadding(10, 10, 10, 10);
			
			// add the Backend Address field
			layout = new LinearLayout(getContext());
			layout.setOrientation(VERTICAL);
			layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			label = new TextView(getContext());
			label.setText("Backend's address");
			
			backendText = new EditText(getContext());
			backendText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			backendText.setHint("e.g. 192.168.99.1:8080");
			backendText.setText(loadBackendAddress());
			layout.addView(label);
			layout.addView(backendText);
			addView(layout);
			
			// add the buttons
			layout = new LinearLayout(getContext());
			layout.setOrientation(HORIZONTAL);
			layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			layout.setPadding(0, 30, 0, 30);
			
			// do Cycle button
			Button buttonDoCycle = new Button(getContext());
			buttonDoCycle.setText("Activation Cycle");
			buttonDoCycle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					buttonClicked();
				}
			});
			
			// Reset data button
			Button buttonReset = new Button(getContext());
			buttonReset.setText("Reset");
			buttonReset.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					resetData();
				}
			});
			
			// Send custom event button
			Button buttonEvent = new Button(getContext());
			buttonEvent.setText("Send event");
			buttonEvent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					sendCustomEvent();
				}
			});

			
			layout.addView(new ElasticSpacer(getContext()));
			layout.addView(buttonDoCycle);
			layout.addView(buttonReset);
			layout.addView(buttonEvent);
			layout.addView(new ElasticSpacer(getContext()));
			
			addView(layout);

			
			

			// add the Activated and Temporary Activated fields
			layout = new LinearLayout(getContext());
			layout.setOrientation(HORIZONTAL);
			activatedCheckBox = new CheckBox(getContext());
			activatedCheckBox.setText("Activated");
			activatedCheckBox.setEnabled(false);
			tempActivatedCheckBox = new CheckBox(getContext());
			tempActivatedCheckBox.setText("Temporary activation");
			tempActivatedCheckBox.setEnabled(false);
			layout.addView(activatedCheckBox);
			layout.addView(tempActivatedCheckBox);
			addView(layout);
			
			// add the User Id
			layout = new LinearLayout(getContext());
			layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			label = new TextView(getContext());
			label.setText("User Id: ");
			userIdText = new TextView(getContext());
			userIdText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			userIdText.setTextColor(Color.YELLOW);
			layout.addView(label);
			layout.addView(userIdText);
			addView(layout);

			// add the Level field
			layout = new LinearLayout(getContext());
			layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			label = new TextView(getContext());
			label.setText("Level: ");
			levelText = new TextView(getContext());
			levelText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			levelText.setTextColor(Color.YELLOW);
			layout.addView(label);
			layout.addView(levelText);
			addView(new Spacer(getContext(),0,10));
			addView(layout);
			
			// add the Expiration field
			layout = new LinearLayout(getContext());
			layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			label = new TextView(getContext());
			label.setText("Expiration: ");
			expirationText = new TextView(getContext());
			expirationText.setLayoutParams(new LinearLayout.LayoutParams(100, LayoutParams.WRAP_CONTENT));
			expirationText.setTextColor(Color.YELLOW);
			layout.addView(label);
			layout.addView(expirationText);
			addView(new Spacer(getContext(),0,10));
			addView(layout);
			
			// add the Unique Id Text View
			layout = new LinearLayout(getContext());
			layout.setOrientation(VERTICAL);
			label = new TextView(getContext());
			label.setText("Unique Id: ");
			uniqueIdText=new TextView(getContext());
			uniqueIdText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			uniqueIdText.setTextColor(Color.YELLOW);
			layout.addView(label);
			layout.addView(uniqueIdText);
			addView(new Spacer(getContext(),0,10));
			addView(layout);

			
		}
		
		
		
		
	}
	
	
	/**
	 * Spacer Layout<br>
	 * Can be used horizontally or vertically<br>
	 */
	public class Spacer extends LinearLayout {

		public Spacer(Context context) {
			this(context,4,4);
		}
		
		/**
		 * @param context the context
		 * @param size of the spacer in dp
		 */
		public Spacer(Context context, int dpWidth, int dpHeight) {
			super(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpWidth,dpHeight);
			setLayoutParams(params);
		}

	}

	/**
	 * A spacer with a default weigth of 1.<br>
	 * Can be used horizontally or vertically.<br>
	 * You can change the weight with setWeigth()<br>
	 */
	class ElasticSpacer extends Spacer {

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