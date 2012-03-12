package com.algos.droidactivator.util;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * A spacer with a default weigth of 1.<br>
 * Can be used horizontally or vertically.<br>
 * You can change the weight with setWeigth()<br>
 */
public class ElasticSpacer extends Spacer {

	public ElasticSpacer(Context context) {
		super(context);
		init();
	}


	private void init() {
		setWeight(1);
	}


	/**
	 * Sets the weigth of the spacer
	 * @param weight the weight
	 */
	public void setWeight(int weight) {
		ViewGroup.LayoutParams params = getLayoutParams();
		if ((params != null) & (params instanceof LinearLayout.LayoutParams)) {
			LinearLayout.LayoutParams lparams = (LinearLayout.LayoutParams) params;
			lparams.weight = 1;
		}
	}

}
