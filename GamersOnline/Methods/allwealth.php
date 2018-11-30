<?php
	require_once '../Includes/GamersOperations.php';
	
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){		
		
		$db = new DbOperations();
		$result = $db->getWealthFromAll();
		$response['Wealth'] = $result;

	}else{
		$response['error'] = true;
		$response['message'] = "Invalid Request";
	}

echo json_encode($response);
?>