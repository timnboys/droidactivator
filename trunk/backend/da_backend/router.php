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
include 'model.php';

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
				
			// submit an activation form (either new or existing record)
			case 'submit':
				ActivationsController::submit();
				break;
				
			// submit a search form
			case 'search':
				
				$map=$_POST;
				
				// show all button was pressed
				if (isset($map['all'])) {
					
					// clear the search map stored in the session
					unset($_SESSION['activation_search_map']);
					
					// clear the last filter session variable (but don't unset it)
					$_SESSION['last_activations_filter']="";
				
				}
				
				
				// reset button was pressed
				if (isset($map['reset'])) {
					
					// clear the search map stored in the session
					unset($_SESSION['activation_search_map']);
				
					// retrieve the last filter from the session
					$filter=$_SESSION['last_activations_filter'];
				
				}
				
				// search button was pressed
				if (isset($map['search'])) {
					
					// store search data in the session
					$_SESSION['activation_search_map']=$map;
				
					// create a filter
					$filter = ActivationModel::createFilter($map);
				
					// store the last filter in the session
					$_SESSION['last_activations_filter']=$filter;
					
				}
				
				// list the rows using the filter
				ActivationsController::listRows($filter);
				
				break;
				
				
			// toggle the search box visibility
			case 'togglesearch':
				// invert the session variable
				if ($_SESSION['hide_activation_searchbox']=="false") {
					$_SESSION['hide_activation_searchbox']="true";
				}else{
					$_SESSION['hide_activation_searchbox']="false";
				}

				// redirect to the activations page
   				header("Location: activations.php");
   				
			default:
				break;
		}
		
		break;
		

	case 'events':
		switch ($action) {
			
			case 'list':
				$activation_id=$_GET['activation_id'];
				$_SESSION['activation_id']=$activation_id;
				$filter="activation_id=".$activation_id;
				EventsController::listRows($filter);
				break;
			
			case 'delete':
				$id=$_GET['id'];
				EventsController::deleteEvent($id);
				break;
				
			// submit a search form
			case 'search':
				
				$map=$_POST;
				
				// show all button was pressed
				if (isset($map['all'])) {
					
					// clear the search map stored in the session
					unset($_SESSION['event_search_map']);
					
					// clear the last filter session variable (but don't unset it)
					$_SESSION['last_events_filter']="";
				
				}
				
				
				// reset button was pressed
				if (isset($map['reset'])) {
					
					// clear the search map stored in the session
					unset($_SESSION['event_search_map']);
				
					// retrieve the last filter from the session
					$filter=$_SESSION['last_events_filter'];
				
				}
				
				// search button was pressed
				if (isset($map['search'])) {
					
					// store search data in the session
					$_SESSION['event_search_map']=$map;
				
					// create a filter
					$filter = EventModel::createFilter($map);
				
					// store the last filter in the session
					$_SESSION['last_events_filter']=$filter;
					
				}
				
				// list the rows using the filter
				EventsController::listRows($filter);
				
				break;
				
			// toggle the search box visibility
			case 'togglesearch':
				// invert the session variable
				if ($_SESSION['hide_event_searchbox']=="false") {
					$_SESSION['hide_event_searchbox']="true";
				}else{
					$_SESSION['hide_event_searchbox']="false";
				}

				// redirect to the events page
   				header("Location: events.php");
				
				
			default:
				break;
		}
		
		break;

	default:
		break;
}


?>


