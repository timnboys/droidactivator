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

        /*** include the html header file ***/
        include 'includes/header.html';

        /*** set a form token and store it in the session ***/
		$form_token = md5(rand(time(), true));
        $_SESSION['form_token'] = $form_token;
?>


<form action="login_submit.php" method="post">
	Enter Admin password: 
	<input type="password" name="password" /> 
	<input type="submit" value="Login" />
	<input type="hidden" name="form_token" value="<?php echo $form_token; ?>" />
</form>


<?php include 'includes/footer.php'; ?>
