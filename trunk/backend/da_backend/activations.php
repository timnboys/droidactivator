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

/*** Activations List View ***/
session_start();
include 'includes/authorize.php';
include 'includes/header.php';

// retrieve search datamap from session
$searchdata = $_SESSION['search_map'];

?>

<table border="0">
	<tr>
		<td>
			<form action="router.php?zone=activations&action=create" method="POST">
				<input type="submit" name="addrecord" value="Add record" />
			</form>
		</td>

		<td>
			<form action="router.php?zone=activations&action=refreshlist" method="POST">
				<input type="submit" name="refresh" value="Refresh list" />
			</form>
		</td>
	</tr>

</table>

<div id="searchbox-collapsed" class="searchbox">
	<a href="#" onclick="show('searchbox');">Show search box</a>
</div>

<div id="searchbox" class="searchbox">

	<a href="#" onclick="hide('searchbox');">Hide search box</a>

<form action="router.php?zone=activations&action=search" method="POST">

	<table border="0">
		<tr>
			<td>Id</td>
			<td><input type="number" name="id" size="8" value=<?php echo $searchdata['id']?>></td>
		</tr>
		
		<tr>
			<td>User Id</td>
			<td><input type="text" name="userid" size="40" value=<?php echo $searchdata['userid']?>></td>
		</tr>
		
		<tr>
			<td>App name</td>
			<td><input type="text" name="appname" size="20" value=<?php echo $searchdata['appname']?>></td>
		</tr>
		
		<tr>
			<td>Activation code</td>
			<td><input type="number" name="activationcode" size="8" value=<?php echo $searchdata['activationcode']?>></td>
		</tr>
		
		<tr>
			<td>Producer id</td>
			<td><input type="number" name="producerid" size="4" value=<?php echo $searchdata['producerid']?>></td>
		</tr>
		
		<tr>
			<td>Activated</td>
			<td>
            <select name="activated">
            	<option value="any" <?php if ($searchdata['activated']=="any") {echo "selected";}?>>Any</option>
            	<option value="true" <?php if ($searchdata['activated']=="true") {echo "selected";}?>>Yes</option>
            	<option value="false" <?php if ($searchdata['activated']=="false") {echo "selected";}?>>No</option>
            </select>
			</td>
		</tr>
		
		<tr>
			<td>Unique id</td>
			<td><input type="text" name="uniqueid" size="90" value=<?php echo $searchdata['uniqueid']?>></td>
		</tr>
		
		<tr>
			<td>Track Only</td>
			<td>
            <select name="trackonly">
            	<option value="any" <?php if ($searchdata['trackonly']=="any") {echo "selected";}?>>Any</option>
            	<option value="true" <?php if ($searchdata['trackonly']=="true") {echo "selected";}?>>Yes</option>
            	<option value="false" <?php if ($searchdata['trackonly']=="false") {echo "selected";}?>>No</option>
            </select>
			</td>
		</tr>
		
		<tr>
			<td>Level</td>
			<td><input type="number" name="level" size="4" value=<?php echo $searchdata['level']?>></td>
		</tr>
		
		<tr>
			<td colspan="2" align="center">
				<input type="submit" name="reset" value="Reset">
				<input type="submit" name="search" value="Search">
			</td>
		</tr>

	</table>

</form>
</div>


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

	<?php createTableData(); ?>

</table>


	<?php
	function createTableData() {

		date_default_timezone_set("UTC");

		// retrieve datamap from session
		$datamap = $_SESSION['activation_datamap'];

		// iterate over result and create table rows
		foreach ($datamap as $row){

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

			$editLink = "router.php?zone=activations&action=edit&id=".$row['id'];
			$deleteLink = "router.php?zone=activations&action=delete&id=".$row['id'];
			$eventsLink = "router.php?zone=events&action=list&activation_id=".$row['id'];

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
		<td><small><a href="'.$editLink. '">Edit</a></small></td>
		<td><small><a href="'.$deleteLink.'" onclick="return(confirm(\'Confirm record deletion?\'))">Delete</a></small></td>
		<td><small><a href="'.$eventsLink.'">Events</a></small></td>
		</tr>
		';
	}
}
?>
