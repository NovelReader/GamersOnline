<?php
	require_once '../Includes/GamersOperations.php';
	//$db = new DbOperations();
	//$db->getId("user1@user.com", "user1");
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){		
		if(isset($_POST['Email']) and isset($_POST['Password'])){
			$db = new DbOperations();
			$result = $db->getId(
			$_POST['Email'],
			$_POST['Password']);
			$response = $result;
			
		}else{
			$response['error'] = true;
			$response['message'] = "Required Fields Missing";
		}

	}else{
		$response['error'] = true;
		$response['message'] = "Invalid Request";
	}

echo json_encode($response);
?>