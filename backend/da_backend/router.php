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
		switch ($action) {
			case 'activations':
				MenuController::activations();
				break;
			case 'events':
				MenuController::events();
				break;
			case 'logout':
				MenuController::logout();
				break;
			case 'about':
				MenuController::about();
				break;
			default:
				break;
		}

		break;
		
	case 'activations':
		switch ($action) {
			case 'delete':
				$id=$_GET['id'];
				ActivationsController::deleteActivation($id);
				break;
				
			case 'edit':
				$id=$_GET['id'];
				ActivationsController::edit($id);
				break;
				
			case 'create':
				ActivationsController::create();
				break;
				
			case 'refreshlist':
				ActivationsController::listRows();
				break;
				
			// submit an activation form (either new or existing record)
			case 'submit':
				ActivationsController::submit();
				break;
				
				
			default:
				break;
		}
		
		break;
		

	case 'events':
		switch ($action) {
			
			case 'list':
				$activation_id=$_GET['activation_id'];
				EventsController::listRows($activation_id);
				break;
			
			case 'delete':
				$id=$_GET['id'];
				EventsController::deleteEvent($id);
				break;
				
			default:
				break;
		}
		
		break;

	default:
		break;
}


?>


