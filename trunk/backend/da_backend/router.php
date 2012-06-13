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

/*** start the session ***/
session_start();

include 'includes/authorize.php';
include 'controller.php';

$zone = $_GET['zone'];
$action = $_GET['action'];

switch ($zone) {
	
	case 'menu':
		$controller = MenuController;
		switch ($action) {
			case 'activations':
				$controller::activations();
				break;
			case 'events':
				$controller::events();
				break;
			case 'logout':
				$controller::logout();
				break;
			default:
				break;
		}

		break;
		
	case 'activations':
		$controller = ActivationsController;
		switch ($action) {
			case 'delete':
				$id=$_GET['id'];
				$controller::deleteActivation($id);
				break;
				
			case 'edit':
				$id=$_GET['id'];
				$controller::edit($id);
				break;
				
			case 'create':
				$controller::create();
				break;
				
			case 'refreshlist':
				$controller::listRows();
				break;
				
			// submit an activation form (either new or existing record)
			case 'submit':
				$controller::submit();
				break;
				
				
			default:
				break;
		}
		
		break;
		

	case 'events':
		$controller = EventsController;
		switch ($action) {
			
			case 'list':
				$activation_id=$_GET['activation_id'];
				$controller::listRows($activation_id);
				break;
			
			case 'delete':
				$id=$_GET['id'];
				$controller::deleteEvent($id);
				break;
				
			default:
				break;
		}
		
		break;

	case 'pluto':
		break;


	default:
		break;
}


?>


