package com.algos.droidactivator.dialog;


import android.content.Context;
import android.content.DialogInterface;

import com.algos.droidactivator.resources.Strings;
import com.algos.droidactivator.resources.WarningIcon;


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
		setTitle(Strings.notify_dialog_default_title.get());
		setIcon(WarningIcon.getDrawable());
		setButton(BUTTON_POSITIVE, Strings.notify_dialog_button_text.get(), (DialogInterface.OnClickListener)null);
	}
	
}
