package com.algos.droidactivator;

/**
 * Wrapper to transport the result of an activation request
 */
class ActivationResult {
	boolean success;
	int failureCode;

	ActivationResult(boolean success, int failureCode) {
		super();
		this.success = success;
		this.failureCode = failureCode;
	}
}
