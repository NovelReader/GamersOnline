<?php

	class DbOperations{
		private $con;
		
		function __construct(){
			require_once dirname(__FILE__).'/DBConnect.php';
			$db = new DbConnects();
			$this->con = $db->connect();
		}
		
		public function getID($username, $password){
			$stmt = $this->con->prepare("SELECT `user_id` FROM `users` WHERE `email` = ? AND `password` = ?");
			$stmt->bind_param("ss",$username, $password);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_array(MYSQLI_NUM);
			$userid = array_values($row)[0];
			$profile = $this->getProfileID($userid);
			echo $profile[0];
		}
		
		private function getProfileID($userid){
			$stmt = $this->con->prepare("SELECT * FROM `profile` WHERE `user_id` = ?");
			$stmt->bind_param("s",$userid);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_array(MYSQLI_NUM);			
			return $row;
		}
		
		public function getLocation($profile){
			$stmt = $this->con->prepare("SELECT `server` FROM `profile` WHERE `profile_id` = ?");
			$stmt->bind_param("s",$profile);
			$stmt->execute();
			$result = $stmt->get_result();
			$row = $result->fetch_array(MYSQLI_NUM);
			//$this->getLocationImage($row[0]);
			
			$stmt2 = $this->con->prepare("SELECT `map_image` FROM `server` WHERE `server_id` = ?");
			$stmt2->bind_param("i",$row[0]);
			$stmt2->execute();
			$result2 = $stmt2->get_result();
			$row2 = $result2->fetch_array(MYSQLI_NUM);
			echo '<img src="data:image/jpeg;base64,'.base64_encode($row2[0]) .'" />';
			//echo $row2[0];
		}
		
		public function getItemsFromInventory($userid){
			$itemarray = array();
			$stmt = $this->con->prepare("SELECT `item` FROM `inventory` WHERE `profile` = ?");
			$stmt->bind_param("s",$userid);
			$stmt->execute();
			$result = $stmt->get_result();
			//$row = $result->fetch_array(MYSQLI_NUM);
			foreach($result as $item){
				array_push($itemarray,$item['item']);
			}
			echo json_encode($itemarray);
			$items = $this->getItemsInfo($result);
			echo json_encode($items);
		}
		
		private function getItemsInfo($list){
			$itemnames = array();
			foreach($list as $item){
				$stmt = $this->con->prepare("SELECT * FROM `items` WHERE `item_id` = ?");
				$stmt->bind_param("s",$item['item']);
				$stmt->execute();
				$result = $stmt->get_result();
				foreach($result as $item2){
					array_push($itemnames,$item2['name']);
				}
			}
			return $itemnames;
		}
	}
		
?>