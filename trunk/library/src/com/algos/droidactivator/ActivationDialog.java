package com.algos.droidactivator;

import com.algos.droidactivator.dialog.WarningDialog;
import com.algos.droidactivator.util.ElasticSpacer;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


class ActivationDialog extends Dialog {

	private Button activateButton;
	private Button laterButton;
	private EditText inputField;
	
	private OnActivationRequestedListener activationRequestedListener;
	
	
	ActivationDialog(Context context) {
		super(context);
		init();
	}

	private void init(){
		
		// never show the soft imput area (soft keyboard) when the window receives focus!
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // call this before setting content!
        requestWindowFeature(Window.FEATURE_LEFT_ICON);
        
        // Android bug: this line should go here but if i put it here the icon is invisibe!
        //setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.warning_icon);

        setTitle(R.string.dialog_title);
		setContentView(createContentView());
		
        // Android bug: must be called after setContentView()!
        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.warning_icon);

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
	private View createContentView(){
		TextView tv;
		
		// main container
		LinearLayout vLayout = new LinearLayout(getContext());
		vLayout.setOrientation(LinearLayout.VERTICAL);
		vLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		vLayout.setPadding(10, 10, 10, 10);

		
		// add message panel
		tv = new TextView(getContext());
		tv.setText(R.string.dialog_message);
		vLayout.addView(tv);
		
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
	private View createInputPanel(){
		TextView tv;

		// label and code panel
		LinearLayout panel = new LinearLayout(getContext());
		panel.setOrientation(LinearLayout.VERTICAL);
		panel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		// label
		tv = new TextView(getContext());
		tv.setText(R.string.input_field_label);
		panel.addView(tv);

		// input field
		int wdp=140;
		Resources r = getContext().getResources();
		int widthPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wdp, r.getDisplayMetrics());
		
		EditText et = new EditText(getContext());
		et.setLayoutParams(new LinearLayout.LayoutParams(widthPx,LayoutParams.WRAP_CONTENT));
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(8);
		et.setFilters(FilterArray);
		et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
		
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				sync();
			}
		});
		this.inputField=et;
		panel.addView(et);

		
		// main panel
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		layout.setPadding(0, 15, 0, 15);
		layout.addView(new ElasticSpacer(getContext()));
		layout.addView(panel);
		layout.addView(new ElasticSpacer(getContext()));

		return layout;
		
	}
	
	
	/**
	 * Creates the button panel
	 */
	private View createButtonPanel(){
		
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

		Button button;
		
		// cancel button
		button = new DialogButton(getContext(), R.string.cancel_button_text, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelButtonPressed();
			}
		});
		layout.addView(button);
		
		// later button
		button = new DialogButton(getContext(),R.string.temporary_button_text, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				laterButtonPressed();
			}
		});
		this.laterButton=button;
		layout.addView(button);
		
		// activate button
		button = new DialogButton(getContext(),R.string.confirm_button_text, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activateButtonPressed();
			}
		});
		this.activateButton=button;
		layout.addView(button);

		
		return layout;

		
		
	}
	
	
	/**
	 * Creates the bottom panel (time remaining & powered by)
	 */
	private View createBottomPanel(){
		TextView tv;
		
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

		// add time remaining text
		tv = new TextView(getContext());
		tv.setTextSize(12);
		tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.LEFT);
		tv.setText(R.string.time_remaining);
		layout.addView(tv);

		// add elastic spacer
		layout.addView(new ElasticSpacer(getContext()));

		// add powered by text
		tv = new TextView(getContext());
		tv.setTextSize(12);
		tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.RIGHT);
		tv.setText(R.string.powered_by);
		layout.addView(tv);

		return layout;

	}


	
	private void sync(){
		
		boolean enabled;
		String inputString = getInputString();
		
		// sync the Activate button: input field length must be 8 digits
		enabled=false;
		if (inputString.length()==8) {
			enabled=true;
		}
		this.activateButton.setEnabled(enabled);
		

		// sync the Later button: input field must be empty
		enabled=false;
		if (inputString.length()==0) {
			enabled=true;
		}
		this.laterButton.setEnabled(enabled);


	}
	
	
	/**
	 * Cancel button has been pressed
	 */
	private void cancelButtonPressed(){

		dismiss();		
		
	}
	
	/**
	 * Later button has been pressed
	 */
	private void laterButtonPressed(){

		// notify the listener
		if (this.activationRequestedListener!=null) {
			this.activationRequestedListener.onActivationRequested(true, "");
		}

		dismiss();		
		
	}


	/**
	 * Activate button has been pressed
	 */
	private void activateButtonPressed(){

		// check if network is available
		if (DroidActivator.isNetworkAvailable()) {
			
			// check if backend is reachable
			if (DroidActivator.isBackendResponding()) {
				
				// notify the listener
				if (this.activationRequestedListener!=null) {
					this.activationRequestedListener.onActivationRequested(false, getInputString());
				}
				
				dismiss();
				
			}
			else {
				new WarningDialog(getContext(), getResourceString(R.string.backend_not_responding)).show();
			}
			
		}
		else {
			new WarningDialog(getContext(), getResourceString(R.string.network_unavailable)).show();
		}
		
	}

	
	/**
	 * @return the current input string
	 */
	private String getInputString(){
		return this.inputField.getText().toString();
	}
	
	/**
	 * @return a string from the resources
	 */
	private String getResourceString(int id){
		return getContext().getResources().getString(id);
	}

	
	public void setOnActivationRequestedListener(OnActivationRequestedListener l){
		this.activationRequestedListener=l;
	}

	private class DialogButton extends Button{

		public DialogButton(Context context, int textRes, View.OnClickListener listener) {
			super(context);
			setText(textRes);
			setOnClickListener(listener);
			init();
		}
		
		private void init(){
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, LayoutParams.WRAP_CONTENT, 1);
			setLayoutParams(params);
		}
		
	}
	
	
}
