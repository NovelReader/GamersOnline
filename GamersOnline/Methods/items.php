<?php
	require_once '../Includes/GamersOperations.php';
	//$db = new DbOperations();
	//$db->getItemsFromInventory("1");
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){		
		if(isset($_POST['User'])){
			$db = new DbOperations();
			$result = $db->getItemsFromInventory(
			$_POST['User']);
			
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