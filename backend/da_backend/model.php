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


class ActivationModel extends AbstractModel{

	
	// constructor
	public function __construct() {
		
		parent::__construct('activation', array(
        "producer_id INT",
		"app_name VARCHAR(255)",
        "active BIT(1)",
    	"tracking_only BIT(1)",
        "uniqueid VARCHAR(255)",
		"level INT",
		"userid VARCHAR(255)",
		"expiration DATETIME",
		"activation_code VARCHAR(8)",
		"last_activation DATETIME",
		"last_update DATETIME",
		"device_info VARCHAR(255)"
		));
	}
	
	// deletes a single row
	// deletes events first
	// @param $id the id of the row to delete
	// @return true on success, false on failure
	public function delete($id){
		
		$success= false;
		
		// retrieve a connection
		$conn = $this->getConnection();
		
		// first of all delete related events
        $eventmodel = unserialize($_SESSION['eventmodel']);
		$table = $eventmodel->getTable();
		$query="DELETE FROM " .$table. " WHERE activation_id=" .$id;
		$result = $conn->query($query);

		// if succeeded, delete row in superclass
		if ($result) {
			$success = parent::delete($id);
		}
		
		return $success;
	}
	
	// saves an existing record
	// @param $map map containing values for the fields
	// @return "OK" if saved successfully, or the error message if failed
	protected function saveRecord($map){
		$error="";
		$cont=true;
		
		
		// retrieve record id
		if ($cont) {
			$id=$map['id'];
			if ($id<=0) {
				$error="missing record id";	
				$cont=false;
			}
		}
		
		// check for mandatory fields
		if ($cont) {
			$ret=$this->checkMandatory($map);
			if (isset($ret)) {
				$error=$ret;	
				$cont=false;
			}
		}
		
		// save the record
		if ($cont) {
			$query="UPDATE " . $this->getTable() ." SET ";
			
			$key='producer_id';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}

			$key='app_name';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}
						
			$key='active';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "=b'" . "1', ";
			}else{
				$query = $query . $key . "=b'" . "0', ";
							}
			
			$key='tracking_only';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "=b'" . "1', ";
			}else{
				$query = $query . $key . "=b'" . "0', ";
			}
						
			$key='uniqueid';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}
			
			$key='level';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}
			
			$key='userid';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}
			
			$key='expiration';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}
			
			$key='activation_code';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}
			
			$key='last_activation';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}

			$key='last_update';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}
			
			$key='device_info';
			$value=$map[$key];
			if (isset($value)) {
				$query = $query . $key . "='" . $value ."', ";
			}
			
			//remove last 2 chars (comma and space)
			$query = substr($query, 0, strlen($query)-2);
			
			// condition
			$query=$query . " WHERE id = '$id'";
			
			// exec query
			$conn = $this->getConnection();
			$success = $conn->query($query);
			if (!$success) {
				$cont=false;
				$error=$conn->error;
			}
			
		}

		// return value
		if ($cont) {
			$retval="OK";
		}else{
			$retval=$error;
		}
		return $retval;
	}
	
	// creates a new record
	// @param $map map containing values for the fields
	// @return "OK" followed by the record id if created successfully, or the error message if failed
	protected function createRecord($map) {
		$error="";
		$cont=true;
		
		// check for mandatory fields
		if ($cont) {
			$ret=$this->checkMandatory($map);
			if (isset($ret)) {
				$error=$ret;	
				$cont=false;
			}
		}
		
		// build query
		if ($cont){
			$query="INSERT INTO " . $this->getTable() . " (";
			$query = $query . "producer_id, ";
			$query = $query . "app_name, ";
			$query = $query . "active, ";
			$query = $query . "tracking_only, ";
			$query = $query . "uniqueid, ";
			$query = $query . "level, ";
			$query = $query . "userid, ";
			$query = $query . "expiration, ";
			$query = $query . "activation_code, ";
			$query = $query . "last_activation, ";
			$query = $query . "last_update, ";
			$query = $query . "device_info";
			$query = $query . ")";

			$query = $query . " VALUES (";

			$value=$map['producer_id'];
			if (!isset($value)) {
				$value="0";
			}
			$query = $query. "'" . $value ."', ";

			$value=$map['app_name'];
			if (!isset($value)) {
				$value="";
			}
			$query = $query. "'" . $value ."', ";

			$value=$map['active'];
			if (isset($value)) {
				if ($value==1) {
					$value="b'1'";
				}else{
					$value="b'0'";
				}
			}else{
				$value="b'0'";
			}
			$query = $query . $value . ", ";

			$value=$map['tracking_only'];
			if (isset($value)) {
				if ($value==1) {
					$value="b'1'";
				}else{
					$value="b'0'";
				}
			}else{
				$value="b'0'";
			}
			$query = $query . $value . ", ";

			$value=$map['uniqueid'];
			if (!isset($value)) {
				$value="";
			}
			$query = $query. "'" . $value ."', ";

			$value=$map['level'];
			if (!isset($value)) {
				$value="0";
			}
			$query = $query. "'" . $value ."', ";

			$value=$map['userid'];
			if (!isset($value)) {
				$value="";
			}
			$query = $query. "'" . $value ."', ";
			
			$value=$map['expiration'];
			if (!isset($value)) {
				$value="";
			}
			$query = $query. "'" . $value ."', ";
			
			$value=$map['activation_code'];
			if (!isset($value)) {
				$value="";
			}
			$query = $query. "'" . $value ."', ";
			
			$value=$map['last_activation'];
			if (!isset($value)) {
				$value="";
			}
			$query = $query. "'" . $value ."', ";
			
			$value=$map['last_update'];
			if (!isset($value)) {
				$value="";
			}
			$query = $query. "'" . $value ."', ";
			
			$value=$map['device_info'];
			if (!isset($value)) {
				$value="";
			}
			$query = $query. "'" . $value ."'";
			
			$query = $query . ")";
		}
		
		
		// exec query
		if ($cont) {
			$conn = $this->getConnection();
			$success = $conn->query($query);
			if ($success) {
				$insert_id=$conn->insert_id;
			}else{
				$cont=false;
				$error=$conn->error;
			}
		}
		
		// after successful creation, when we have the id,
		// if it is a Tracking Only record, automatically calculate the userid
		if ($cont) {
			if (($insert_id>0) && ($map['tracking_only']=="1")) {
				$userid = "Z_T" . sprintf("%06d", $insert_id);
				$query = "UPDATE " . $this->getTable() . " SET userid = '" . $userid . "' WHERE id = " .$insert_id;
				$success = $conn->query($query);
				if (!$success) {
					$cont=false;
					$error=$conn->error;
				}
			}
		}		
		
		// return value
		if ($cont) {
			$retval="OK".$insert_id;
		}else{
			$retval=$error;
		}
		return $retval;
		
	}
	
	
	
	// Checks that mandatory values are present in the data map
	// @return a description of the missing fields, if any
	private function checkMandatory($map){
		
		
		if (!isset($map['app_name']) || $map['app_name']=="") {
			if (isset($retval)) {
				$retval=$retval."\n";
			}
			$retval=$retval . "App name is mandatory";
		}
		
		// if not tracking_only, userid and activation code are required
		if (!isset($map['tracking_only']) || $map['tracking_only']=="0") {
			
			if (!isset($map['userid']) || $map['userid']=="") {
				if (isset($retval)) {
					$retval=$retval."\n";
				}
				$retval="User Id is mandatory";
			}
			
			
			if (!isset($map['activation_code']) || $map['activation_code']=="") {
				if (isset($retval)) {
					$retval=$retval."\n";
				}
				$retval=$retval."Activation code is mandatory";
			}
		}
		
		return $retval;
	}

	// Creates a SQL filter from posted query conditions
	// @param the map with the conditions
	// @return the SQL filter
	static function createFilter($map){
		$filter="";
		
		if (isset($map['id'])) {
			$value=$map['id'];
			if ($value!=0) {
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "id=".$value;
			}
		}
		
		if (isset($map['userid'])) {
			$value=$map['userid'];
			if ($value!="") {
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "userid='".$value."'";
			}
		}
		
		if (isset($map['appname'])) {
			$value=$map['appname'];
			if ($value!="") {
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "app_name='".$value."'";
			}
		}
		
		if (isset($map['activationcode'])) {
			$value=$map['activationcode'];
			if ($value!="") {
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "activation_code='".$value."'";
			}
		}
		
		if (isset($map['producerid'])) {
			$value=$map['producerid'];
			if ($value!=0) {
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "producer_id=".$value;
			}
		}
		
		if (isset($map['activated'])) {
			$value=$map['activated'];
			if (strtoupper($value)!=strtoupper("Any")) {
				$comp="";
				if ($value=="true") {
					$comp="true";
				}
				if ($value=="false") {
					$comp="false";
				}
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "active=".$comp;
			}
		}
		
		if (isset($map['uniqueid'])) {
			$value=$map['uniqueid'];
			if ($value!="") {
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "uniqueid='".$value."'";
			}
		}
		
		if (isset($map['trackonly'])) {
			$value=$map['trackonly'];
			if (strtoupper($value)!=strtoupper("Any")) {
				$comp="";
				if ($value=="true") {
					$comp="true";
				}
				if ($value=="false") {
					$comp="false";
				}
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "tracking_only=".$comp;
			}
		}
		
		if (isset($map['level'])) {
			$value=$map['level'];
			if ($value!=0) {
				if ($filter!="") {
					$filter = $filter ." AND ";
				}
				$filter = $filter . "level=".$value;
			}
		}
		
		
		return ($filter);
	}
	
	

}


class EventModel extends AbstractModel{

	// constructor
	public function __construct() {
		
		parent::__construct('event', array(
        "timestamp DATETIME",
        "code INT",
    	"details VARCHAR(255)"
		));
	}
	
}


abstract class AbstractModel{
	
	
	// the table name
	private $table;
	
	// the field definition
	private $fieldDef;
	
	

	// constructor
	public function __construct($table, $fieldDef) {
		
		$this->table=$table;
		$this->fieldDef=$fieldDef;
		
		// create a new connection without database
		$conn = new mysqli(Config::HOST, Config::USERNAME, Config::PASSWORD);
		
		/* check connection */
		if (mysqli_connect_errno()) {
		    echo("Connect failed: %s\n". mysqli_connect_error());
			die();
		}
		
		//Check that database exists or create it
		$this->checkDb($conn);
		
		//Check that the table exists or create it
		$this->checkTable();
		
		//Check that the columns exists or create them
		$this->checkColumns();
				
	}

		
	// creates and returns a new connection
	function getConnection(){
		include_once 'config.php';
		return new mysqli(Config::HOST, Config::USERNAME, Config::PASSWORD, Config::DATABASE);
	}
	

	// check for database existence, create if not exists
	private function checkDb($conn){
		if (!$this->existsDatabase($conn)) {
			if (!$this->createDatabase($conn)) {
				echo($conn->error);
			}
		}
	}

	// check for table existence, create if not exists
	private function checkTable(){
		$conn = $this->getConnection();
		if (!$this->existsTable($conn)) {
			if (!$this->createTable($conn)) {
				echo($conn->error);
			}
		}
	}

	// check for columns existence, create missing columns
	private function checkColumns(){
		$conn = $this->getConnection();
		$baseQuery = "ALTER TABLE ".$this->table." ADD COLUMN ";
		foreach ($this->fieldDef as $columnDef) {
			$query=$baseQuery . $columnDef;
			$conn->query($query);
		}
	}
	
	// check if the database exists
	// @param $conn the connection to use
	// @return true if it exists
	private function existsDatabase($conn){
		$exists= false;
		$query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" . Config::DATABASE . "'";
		$result = $conn->query($query);
		if ($result){
			if ($result->num_rows == 1) {
				$exists= true;
			}
		}
		return $exists;
	}
	
	// creates the database
	// @param $conn the connection to use
	// @return a resource if created successfully, false on error
	private function createDatabase($conn){
		include_once 'config.php';
		$query = "CREATE DATABASE IF NOT EXISTS " . Config::DATABASE;
		$result=$conn->query($query);
		return $result;
	}
	
	// check if the model's table exists
	// @param $conn the connection to use
	// @return true if the table exists
	private function existsTable($conn){
		$exists=false;
		$query = "SHOW TABLES LIKE '".$this->table."'";
		$result = $conn->query($query);
		if ($result) {
			if ($result->num_rows > 0) {
				$exists=true;
			}
		}
		return $exists;
	}
	
	// creates the model's table
	// @param $conn the connection to use
	// @return a resource if created successfully, false on error
	private function createTable($conn){
		$query = "CREATE TABLE IF NOT EXISTS " . $this->table . " (id SERIAL PRIMARY KEY)";
		return $conn->query($query);
	}
	
	// retrieves all rows from the table ordered by id
	// @param a filter for the records (no filter = all records)
	// @return a mysqli_result object, or false on failure
	public function listData($filter){
		$conn = $this->getConnection();
		$query="SELECT * FROM " .$this->table;
		if (isset($filter)){
			if ($filter!="") {
				$query = $query . " WHERE " . $filter;
			}
		}
		$query = $query . " ORDER BY id";
		$result = $conn->query($query);
		return $result;
	}
	
	// deletes a single row
	// @param $id the id of the row to delete
	// @return true on success, false on failure
	public function delete($id){
		$result = false;
		$conn = $this->getConnection();
		if ($id) {
			$query="DELETE FROM " .$this->table. " WHERE id=" .$id;
			$result = $conn->query($query);
		}
		return $result;
	}
	
	// @return data for a single row
	// @param $id the id of the row to retrieve
	// @return an associative array (map) with row data
	public function get($id){
		$filter = "id=" . $id;
		$result = $this->listData($filter);
		$map = $result->fetch_array(MYSQLI_ASSOC);
		return $map;
	}
	
	
	// creates or saves a record
	// if the id is specified in the map, the record is modified and saved
	// if the id is not specified, the record is created
	// @param $map map containing values for the fields
	// @return "OK" followed by record id if created/saved successfully, or the error message if failed
	public function save($map) {
		$retval="";
		$id=$map['id'];
		if($id>0){
			$result=$this->saveRecord($map);
			if ($result=="OK"){
				$retval="OK".$id;
			}else{
				$retval=$result;
			}
		}else{
			$retval = $this->createRecord($map);
		}
		return $retval;
	}
	
	// saves an existing record - to be implemented in the concrete class
	// @param $map map containing values for the fields
	// @return "OK" if saved successfully, or the error message if failed
	protected function saveRecord($map) {
		$success=false;
		return $success;
	}
	
	// saves an existing record - to be implemented in the concrete class
	// @param $map map containing values for the fields
	// @return "OK" followed by the record id if created successfully, or the error message if failed
	protected function createRecord($map) {
		$success=false;
		return $success;
	}
	
	
	// @return the table name
	function getTable(){
		return $this->table;
	}
	

}



?>