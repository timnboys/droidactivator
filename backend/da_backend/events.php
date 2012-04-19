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

$delete = $_GET['delete'];
$activation = $_GET['activation'];

if (isset($pwd)) {
	if ($pwd == Config::ADMIN_PASSWORD) {
		$_SESSION['authorized'] = true;
	}else{
		$wrongPassword = "<p>Wrong password</p>";
	}
}

$db = new Database();
$db->prepare();

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
	if (isset($delete)) {
		
		$retcode = $db->deleteEventRecord($delete);
		switch ($retcode) {
			case -1:	// generic error
				echo("<strong>Unable to delete event record ". $delete . "</strong>");
				break;
			case 0:	// deleted OK
				echo("<strong>Event record " .  $delete . " deleted.</strong>");
				break;
		}
	}
	?>


	<p>
	

	<br>Events for Activation #<?php echo($activation); ?>
	<table border="1">
		<tr>
			<th><small>Evt Id</small></th>
			<th><small>Timestamp</small></th>
			<th><small>Code</small></th>
			<th><small>Details</small></th>
		</tr>
		<?php $db->createEventList($activation); ?>
	</table>



	<?php
	endif;
	unset($db);
	?>

</body>
</html>
