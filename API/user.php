<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
$configs = include('config.php');
$read_con = mysqli_connect($configs["mysql_host"],$configs["user_read"], $configs["password_read"],$configs["database_name"]);
$write_con = mysqli_connect($configs["mysql_host"],$configs["user_write"], $configs["password_write"],$configs["database_name"]);


// Check connection
if (mysqli_connect_errno()) {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

$json       = file_get_contents('php://input');
$json_array = json_decode($json, true);
//echo $json_array["action"];

if ($json_array["action"] == 'Signup') {
    adduser($json_array);
}
if ($json_array["action"] == 'Logout') {
    logout($json_array["email"]);
}
if ($json_array["action"] == 'Update') {
    updateuser($json_array);
}
if ($json_array["action"] == 'Startup') {
    startup($json_array["email"]);
}
if ($json_array["action"] == 'Login') {
    loginuser($json_array);
}
if($json_array["action"]=="Reverify"){
  reverify($json_array["email"]);
}
if($json_array["action"]=="ChangePass"){
  regenpass($json_array["email"]);
}
if($json_array["action"]=="Rate"){
  rateapp($json_array["email"],$json_array["rating"]);
}
function rateapp($email,$rating){
	global $write_con;
	$response = array();
	$result = mysqli_query($write_con, "UPDATE accountTable SET rating='$rating' WHERE email='$email' ");
	if($result){
        $response["success"]=1;
	}
	else{
        $response["success"]=0;
	}
        echo json_encode($response);
}
function regenpass($email){
global $read_con;
$response = array();
$result   = mysqli_query($read_con, "SELECT * FROM accountTable WHERE email='$email'");
    if (mysqli_num_rows($result) == 0) {
	$response["success"]=0;
        $response["message"]="Email not registered";
        echo json_encode($response);
}
else{
$row = mysqli_fetch_array($result,MYSQLI_ASSOC);
if($row["email_activated"]==0){
        $response["success"]=0;
        $response["message"]="Email not verified";
        echo json_encode($response);
}
else{
         $email_hash = $row["email_hash"];
         $pass_hash= md5($row["password"]);
$username="api";
$password="key-fdcb33113caf5da81f60f9339b065538";

$ch = curl_init();
 curl_setopt($ch, CURLOPT_URL, "https://api.mailgun.net/v3/www.cafeteatime.in/messages"); 
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($ch, CURLOPT_USERPWD, "$username:$password");
 curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
 curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
$data = array( 'from' => 'Admin Cafeteatime <admin@cafeteatime.in>', 
'to' => $email, 
'subject' => 'Password Change ',
'text' => "Hey there!
Forgot your account password?
Don't worry, you can reset it here :

http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/regeneratepass.php?ehash=" . $email_hash."&phash=".$pass_hash. "

Feel free to contact us in case of any queries or problems.
Technical team - Skyline labs : skylinelabs.tech@gmail.com
Cafe Tea time - contactcafett@gmail.com

Thanks and Regards,
Team Cafe Tea Time & Skyline labs"); 
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
 $output = curl_exec($ch);
 curl_close($ch);
//echo $output;
        $response["success"]=1;
        echo json_encode($response);
}
}
}

function logout($email){
global $read_con,$write_con;
$response = array();
$result = mysqli_query($write_con, "UPDATE accountTable SET logged_in=0 WHERE email='$email' ");
if($result){
$response["success"]=1;
echo json_encode($response);
}
else{
$response["success"]=0;
echo json_encode($response);

}
}
function reverify($email){
global $read_con;
$response = array();
$result   = mysqli_query($read_con, "SELECT email_activated FROM accountTable WHERE email='$email'");
    if (mysqli_num_rows($result) == 0) {
	$response["success"]=0;
        $response["message"]="Email not registered";
        echo json_encode($response);
}
else{
$row = mysqli_fetch_array($result,MYSQLI_ASSOC);
if($row["email_activated"]==1){
        $response["success"]=0;
        $response["message"]="Email already verified";
        echo json_encode($response);
}
else{
        sendverificationmail($email);
        $response["success"]=1;
        echo json_encode($response);
}
}
}
function updateuser($update_array){
global $write_con,$read_con;
$response = array();
$email=$update_array["email"];
$branch=$update_array["branch"];
$block=$update_array["block"];
$room=$update_array["room"];
$address=$update_array["address"];
$year=$update_array["year"];
$mobile=$update_array["mobile"];
$result = mysqli_query($write_con, "UPDATE accountTable SET branch='$branch',block='$block',room='$room',year='$year',address='$address' ,mobile='$mobile' WHERE email='$email' ");
echo mysqli_error($write_con);
if($result){
$response["success"]=1;
echo json_encode($response);
}
else{
$response["success"]=0;
echo json_encode($response);

}
}
function startup($email){
global $read_con;
global $configs;
$response = array();
$response["latest_version_code"]=$configs["latest_version_code"];
$response["latest_url"]=$configs["latest_url"];
$response["success"]=1;
$result   = mysqli_query($read_con, "SELECT logged_in FROM accountTable WHERE email='$email'");
if(mysqli_num_rows($result) == 1){
$row = mysqli_fetch_array($result,MYSQLI_ASSOC);
$response["logged_in"]=$row["logged_in"];
$query=mysqli_query($read_con,"SELECT value FROM detailsTable WHERE key_name='is_katta_open'");
$query_array=mysqli_fetch_assoc($query);
$response["is_katta_open"]=$query_array["value"];
echo json_encode($response);
}
else{
$response["success"]=1;
$response["logged_in"]=0;
echo json_encode($response);

}
}
function loginuser($login_array)
{
    global $read_con,$write_con;
    $response = array();
    $email    = $login_array["email"];
    $password = $login_array["password"];
    $result   = mysqli_query($read_con, "SELECT * FROM accountTable WHERE email='$email'");
    $row = mysqli_fetch_array($result,MYSQLI_ASSOC);
    if ($row == null) {
        $response["success"] = 0;
        $response["message"] = "Email dosen't exist!!";
        echo json_encode($response);
    }
else    if ($row["email_activated"] != 1) {
        $response["success"] = 0;
        $response["message"] = "Email not verified !!";
        echo json_encode($response);
    }
    else if (password_verify($password, $row["password"])) {
        $result = mysqli_query($write_con, "UPDATE accountTable SET logged_in=1 WHERE email='$email' ");
        $result   = mysqli_query($read_con, "SELECT * FROM accountTable WHERE email='$email'");
        $keys=array_keys($row);
        foreach($keys as $key){
        if($key!="email_hash" && $key!="password" && $key!="firebaseid" && $key!="logged_in"){
	$response[$key]=$row[ $key];
}
 }
        $response["success"] = 1;
	$response["logged_in"] = 1 ;
        $response["message"] = "Login Successfull.";

        echo json_encode($response);
    } else {
        $response["success"] = 0;
        $response["message"] = "Incorrect Password.";
        echo json_encode($response);
    }
}
function adduser($user_array)
{
    global $read_con,$write_con;
    $response   = array();
    $mis        = $user_array["mis"];
    $email      = $user_array["email"];
    $password   = password_hash($user_array["password"], PASSWORD_DEFAULT);
    $name       = $user_array["name"];
    $mobile     = $user_array["mobile"];
    $email_hash = md5($email);
    $block = $user_array["block"];
    $room = $user_array["room"];
    $address    = $user_array["address"];
    $firebaseid = $user_array["firebaseid"];
    $branch = $user_array["branch"];
    $year = $user_array["year"];
    $hometown=$user_array["hometown"];
    $exists     = mysqli_num_rows(mysqli_query($read_con, "SELECT email FROM accountTable WHERE email='$email'"));
    //echo $exists;
    //echo $email,$password;
    //verify mobile
    //verify email
    if ($exists == 0) {
        $result = mysqli_query($write_con, "INSERT INTO accountTable (email,email_hash,password,name,mobile,address,mis,firebaseid,branch,year,hometown,room,block) VALUES('$email','$email_hash', '$password','$name','$mobile','$address','$mis','$firebaseid','$branch','$year','$hometown','$room','$block')");
        if ($result) {
            $response["success"] = 1;
            $response["message"] = "Registeration successfull.";
            
            sendverificationmail($email);
            
            echo json_encode($response);
        } else {
            $response["success"] = 0;
            $response["message"] = "Oops! An error occurred.". mysqli_error($write_con);
            echo json_encode($response);
        }
    } else {
        $response["success"] = 0;
        $response["message"] = "Email already registered!";
        echo json_encode($response);
        
    }
    
}
function sendverificationmail($target){
$username="api";
$password="key-fdcb33113caf5da81f60f9339b065538";
$email_hash = md5($target);

$ch = curl_init();
 curl_setopt($ch, CURLOPT_URL, "https://api.mailgun.net/v3/www.cafeteatime.in/messages"); 
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
curl_setopt($ch, CURLOPT_USERPWD, "$username:$password");
 curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
 curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
$data = array( 'from' => 'Admin Cafeteatime <admin@cafeteatime.in>', 
'to' => $target, 
'subject' => ' Verification Email ',
'text' => "Welcome to Cafe Tea Time.
You are just one step away from being able to order awesome food! Click on the following link to activate your account : 

http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/verify.php?activation=$email_hash 

Feel free to contact us in case of any queries or problems.
Technical team - Skyline labs : skylinelabs.tech@gmail.com
Cafe Tea time - contactcafett@gmail.com

Thanks and Regards,
Team Cafe Tea Time & Skyline labs"
); 
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
 $output = curl_exec($ch);
 curl_close($ch);
//echo $output;
}
?>
