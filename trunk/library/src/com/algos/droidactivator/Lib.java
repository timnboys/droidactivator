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

import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Pattern;


class Lib {


	private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
			+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");



	/**
	 * Email address validator
	 * @param email to validate
	 * @return true if valid
	 */
	static boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
	
	/**
	 * Converts an Object to a Date 
	 * @param obj the object to convert
	 * @return the date
	 */
	static Date getDate(Object obj) {
		Date date=null;
		if (obj != null) {
			if (obj instanceof Date) {
				date = (Date)obj;
			}			
		}
		return date;
	}
	
	/**
	 * Converts an Object to an int 
	 * @param obj the object to convert
	 * @return the int
	 */
	static int getInt(Object obj) {
		int integer=0;
		if (obj != null) {
			if (obj instanceof Number) {
				Number num = (Number)obj;
				integer = num.intValue();
			}			
		}
		return integer;
	}
	
	
	/**
	 * Converts an Object to a long
	 * 
	 * @param obj the object to convert
	 * @return the long
	 */
	static long getLong(Object obj) {
		long longint=0;
		if (obj != null) {
			if (obj instanceof Number) {
				Number num = (Number)obj;
				longint = num.longValue();
			}			
		}
		return longint;
	}

	/**
	 * Converts an Object to a float
	 * 
	 * @param obj the object to convert
	 * @return the float
	 */
	static float getFloat(Object obj) {
		float floatNum=0f;
		if (obj != null) {
			if (obj instanceof Number) {
				Number num = (Number)obj;
				floatNum = num.floatValue();
			}			
		}
		return floatNum;
	}
	
	
	/**
	 * Converts an Object to a double
	 * 
	 * @param obj the object to convert
	 * @return the double
	 */
	public static double getDouble(Object obj) {
		double doubleNum=0d;
		if (obj != null) {
			if (obj instanceof Number) {
				Number num = (Number)obj;
				doubleNum = num.doubleValue();
			}			
		}
		return doubleNum;
	}


	/**
	 * Converts an Object to a boolean
	 * 
	 * @param obj the object to convert
	 * @return the boolean
	 */
	static boolean getBool(Object obj) {
		boolean bool = false;
		if (obj != null) {
			if (obj instanceof Boolean) {
				bool = (Boolean)obj;
			}	
			
			if (obj instanceof String) {
				String string = Lib.getString(obj);
				if ((string.toUpperCase().equals("TRUE")) || (string.equals("1"))) {
					bool=true;
				}
			}			
			
			if (obj instanceof Number) {
				Number number = (Number)obj;
				int intnum = number.intValue();
				if (intnum==1) {
					bool=true;
				}
			}			


		}
		
		return bool;
	}

	/**
	 * Converts an Object to a String
	 * 
	 * @param obj the object to convert
	 * @return the string
	 */
	static String getString(Object obj) {
		String string = "";
		if (obj != null) {
			if (obj instanceof String) {
				string = (String)obj;
			}			
		}
		return string;
	}

	
	/**
	 * Converts an Object to a BigDecimal
	 * 
	 * @param obj the object to convert
	 * @return the BigDecimal
	 */
	public static BigDecimal getBigDecimal(Object obj) {
		BigDecimal bd = new BigDecimal(0);
		if (obj != null) {
			if (obj instanceof BigDecimal) {
				bd = (BigDecimal)obj;
			}			
			if (obj instanceof String) {
				String str = (String)obj;
				bd = new BigDecimal(str);
			}
		}
		return bd;
	}


	

}
