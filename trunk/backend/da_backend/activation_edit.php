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

/*** Activation Edit Form ***/
session_start();
include 'includes/authorize.php';
include 'includes/header.php';

// retrieve datamap from session
$data = $_SESSION['activation_editmap'];
$activation = $data['id'];
$error = $_GET['error'];


?>
<div class="editform">

<form name="activationform" action="router.php?zone=activations&action=submit" method="POST">

	<table border="0">

	<?php if ($activation!=0) {
		?>
		<tr>
			<td>Id:</td>
			<td><input type="text" size="10" name="id" readonly
				value=<?php echo $data['id']?>></td>
		</tr>
		<?php
	}?>

		<tr>
			<td>User Id (e-mail):*</td>
			<td><input type="text" size="35" name="userid"
				value=<?php echo $data['userid']?>></td>
		</tr>

		<tr>
			<td>App Name:*</td>
			<td><input type="text" size="25" name="app_name"
				value=<?php echo $data['app_name']?>></td>
		</tr>


		<tr>
			<td>Activation code (8-digits):*</td>
			<td>
				<input id="activationcode" type="text" size="8" maxlength="8" name="activation_code" value=<?php echo $data['activation_code']?>>
				<button type="button" onclick="generateCode('activationcode')">Generate</button>
			</td>
		</tr>

		<tr>
			<td>Producer Id:</td>
			<td><input type="text" size="8" name="producer_id"
				value=<?php echo $data['producer_id']?>></td>
		</tr>

		<?php if ($activation!=0) {
			?>
		<tr>
			<td>Activated:</td>
			<td><input type="checkbox" name="active"
			<?php if ($data['active']==1) {echo 'checked';}?>></td>
		</tr>
		<?php
		}?>

		<?php if ($activation!=0) {
			?>
		<tr>
			<td>Unique Id:</td>
			<td><input type="text" size="90" name="uniqueid"
				value=<?php echo $data['uniqueid'] ?>></td>
		</tr>
		<?php
		}?>

		<?php if ($activation!=0) {
			?>
		<tr>
			<td>Tracking only:</td>
			<td><input type="checkbox" name="tracking_only"
			<?php if ($data['tracking_only']==1) {echo 'checked';}?>></td>
		</tr>
		<?php
		}?>

		<tr>
			<td>Level:</td>
			<td><input type="text" size="2" name="level"
				value=<?php echo $data['level']?>></td>
		</tr>

		<tr>
			<td>Expiration (yyyy-mm-dd):</td>
			<td><input type="text" size="10" maxlength="10" name="expiration"
				value=<?php echo $data['expiration']?>></td>
		</tr>


		<?php if ($activation!=0) {
			?>
		<tr>
			<td>Last Activation (yyyy-mm-dd):</td>
			<td><input type="text" size="10" maxlength="10"
				name="last_activation" value=<?php echo $data['last_activation']?>>
			</td>
		</tr>
		<?php
		}?>

		<?php if ($activation!=0) {
			?>
		<tr>
			<td>Last Update (yyyy-mm-dd):</td>
			<td><input type="text" size="10" maxlength="10" name="last_update"
				value=<?php echo $data['last_update']?>></td>
		</tr>
		<?php
		}?>

		<?php if ($activation!=0) {
			?>
		<tr>
			<td>Device info:</td>
			<td><input type="text" size="90" name="device_info"
				value='<?php echo $data['device_info']?>'></td>
		</tr>
		<?php
		}?>




	</table>

	<br> <input type="submit" name = "submit" value="Save Record"> 
	
</form>

<?php 
// display the error if it has been set
if (isset($error)) {
	echo("<br><strong>".$error."</strong>");
}
?>

</div>

