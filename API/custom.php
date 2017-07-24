<?php
$configs = include('config.php');
$read_con = mysqli_connect($configs["mysql_host"],$configs["user_read"], $configs["password_read"],$configs["database_name"]);
$result   = mysqli_query($read_con, "SELECT email FROM accountTable WHERE email_activated=0");

while($row = mysqli_fetch_assoc($result))
{
    sendverificationmail($row['email']);
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
'text' => "'Welcome to Cafe Tea Time.
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
