package com.algos.droidactivator.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.algos.droidactivator.R;



/**
 * Dialog to display an information dialog with a single  
 * Confirm button or no buttons at all<br>
 * Call setButtonText(null) to display no buttons
 */
public class InfoDialog extends AlgosAlertDialog {

	

	/**
	 * Builds a dialog with a message and a Runnable
	 */
	public InfoDialog(Context context, String message, Runnable runnable) {
		super(context, runnable);
		init();
		setMessage(message);
	}
	
	/**
	 * Builds a dialog with a message
	 */
	public InfoDialog(Context context, String message) {
		this(context, message , null);
	}
	
	/**
	 * Builds a dialog with a Runnable
	 */
	public InfoDialog(Context context, Runnable runnable) {
		this(context, null , runnable);
	}

	/**
	 * Builds an empty dialog
	 */
	public InfoDialog(Context context) {
		this(context, null , null);
	}
	
	
	
	private void init(){
		setTitle(getContext().getResources().getString(R.string.info_dialog_default_title));
		setIcon(getContext().getResources().getDrawable(R.drawable.info_icon));
		setButtonText(getContext().getResources().getString(R.string.info_dialog_button_text));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void show() {

		super.show();
		
		// here we could change the dialog size
		// getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public void setButtonText(String text){
		setButton(BUTTON_POSITIVE, text, (DialogInterface.OnClickListener)null);
	}

	
}
