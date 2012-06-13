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

	/*** Events List View ***/
	session_start();
	include 'includes/authorize.php';
	include 'includes/header.php';
?>

<?php
$activation_id=$_SESSION[activation_id];
if (isset($activation_id)) {
	echo "<strong>Events for activation #".$activation_id."</strong>";
}
?>

<table border="1">

	<tr>
		<th><small>Evt Id</small></th>
		<th><small>Activation Id</small></th>
		<th><small>Timestamp</small></th>
		<th><small>Code</small></th>
		<th><small>Details</small></th>
	</tr>

	<?php createTableData(); ?>

</table>


<?php
function createTableData() {
	
	// retrieve datamap from session
	$datamap = $_SESSION['event_datamap'];
	
	// iterate over result and create table rows
	foreach ($datamap as $row){

		$eventId=$row['id'];
		$activationId=$row['activation_id'];
		$timestamp=$row['timestamp'];
		$code=$row['code'];
		$details=$row['details'];
		$deleteLink = "router.php?zone=events&action=delete&id=".$eventId;

		echo '<tr>
				<td><small>'.$eventId.'</small></td>
				<td><small>'.$activationId.'</small></td>
				<td><small>'.$timestamp.'</small></td>
				<td><small>'.$code.'</small></td>
				<td><small>'.$details.'</small></td>
				<td><small><a href="'.$deleteLink.'" onclick="return(confirm(\'Confirm record deletion?\'))">Delete</a></small></td>
				</tr>
			';
	}
}
?>
