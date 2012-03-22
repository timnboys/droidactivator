package com.algos.droidactivator.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import com.algos.droidactivator.resources.Strings;
import com.algos.droidactivator.resources.WarningIcon;



/**
 * Implementation of a modal dialog with "Confirm" and "Cancel" buttons
 * A Runnable is supplied in the constructor
 * This Runnable is run when Confirm button is pressed
 */
public class ConfirmOrCancelDialog extends AlgosAlertDialog {

	
	public ConfirmOrCancelDialog(Context context, Runnable confirmRunnable) {
		super(context);
		setConfirmRunnable(confirmRunnable);
		init();
	}
	
	private void init(){
		setTitle(Strings.confirm_cancel_dialog_default_title.get());
		setMessage(Strings.confirm_cancel_dialog_default_message.get());
		setIcon(WarningIcon.getDrawable());
		// use null listeners, real listeners are assigned after show()
		setButton(BUTTON_POSITIVE, Strings.button_confirm_dialog_text.get(), (DialogInterface.OnClickListener)null);
		setButton(BUTTON_NEGATIVE, Strings.button_cancel_dialog_text.get(), (DialogInterface.OnClickListener)null);
		
	}
	

	protected void cancelPressed(){
		dismiss();
	}
	
	
	

	/**
	 * After dialog is shown, change the listeners of the buttons
	 * to avoid automatic dismission when a button is pressed
	 */
	@Override
	public void show() {
		super.show();
		Button cancelButton = getButton(DialogInterface.BUTTON_NEGATIVE); 
		cancelButton.setOnClickListener(new CancelButtonListener());
	}

	protected void setNegativeButtonText(String text){
		setButton(BUTTON_NEGATIVE, text, (DialogInterface.OnClickListener)null);
		Button button = getButton(BUTTON_NEGATIVE);
		if (button!=null) {
			button.setText(text);
		}
	}

	protected void setPositiveButtonText(String text){
		setButton(BUTTON_POSITIVE, text, (DialogInterface.OnClickListener)null);
		Button button = getButton(BUTTON_POSITIVE);
		if (button!=null) {
			button.setText(text);
		}
	}
	
	public void setConfirmButtonText(String text){
		setButton(BUTTON_POSITIVE, text, (DialogInterface.OnClickListener)null);
	}
	
	public void setCancelButtonText(String text){
		setButton(BUTTON_NEGATIVE, text, (DialogInterface.OnClickListener)null);
	}

	
	
	private class CancelButtonListener implements  View.OnClickListener{
		public void onClick(View v) {
			cancelPressed();
		}
	}



}
