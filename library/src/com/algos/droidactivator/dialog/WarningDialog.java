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
