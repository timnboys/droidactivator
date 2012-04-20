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
$activation = $_GET['activation'];

$save_flag = $_POST['save_flag'];

if (isset($pwd)) {
	if ($pwd == Config::ADMIN_PASSWORD) {
		$_SESSION['authorized'] = true;
	}else{
		$wrongPassword = "<p>Wrong password</p>";
	}
}

$db = new Database();
$db->prepare();


if ($save_flag) {
	$id = $_POST['id'];
	$map=array();
	$map['userid'] = $_POST['userid'];
	$map['app_name'] = $_POST['app_name'];
	$map['activation_code'] = $_POST['activation_code'];
	$map['producer_id'] = $_POST['producer_id'];
	
	$value=$_POST['active'];
	if (isset($value)) {
		$value="1";
	}else{
		$value="0";
	}
	$map['active'] = $value;
	
	$map['uniqueid'] = $_POST['uniqueid'];
	
	$value=$_POST['tracking_only'];
	if (isset($value)) {
		$value="1";
	}else{
		$value="0";
	}
	$map['tracking_only'] = $value;
	
	$map['level'] = $_POST['level'];
	$map['expiration'] = $_POST['expiration'];
	
	$time=$_POST['last_activation'];
	if (!isset($time)) {
		$time="0000-00-00 00:00:00";
	}
	$map['last_activation'] = $time;
	
	$time=$_POST['last_update'];
	if (!isset($time)) {
		$time="0000-00-00 00:00:00";
	}
	$map['last_update'] = $time;

	$db -> saveActivationRecord($id, $map);
	
	?>
	<script type="text/javascript">window.close();</script>
	<?php
	
}
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

	<?php $data = $db -> getActivationMap($activation) ?>


	<form name="addactivation" action="<?php echo $_SERVER['PHP_SELF']; ?>" method="POST">
		
		<table border="0">

		<tr>
			<td>Id:</td>
			<td><input type="text" size="10" name="id" readonly value=<?php echo $data['id']?> ></td>
		</tr>

		<tr>
			<td>User Id (e-mail):*</td>
			<td><input type="text" size="35" name="userid" value=<?php echo $data['userid']?> ></td>
		</tr>
		
		<tr>
			<td>App Name:*</td>
			<td><input type="text" size="25" name="app_name" value=<?php echo $data['app_name']?> ></td>
		</tr>
		
		
		<tr>
			<td>Activation code (8-digits):*</td>
			<td><input type="text" size="8" maxlength="8" name="activation_code" value=<?php echo $data['activation_code']?> ></td>
		</tr>
		
		<tr>
			<td>Producer Id:</td>
			<td><input type="text" size="8" name="producer_id" value=<?php echo $data['producer_id']?> ></td>
		</tr>
		
		<tr>
			<td>Activated:</td>
			<td><input type="checkbox" name="active" <?php if ($data['active']==1) {echo 'checked';}?> ></td>
		</tr>
		
		<tr>
			<td>Unique Id:</td>
			<td><input type="text" size="90" name="uniqueid" value=<?php echo $data['uniqueid'] ?> ></td>
		</tr>
		
		<tr>
			<td>Tracking only:</td>
			<td><input type="checkbox" name="tracking_only" <?php if ($data['tracking_only']==1) {echo 'checked';}?> ></td>
		</tr>
		
		<tr>
			<td>Level:</td>
			<td><input type="text" size="2" name="level" value=<?php echo $data['level']?> ></td>
		</tr>
		
		<tr>
			<td>Expiration (yyyy-mm-dd):</td>
			<td><input type="text" size="10" maxlength="10" name="expiration" value=<?php echo $data['expiration']?> ></td>
		</tr>
		
		<tr>
			<td>Last Activation (yyyy-mm-dd):</td>
			<td><input type="text" size="10" maxlength="10" name="last_activation" value=<?php echo $data['last_activation']?> ></td>
		</tr>
		
		<tr>
			<td>Last Update (yyyy-mm-dd):</td>
			<td><input type="text" size="10" maxlength="10" name="last_update" value=<?php echo $data['last_update']?> ></td>
		</tr>

		</table>
		
		<br><input type="submit" value="Save Record" >
		<br><input type="hidden" name="save_flag" value="true" > 
	</form>


	<?php
	endif;
	unset($db);
	?>

</body>
</html>

