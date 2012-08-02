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


define('ACTIVATION_CODE_WRONG', 1);
define('RECORD_FOR_OTHER_APP', 2);
define('ACTIVATION_RECORD_NOT_FOUND', 3);
		
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
	case "ensureactivationrecord":
		ensureactivation($headers);
		break;
		

}

// request to check if the backend is responding
function checkresponding(){
	header('success: true');
}

// request to check if a unique id is present in the database
function checkuidpresent($headers){
	$conn=getConnection();

	// The Activation record is searched by strong link
	$unique_id = $headers['Uniqueid'];
	$id = findActivationRecordStrong($unique_id, $conn);
	unset($conn);

	if ($id>0){
		header('success: true');
	}else{
		header('success: false');
	}

}




// request for an Update
function update($headers){
	$success=false;
	$conn=getConnection();

	// The Activation record is searched by strong link
	$unique_id = $headers['Uniqueid'];
	$id = findActivationRecordStrong($unique_id, $conn);

	if ($id>0){
		// Activation Record found by strong link, this is the normal situation
		// put bundle in response and return success
		putBundleInResponse($conn,$id);
		$success=true;

	}else{

		// Activation Record not found by strong link, search by loose link
		$user_id = $headers['Userid'];
		$app_name = $headers['Appname'];
		$producer_id = $headers['Producerid'];
		$id = findActivationRecordLoose($user_id, $app_name, $producer_id, $conn);

		if ($id>0){

			// turn off activation flag and reset Unique Id
			$query="UPDATE activation SET active = 0, uniqueid = '' WHERE id = '$id'";
			mysqli_query($conn,$query);

			// put bundle in response and return success
			putBundleInResponse($conn,$id);
			$success=true;

		}

	}

	// Save last successful update timestamp
	if ($success) {
		$phpdate=time();
		$mysqldate = date( 'Y-m-d H:i:s', $phpdate );
		$query="UPDATE activation SET last_update = '$mysqldate' WHERE id = '$id'";
		mysqli_query($conn,$query);
	}
	
	// set success header
	if ($success) {
		header('success: true');
	}else{
		header('success: false');
	}

	unset($conn);

}


// request for an Activation
function activate($headers){

	$success=false;
	$conn = getConnection();
	

	// The Activation record is searched by strong link.
	$unique_id = $headers['Uniqueid'];
	$id = findActivationRecordStrong($unique_id, $conn);

	if ($id>0){

		// Found by strong link. This is a reactivation.
		// Check the Activation code.
		$code = $headers['Activationcode'];
		if (checkActivationCode($conn, $id, $code)) {

			// Success: set the Activation flag to true
			$query="UPDATE activation SET active = 1 WHERE id = '$id'";
			mysqli_query($conn, $query);

			// put bundle in response and return success
			putBundleInResponse($conn,$id);
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
		$id = findActivationRecordLoose($user_id, $app_name, $producer_id, $conn);

		if ($id > 0) {

			// found by loose link
			// this is a new activation: check the activation code
			$code = $headers['Activationcode'];

			if (checkActivationCode($conn, $id, $code)) {
				// activation code ok
				// set the Activation flag to true and register Unique Id and device info
				$uniqueid = $headers['Uniqueid'];
				$device_info = $headers['Deviceinfo'];
				$query = "UPDATE activation SET active = 1, uniqueid='$uniqueid'";
				if (isset($device_info)) {
					$query = $query . ", device_info='$device_info'";
				}
				$query = $query . " WHERE id = '$id'";
				
				mysqli_query($conn, $query);

				// put bundle in response and return success
				putBundleInResponse($conn,$id);
				$success=true;

			}else{
				// activation code wrong
				header('failurecode: ' . ACTIVATION_CODE_WRONG);

			}

		}else{

			//not found by loose link
			//try by producer_id/user_id (just to return a more clear error response)
			$producer_id = $headers['Producerid'];
			$user_id = $headers['Userid'];
			$query = "SELECT id FROM activation
			WHERE userid = '$user_id' 
			AND producer_id = $producer_id 
			AND tracking_only = 0";
			$resource = mysqli_query($conn, $query);
			if (mysqli_num_rows($resource) > 0) {
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
		$phpdate=time();
		date_default_timezone_set("UTC");
		$mysqldate = date( 'Y-m-d H:i:s', $phpdate );
		$query = "UPDATE activation SET last_activation='$mysqldate', last_update = '$mysqldate' WHERE id = '$id'";
		mysqli_query($conn, $query);
	}
	
	// Check if a Tracking Record is already present for this installation.
	// In this case, move all the events from the Tracking Record to the
	// Activation Record and delete the Tracking record.
	if ($success) {
		$uniqueid = $headers['Uniqueid'];
		moveEvents($uniqueid, $id, $conn);
	}
	
	// set success header
	if ($success) {
		header('success: true');
	}else{
		header('success: false');
	}


	unset($conn);

}


// Check if a Tracking Record is already present for an installation.
// In this case, move all the events under the Activation record and delete the Tracking record.
// @param $uniqueid the unique id
// @param $activationid the activation id
// @param $conn the connetcion
function moveEvents($uniqueid, $activationid, $conn){

	// retrieve the tracking record id
	$trackingid=0;
	$query = "SELECT id FROM activation
	WHERE uniqueid = '$uniqueid' 
	AND tracking_only = 1";
	$resource = mysqli_query($conn, $query);
	if (mysqli_num_rows($resource) > 0) {
		$row = mysqli_fetch_array($resource);
		$trackingid=$row['id'];
	}
	
	// move events to the new Activation record and delete the Tracking record
	if ($trackingid>0) {
		$query = "UPDATE event SET activation_id = '$activationid' WHERE activation_id = '$trackingid'";
		mysqli_query($conn, $query);
		$query = "DELETE FROM activation WHERE id = '$trackingid'";
		mysqli_query($conn, $query);
	}
	
}


// request for the creation of a Custom Event
function event($headers){
	$success = false;

	$conn = getConnection();

	$unique_id = $headers['Uniqueid'];

	// search the Activation Record
	$id = findActivationRecordStrong($unique_id, $conn);

	// if not found, search the Tracking Only Record
	if ($id==0) {
		$query = "SELECT id FROM activation
				WHERE uniqueid = '$unique_id' 
				AND tracking_only = 1";
		$resource = mysqli_query($conn, $query);

		if (mysqli_num_rows($resource) > 0) {
			$row = mysqli_fetch_array($resource);
			$id=$row['id'];
		}
	}

	// create the Event Record as a child
	if ($id>0) {
		$phpdate = time();
		date_default_timezone_set("UTC");
		$mysqldate = date( 'Y-m-d H:i:s', $phpdate );
		
		$event_code =  $headers['Eventcode'];
		$event_details = $headers['Eventdetails'];
		$query = "INSERT INTO event (activation_id, timestamp, code, details) VALUES ('$id','$mysqldate','$event_code','$event_details')";
		$resource = mysqli_query($conn, $query);
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

	unset($conn);


}

// request to check for the existence and eventually create 
// an Activation Record for a given uniqueid
function ensureactivation($headers){
	$success=false;
	
	// retrieve informations from header
	$unique_id = $headers['Uniqueid'];
	$producer_id = $headers['Producerid'];
	$app_name = $headers['Appname'];
	$tracking_only = $headers['Trackingonly'];
	$device_info = $headers['Deviceinfo'];
	
	$conn = getConnection();
	
	// search the Activation record by $unique_id
	$query = "SELECT id FROM activation
	WHERE uniqueid = '$unique_id'";
	$resource = mysqli_query($conn, $query);
	if (mysqli_num_rows($resource) > 0) {
		$row = mysqli_fetch_array($resource);
		$id=$row['id'];
	}
	

	if($id>0){
		$success=true;
	}else{
		$map=array();
		
		if (isset($unique_id)) {
			$map['uniqueid'] = $unique_id;
		}
		if (isset($producer_id)) {
			$map['producer_id'] = $producer_id;
		}
		if (isset($app_name)) {
			$map['app_name'] = $app_name;
		}
		if (isset($tracking_only)) {
			$map['tracking_only'] = $tracking_only;
		}
		if (isset($device_info)) {
			$map['device_info'] = $device_info;
		}
		
		include_once 'model.php';
		$model = new ActivationModel();
		$result = $model -> save($map);
		$code = substr($result, 0, 2);
		if ($code=="OK"){
			$success=true;
		}
		
	}
	
	unset($conn);
	
	
	// set success header
	if ($success) {
		header('success: true');
	}else{
		header('success: false');
	}
		
}

	


// put the data bundle in the response for a given activation record
// @param $conn the connection
// @param $id the activation record id
function putBundleInResponse($conn,$id){
	$query = "SELECT active, level, expiration FROM activation WHERE id = '$id'";
	$resource = mysqli_query($conn, $query);
	if ($resource) {
		if (mysqli_num_rows($resource) == 1) {

			$row = mysqli_fetch_array($resource);

			// activation flag
			if ($row['active']==1) {
				header('activated: true');
			} else {
				header('activated: false');
			}

			// level
			header('level: ' . $row['level']);

			// expiration
			$mysqldate=$row['expiration'];
			date_default_timezone_set("UTC");
			$php_date=strtotime($mysqldate);
			header('expiration: ' . $php_date);

		}
	}
}


// Search an activation record by strong link
// @param $unique_id the unique id
// @param $conn the connection
// @return the activation record id, 0 if not found
function findActivationRecordStrong($unique_id,$conn){
	$id=0;
	$query = "SELECT id FROM activation
	WHERE uniqueid = '$unique_id' 
	AND tracking_only = 0";

	$resource = mysqli_query($conn, $query);

	if (mysqli_num_rows($resource) > 0) {

		$row = mysqli_fetch_array($resource);
		$id=$row['id'];

	}

	return $id;
}


// Search an activation record by loose link
// @param $user_id the user id
// @param $app_name the app name
// @param $producer_id the producer id
// @param $conn the connection
// @return the activation record id, 0 if not found
function findActivationRecordLoose($user_id,$app_name,$producer_id,$conn){
	$id=0;
	$query = "SELECT id FROM activation
	WHERE userid = '$user_id' 
	AND app_name = '$app_name' 
	AND producer_id = '$producer_id' 
	AND tracking_only = 0";

	$resource = mysqli_query($conn, $query);

	if (mysqli_num_rows($resource) > 0) {

		$row = mysqli_fetch_array($resource);
		$id=$row['id'];

	}

	return $id;
}

// checks the activation code for a given activation record
// @param $conn the connection
// @param $id the activation record id
// @param $code the activation code to check
// @return true if the code is correct
function checkActivationCode($conn, $id, $code) {
	$correct = false;
	$query = "SELECT activation_code FROM activation WHERE id = '$id'";
	$resource = mysqli_query($conn, $query);
	if ($resource) {
		if (mysqli_num_rows($resource) == 1) {
			$row = mysqli_fetch_array($resource);
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

// creates and returns a new connection
function getConnection(){
	include_once 'config.php';
	return new mysqli(Config::HOST, Config::USERNAME, Config::PASSWORD, Config::DATABASE);
}


?>