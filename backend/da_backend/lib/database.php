<?php

include_once "lib/dbconfig.php";

date_default_timezone_set('UTC');

// Database description
// Holds the description of the database structure
// Database and tables are created by calling the Tables are created the first time this object is instantiated
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
						 expiration BIGINT,
						 activation_code VARCHAR(8),
						 last_activation BIGINT,
						 last_update BIGINT)";

	// event table description
	const CREATE_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS event
						 (id SERIAL PRIMARY KEY, 
						 activation_id BIGINT UNSIGNED NOT NULL,
 						 timestamp BIGINT,
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
	// return true if it exists
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
				$expiration = date("d-m-Y", $row['expiration']);
			}
				
			$last_activation="none";
			if ($row['last_activation']>0){
				$last_activation = date("d-m-Y", $row['last_activation']);
			}
			
			$last_update="none";
			if ($row['last_update']>0){
				$last_update = date("d-m-Y", $row['last_update']);
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
					<td><small><a href="'.$_SERVER['PHP_SELF'].'?delete='.$row['id'].'">Delete</a></small></td>
				</tr>
				';
		}
	}


	// adds a record to the Activation table
	// @return code: 0 if added successfully, 1 if already existing, -1 generic error
	public function addActivationRecord($userid, $appname, $producerid, $activationcode, $level, $expiration) {
		$retcode=-1;

		// unix timestamp from string
		$a = strptime($expiration, '%d-%m-%Y');
		$expiration = mktime(0, 0, 0, $a['tm_mon']+1, $a['tm_mday'], $a['tm_year']+1900);
		
		// set some defaults
		if (!isset($producerid)) {$producerid=0;}
		if (!isset($level)) {$level=0;}
		if (!isset($expiration)) {$expiration=0;}

		// check if already existing
		$exists=false;
		$query = "SELECT id FROM activation where userid='$userid' AND app_name = '$appname' AND producer_id = '$producerid'";
		$result = mysql_query($query, $this->connection);
		if ($result) {
			if (mysql_num_rows($result) >0) {
				$exists= true;
				$retcode=1;	// record already existing
			}
		}

		// add the record
		if (!$exists) {
			$query = "INSERT INTO activation (userid, app_name, producer_id, activation_code, level, expiration, active, tracking_only) VALUES ('$userid','$appname','$producerid','$activationcode','$level','$expiration',0,0)";
			$result = mysql_query($query, $this->connection);
			if ($result) {
				$retcode=0;	// OK
			}
		}

		return $retcode;
	}

	// deletes a record from the Activation table
	// @return code: 0 if deleted successfully, -1 generic error
	public function deleteActivationRecord($id) {
		$retcode=-1;
		$query = "DELETE FROM activation WHERE id = '$id'";
		$result = mysql_query($query, $this->connection);
		if ($result) {
			$retcode=0;
		}
		return $retcode;
	}



}

?>