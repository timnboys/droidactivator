package com.algos.droidactivator.dialog;


import com.algos.droidactivator.R;
import com.algos.droidactivator.resources.WarningIcon;

import android.content.Context;
import android.content.DialogInterface;


/**
 * Dialog to display a notification dialog with a single Confirm button
 */
public class WarningDialog extends InfoDialog {

	/**
	 * Builds a dialog with a message
	 */
	public WarningDialog(Context context, String message) {
		this(context);
		init();
		setMessage(message);
	}

	/**
	 * Builds an empty dialog
	 */
	public WarningDialog(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		setTitle(getContext().getResources().getString(R.string.notify_dialog_default_title));
		setIcon(WarningIcon.getDrawable());
		setButton(BUTTON_POSITIVE, getContext().getResources().getString(R.string.notify_dialog_button_text), (DialogInterface.OnClickListener)null);
	}
	
}
