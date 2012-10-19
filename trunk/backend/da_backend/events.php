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

// retrieve search datamap from session
$searchdata = $_SESSION['event_search_map'];

// set the hide searchbox variable to false if not already set
if (!isset($_SESSION['hide_event_searchbox'])) {
	$_SESSION['hide_event_searchbox']="false";
}

?>


<div id="searchbox-collapsed" class="searchbox">
	<div style="text-align:right">
		<a href="router.php?zone=events&action=togglesearch">Show search box</a>
	</div>
</div>


<div id="searchbox" class="searchbox">
	<div style="text-align:right">
		<a href="router.php?zone=events&action=togglesearch">Hide search box</a>
	</div>
	
	<form action="router.php?zone=events&action=search" method="POST">
	
		<table border="0">
			<tr>
				<td>Event Id</td>
				<td><input type="number" name="id" size="8" value="<?php echo $searchdata['id']?>"></td>
			</tr>
			
			<tr>
				<td>Activation Id</td>
				<td><input type="number" name="activationid" size="8" value="<?php echo $searchdata['activationid']?>"></td>
			</tr>
			
			<tr>
				<td>Event code</td>
				<td><input type="number" name="code" size="8" value="<?php echo $searchdata['code']?>"></td>
			</tr>
			
			<tr>
				<td>Event details</td>
				<td><input type="text" name="details" size="40" value="<?php echo $searchdata['details']?>"></td>
			</tr>
			
		</table>
		
		<div align="center">
			<input type="submit" name="all" value="Show all">
			<input type="submit" name="reset" value="Reset">
			<input id="search_btn" type="submit" name="search" value="Search">
		</div>
	
	</form>
	
</div>

<?php
$activation_id=$_SESSION['activation_id'];
if (isset($activation_id)) {
	echo "<strong>Events for activation #".$activation_id."</strong>";
	unset($_SESSION['activation_id']);
}
?>

<div class="counter"><?php echo(counterString())?></div>
<?php
// @return a string for the result counter
function counterString(){
	// rows to be listed
    $datamap = $_SESSION['event_datamap'];
	$partial=count($datamap);
	
	// total rows
	include_once 'model.php';
    $model=EventModel::getInstance();
    $total=$model->countRows();
    
    // display counter
    $string=$partial." of ".$total." records";
    return $string;
}
?>

<table class="table" border="1">

	<tr>
		<th>Evt Id</th>
		<th>Activation Id</th>
		<th>Timestamp</th>
		<th>Code</th>
		<th>Details</th>
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
				<td>'.$eventId.'</td>
				<td>'.$activationId.'</td>
				<td>'.$timestamp.'</td>
				<td>'.$code.'</td>
				<td>'.$details.'</td>
				<td><a href="'.$deleteLink.'" onclick="return(confirm(\'Confirm record deletion?\'))">Delete</a></td>
				</tr>
			';
	}
}
?>

<script>
// read the session variable and show or hide the searchbox
var hidden = "<?php echo($_SESSION['hide_event_searchbox']); ?>";
if (hidden=="true") {
	hide('searchbox');
	show('searchbox-collapsed');
}else{
	show('searchbox');
	hide('searchbox-collapsed');
}
</script>

