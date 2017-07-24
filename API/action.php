<?php

$configs = include('config.php');

$read_con = mysqli_connect($configs["mysql_host"],$configs["user_read"], $configs["password_read"],$configs["database_name"]);
$write_con = mysqli_connect($configs["mysql_host"],$configs["user_write"], $configs["password_write"],$configs["database_name"]);

// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

$json=file_get_contents('php://input');
$json_array=json_decode($json,true);

if($json_array["action"]=='Add'){
	additem($json_array);	
}
if($json_array["action"]=='Delete'){
	deleteitem($json_array);
}
if($json_array["action"]=='Update'){
	updateitem($json_array);
}
if($json_array["action"]=='ReadAll'){
	//echo "Hello";
	readall($json_array);
}
if($json_array["action"]=='ReadAllAvailable'){
	//echo "Hello";
	readallavailable($json_array);
}
if($json_array["action"]=='setAvailability'){
	setAvailability($json_array);
}

function setAvailability($name_array){
global $write_con;
$special=$name_array["special"];
    $update_name=$name_array["name"];
    $availability=$name_array["availability"];
    $special=$name_array["special"];
    $response=array();
    
    if($availability==1){
        if($special==0){
        $result=mysqli_query($write_con,"UPDATE menuTable SET availability='1' WHERE name = '$update_name'");
        }
        else{
        $result=mysqli_query($write_con,"UPDATE specialTable SET availability='1' WHERE name = '$update_name'");
        }
        $response["message"]="Product made Available";
    }
    else{
        if($special==0){
        $result=mysqli_query($write_con,"UPDATE menuTable SET availability='0' WHERE name = '$update_name'");
}
else{
        $result=mysqli_query($write_con,"UPDATE specialTable SET availability='0' WHERE name = '$update_name'");
}
        $response["message"] = "Product made Unavailable.";
    }
    if ($result) {
			$response["success"] = 1;
			echo json_encode($response);
	} else {
 		$response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
		echo json_encode($response);	
}
}
function additem($add_array){
        global $read_con, $write_con;
	$response=array();
	$name=$add_array["name"];
	$price=$add_array["price"];
        $category=$add_array["category"];
	$is_veg=$add_array["is_veg"];
$special=$add_array["special"];
if($special==0){
	$exists = mysqli_num_rows(mysqli_query($read_con,"SELECT * FROM menuTable WHERE name='$name'"));
}
else{
	$exists = mysqli_num_rows(mysqli_query($read_con,"SELECT * FROM specialTable WHERE name='$name'"));
}
	if($exists==0){
if($special==0){
		$result = mysqli_query($write_con,"INSERT INTO menuTable (name,price,category,is_veg) VALUES('$name', '$price','$category','$is_veg')");
}
else{
	$result = mysqli_query($write_con,"INSERT INTO specialTable (name,price,category) VALUES('$name', '$price','$category','$is_veg')");
        notify("/topics/clients","Special item " . $name . " added.");
}
		if ($result) {
			$response["success"] = 1;
			$response["message"] = "Product successfully updated.";
	        	echo json_encode($response);
		} else {
	 		$response["success"] = 0;
        		$response["message"] = "Oops! An error occurred.";
		        echo json_encode($response);
			}
	}
	else{
		$response["success"] = 0;
                $response["message"] = "Item already exists!";
                echo json_encode($response);

	}
}

function deleteitem($delete_array){
        global $write_con;
	$delete_name=$delete_array["delete_name"];
$special=$delete_array["special"];
if($special==0){
	$result = mysqli_query($write_con,"DELETE FROM menuTable WHERE name = '$delete_name'");
}
else{
	$result = mysqli_query($write_con,"DELETE FROM specialTable WHERE name = '$delete_name'");
}
	if ($result) {
                //unlink("./uploads/" . $delete_name . '*');
                $response["success"] = 1;
                $response["message"] = "Product successfully Deleted.";
                echo json_encode($response);
        } else {
                $response["success"] = 0;
                $response["message"] = "Oops! An error occurred.";
                echo json_encode($response);
                }
}

function updateitem($update_array){
        global $write_con;
	$update_name=$update_array["old_name"];
	$name=$update_array["new_name"];
	$price=$update_array["new_price"];
        $category=$update_array["new_category"];
$special=$update_array["special"];
if($special==0){
	$result = mysqli_query($write_con,"UPDATE menuTable SET name = '$name', price = '$price', category='$category' WHERE name = '$update_name'");
}
else{
	$result = mysqli_query($write_con,"UPDATE specialTable SET name = '$name', price = '$price', category='$category' WHERE name = '$update_name'");
}
	if ($result) {
                $response["success"] = 1;
                $response["message"] = "Product successfully updated.";
                echo json_encode($response);
        } else {
                $response["success"] = 0;
                $response["message"] = "Oops! An error occurred.";
                echo json_encode($response);
                }

}

function readall($readall){
global $read_con;
$response = array();
$special=$readall["special"];
if($special==0){
$result = mysqli_query($read_con,"SELECT * FROM menuTable ORDER BY name");
}
else{
$result = mysqli_query($read_con,"SELECT * FROM specialTable ORDER BY name");
}

if (mysqli_num_rows($result) > 0) {
    $response["success"] = 1;
    $response["items"] = array();
 
    while ($row = mysqli_fetch_array($result,MYSQLI_ASSOC)) {
        $product = array();
        $product["name"] = $row["name"];
        $product["price"] = $row["price"];
        $product["created_at"] = $row["created_at"];
        $product["availability"] = $row["availability"]; 
        $product["image_url"] = $row["image_url"];
        $product["category"] = $row["category"];
        array_push($response["items"], $product);
    }
 
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No products found";
    echo json_encode($response);
}
}

function readallavailable($readallavailable){
global $read_con;
$response = array();
$special=$readallavailable["special"];
if($special==0){
$result = mysqli_query($read_con,"SELECT * FROM menuTable WHERE availability=1 ORDER BY name");
}
else{
$result = mysqli_query($read_con,"SELECT * FROM specialTable WHERE availability=1 ORDER BY name");
}

if (mysqli_num_rows($result) > 0) {
    $response["success"] = 1;
    $response["items"] = array();
 
    while ($row = mysqli_fetch_array($result,MYSQLI_ASSOC)) {
        $product = array();
if($special==0){
$product["id"] ="m";
}
else{
$product["id"] ="s";
}
$product["id"] .= $row["id"];
        
$product["name"] = $row["name"];
        $product["price"] = $row["price"];
        $product["created_at"] = $row["created_at"];
        $product["availability"] = $row["availability"]; 
        $product["image_url"] = $row["image_url"];
        $product["category"] = $row["category"];
	$product["is_veg"] = $row["is_veg"];
        array_push($response["items"], $product);
    }
 
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No products found";
    echo json_encode($response);
}
}
function notify($firebaseid,$status){
// Your ID and token
$authToken = 'AIzaSyACKLxJGZgtzHkBwJme2t3eOuORaWnkH4E';

// The data to send to the API
$postData = array(
    'notification' => array('title'=>'Cafe Tea Time','text'=>$status),
    'to' => $firebaseid
);

// Create the context for the request
$context = stream_context_create(array(
    'http' => array(
        //https://fcm.googleapis.com/fcm/send,
        'method' => 'POST',
        'header' => "Authorization: key={$authToken}\r\n". "Content-Type: application/json\r\n",
        'content' => json_encode($postData)
    )
));

// Send the request
$response = file_get_contents('https://fcm.googleapis.com/fcm/send', FALSE, $context);
}
 ?>

