<html>
<body> 

<?php
$configs = include('config.php');
$write_con = mysqli_connect($configs["mysql_host"],$configs["user_write"], $configs["password_write"],$configs["database_name"]);
$read_con = mysqli_connect($configs["mysql_host"],$configs["user_read"], $configs["password_read"],$configs["database_name"]);
function phpAlert($msg) {
    echo '<script type="text/javascript">alert("' . $msg . '")</script>';
}
// Check connection

if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

$pass_hash=$_GET["phash"];
$email_hash=$_GET["ehash"];
$changed=0;
$new_password = $confirm_password = "";



$result   = mysqli_query($read_con, "SELECT * FROM accountTable WHERE email_hash='$email_hash'");
$row=mysqli_fetch_array($result,MYSQLI_ASSOC);
if(isset($_POST["new_password"]) && isset($_POST["confirm_password"])){
  $new_password = $_POST["new_password"];
  $confirm_password = $_POST["confirm_password"];
if(strlen($new_password)>=8 && strcmp($new_password,$confirm_password)==0){
        $password   = password_hash($new_password, PASSWORD_DEFAULT);
	$result = mysqli_query($write_con, "UPDATE accountTable SET password='$password', logged_in=0 WHERE email_hash='$email_hash' ");
	if($result){	
		phpAlert("password changed successfully");
		$changed=1;
	}
}	
else {
	phpAlert("Passwords are less than 8 charachter or don't match");
	$changed=0;
}
}
if(md5($row["password"])==$pass_hash && $changed==0){

/*echo'<form method="post" action="">'.'  
  New Password:  <input type="password" name="new_password">
  <br><br>
  Confirm Password:  <input type="password" name="confirm_password">
  <br><br>
  <input type="submit" name="submit" value="Submit">  
</form>
';*/
echo "<html lang=\"en\">

	<head>
		<title>Cafe tea time | Change password</title>
	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>
	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=false\" />
	<meta name=\"viewport\" content=\"user-scalable=no\" />
	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>
	<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />	
	<meta name=\"msapplication-TileColor\" content=\"#2196f3\">
	<meta name=\"msapplication-TileImage\" content=\"images/Logo/s_small.png\">



		<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.5/css/materialize.min.css\">
		
		<script src=\"http://code.jquery.com/jquery-latest.min.js\"></script>
		<script type=\"text/javascript\" src=\"http://skylinelabs.in/js/js/materialize.min.js\"></script>

	</head>

<body  class=\" white darken-1 \">


<div class=\"row\" style=\"position:relative; top:20vh;\">
		<div class=\"col s12 m8 offset-m2\">
			<div class=\"card-panel white center\">
			
				</br></br>
				<span class=\"blue-text text-darken-3 flow-text center\">Cafe Tea Time Password Change</span>
				</br></br></br></br>
				
			
				<div class=\"row\"> 

					<form class=\"col s12\" action=\"\" method=\"POST\" id=\"password_change_form\">

						<div class=\"row\">
							<div class=\"input-field col s12\">
								<input id=\"new_password\" type=\"password\" name=\"new_password\"  class=\"validate\">
								<label for=\"new_password\">New password</label>
							</div>
							
						</div>
						
						
						<div class=\"row\">
							<div class=\"input-field col s12\">
								<input id=\"confirm_password\" type=\"password\" name=\"confirm_password\" class=\"validate\">
								<label for=\"confirm_password\">Confirm password</label>
							</div>
							
						</div>
						
		<a href=\"javascript:{}\" class=\"waves-effect blue darken-3 waves-dark btn\"  onclick=\"document.getElementById('password_change_form').submit(); return false;\">Change</a>	
<!--					<a class=\"waves-effect blue darken-3 waves-dark btn\">Change</a> -->
					</form>
				</div>
			</div>

		</div>

</div>

</body>


	
</html>";
}
else {
phpAlert("You are Unauthorised to change password");
}
?>

</body>
</html>
