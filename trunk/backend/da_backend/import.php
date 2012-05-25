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


$file = $_POST['importfile'];
if (isset($file)) {
	if ($file!="") {
		import($file);
	}
}


?>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=MacRoman">
<title>Import data</title>
</head>

<body bgcolor="Gainsboro">
<IMG SRC="images/droidactivator_logo.png">

<p>Import activation records in the database from a .csv file.</p>

<form name="setupimport" action="<?php echo $_SERVER['PHP_SELF']; ?>" method="POST">
	File path
	<input name="importfile" type="text" size="80" value="" >
	<input type="submit" value="Import data" >
</form>


</body>
</html>


<?php
// import a csv file in the activation table
function import($filepath){
	
	if (file_exists($filepath)) {
		$handle = fopen ($filepath, 'r');
		if ($handle!=false) {
			
			$db = new Database();
			$count=0;
			while (($data = fgetcsv($handle, 1000, ',', '"')) !== false)
			{
				
				$map=array();
				
				$map['producer_id']="1";
				$map['app_name']="eStudio";
				$map['active']="0";
				$map['level']="1";
				$map['userid']=$data[1];
				$map['activation_code']=$data[2];
	
				//$map['language']="it";
				
				$db->createActivationRecord($map);
				$count=$count+1;
				
			}
			
			fclose($handle);
			
			echo("<strong>Import completed. ".$count." records imported.</strong><br>");
			
			
		}
	}else{
		echo("<strong>File ".$filepath." not found.</strong><br>");
	}
	

}
?>
