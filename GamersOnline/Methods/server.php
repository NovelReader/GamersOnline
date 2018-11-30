<?php
	require_once '../Includes/GamersOperations.php';
	//$db = new DbOperations();
	//$db->getLocation("1");
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){		
		if(isset($_POST['User'])){
			$db = new DbOperations();
			$result = $db->getLocation(
			$_POST['User']);
			$response['image'] = $result;
			
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