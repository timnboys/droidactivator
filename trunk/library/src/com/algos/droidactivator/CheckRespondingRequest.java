package com.algos.droidactivator;

/**
 * A Request to the backend just to ckeck if it is responding
 */
public class CheckRespondingRequest extends BackendRequest {

	public CheckRespondingRequest() {
		super("checkresponding", 5000);
	}

}
