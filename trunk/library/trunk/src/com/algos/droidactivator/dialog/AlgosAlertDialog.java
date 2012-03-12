package com.algos.droidactivator.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;


public class AlgosAlertDialog extends AlertDialog {

	// runnable to be run when Confirm button is pressed
	private Runnable confirmRunnable;

	private int width;
	
	
	public AlgosAlertDialog(Context context, Runnable confirmRunnable) {
		super(context);
		setConfirmRunnable(confirmRunnable);
		init();
	}

	protected AlgosAlertDialog(Context context) {
		this(context, null);
	}
	
	private void init(){
		// never show the soft imput area (soft keyboard) when the window receives focus!
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	public void show() {

		super.show();
		
		Button confirmButton = getButton(DialogInterface.BUTTON_POSITIVE); 
		if (confirmButton!=null) {
			confirmButton.setOnClickListener(new ConfirmButtonListener());			
		}

		if (width!=0) {
			getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
		}
	}

	
	protected void confirmPressed(){
		String error = getError();
		if (error.equals("")) {
			dismiss();
			Runnable runnable = getConfirmRunnable();
			if (runnable!=null) {
				runnable.run();
			}

		}
		else {
			new WarningDialog(getContext(),error).show();
		}
	}

	/**
	 * Checks if dialog is confirmable
	 * returns the error text
	 * (empty string if ok)
	 * To be overridden by subclasses
	 * Invoked when the Confirm button is pressed
	 * If there is an error, the error is displayed
	 * and the dialog is not confirmed.
	 */
	protected String getError() {
		return "";
	}


	protected Runnable getConfirmRunnable() {
		return confirmRunnable;
	}

	protected void setConfirmRunnable(Runnable confirmRunnable) {
		this.confirmRunnable = confirmRunnable;
	}

	protected int getWidth() {
		return width;
	}

	
	public void setWidth(int width) {
		this.width = width;
	}
	
	
	private class ConfirmButtonListener implements  View.OnClickListener{
		public void onClick(View v) {
			confirmPressed();
		}
	}


}
