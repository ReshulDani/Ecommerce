<?php
$configs = include('config.php');
$write_con = mysqli_connect($configs["mysql_host"],$configs["user_write"], $configs["password_write"],$configs["database_name"]);



// Check connection
if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

$json=file_get_contents('php://input');
$json_array=json_decode($json,true);

if($json_array["action"]=='setFirebaseID'){
	setFirebaseID($json_array);
}

if($json_array["action"]=='Notify'){
        $topicarray=$json_array["topicarray"];
        foreach($topicarray as $topic){
	notify("/topics/".$topic["name"],$json_array["data"]);
        }
        $response=array();
                        $response["success"] = 1;
			echo json_encode($response);
}
function setFirebaseID($fire_array){
global $write_con;
$response=array();

$email=$fire_array["email"];
$fid=$fire_array["firebaseid"];

$result=mysqli_query($write_con,"UPDATE accountTable SET firebaseid='$fid' WHERE email = '$email'");

    if ($result) {
			$response["success"] = 1;
			echo json_encode($response);
	} else {
 		$response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
		echo json_encode($response);
}
}

function notify($firebaseid,$status){
	
// Your ID and token
$authToken = '';

// The data to send to the API
$postData = array(
    'data' => array('title'=>'Cafe Tea Time','text'=>$status,'type'=>'news','action'=>'news'),
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


//$id='cL-kkYL1NU8:APA91bFufW1CrZ-WVK3aaQVJ_F_VnttkKrz9F2KlVxqWiJ6ty3vvfJfv9r1BTkOxnOl6Sd8WJJXCHU4fw1XKVzx-zPW16Ow8g3TrJA6-VYLcIB5n9uFUCko0oZD2oT-QVAMi8gIiRG1N';
//$status='PHP!!!';
//notify($id,$status);
?>
