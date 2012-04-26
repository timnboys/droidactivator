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

include_once "lib/dbconfig.php";

date_default_timezone_set('UTC');

// Database object
// Holds the description of the database structure
// Performs operations on the database
// Database and tables are created the first time this object is instantiated
class Database {

	// a connection is opened each time the Database is instantiated
	// and is closed when the db is released (unset)
	private $connection;

	// activation table description
	const CREATE_ACTIVATION_TABLE =
						"CREATE TABLE IF NOT EXISTS activation
						 (id SERIAL PRIMARY KEY, 
						 producer_id INT,
						 app_name VARCHAR(255),
						 active BIT(1),
						 tracking_only BIT(1),
						 uniqueid VARCHAR(255),
						 level INT,
						 userid VARCHAR(255),
						 expiration DATETIME,
						 activation_code VARCHAR(8),
						 last_activation DATETIME,
						 last_update DATETIME)";

	// event table description
	const CREATE_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS event
						 (id SERIAL PRIMARY KEY, 
						 activation_id BIGINT UNSIGNED NOT NULL,
 						 timestamp DATETIME,
						 code INT,
						 details VARCHAR(255))";



	public function __construct() {

		// create and register a connection
		$this->connection = mysql_connect(Config::HOST, Config::USERNAME, Config::PASSWORD);
		if (!$this->connection){
			echo('Could not connect: ' . mysql_error());
			die();
		}

		//Sets the current active database
		mysql_select_db(Config::DATABASE, $this->connection);

	}

	public function __destruct() {
		if ($this->connection) {
			mysql_close($this->connection);
		}
	}

	// prepares the database for usage
	// return true if the database was already prepared or has been prepared correctly
	public function prepare(){
		$success=true;

		// check for database existence, create if not exists
		if (!$this->existsDatabase()) {
			if ($this->createDatabase()) {
				mysql_select_db(Config::DATABASE, $this->connection);//Sets the current active database
			}else{
				$success=false;
				$error = mysql_error($this->getConnection());
				echo($error);
			}
		}

		// check if activation table exists, create if not exists
		if (!$this->existsTable('activation')) {
			if (!$this->createActivationTable()) {
				$success=false;
				$error = mysql_error($this->getConnection());
				echo($error);
			}
		}

		// check if event table exists, create if not exists
		if (!$this->existsTable('event')) {
			if (!$this->createEventTable()) {
				$success=false;
				$error = mysql_error($this->getConnection());
				echo($error);
			}
		}

		return $success;

	}


	public function getConnection() {
		return $this->connection;
	}

	public function isSetUp() {
		return mysql_query("SELECT * FROM activation", $this->connection);
	}

	public function setupDb() {
		$result = mysql_query(Database::CREATE_ACTIVATION_TABLE, $this->connection);
		if($result){
			$result = mysql_query(Database::CREATE_EVENT_TABLE, $this->connection);
		}
		return $result;
	}


	// creates the database
	// @return a resource if created successfully, false on error
	function createDatabase(){
		$query = "CREATE DATABASE IF NOT EXISTS " . Config::DATABASE;
		return mysql_query($query, $this->connection);
	}

	// creates the Activation table
	// @return a resource if created successfully, false on error
	function createActivationTable(){
		return mysql_query(Database::CREATE_ACTIVATION_TABLE, $this->connection);
	}

	// creates the Event table
	// @return a resource if created successfully, false on error
	function createEventTable(){
		return mysql_query(Database::CREATE_EVENT_TABLE, $this->connection);
	}

	// check if a table exists
	// @return true if created successfully, false on error
	function existsTable($tableName){
		$exists=false;
		$query = "SHOW TABLES LIKE '".$tableName."'";
		$resource = mysql_query($query, $this->connection);
		if ($resource) {
			if (mysql_num_rows($resource)>0) {
				$exists=true;
			}
		}
		return $exists;
	}

	// check if the database exists
	// @return true if it exists
	function existsDatabase(){
		$exists= false;
		$query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" . Config::DATABASE . "'";
		$resource = mysql_query($query, $this->connection);
		if ($resource){
			if (mysql_num_rows($resource) == 1) {
				$exists= true;
			}
		}
		return $exists;
	}

	// create the html code for the activation list
	public function createActivationList() {


		$resource = mysql_query("SELECT * FROM activation ORDER BY id", $this->connection);
		while ($row = mysql_fetch_array($resource)) {

			$activated="";
			if ($row['active']==1) {
				$activated="checked";
			}

			$tracking_only="";
			if ($row['tracking_only']==1) {
				$tracking_only="checked";
			}

			$expiration="none";
			if ($row['expiration']>0){
				$expiration_dt=$row['expiration'];
				$expiration_php= strtotime( $expiration_dt );
				$expiration = date("d-m-Y", $expiration_php);
			}

			$last_activation="none";
			if ($row['last_activation']>0){
				$last_activation=$row['last_activation'];
				$last_activation_php= strtotime($last_activation);
				$last_activation = date("d-m-Y", $last_activation_php);
			}

			$last_update="none";
			if ($row['last_update']>0){
				$last_update=$row['last_update'];
				$last_update_php= strtotime($last_update);
				$last_update = date("d-m-Y", $last_update_php);
			}



			echo '<tr>
					<td><small>'.$row['id'].'</small></td>
					<td><small>'.$row['userid'].'</small></td>
					<td><small>'.$row['app_name'].'</small></td>
					<td><small>'.$row['activation_code'].'</small></td>
					<td><small>'.$row['producer_id'].'</small></td>
					<td><small><input type="checkbox" disabled="disabled" '.$activated.' /></small></td>
					<td><small>'.$row['uniqueid'].'</small></td>
					<td><small><input type="checkbox" disabled="disabled" '.$tracking_only.' /></small></td>
					<td><small>'.$row['level'].'</small></td>
					<td><small>'.$expiration.'</small></td>
					<td><small>'.$last_activation.'</small></td>
					<td><small>'.$last_update.'</small></td>
					<td><small><a href="edit.php?activation='.$row['id'].'" TARGET="_blank">Edit</a></small></td>
					<td><small><a href="'.$_SERVER['PHP_SELF'].'?delete='.$row['id'].'">Delete</a></small></td>
					<td><small><a href="events.php?activation='.$row['id'].'" TARGET="_blank">Events</a></small></td>
				</tr>
				';
		}
	}



	// deletes a record from the Activation table
	// @return code: 0 if deleted successfully, -1 error
	public function deleteActivationRecord($id) {
		$retcode=-1;

		// delete events
		$query = "DELETE FROM event WHERE activation_id = '$id'";
		$result = mysql_query($query, $this->connection);

		// delete activation record
		$query = "DELETE FROM activation WHERE id = '$id'";
		$result = mysql_query($query, $this->connection);
		if ($result) {
			$retcode=0;
		}else{
			echo(mysql_error($this->connection) . '<br>');
		}
		return $retcode;
	}


	// create the html code for the event list
	public function createEventList($activationId) {

		$resource = mysql_query("SELECT * FROM event WHERE activation_id='$activationId'  ORDER BY id", $this->connection);
		while ($row = mysql_fetch_array($resource)) {

			$eventId=$row['id'];
			$timestamp=$row['timestamp'];
			$code=$row['code'];
			$details=$row['details'];

			echo '<tr>
					<td><small>'.$eventId.'</small></td>
					<td><small>'.$timestamp.'</small></td>
					<td><small>'.$code.'</small></td>
					<td><small>'.$details.'</small></td>
					<td><small><a href="'.$_SERVER['PHP_SELF'].'?activation='.$activationId.'&delete='.$row['id'].'">Delete</a></small></td>
				</tr>
				';
		}
	}


	// deletes a record from the Event table
	// @return code: 0 if deleted successfully, -1 error
	public function deleteEventRecord($id) {
		$retcode=-1;

		// delete event record
		$query = "DELETE FROM event WHERE id = '$id'";
		$result = mysql_query($query, $this->connection);
		if ($result) {
			$retcode=0;
		}else{
			echo(mysql_error($this->connection) . '<br>');
		}
		return $retcode;
	}


	// returns an array (map) containing data for a given activation record
	// @param $id the activation record id
	// @return an array containing data for all the fields
	public function getActivationMap($id) {

		$query = "SELECT * FROM activation WHERE id = '$id'";
		$resource = mysql_query($query, $this->connection);
		$row = mysql_fetch_array($resource);
		return $row;

	}

	// creates or saves an Activation record
	// if the id is specified, the record is modified and saved
	// if the id is not specified, the record is created
	// @param $id the activation record id
	// @param $map map containing values for the fields
	// @return the id of the created/saved record, or 0 if failed
	public function createOrSaveActivationRecord($id, $map) {
		$returnId=0;
		if($id>0){
			$result=Database::saveActivationRecord($id, $map);
			if ($result==true){
				$returnId=$id;
			}
		}else{
			$returnId = Database::createActivationRecord($map);
		}
		return $returnId;
	}


	// writes the given values in an Activation record and saves the record
	// @param $id the activation record id
	// @param $map map containing values for the fields
	// @return true if record was saved successfully
	public function saveActivationRecord($id, $map) {

		$query="UPDATE activation SET ";
		$i=0;
		foreach ($map as $key => $value) {
			$i++;
			$query = $query . $key;
			$query = $query . "=";

			if ($value!='null') {
				$value= "'" . $value . "'";
			}
				
			// bit values patch
			if ($key=='active' | $key=='tracking_only') {
				$value = "b" . $value;
			}
				
			$query = $query . $value;
				
			if ($i < sizeof($map)) {
				$query = $query . ", ";
			}
		}
		$query=$query . " WHERE id = '$id'";
		$result = mysql_query($query, $this->connection);

		if (!$result) {
			echo(mysql_error($this->connection) . '<br>');
		}

		return $result;

	}


	// creates a new Activation record with the given data
	// @param $map map containing values for the fields
	// @return the id of the created record, or 0 if failed
	public function createActivationRecord($map) {

		$insertId=0;
		
		$query="INSERT INTO activation ";
		$query = $query . "(";
		$i=0;
		foreach ($map as $key => $value) {
			$i++;
			$query = $query . $key;
			if ($i < sizeof($map)) {
				$query = $query . ", ";
			}
		}
		$query = $query . ")";
		
		$query = $query . " VALUES ";
		
		$query = $query . "(";
		$i=0;
		foreach ($map as $key => $value) {
			$i++;
			
			if ($value!='null') {
				$value= "'" . $value . "'";
			}
			
			// bit values patch
			if ($key=='active' | $key=='tracking_only') {
				$value = "b" . $value;
			}
			
			$query = $query . $value;

			if ($i < sizeof($map)) {
				$query = $query . ", ";
			}
		}
		$query = $query . ")";
		
		$result = mysql_query($query, $this->connection);

		if ($result) {
			$insertId = mysql_insert_id();
		}else{
			echo(mysql_error($this->connection) . '<br>');
		}

		return $insertId;

	}





}

?>