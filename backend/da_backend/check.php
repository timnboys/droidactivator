<?php 
//    DroidAcrivator's Php Backend
//    Copyright (C) 2012 algos.it
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as
//    published by the Free Software Foundation, either version 3 of the
//    License, or (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see http://www.gnu.org/licenses/
?>
<?php

include_once "lib/database.php";

const 	ACTIVATION_CODE_WRONG = 1,
		RECORD_FOR_OTHER_APP = 2,
		ACTIVATION_RECORD_NOT_FOUND = 3;

$headers = parseRequestHeaders();
$action = $headers['Action'];

switch ($action) {
	case "checkresponding":
		checkresponding();
		break;
	case "checkuidpresent":
		checkuidpresent($headers);
		break;
	case "activate":
		activate($headers);
		break;
	case "update":
		update($headers);
		break;
	case "event":
		event($headers);
		break;

}

// request to check if the backend is responding
function checkresponding(){
	header('success: true');
}

// request to check if a unique id is present in the database
function checkuidpresent($headers){
	$db = new Database();

	// The Activation record is searched by strong link
	$unique_id = $headers['Uniqueid'];
	$id = findActivationRecordStrong($unique_id, $db);
	unset($db);

	if ($id>0){
		header('success: true');
	}else{
		header('success: false');
	}

}




// request for an Update
function update($headers){
	$success=false;

	$db = new Database();

	// The Activation record is searched by strong link
	$unique_id = $headers['Uniqueid'];
	$id = findActivationRecordStrong($unique_id, $db);

	if ($id>0){
		// Activation Record found by strong link, this is the normal situation
		// put bundle in response and return success
		putBundleInResponse($db,$id);
		$success=true;

	}else{

		// Activation Record not found by strong link, search by loose link
		$user_id = $headers['Userid'];
		$app_name = $headers['Appname'];
		$producer_id = $headers['Producerid'];
		$id = findActivationRecordLoose($user_id, $app_name, $producer_id, $db);

		if ($id>0){

			// turn off activation flag and reset Unique Id
			mysql_query("UPDATE activation SET active = 0, uniqueid = '' WHERE id = '$id'");

			// put bundle in response and return success
			putBundleInResponse($db,$id);
			$success=true;

		}

	}

	// Save last successful update timestamp
	if ($success) {
		$current_time=time();
		mysql_query("UPDATE activation SET last_update = '$current_time' WHERE id = '$id'");
	}
	
	// set success header
	if ($success) {
		header('success: true');
	}else{
		header('success: false');
	}

	unset($db);

}


// request for an Activation
function activate($headers){

	$success=false;

	$db = new Database();

	// The Activation record is searched by strong link.
	$unique_id = $headers['Uniqueid'];
	$id = findActivationRecordStrong($unique_id, $db);

	if ($id>0){

		// Found by strong link. This is a reactivation.
		// Check the Activation code.
		$code = $headers['Activationcode'];
		if (checkActivationCode($db, $id, $code)) {

			// Success: set the Activation flag to true
			mysql_query("UPDATE activation SET active = 1 WHERE id = '$id'");

			// put bundle in response and return success
			putBundleInResponse($db,$id);
			$success=true;

		}else{
			// activation code wrong
			header('failurecode: ' . ACTIVATION_CODE_WRONG);
		}

	}else{

		// Activation Record not found by strong link, search by loose link
		$user_id = $headers['Userid'];
		$app_name = $headers['Appname'];
		$producer_id = $headers['Producerid'];
		$id = findActivationRecordLoose($user_id, $app_name, $producer_id, $db);

		if ($id > 0) {

			// found by loose link
			// this is a new activation: check the activation code
			$code = $headers['Activationcode'];

			if (isset($code)) {
				header('acode: ' . $code);
			}else{
				header('acode: not set');
			}


			if (checkActivationCode($db, $id, $code)) {
				// activation code ok
				// set the Activation flag to true and register Unique Id
				$uniqueid = $headers['Uniqueid'];
				mysql_query("UPDATE activation SET active = 1, uniqueid='$uniqueid' WHERE id = '$id'");

				// put bundle in response and return success
				putBundleInResponse($db,$id);
				$success=true;

			}else{
				// activation code wrong
				header('failurecode: ' . ACTIVATION_CODE_WRONG);

			}

		}else{

			//not found by loose link
			//try by oprducer_id/user_id (just to return a more clear error response)
			$producer_id = $headers['Producerid'];
			$user_id = $headers['Userid'];
			$query = "SELECT id FROM activation
			WHERE userid = '$user_id' 
			AND producer_id = $producer_id 
			AND tracking_only = 0";
			$resource = mysql_query($query, $db->getConnection());
			if (mysql_num_rows($resource) > 0) {
				// found by producer/user but not app_name:
				// there is an Activation Record for the producer/user
				// but it relates to another app
				header('failurecode: ' . RECORD_FOR_OTHER_APP);
			}else{
				// activation record not found
				// this is an attempt to activate the installation
				// before purchase data is added to the backend.
				header('failurecode: ' . ACTIVATION_RECORD_NOT_FOUND);
			}

		}

	}

	// Save last successful activation and update timestamp
	if ($success) {
		$current_time=time();
		mysql_query("UPDATE activation SET last_activation='$current_time', last_update = '$current_time' WHERE id = '$id'");
	}
	
	// set success header
	if ($success) {
		header('success: true');
	}else{
		header('success: false');
	}


	unset($db);

}

// request for the creation of a Custom Event
function event($headers){
	$success = false;

	$db = new Database();

	$unique_id = $headers['Uniqueid'];

	// search the Activation Record
	$id = findActivationRecordStrong($unique_id, $db);

	// if not found, search the Tracking Record
	if ($id==0) {
		$query = "SELECT id FROM activation
				WHERE uniqueid = '$unique_id' 
				AND tracking_only = 1";
		$resource = mysql_query($query, $db->getConnection());

		if (mysql_num_rows($resource) > 0) {
			$row = mysql_fetch_array($resource);
			$id=$row['id'];
		}
	}

	// if not found, create a Tracking Record now
	if ($id==0) {
		$producer_id = $headers['Producerid'];
		$app_name = $headers['Appname'];
		$query = "INSERT INTO activation (uniqueid, producer_id, app_name, tracking_only) VALUES ('$unique_id','$producer_id','$app_name',1)";
		$resource = mysql_query($query, $db->getConnection());
		if ($resource) {
			$id = mysql_insert_id($db->getConnection());
		}
	}

	// create the Event Record as a child
	if ($id>0) {
		$timestamp = time();
		$event_code =  $headers['Eventcode'];
		$event_details = $headers['Eventdetails'];
		$query = "INSERT INTO event (activation_id, timestamp, code, details) VALUES ('$id','$timestamp','$event_code','$event_details')";
		$resource = mysql_query($query, $db->getConnection());
		if ($resource) {
			$success=true;
		}
	}

	// set success header
	if ($success) {
		header('success: true');
	}else{
		header('success: false');
	}

	unset($db);


}




// put the data bundle in the response for a given activation record
// @param $db the Database instance
// @param $id the activation record id
function putBundleInResponse($db,$id){
	$query = "SELECT active, level, expiration FROM activation WHERE id = '$id'";
	$resource = mysql_query($query , $db->getConnection());
	if ($resource) {
		if (mysql_num_rows($resource) == 1) {

			$row = mysql_fetch_array($resource);

			// activation flag
			if ($row['active']==1) {
				header('activated: true');
			} else {
				header('activated: false');
			}

			// level
			header('level: ' . $row['level']);

			// expiration
			header('expiration: ' . $row['expiration']);

		}
	}
}


// Search an activation record by strong link
// @param $unique_id the unique id
// @param $db the database
// @return the activation record id, 0 if not found
function findActivationRecordStrong($unique_id,$db){
	$id=0;
	$query = "SELECT id FROM activation
	WHERE uniqueid = '$unique_id' 
	AND tracking_only = 0";

	$resource = mysql_query($query, $db->getConnection());

	if (mysql_num_rows($resource) > 0) {

		$row = mysql_fetch_array($resource);
		$id=$row['id'];

	}

	return $id;
}


// Search an activation record by loose link
// @param $user_id the user id
// @param $app_name the app name
// @param $producer_id the producer id
// @param $db the database
// @return the activation record id, 0 if not found
function findActivationRecordLoose($user_id,$app_name,$producer_id,$db){
	$id=0;
	$query = "SELECT id FROM activation
	WHERE userid = '$user_id' 
	AND app_name = '$app_name' 
	AND producer_id = '$producer_id' 
	AND tracking_only = 0";

	$resource = mysql_query($query, $db->getConnection());

	if (mysql_num_rows($resource) > 0) {

		$row = mysql_fetch_array($resource);
		$id=$row['id'];

	}

	return $id;
}

// checks the activation code for a given activation record
// @param $db the Database instance
// @param $id the activation record id
// @param $code the activation code to check
// @return true if the code is correct
function checkActivationCode($db, $id, $code) {
	$correct = false;
	$query = "SELECT activation_code FROM activation WHERE id = '$id'";
	$resource = mysql_query($query , $db->getConnection());
	if ($resource) {
		if (mysql_num_rows($resource) == 1) {
			$row = mysql_fetch_array($resource);
			if ($row['activation_code'] == $code) {
				$correct = true;
			}
		}
	}

	return $correct;
}


// parse the request headers form the $_SERVER variable
// @return a map containing the request headers
function parseRequestHeaders() {
	$headers = array();
	foreach($_SERVER as $key => $value) {
		if (substr($key, 0, 5) <> 'HTTP_') {
			continue;
		}
		$header = str_replace(' ', '-', ucwords(str_replace('_', ' ', strtolower(substr($key, 5)))));
		$headers[$header] = $value;
	}
	return $headers;
}



?>