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

session_set_cookie_params(3600);
session_start();


$pwd = $_POST['pwd'];
$userid = $_POST['userid'];
$appname = $_POST['appname'];
$producerid = $_POST['producerid'];
$activationcode = $_POST['activationcode'];
$level = $_POST['level'];
$expiration = $_POST['expiration'];
$addrecord = $_POST['addrecord'];

$delete = $_GET['delete'];
$execdelete = $_GET['execdelete'];

$listevents = $_GET['listevents'];

if (isset($pwd)) {
	if ($pwd == Config::ADMIN_PASSWORD) {
		$_SESSION['authorized'] = true;
	}else{
		$wrongPassword = "<p>Wrong password</p>";
	}
}

$db = Database::getInstance();
$db2 = Database::getInstance();
$db = new Database();

		$_SESSION['database'] = $db;

//$db->prepare();

?>




<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 //EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=Cp1252">
<title>DroidActivator Admin</title>
</head>

<body bgcolor="Gainsboro">

	<IMG SRC="images/droidactivator_logo.png">
	<p>

	<?php if (!isset($_SESSION['authorized']) || $_SESSION['authorized'] == false): ?>

	<?php
	if (isset($wrongPassword)) {
		echo $wrongPassword;
	}
	?>
	
	
	<form name="Login" action="<?php echo $_SERVER['PHP_SELF']; ?>"
		method="POST">
		Enter Admin password: <input type="password" name="pwd" /> <input
			type="submit" value="Login" />
	</form>

	<?php else: ?>

	<?php


	if (isset($addrecord)) {
		?>
		<script type="text/javascript">
		window.open('./edit.php','','scrollbars=no,menubar=no,resizable=yes,width=800,height=600,toolbar=no,location=no,status=no');
		</script>
		<?php
	}
	

	// executes a Javascript requesting user confirmation
	// if confirmed calls the same page with an "execdelete" parameter
	if (isset($delete)) {
		
		?>
		<script type="text/javascript">
		if (confirm("Delete the record?")){
            window.location.href = "<?php echo $_SERVER['PHP_SELF']; ?>?execdelete=<?php echo $delete?>"; 
		}
		</script>
		<?php
		
	}
	
	// deletes an Activation record
	if (isset($execdelete)) {
		$retcode = $db->deleteActivationRecord($execdelete);
		switch ($retcode) {
			case -1:	// generic error
				echo("<strong>Unable to delete activation record ". $execdelete . "</strong>");
				break;
			case 0:	// deleted OK
				echo("<strong>Activation record " .  $execdelete . " deleted.</strong>");
				break;
		}
	}
	

	?>

	
	<form action="<?php echo $_SERVER['PHP_SELF']; ?>" method="POST">
		<input type="submit" name="addrecord" value="Add record" />
		<input type="submit" name="refresh" value="Refresh list" />
	</form>
	
	<table border="1">

		<tr>
			<th><small>Id</small></th>
			<th><small>User Id</small></th>
			<th><small>App Name</small></th>
			<th><small>Activation code</small></th>
			<th><small>Producer Id</small></th>
			<th><small>Activated</small></th>
			<th><small>Unique Id</small></th>
			<th><small>TrackOnly</small></th>
			<th><small>Level</small></th>
			<th><small>Expiration</small></th>
			<th><small>Last activation</small></th>
			<th><small>Last update</small></th>
		</tr>

		<?php $db->createActivationList(); ?>

	</table>


	<?php if (isset($listevents)) { ?>
		<br>Events for Activation #<?php echo($listevents); ?>
		<table border="1">
			<tr>
				<th><small>Evt Id</small></th>
				<th><small>Timestamp</small></th>
				<th><small>Code</small></th>
				<th><small>Details</small></th>
			</tr>
	
			<?php $db->createEventList($listevents); ?>
	
		</table>

	<?php } ?>



	<?php
	endif;
	unset($db);
	?>

</body>
</html>
