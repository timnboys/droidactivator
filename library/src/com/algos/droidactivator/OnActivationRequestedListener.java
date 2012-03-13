package com.algos.droidactivator;


interface OnActivationRequestedListener {

	/**
	 * Called when an Activation is requested.
	 */
	abstract void onActivationRequested(boolean temporary, String userid, String code);

}
