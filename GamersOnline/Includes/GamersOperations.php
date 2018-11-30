<?php

	class DbOperations{
		private $con;
		
		function __construct(){
			require_once dirname(__FILE__).'/DBConnect.php';
			$db = new DbConnects();
			$this->con = $db->connect();
		}
		
		public function getID($username, $password){
			$output = array();
			$stmt = $this->con->prepare("SELECT `user_id` FROM `users` WHERE `email` = ? AND `password` = ?");
			$stmt->bind_param("ss",$username, $password);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_array(MYSQLI_NUM);
			if($row[0] == null){
				$output['error'] = true;
				$output['message'] = "Email or Password incorrect.";
				
			}else{
				$userid = array_values($row)[0];
				$profile = $this->getProfileID($userid);
				$output = $profile;
			}
			return $output;
			//echo $profile[0];
		}
		
		private function getProfileID($userid){
			$output = array();
			$stmt = $this->con->prepare("SELECT `profile_id`, `name`, `image`, `currency` FROM `profile` WHERE `user_id` = ?");
			$stmt->bind_param("s",$userid);
			
			$stmt->execute();
			
			$result = $stmt->get_result();
			
			$row = $result->fetch_array(MYSQLI_NUM);			
			//return $row;
			if($row[0] == null){
				$output['error'] = true;
				$output['message'] = "No Profile associated with User.";
			}else{
				$output['error'] = false;
				$output['message'] = "Logged In.";
				$output['User'] = $row[0];
				$output['Name'] = $row[1];
				$output['Currency'] = $row[3];
				$output['Image'] = base64_encode($row[2]);
				
			}
			return $output;
		}
		
		public function getLocation($profile){
			$stmt = $this->con->prepare("SELECT `server` FROM `profile` WHERE `profile_id` = ?");
			$stmt->bind_param("s",$profile);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_array(MYSQLI_NUM);
			//echo $row[0];
			//$this->getLocationImage($row[0]);
			
			$stmt2 = $this->con->prepare("SELECT `map_image` FROM `server` WHERE `server_id` = ?");
			$stmt2->bind_param("i",$row[0]);
			$stmt2->execute();
			$result2 = $stmt2->get_result();
			$row2 = $result2->fetch_array(MYSQLI_NUM);
			return base64_encode($row2[0]);
			//echo '<img src="data:image/jpeg;base64,'.base64_encode($row2[0]) .'" />';
			//echo $row2[0];
		}
		
		public function getItemsFromInventory($userid){
			$itemarray = array();
			$itemarray['key'] = [];
			$itemarray['number'] = [];
			$stmt = $this->con->prepare("SELECT `item`, `number_of` FROM `inventory` WHERE `profile` = ?");
			$stmt->bind_param("s",$userid);
			$stmt->execute();
			$result = $stmt->get_result();
			//$row = $result->fetch_array(MYSQLI_NUM);
			foreach($result as $item){
				//echo $item['item'];
				//echo "\n";
				//echo $item['number_of'];
				//echo "\n";
				//array_push($itemarray,$item['item']);
				array_push($itemarray['number'], $item['number_of']);
			}
			//echo json_encode($itemarray);
			$items = $this->getItemsInfo($result);
			return $items;
		}
		
		private function getItemsInfo($list){
			$items = array();
			$items['name'] = [];
			$items['number'] = [];
			$items['cost'] = [];
			$items['description'] = [];
			$items['image'] = [];
			
			
			foreach($list as $item){
				array_push($items['number'], $item['number_of']);
				$stmt = $this->con->prepare("SELECT * FROM `items` WHERE `item_id` = ?");
				$stmt->bind_param("s",$item['item']);
				$stmt->execute();
				$result = $stmt->get_result();
				foreach($result as $item2){
					array_push($items['name'],$item2['name']);
					array_push($items['cost'],$item2['cost']);
					array_push($items['description'],$item2['description']);
					array_push($items['image'],base64_encode($item2['image']));
				}
			}
			return $items;
		}

		public function getProfilesWithXWealth($minValue){
			$itemarray = array();
			$itemMinWorth = $minValue;

			$stmt = $this->con->prepare("SELECT `profile_id` FROM `profile` WHERE `currency` >= ?");
			$stmt->bind_param("i",$itemMinWorth);
			$stmt->execute();
			$result = $stmt->get_result();

			foreach($result as $item){
				array_push($itemarray,$item['profile_id']);
			}
			echo json_encode($itemarray);
		}
		
		// -------------------------------------------------------------------------------------------------------------------------------------------------------------------

		public function getWealthFromProfile($profile){
			//Gets the currency that the profile has attributed
			$itemarray = array();
			$stmt = $this->con->prepare("SELECT `currency` FROM `profile` WHERE `profile_id` = ?");
			$stmt->bind_param("i",$profile);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_array(MYSQLI_NUM);
			//Gets an array with the cost off all the items in the profiles inventory
			$totalWealth = $row[0];
			$itemCosts = $this->getItemCostFromInventory($profile);
			
			//Adds and returns the result
			foreach($itemCosts as $itemC){
				$totalWealth = $totalWealth + $itemC;
			}
			return $totalWealth;
		}

		private function getItemCostFromInventory($userId){
			$itemarray = array();
			$itemcountarray = array();
			$stmt = $this->con->prepare("SELECT `item`, `number_of` FROM `inventory` WHERE `profile` = ?");
			$stmt->bind_param("i",$userId);
			$stmt->execute();
			$result = $stmt->get_result();
			foreach($result as $item){
				
				array_push($itemarray,$item['item']);

				array_push($itemcountarray,$item['number_of']);
			}
			$items = $this->getItemsCost($itemarray, $itemcountarray);
			return $items;
		}

		private function getItemsCost($list,$number){
			$itemnames = array();			
			foreach($list as $item){
				$stmt = $this->con->prepare("SELECT `cost` FROM `items` WHERE `item_id` = ?");
				$stmt->bind_param("s",$item);
				$stmt->execute();
				$result = $stmt->get_result();
				foreach($result as $item2){
					array_push($itemnames,$item2['cost']);
				}
			}
			$max = sizeof($itemnames);
			for($i = 0; $i < $max;$i++){
				$itemnames[$i] = $itemnames[$i] * $number[$i];
				
			}
			
			return $itemnames;
		}
		
		public function getWealthFromAll(){
			//Gets the currency that the profile has attributed
			$resultarray = array();
			//$resultarray['Profiles'] = [];
			$resultarray['Wealth'] = [];
			
			$stmt = $this->con->prepare("SELECT `profile_id` FROM `profile`");
			$stmt->execute();
			$result = $stmt->get_result();
			
			foreach($result as $user){
				//array_push($resultarray['Profiles'],$user['profile_id']);
				$stmt2 = $this->con->prepare("SELECT `currency` FROM `profile` WHERE `profile_id` = ?");
				$stmt2->bind_param("i",$user['profile_id']);
				$stmt2->execute();				
				$result2 = $stmt2->get_result();
				$row2 = $result2->fetch_array(MYSQLI_NUM);
				
				//Gets an array with the cost off all the items in the profiles inventory
				$totalWealth = $row2[0];
				$itemCosts = $this->getItemCostFromInventory($user['profile_id']);
			
				//Adds and returns the result
				foreach($itemCosts as $itemC){
					$totalWealth = $totalWealth + $itemC;
				}
				array_push($resultarray['Wealth'],$totalWealth);
			}
			$aggregateWealth = 0;
			$max = sizeof($resultarray['Wealth']);
			for($i = 0; $i < $max;$i++){
				$aggregateWealth = $aggregateWealth + $resultarray['Wealth'][$i];
				
			}
			
			return $aggregateWealth;
		}
		
		public function useItemOnPlayer($user, $item){
			
			//UPDATE `inventory` SET `number_of`= `number_of` - 1 WHERE `profile` = "1" AND `item` = "1"
			$stmt = $this->con->prepare("UPDATE `inventory` SET `number_of`= `number_of` - 1 WHERE `profile` = ? AND `item` = ?");
			$stmt->bind_param("ss",$user, $item);
			$stmt->execute();
			
			
			$stmt2 = $this->con->prepare("SELECT `effect` FROM `items` WHERE `item_id` = ?");
			$stmt2->bind_param("s",$item);
			$stmt2->execute();
			$result2 = $stmt2->get_result();
			$row = $result2->fetch_array(MYSQLI_NUM);
			$string = $row[0];
			$updates = explode(",",$string);
			
			$stmt2 = $this->con->prepare("UPDATE `status` SET `current_health`= `current_health` + ?,`max_health`= `max_health` + ?,`current_mp`= `current_mp` + ?,`max_mp`= `max_mp` + ?,`current_strength`=`current_strength` + ?,`max_strength`=`max_strength` + ?,`current_speed`=`current_speed` + ?,`max_speed`= `max_speed` + ?,`current_intelligence`= `current_intelligence` + ?,`max_intelligence`= `max_intelligence` + ?,`current_wisdom`= `current_wisdom` + ?,`max_wisdom`= `max_wisdom` + ? WHERE `profile` = ?");
			$stmt2->bind_param("sssssssssssss",$updates[0],$updates[1],$updates[2],$updates[3],$updates[4],$updates[5],$updates[6],$updates[7],$updates[8],$updates[9],$updates[10],$updates[11],$user);
			$stmt2->execute();
			$response = array();
			$response = $this -> getStats($user);
			return $response;
			
		}
		
		public function getStats($user){
			$Stats = array();
			$stmt = $this->con->prepare("SELECT `current_health`, `max_health`, `current_mp`, `max_mp`, `current_strength`, `max_strength`, `current_speed`, `max_speed`, `current_intelligence`, `max_intelligence`, `current_wisdom`, `max_wisdom` FROM `status` WHERE `profile` = ?");
			$stmt->bind_param("s",$user);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_array(MYSQLI_NUM);
			$Stats['current_health'] = $row[0];
			$Stats['max_health'] = $row[1];
			$Stats['current_mp'] = $row[2];
			$Stats['max_mp'] = $row[3];
			$Stats['current_strength'] = $row[4];
			$Stats['max_strength'] = $row[5];
			$Stats['current_speed'] = $row[6];
			$Stats['max_speed'] = $row[7];
			$Stats['current_intelligence'] = $row[8];
			$Stats['max_intelligence'] = $row[9];
			$Stats['current_wisdom'] = $row[10];
			$Stats['max_wisdom'] = $row[11];
			return $Stats;
		}

	}
		
?>