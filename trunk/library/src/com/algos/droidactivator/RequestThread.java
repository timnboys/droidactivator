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

package com.algos.droidactivator;


public class RequestThread extends Thread {

	private boolean successful;
	private boolean finished = false;


	RequestThread() {
		super();
		setDaemon(true);
	}
	
	boolean isSuccessful() {
		return this.successful;
	}
	
	void setSuccessful(boolean successful){
		this.successful=successful;
	}
	
	boolean isFinished() {
		return this.finished;
	}
	
	void setFinished(boolean finished){
		this.finished=finished;
	}




}
