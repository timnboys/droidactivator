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

	boolean useridRequested;
	private Button activateButton;
	private Button laterButton;
	private EditText inputUseridField;
	private EditText inputCodeField;
	
	private OnActivationRequestedListener activationRequestedListener;
	
	
	ActivationDialog(Context context, boolean useridRequested) {
		super(context);
		this.useridRequested=useridRequested;
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
		EditText et;
		int wdp;
		int widthPx;
		Resources r=getContext().getResources();
		InputFilter[] filterArray;

		// input userid panel
		LinearLayout useridPanel = new LinearLayout(getContext());
		useridPanel.setOrientation(LinearLayout.VERTICAL);
		useridPanel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

		// input userid label
		tv = new TextView(getContext());
		tv.setText(R.string.input_userid_label);
		useridPanel.addView(tv);

		// input userid field
		wdp=240;
		widthPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wdp, r.getDisplayMetrics());
		//widthPx=LayoutParams.MATCH_PARENT;
		
		et = new EditText(getContext());
		et.setLayoutParams(new LinearLayout.LayoutParams(widthPx,LayoutParams.WRAP_CONTENT));
		et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

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
		this.inputUseridField=et;
		useridPanel.addView(et);

		
		// input code panel
		LinearLayout codePanel = new LinearLayout(getContext());
		codePanel.setOrientation(LinearLayout.VERTICAL);
		codePanel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		// "input code" label
		tv = new TextView(getContext());
		tv.setText(R.string.input_code_label);
		codePanel.addView(tv);

		// input code field
		wdp=140;
		 widthPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, wdp, r.getDisplayMetrics());
		
		et = new EditText(getContext());
		et.setLayoutParams(new LinearLayout.LayoutParams(widthPx,LayoutParams.WRAP_CONTENT));
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(8);
		et.setFilters(filterArray);
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
		this.inputCodeField=et;
		codePanel.addView(et);

		
		// horizontal panel with centered userid
		LinearLayout centeredUseridPanel = new LinearLayout(getContext());
		centeredUseridPanel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		centeredUseridPanel.setOrientation(LinearLayout.HORIZONTAL);
		centeredUseridPanel.addView(new ElasticSpacer(getContext()));
		centeredUseridPanel.addView(useridPanel);
		centeredUseridPanel.addView(new ElasticSpacer(getContext()));

		// horizontal panel with centered code
		LinearLayout centeredCodePanel = new LinearLayout(getContext());
		centeredCodePanel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		centeredCodePanel.setOrientation(LinearLayout.HORIZONTAL);
		centeredCodePanel.addView(new ElasticSpacer(getContext()));
		centeredCodePanel.addView(codePanel);
		centeredCodePanel.addView(new ElasticSpacer(getContext()));

		
		// main panel
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		layout.setPadding(0, 15, 0, 15);
		if (isUseridRequested()) {
			layout.addView(centeredUseridPanel);			
		}
		layout.addView(centeredCodePanel);

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
		
		// sync the Activate button: 
		enabled=true;
		
		// if userid is requested, perform a preliminary check: it must be longer 
		// than 6 chars and contain the @ char and a dot. A deeper check
		// will be performed when Activate button is pressed.
		if (isUseridRequested()) {
			String uString = getUseridString();
			if ((uString.length()<6) || (!uString.contains("@") ) || (!uString.contains(".") )) {
				enabled=false;
			}
		}
		
		// code field length must be 8 digits
		if (getCodeString().length()!=8) {
			enabled=false;
		}

		this.activateButton.setEnabled(enabled);
		

		// sync the Later button: time must not be over
		enabled=false;
		if (true) {
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
			this.activationRequestedListener.onActivationRequested(true, "", "");
		}

		dismiss();		
		
	}


	/**
	 * Activate button has been pressed
	 */
	private void activateButtonPressed(){
		boolean cont = true;
		
		//if used, check if userid is a valid email address
		if (cont) {
			if (isUseridRequested()) {
				if (!Lib.checkEmail(getUseridString())) {
					WarningDialog dialog = new WarningDialog(getContext());
					dialog.setMessage(getContext().getString(R.string.invalid_email_address));
					dialog.show();
					cont = false;
				}
			}
		}
		
		// check if network is available
		if (cont) {
			if (!DroidActivator.isNetworkAvailable()) {
				new WarningDialog(getContext(), getResourceString(R.string.network_unavailable)).show();
				cont = false;
			}
		}
		
		// check if backend is reachable
		if (cont) {
			if (!DroidActivator.isBackendResponding()) {
				new WarningDialog(getContext(), getResourceString(R.string.backend_not_responding)).show();
				cont = false;
			}
		}
		
		
		// perform the activation
		if (cont) {
			if (DroidActivator.requestActivation(getUseridString(), getCodeString())) {
				dismiss();
			}
		}
		
		
//		// notify the listener
//		if (cont) {
//			// notify the listener
//			if (this.activationRequestedListener!=null) {
//				this.activationRequestedListener.onActivationRequested(false, getUseridString(), getCodeString());
//			}
//			
//			
//			
//			dismiss();
//		}


	}

	
	/**
	 * Check if userid is requested
	 * @return true if requested
	 */
	private boolean isUseridRequested(){
		return this.useridRequested;
	}

	/**
	 * @return the current input userid string
	 */
	private String getUseridString(){
		return this.inputUseridField.getText().toString();
	}

	/**
	 * @return the current input code string
	 */
	private String getCodeString(){
		return this.inputCodeField.getText().toString();
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
