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
	include 'includes/header.php';
	
	define('VERSION', '2.0.3');
	
?>


<p>
<strong>DroidActivator is an Open Source licensing control system for Android.</strong>
<p>It is a project available on <a href="http://code.google.com/p/droidactivator/">Google Code</a>
<p>It is composed by an Android Library and a Php backend (this app!)
<p>It was started in April 2012 by Algos, a mobile development software house located in Milan, Italy
<p>DroidActivator also has a <a href="http://www.droidactivator.org">Web Site</a>
<p>For any question you can <a href="mailto:info@algos.it">contact the authors.</a> We appreciate your comments and suggestions!
<p>Current version of the backend is <strong><?php echo VERSION?></strong>
<p>Current version of Php is <strong><?php echo phpversion()?></strong>
<p>Web server address is <strong><?php echo $_SERVER['SERVER_ADDR']?></strong>

<p>The code for instantiating DroidActivator is:<br>
<code>
DroidActivator.newInstance(this, "http://<?php echo $_SERVER['SERVER_ADDR']?>",new Runnable() {<br>    
&nbsp&nbsppublic void run() {startMyApp();}<br>    
});<br>    
</code>



</p>

<?php include 'includes/footer.php'; ?>



