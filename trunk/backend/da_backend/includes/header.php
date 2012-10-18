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
<?php include 'constants.php'; ?>

<?php if(isset($_SESSION['authorized'])){?>
		
	<table cellspacing="10px">
		<tr>
			<td><a href="router.php?zone=menu&action=activations"><?php echo(menuitem(Constants::ACTIVATION_SECTION));?></a></td>
			<td><a href="router.php?zone=menu&action=events"><?php echo(menuitem(Constants::EVENTS_SECTION));?></a></td>
			<td><a href="router.php?zone=menu&action=logout"><?php echo(menuitem(Constants::LOGOUT_SECTION));?></a></td>
			<td><a href="router.php?zone=menu&action=about"><?php echo(menuitem(Constants::ABOUT_SECTION));?></a></td>
		</tr>
	</table>
		
<?php } ?>

<?php 

	// Returns the HTML string corresponding to a menu item
	// @param the section code constant
	// @return the HTML string (bold if is the active section)
function menuitem($sectioncode){
		
		switch ($sectioncode) {
    		case Constants::ACTIVATION_SECTION:
			$section="Activations";
    		break;
    	case Constants::EVENTS_SECTION:
			$section="Events";
    		break;
       	case Constants::LOGOUT_SECTION:
			$section="Logout";
       		break; 	    
    	case Constants::ABOUT_SECTION:
			$section="About";
    		break;
		}
		
		// if this is the active section make it bold
		if ($_SESSION['sectioncode']==$sectioncode) {
			$section="<strong>".$section."</strong>";
		}
		
		return ($section);
	}
?>

<hr></hr>
