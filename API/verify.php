<?php
$configs = include('config.php');
$read_con = mysqli_connect($configs["mysql_host"],$configs["user_read"], $configs["password_read"],$configs["database_name"]);
$write_con = mysqli_connect($configs["mysql_host"],$configs["user_write"], $configs["password_write"],$configs["database_name"]);



// Check connection

if (mysqli_connect_errno())
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

$email_hash=$_GET["activation"];
$exists = mysqli_num_rows(mysqli_query($read_con,"SELECT * FROM accountTable WHERE email_hash='$email_hash'"));
if($exists!=1){
echo "This account dosen't exists Create new account and come back!!";
}
else{
$result=mysqli_query($write_con,"UPDATE accountTable SET email_activated=1 WHERE email_hash = '$email_hash'");
echo "<html lang=\"en\">

	<head>
		<title>Cafe tea time | Account verified</title>
	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>
	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=false\" />
	<meta name=\"viewport\" content=\"user-scalable=no\" />
	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>
	<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />	
	<meta name=\"msapplication-TileColor\" content=\"#2196f3\">
	<meta name=\"msapplication-TileImage\" content=\"images/Logo/s_small.png\">



		<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.5/css/materialize.min.css\">
		
		<script src=\"http://code.jquery.com/jquery-latest.min.js\"></script>
		<script type=\"text/javascript\" src=\"js/js/materialize.min.js\"></script>


		
	</head>

<body  class=\" white darken-1 \">

		
<div class=\"center vertical-align\">
	<div class=\"flow-text\" style=\"position:relative; top:46vh;\">
		Welcome to cafe tea time</br>
		Your account has been verified.</br>
		Login to your app to continue
	</div>
	<div class=\"blue-text text-darken-3\" style=\"position:relative; top:70vh;\">
		Copyright Cafe tea time</br>Developed by Skyline labs
	 
	</div>

</div>

		
</body>


	
</html>
";
}

?>
