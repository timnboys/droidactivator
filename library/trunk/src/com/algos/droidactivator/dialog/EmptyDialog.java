package com.algos.droidactivator.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

/**
 * A dialog without title and buttons
 */
public class EmptyDialog extends AlertDialog {

	/**
	 * Builds an empty dialog
	 */
	public EmptyDialog(Context context) {
		super(context);
		init();
	}

	
	private void init(){

		// never show the soft imput area (soft keyboard) when the window receives focus!
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setTitle(null);
		setIcon(null);
		setButton(BUTTON_POSITIVE, null, (DialogInterface.OnClickListener)null);
	}
	
}
