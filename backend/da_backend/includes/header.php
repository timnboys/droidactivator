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

<?php include 'includes/header.html'; ?>

<div class="menu">
	<ul>

		<?php
		if(isset($_SESSION['authorized'])){
		?>
		
		<li><a href="router.php?zone=menu&action=activations">Activations</a></li>
		<li><a href="router.php?zone=menu&action=events">Events</a></li>
		<li><a href="router.php?zone=menu&action=logout">Logout</a></li>
		<li><a href="router.php?zone=menu&action=about">About</a></li>
		
		<?php
		}
		?>
		
	</ul>
</div>
<hr>
