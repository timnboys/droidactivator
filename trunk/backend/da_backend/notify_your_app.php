<?php

session_start();

$submit_flag = $_POST['submit_flag'];
$app_name = $_POST['app_name'];
$web_site = $_POST['web_site'];
$email = $_POST['email'];
$notes = $_POST['notes'];

if (!isset($_SESSION['referrer']) or $_SESSION['referrer']==$_SERVER['PHP_SELF']) {
	$_SESSION['referrer']=$_SERVER["HTTP_REFERER"];
}
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 //EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=Cp1252">
<title>Add your app</title>
</head>

<body bgcolor="Gainsboro">


	<IMG SRC="images/droidactivator_logo.png">
	<p>
	
	
	<h1>Add your app!</h1>

	<p>
		If you are using DroidActivator, let us know about your app! <br>Your data will be reviewed and your
		app will be added <br>to the list of the apps protected by
		DroidActivator.
	
	
	<form name="notify" action="<?php echo $_SERVER['PHP_SELF']; ?>"
		method="POST">

		<table border="0">

			<tr>
				<td>App Name:*</td>
				<td><input type="text" size="40" name="app_name"
					value=<?php echo $app_name?>></td>
			</tr>

			<tr>
				<td>Web site or Google Play app's page:*</td>
				<td><input type="text" size="60" name="web_site"
					value=<?php echo $web_site?>></td>
			</tr>

			<tr>
				<td>Your e-mail address:</td>
				<td><input type="text" size="40" name="email"
					value=<?php echo $email?>></td>
			</tr>

			<tr>
				<td>Notes:</td>
				<td><TEXTAREA NAME=notes ROWS=6, COLS=40>
				<?php echo $notes?>
					</TEXTAREA></td>
			</tr>


		</table>

		<br> <input type="submit" value="Submit data"> <br> <input
			type="hidden" name="submit_flag" value="true">
	</form>




	<?php
	if (isset($submit_flag)) {

		$cont=true;

		// check for mandatory fields
		if (!isset($app_name) || $app_name=="") {echo("<strong>App Name is mandatory</strong><br>");$cont=false;}
		if (!isset($web_site) || $web_site=="") {echo("<strong>Web site is mandatory</strong><br>");$cont=false;}

		if ($cont) {

			// send mail
			$message = "app_name: ".$app_name."\n";
			$message = $message."web_site: ".$web_site."\n";
			$message = $message."email: ".$email."\n";
			$message = $message."notes: ".$notes."\n";

			// In case any of our lines are larger than 70 characters, we should use wordwrap()
			$message = wordwrap($message, 70);

			// Send
			$error=send_mail($message);
			if (isset($error) and ($error!="")) {
				echo "<p>".$error."<p>";
			}

			if (isset($error) and ($error!="")) {
				
				// js with error
				?>
				<script type="text/javascript">
				alert("<?php echo $error?>");
				</script>
				<?php
				
			}else{
				
				// js thank you and go back
				?>
				<script type="text/javascript">
				alert("Thank You! Your data will be reviewed asap.");

				<?php 
					if (isset($_SESSION['referrer'])) {
						echo 'window.location.href="'.$_SESSION['referrer'].'"';
					}
				?>
				</script>
				<?php
			}
			
			

		}
	}
	?>

	<?php
	
	/**
	 * sends an email
	 * @param $message the message body
	 * @return the error message, empty string if succeeded
	 */
	function send_mail($message) {
		
		require_once "Mail.php";
		
		$error = "";

		$from = "droidactivator@google.com";
		$to = "alex@algos.it";
		$subject = "Segnalazione app Droidactivator";
		$body = $message;

		$host = "smtp.algos.it";
		$port = "25";
		$username = "info@algos.it";
		$password = "algos";

		$headers = array ('From' => $from,
          'To' => $to,
          'Subject' => $subject);
		$smtp = Mail::factory('smtp',
		array ('host' => $host,
            'port' => $port,
            'auth' => true,
            'username' => $username,
			'password' => $password));

		$mail = $smtp->send($to, $headers, $body);

		if (PEAR::isError($mail)) {
			$error=$mail->getMessage();
		} else {
			$success=true;
		}
			
		return $error;
			
	}
		
	?>



</body>
</html>
