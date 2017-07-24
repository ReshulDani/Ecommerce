<?php
$configs = include('config.php');
$read_con = mysqli_connect($configs["mysql_host"],$configs["user_read"], $configs["password_read"],$configs["database_name"]);
$write_con = mysqli_connect($configs["mysql_host"],$configs["user_write"], $configs["password_write"],$configs["database_name"]);


//

// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

$json=file_get_contents('php://input');
$json_array=json_decode($json,true);

if($json_array["action"]=="getDetails"){
	getDetails($json_array);
}

if($json_array["action"]=="setDetails"){
	setdetails($json_array);
}

if($json_array["action"]=="setOpen"){
	setopen($json_array);
}

function setOpen($array){

        global $write_con;
	$response = array();
	$response["success"]=1;

        $property=$array["property"];
        $value=$array["value"];

        $query=mysqli_query($write_con,"UPDATE detailsTable SET value=$value WHERE key_name='$property'");

        $response["success"]=1;
	$response["message"]="Successful";
        echo json_encode($response);
}
function getDetails($array){
	global $read_con;
	$response = array();
	$response["success"]=1;
   
	$query=mysqli_query($read_con,"SELECT value FROM detailsTable WHERE key_name='minTotalforDelivery'");
	$query_array=mysqli_fetch_assoc($query);
	$response["minTotalforDelivery"]=$query_array["value"];

	$query=mysqli_query($read_con,"SELECT value FROM detailsTable WHERE key_name='minTotalforFreeDelivery'");
	$query_array=mysqli_fetch_assoc($query);
	$response["minTotalforFreeDelivery"]=$query_array["value"];

	$query=mysqli_query($read_con,"SELECT value FROM detailsTable WHERE key_name='deliveryCharge'");
	$query_array=mysqli_fetch_assoc($query);
	$response["deliveryCharge"]=$query_array["value"];

	$query=mysqli_query($read_con,"SELECT value FROM detailsTable WHERE key_name='pickUpCharge'");
	$query_array=mysqli_fetch_assoc($query);
	$response["pickUpCharge"]=$query_array["value"];

	$query=mysqli_query($read_con,"SELECT value FROM detailsTable WHERE key_name='is_katta_open'");
	$query_array=mysqli_fetch_assoc($query);
	$response["is_katta_open"]=$query_array["value"];

	$query=mysqli_query($read_con,"SELECT value FROM detailsTable WHERE key_name='is_delivery_available'");
	$query_array=mysqli_fetch_assoc($query);
	$response["is_delivery_available"]=$query_array["value"];
	
	echo json_encode($response);

}

function setDetails($array){
        global $write_con;
        $response=array();

        $value=$array["minTotalforDelivery"];
        $query=mysqli_query($write_con,"UPDATE detailsTable SET value=$value WHERE key_name='minTotalforDelivery'");
    
        $value=$array["minTotalforFreeDelivery"];
        $query=mysqli_query($write_con,"UPDATE detailsTable SET value=$value WHERE key_name='minTotalforFreeDelivery'");
    
        $value=$array["deliveryCharge"];
        $query=mysqli_query($write_con,"UPDATE detailsTable SET value=$value WHERE key_name='deliveryCharge'");
        
        $value=$array["pickUpCharge"];
        $query=mysqli_query($write_con,"UPDATE detailsTable SET value=$value WHERE key_name='pickUpCharge'");
        


        $response["success"]=1;
	$response["message"]="Successful";
        echo json_encode($response);
}

?>		
