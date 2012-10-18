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

include_once 'constants.php';

session_start();

/*** Menu Controller ***/
class MenuController extends AbstractController {
	
	static function activations() {
		$_SESSION['sectioncode']=Constants::ACTIVATION_SECTION;
		$lastFilter=$_SESSION['last_activations_filter'];
		if (!isset($lastFilter)) {
			$lastFilter="id<0";
		}
		ActivationsController::listRows($lastFilter);
   	}
	
	static function events() {
		$_SESSION['sectioncode']=Constants::EVENTS_SECTION;
		$lastFilter=$_SESSION['last_events_filter'];
		if (!isset($lastFilter)) {
			$lastFilter="id<0";
		}
		EventsController::listRows($lastFilter);
	}
	
	static function logout() {
		$_SESSION['sectioncode']=Constants::LOGOUT_SECTION;
		unset($_SESSION['authorized']);
				
		// redirect to the index page
   		header("Location: admin.php");
   		
	}
	
	static function about() {

		$_SESSION['sectioncode']=Constants::ABOUT_SECTION;
				
		// redirect to the about page
   		header("Location: about.php");
		
	}
	
	
	
	
}


/*** Activations Controller ***/
class ActivationsController extends AbstractController {
		
	// lists the rows
	static function listRows($filter) {
		
		// retrieve model instance
		$model=ActivationModel::getInstance();
        
        // request data to the model
		$result = $model->listData($filter);
		
		// create an associative array from model data
		//$map = $result->fetch_all(MYSQLI_ASSOC); //Php 5.3.0+ only
		for ($map = array(); $tmp = $result->fetch_array(MYSQLI_ASSOC);) $map[] = $tmp; //Php 5+

		// store data in the session
		$_SESSION['activation_datamap']=$map;
		
		// redirect to the display page
   		header("Location: activations.php");
				
				
	}
	
	
	// Deletes an activation record
	// @param id of the activation record to delete
	static function deleteActivation($id) {
		
		// retrieve model instance
		$model=ActivationModel::getInstance();
		
        // delegate deletion to the model
		$result = $model->delete($id);
		
		// reload list
		MenuController::activations();
		
	}
	
	// Edit an activation record
	// @param id of the activation record to edit
	static function edit($id) {
		
		// retrieve model instance
		$model=ActivationModel::getInstance();
		        
        // acquire data from model
        $map = $model->get($id);
        
        // store data in the session
		$_SESSION['activation_editmap']=$map;
		
		// redirect to the edit page
   		header("Location: activation_edit.php");
		
		
	}
	
	// Create a new activation record
	// @return id of the created record
	static function create() {
		
        // create a map with id = 0 and default values
        $map = array(
    		"id" => 0,
		);
        
        // store data in the session
		$_SESSION['activation_editmap']=$map;
		
		// redirect to the edit page
   		header("Location: activation_edit.php");
		
		
	}
	
	// Submit a form for an activation record (either new or existing)
	// @return id of the created record
	static function submit() {
		
		// store data posted by the form in the session so it 
		// is still available in case of redirection to the same form
		$_SESSION['activation_editmap']=$_POST;
		
		// retrieve model instance
		$model=ActivationModel::getInstance();
				
        // save or create the record
        $result = $model->save($_POST);
        $code=substr($result, 0, 2);
        
        if ($code=="OK") {
        	
        	$newRecord=($_POST['id']==0);
        	
        	// if this was a new record, show only this record in the list
        	// if this was an existing record, keep the previous filter
        	if ($newRecord) {
        		$newId=substr($result,2,strlen($result)-2);
		     	$filter="id=".$newId;
           	}else{
        		$filter=$_SESSION['last_activations_filter'];
        	}

        	// redirect to the list
			ActivationsController::listRows($filter);
			
        }else {
			// redirect to the edit page with error message
			$result = str_replace("\n", "<br>", $result);// no newlines in headers!
   			header("Location: activation_edit.php?error=" . $result);
        }
        
		
	}
	
	
	
	
}


/*** Events Controller ***/
class EventsController extends AbstractController {
	
	/***
	// lists the rows
	static function listRows($activation_id) {
		
		// retrieve model instance
        $model=EventModel::getInstance();

        // request data to the model
        if (isset($activation_id)) {
        	$filter = "activation_id=" .$activation_id;
        }
        
		$result = $model->listData($filter);
		
		if ($result) {
			
			// create an associative array from model data
			//$map = $result->fetch_all(MYSQLI_ASSOC);  //Php 5.3.0+ only
			for ($map = array(); $tmp = $result->fetch_array(MYSQLI_ASSOC);) $map[] = $tmp;	//Php 5+
		
			// store data in the session
			$_SESSION['event_datamap']=$map;
		
			// if specified, save the parent activation id in the session
			unset($_SESSION['activation_id']);
			if (isset($activation_id)) {
				$_SESSION['activation_id']=$activation_id;
			}
		
			// redirect to the display page
   			header("Location: events.php");
			
		}
		
	}
	***/
	
		// lists the rows
	static function listRows($filter) {
		
		// retrieve model instance
		$model=EventModel::getInstance();
        
        // request data to the model
		$result = $model->listData($filter);
		
		// create an associative array from model data
		//$map = $result->fetch_all(MYSQLI_ASSOC); //Php 5.3.0+ only
		for ($map = array(); $tmp = $result->fetch_array(MYSQLI_ASSOC);) $map[] = $tmp; //Php 5+

		// store data in the session
		$_SESSION['event_datamap']=$map;
				
		// redirect to the display page
   		header("Location: events.php");
						
				
	}
	
	
	
	// Deletes an event
	// @param id of the event
	static function deleteEvent($id) {
		
		// retrieve model instance
        $model=EventModel::getInstance();
				
        // delegate deletion to the model
		$result = $model->delete($id);
		
		// reload events list
		$id=$_SESSION['activation_id'];//use if present
		EventsController::listRows($id);
				
	}
	
	
}



abstract class AbstractController{
}


?>