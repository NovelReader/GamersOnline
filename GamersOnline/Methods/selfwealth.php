<?php
	require_once '../Includes/GamersOperations.php';
	
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){		
		if(isset($_POST['User'])){
			$db = new DbOperations();
			$result = $db->getWealthFromProfile(
			$_POST['User']);
			$response['Wealth'] = $result;
			
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