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


	// include utility files
	require_once 'config.php';


	//*** private file, internal access only ***/
	//if(!defined('BASEPATH')) die('No direct access allowed!');

    /*** begin output buffering ***/
    ob_start();

    /*** begin session ***/
    session_start();

    /*** check the form has been posted and the session variable is set ***/
    if(!isset($_SESSION['form_token']))
    {
        $location = 'login.php';
    }
        
    /*** check all fields have been posted ***/
    elseif(!isset($_POST['form_token'], $_POST['password']))
    {
        $location = 'login.php';
    }
    
    /*** check the form token is valid ***/
    elseif($_SESSION['form_token'] != $_POST['form_token'])
    {
        $location = 'login.php';
    }
    
    /*** check the length of the password ***/
    elseif(strlen($_POST['password']) < 2 || strlen($_POST['password']) > 25)
    {
        $location = 'login.php';
    }
    
    else
    {
    	
    	if ($_POST['password'] == Config::ADMIN_PASSWORD) {
    		
                /*** set the authorized flag ***/
                $_SESSION['authorized'] = true;

                /*** unset the form token ***/
                unset($_SESSION['form_token']);
                
                /*** instantiate the models and save the instance in the session ***/
                require_once 'model.php';
                $_SESSION['activationmodel'] = serialize(new ActivationModel());
                $_SESSION['eventmodel'] = serialize(new EventModel());
                
                /*** send user to starting page ***/
                $location = 'router.php?zone=menu&action=activations';
                
                
       	}else{
       			echo ("wrong password");
                $location = 'login.php';
                
       	}
    	
    }

    /*** redirect ***/
    header("Location: $location");

    /*** flush the buffer ***/
    ob_end_flush();

?>
