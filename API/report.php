<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
    // Outputs all POST parameters to a text file. The file name is the date_time of the report reception
    $report_id=$_POST['REPORT_ID'];
    $fileName = './reports/' . $report_id . '.txt';
    $file = fopen($fileName,'w+') or die('Could not create report file: ' . $fileName);
    foreach($_POST as $key => $value) {
    $reportLine = $key." = ".$value."\n";
        fwrite($file, $reportLine) or die ('Could not write to report file ' . $reportLine);
    }
    fclose($file);
    sendmail("ameyaapte1@gmail.com",$report_id);
    sendmail("skylinelabs.tech@gmail.com",$report_id);
    sendmail("mihirmistry97@gmail.com",$report_id);
function sendmail($target,$id){
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
'subject' => ' App Crashed ',
'text' => 'Katta Client App crashed. Click on the following link for report:http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/reports/' .$id.'.txt');
curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
 $output = curl_exec($ch);
 curl_close($ch);
//echo $output;
}
?>
