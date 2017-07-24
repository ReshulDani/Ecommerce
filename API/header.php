<?php

$configs = include('config.php');


// Check connection

if (mysqli_connect_errno())
{
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

$json = file_get_contents('php://input');
$json_array = json_decode($json, true);

if ($json_array["action"] == "getHeaderImages")
{
	getheaderimages();
}

function getheaderimages(){
	$response = array();
	$response["items"] = array();
	
	$item = array();

	$item["url"] ="http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/uploads/MenuImages/header_1.jpg";
	array_push($response["items"],$item);
	
	$item["url"] = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/uploads/MenuImages/header_2.jpg";
	array_push($response["items"],$item);

	$item["url"] = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/uploads/MenuImages/header_3.jpg";
	array_push($response["items"],$item);

	/*$item["url"] = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/uploads/MenuImages/poha.jpg";
	array_push($response["items"],$item);

	
	$item["url"] = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/uploads/MenuImages/Bhajiya.jpg";
	array_push($response["items"],$item);
	*/

	$response["success"]=1;
	
	echo json_encode($response);
	
	
	
}


?>
