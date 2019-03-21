<?php
	require "connect.php";

	$email = $_POST['email'];
	$password = $_POST['password'];
	$fullname = $_POST['fullname'];
	$phone_number = $_POST['phone_number'];
	$address = $_POST['address'];

	if (strlen($email) > 0 && strlen($password) > 0 && strlen($fullname) > 0 && strlen($phone_number) > 0 && strlen($address) > 0) {
		$query = "INSERT INTO user VALUES (null, '$email', '$password', '$fullname', '$phone_number', '$address')";
		$data = mysqli_query($con, $query);
		if ($data) {
			echo "Success";
		} else {
			echo "Fail";
		}
	}
?>