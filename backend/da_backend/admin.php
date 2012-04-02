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
$addflag = $_POST['add_flag'];

$delete = $_GET['delete'];

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


	if (isset($addflag)) {
		$cont=true;
		
		// check for mandatory fields
		if (!isset($userid) || $userid=="") {echo("<br><strong>User Id is mandatory</strong>");$cont=false;}
		if (!isset($appname) || $appname=="") {echo("<br><strong>App name is mandatory</strong>");$cont=false;}
		if (!isset($activationcode) || $activationcode=="") {echo("<br><strong>Activation code is mandatory</strong>");$cont=false;}
		
		if ($cont) {
			$retcode = $db->addActivationRecord($userid, $appname, $producerid, $activationcode, $level, $expiration);
			switch ($retcode) {
			case -1:	// generic error
				echo("<strong>Unable to add activation record.</strong>");
			case 0:	// added OK
				echo("<strong>Activation record added.</strong>");
				break;
			case 1:	// record already existing
				echo("<strong>Activation record already existing - not added.</strong>");
				break;
			}
		}
	}

	if (isset($delete)) {
		$retcode = $db->deleteActivationRecord($delete);
		switch ($retcode) {
		case -1:	// generic error
			echo("<strong>Unable to delete activation record ". $delete . "</strong>");
		case 0:	// deleted OK
			echo("<strong>Activation record " .  $delete . " deleted.</strong>");
			break;
		}
	}

	?>

	<form name="addactivation" action="<?php echo $_SERVER['PHP_SELF']; ?>"
		method="POST">
		User Id  (e-mail):* <input type="text" size="35" name="userid" /> 
		App Name:* <input type="text"  size="25" name="appname" /> 
		Activation code (8-digits):* <input type="text"  size="8" maxlength="8" name="activationcode" /> 
		<br>
		Producer Id: <input type="text" size="8" name="producerid" /> 
		Level: <input type="text" size="2" name="level" />
		Expiration (dd-mm-yyyy): <input type="text" size="10" maxlength="10" name="expiration" /> 
		<input type="hidden" name="add_flag" value="true" /> 
		<input type="submit" value="Add Record" />
	</form>

	<p>
	<table border="1" >
	
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

	<?php
	endif;
	unset($db);
	?>

</body>
</html>
