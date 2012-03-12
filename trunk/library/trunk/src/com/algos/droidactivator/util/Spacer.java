package com.algos.droidactivator.util;


import android.content.Context;
import android.widget.LinearLayout;

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
