<?php
$configs = include('config.php');
$read_con = mysqli_connect($configs["mysql_host"],$configs["user_read"], $configs["password_read"],$configs["database_name"]);
$write_con = mysqli_connect($configs["mysql_host"],$configs["user_write"], $configs["password_write"],$configs["database_name"]);


// Check connection

if (mysqli_connect_errno())
{
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

$json = file_get_contents('php://input');
$json_array = json_decode($json, true);

if ($json_array["action"] == "PlaceOrder")
{
	placeorder($json_array);
}

if ($json_array["action"] == "getItems")
{
	getitems($json_array);
}

if ($json_array["action"] == "getPastOrders")
{
	getpastorders($json_array);
}

if ($json_array["action"] == "searchPastOrdersByOrderId")
{
	searchpastordersbyorderid($json_array);
}

if ($json_array["action"] == "searchPastOrdersByUserName")
{
	searchpastordersbyusername($json_array);
}

if ($json_array["action"] == "getOrderDetails")
{
	getorderdetails($json_array);
}

if ($json_array["action"] == "ReadUserOrders")
{
	readuserorders($json_array);
}

if ($json_array["action"] == "ReadOrder")
{
	readorder($json_array);
}

if ($json_array["action"] == "setAccepted")
{
	setaccepted($json_array);
}

if ($json_array["action"] == "setServed")
{
	setserved($json_array);
}

if ($json_array["action"] == "setPrepared")
{
	setprepared($json_array);
}


if ($json_array["action"] == "cancelOrder")
{
	cancelorder($json_array);
}

function getitems()
{
	global $read_con;
	$response = array();
        $response["success"]=1;
        $response["items"]=array();
	$items = array();
	$result1 = mysqli_query($read_con, "SELECT DISTINCT name FROM orderitemTable WHERE status<2 ORDER BY name");
	$names = array();
	while (($name = mysqli_fetch_assoc($result1)))
	{
		$names[] = $name;
	}


	foreach($names as $name)
	{
		$item_name = $name["name"];
		$result2 = mysqli_query($read_con, "SELECT quantity FROM orderitemTable WHERE status<2 AND name='$item_name' ");
		$quantities = array();
		while (($quantity = mysqli_fetch_assoc($result2)))
		{
			$quantities[] = $quantity;
		}

		$total = 0;
		foreach($quantities as $quantity)
		{
			$total += $quantity["quantity"];
		}

		$items["name"] = $item_name;
                $items["quantity"]=$total;
                array_push($response["items"], $items);
	}
	echo json_encode($response);
       
}

function notify($firebaseid, $status)
{

	// Your ID and token

	$authToken = 'AIzaSyACKLxJGZgtzHkBwJme2t3eOuORaWnkH4E';

	// The data to send to the API

	$postData = array(
		'data' => array(
			'title' => 'Cafe Tea Time',
			'text' => $status,
			'type' => 'notification'
		) ,
		'to' => $firebaseid
	);

	// Create the context for the request

	$context = stream_context_create(array(
		'http' => array(

			// https://fcm.googleapis.com/fcm/send,

			'method' => 'POST',
			'header' => "Authorization: key={$authToken}\r\n" . "Content-Type: application/json\r\n",
			'content' => json_encode($postData)
		)
	));

	// Send the request

	$response = file_get_contents('https://fcm.googleapis.com/fcm/send', FALSE, $context);
}

function setaccepted($array)
{
	global $read_con,$write_con;
	$order_id = $array["order_id"];
	$result = mysqli_query($write_con, "UPDATE ordersTable SET status=1 WHERE order_id='$order_id' ");
	$result&= mysqli_query($write_con, "UPDATE orderitemTable SET status=1 WHERE order_id='$order_id' ");
	$uid = mysqli_fetch_array(mysqli_query($read_con, "SELECT user_id FROM ordersTable WHERE order_id='$order_id'"), MYSQLI_ASSOC );
   $user_id=$uid['user_id'];
	$fid = mysqli_fetch_array (mysqli_query($read_con, "SELECT firebaseid FROM accountTable WHERE user_id='$user_id'"), MYSQLI_ASSOC );
	if ($result)
	{
		$response = array();
		$response["success"] = 1;
		$response["message"] = "Accepted!";
		echo json_encode($response);
		notify($fid['firebaseid'], "Order no: " . $order_id . " accpeted!");
	}
	else
	{
		$response = array();
		$response["success"] = 0;
		$response["message"] = "Error!";
		echo json_encode($response);
	}
}

function setserved($array)
{
	global $read_con,$write_con;
	$order_id = $array["order_id"];
	$result = mysqli_query($write_con, "UPDATE ordersTable SET status=3 WHERE order_id='$order_id' ");
	$result&= mysqli_query($write_con, "UPDATE orderitemTable SET status=3 WHERE order_id='$order_id' ");
$uid = mysqli_fetch_array(mysqli_query($read_con, "SELECT user_id FROM ordersTable WHERE order_id='$order_id'"), MYSQLI_ASSOC );
   $user_id=$uid['user_id'];
	$fid = mysqli_fetch_array (mysqli_query($read_con, "SELECT firebaseid FROM accountTable WHERE user_id='$user_id'"), MYSQLI_ASSOC );
	if ($result)
	{
		$response = array();
		$response["success"] = 1;
		$response["message"] = "Served!";
		echo json_encode($response);
		notify($fid['firebaseid'], "Order no: " . $order_id . " served!");
	}
	else
	{
		$response = array();
		$response["success"] = 0;
		$response["message"] = "Error!";
		echo json_encode($response);
	}
}

function setprepared($array)
{
	global $write_con,$read_con;
	$order_id = $array["order_id"];
	$result = mysqli_query($write_con, "UPDATE ordersTable SET status=2 WHERE order_id='$order_id' ");
	$result&= mysqli_query($write_con, "UPDATE orderitemTable SET status=2 WHERE order_id='$order_id' ");
$uid = mysqli_fetch_array(mysqli_query($read_con, "SELECT user_id FROM ordersTable WHERE order_id='$order_id'"), MYSQLI_ASSOC );
   $user_id=$uid['user_id'];
	$fid = mysqli_fetch_array (mysqli_query($read_con, "SELECT firebaseid FROM accountTable WHERE user_id='$user_id'"), MYSQLI_ASSOC );
	if ($result)
	{
		$response = array();
		$response["success"] = 1;
		$response["message"] = "Prepared!";
		echo json_encode($response);
		notify($fid['firebaseid'], "Order no: " . $order_id . " prepared!");
	}
	else
	{
		$response = array();
		$response["success"] = 0;
		$response["message"] = "Error!";
		echo json_encode($response);
	}
}


function cancelorder($array)
{
	global $read_con,$write_con;
	$order_id = $array["order_id"];
	$result = mysqli_query($write_con, "UPDATE ordersTable SET status=4 WHERE order_id='$order_id' ");
	$result&= mysqli_query($write_con, "UPDATE orderitemTable SET status=4 WHERE order_id='$order_id' ");
$uid = mysqli_fetch_array(mysqli_query($read_con, "SELECT user_id FROM ordersTable WHERE order_id='$order_id'"), MYSQLI_ASSOC );
   $user_id=$uid['user_id'];
	$fid = mysqli_fetch_array (mysqli_query($read_con, "SELECT firebaseid FROM accountTable WHERE user_id='$user_id'"), MYSQLI_ASSOC );
	if ($result)
	{
		$response = array();
		$response["success"] = 1;
		$response["message"] = "Canceled!";
		echo json_encode($response);
		notify($fid['firebaseid'], "Order no: " . $order_id . " prepared!");
	}
	else
	{
		$response = array();
		$response["success"] = 0;
		$response["message"] = "Error!";
		echo json_encode($response);
	}
}


function placeorder($orderdetails)
{
	global $read_con,$write_con;
	$response = array();
	$email = $orderdetails["email"];
	$id_query = mysqli_query($read_con, "SELECT user_id FROM accountTable WHERE email='$email'");
	$id_array = mysqli_fetch_assoc($id_query);
	$user_id = $id_array["user_id"];
	$name_query = mysqli_query($read_con, "SELECT name FROM accountTable WHERE user_id='$user_id'");
	$name_array = mysqli_fetch_assoc($name_query);
	$user_name = $name_array["name"];
	$order = $orderdetails["order"];
	$order_total = $orderdetails["order_total"];
	$coupon = $orderdetails["coupon"];
	$service = $orderdetails["service"];
	$service_charge = $orderdetails["service_charge"];
	$discount_amount = $orderdetails["discount_amount"];
	
	if($service==3){
		$address_query = mysqli_query($read_con, "SELECT address FROM accountTable WHERE user_id='$user_id'");
		$address_array = mysqli_fetch_assoc($address_query);
		$service = $address_array["address"];
	}
	
	$payment = $orderdetails["payment"];
	$result = mysqli_query($write_con, "INSERT INTO ordersTable (user_id,user_name,order_total,service,service_charge,discount_amount,payment,bill_total,coupon_code) VALUES ('$user_id','$user_name','$order_total','$service',$service_charge,'$discount_amount','$payment',order_total+service_charge-discount_amount,'$coupon')");
	if ($result)
	{
		$order_id = mysqli_insert_id($write_con);
		foreach($order as $orderrow)
		{
			$name = $orderrow["name"];
			$price = $orderrow["price"];
			$quantity = $orderrow["quantity"];
			$result = mysqli_query($write_con, "INSERT INTO orderitemTable (order_id,name,price,quantity,sub_total) VALUES ('$order_id','$name','$price','$quantity',price*quantity)");
			if (!$result)
			{
				break;
			}
		}

		if ($result)
		{
			$response["success"] = 1;
			$response["message"] = "Order Successfully Placed";
			notify("/topics/admins", "New Order Received!!");
		}
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "Oops!Couldn't Place the Order";
	}

	echo json_encode($response);
}

function readorder()
{
	global $read_con;
	$response = array();
	$result = mysqli_query($read_con, "SELECT * FROM ordersTable WHERE status<3 ORDER BY order_id");
	echo mysqli_error($read_con);
	if (mysqli_num_rows($result) > 0)
	{
		$response["success"] = 1;
		$response["orders"] = array();
		while ($orderrow = mysqli_fetch_array($result, MYSQLI_ASSOC))
		{
			$order = array();
			$order["items"] = array();
			$order["order_total"] = $orderrow["order_total"];
			$order["status"] = $orderrow["status"];
			$user_id = $orderrow["user_id"];
			$user_query = mysqli_query($read_con, "SELECT * FROM accountTable WHERE user_id='$user_id'");
			$user_info = mysqli_fetch_assoc($user_query);
			$order["username"] = $user_info["name"];
			$order["mobile"] = $user_info["mobile"];
			$order["order_id"] = $orderrow["order_id"];
			$order_id = $order["order_id"];
			
			$order["service"] = $orderrow["service"];

			$order["service_charge"] = $orderrow["service_charge"];
                        
			

			$order["discount_amount"] = $orderrow["discount_amount"];
			$order["bill_total"] = $orderrow["bill_total"];		
			
			$order["payment"] = $orderrow["payment"];
			$items = mysqli_query($read_con, "SELECT * FROM orderitemTable WHERE order_id='$order_id'");
			while ($row = mysqli_fetch_array($items, MYSQLI_ASSOC))
			{
				$product = array();
				$product["name"] = $row["name"];
				$product["quantity"] = $row["quantity"];
				$product["sub_total"] = $row["sub_total"];
				array_push($order["items"], $product);
			}
                        if($order["service_charge"]!=0){
                        $product["name"] = "Service Charge";
			$product["quantity"] = "";
			$product["sub_total"] =$order["service_charge"];
                	array_push($order["items"], $product);             
                        }
                         
                        if($order["discount_amount"]!=0){
                        $product["name"] = "Discounts";
			$product["quantity"] = "";
			$product["sub_total"] ='-'.$order["discount_amount"];
               		array_push($order["items"], $product);             
                        }
              
			array_push($response["orders"], $order);
		}

		$response["message"] = "Orders Read";
		echo json_encode($response);
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No Orders";
		echo json_encode($response);
	}
}

function readuserorders($json_array)
{
	global $read_con;
	$response = array();
	$email = $json_array["email"];
	$id_query = mysqli_query($read_con, "SELECT user_id FROM accountTable WHERE email='$email'");
	$id_array = mysqli_fetch_assoc($id_query);
	$user_id = $id_array["user_id"];
	$result = mysqli_query($read_con, "SELECT * FROM ordersTable WHERE user_id='$user_id' ORDER BY order_id DESC");
	if (mysqli_num_rows($result) > 0)
	{
		$response["success"] = 1;
		$response["orders"] = array();
		while ($orderrow = mysqli_fetch_array($result, MYSQLI_ASSOC))
		{
			$order = array();
			$order["items"] = array();
			$order["order_total"] = $orderrow["order_total"];
			$order["status"] = $orderrow["status"];
			$user_id = $orderrow["user_id"];
			$user_query = mysqli_query($read_con, "SELECT * FROM accountTable WHERE user_id='$user_id'");
			$user_info = mysqli_fetch_assoc($user_query);
			$order["username"] = $user_info["name"];
			$order["mobile"] = $user_info["mobile"];
			$order["order_id"] = $orderrow["order_id"];
			$order_id = $order["order_id"];
			$order["created_at"] = $orderrow["created_at"];
	
			$order["service"] = $orderrow["service"];
			$order["service_charge"] = $orderrow["service_charge"];
			$order["discount_amount"] = $orderrow["discount_amount"];
			$order["bill_total"] = $orderrow["bill_total"];
			
			$order["payment"] = $orderrow["payment"];
			$items = mysqli_query($read_con, "SELECT * FROM orderitemTable WHERE order_id='$order_id'");
			while ($row = mysqli_fetch_array($items, MYSQLI_ASSOC))
			{
				$product = array();
				$product["name"] = $row["name"];
				$product["quantity"] = $row["quantity"];
				$product["sub_total"] = $row["sub_total"];
				array_push($order["items"], $product);
			}
                       
                        if($order["service_charge"]!=0){
                        $product["name"] = "Service Charge";
			$product["quantity"] = "";
			$product["sub_total"] =$order["service_charge"];
                	array_push($order["items"], $product);             
                        }
                         
                        if($order["discount_amount"]!=0){
                        $product["name"] = "Discounts";
			$product["quantity"] = "";
			$product["sub_total"] ='-'.$order["discount_amount"];
               		array_push($order["items"], $product);             
                        }
                         array_push($response["orders"], $order);
                }
		$response["message"] = "Orders Read";
		echo json_encode($response);
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No Orders";
		echo json_encode($response);
	}
}

function getorderdetails($json_array)
{
	global $read_con;
	$order_id = $json_array["order_id"];
	$items = mysqli_query($read_con, "SELECT * FROM orderitemTable WHERE order_id='$order_id'");
	$result = mysqli_query($read_con, "SELECT * FROM ordersTable WHERE order_id='$order_id'");
        $orderrow = mysqli_fetch_array($result, MYSQLI_ASSOC);

	if (mysqli_num_rows($items) > 0)
	{
		$response["success"] = 1;
		$response["items"] = array();
		while ($row = mysqli_fetch_array($items, MYSQLI_ASSOC))
		{
			$product = array();
			$product["name"] = $row["name"];
		 	$product["quantity"] = $row["quantity"];
 			$product["sub_total"] = $row["sub_total"];
			array_push($response["items"], $product);
		}
                        
                        if($order["service_charge"]!=0){
                        $product["name"] = "Service Charge";
			$product["quantity"] = "";
			$product["sub_total"] =$orderrow["service_charge"];
                	array_push($order["items"], $product);             
                        }
                         
                        if($order["discount_amount"]!=0){
                        $product["name"] = "Discounts";
			$product["quantity"] = "";
			$product["sub_total"] ='-'.$orderrow["discount_amount"];
               		array_push($order["items"], $product);             
                        }    
			array_push($response["orders"], $order);
		$response["message"] = "Items Read";
		echo json_encode($response);
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No Items and that is not possible";
		echo json_encode($response);
	}
}

function getpastorders($json_array)
{
	global $read_con;
	$response = array();
	$result = mysqli_query($read_con, "SELECT * FROM ordersTable WHERE status=3 ORDER BY order_id DESC LIMIT 15");
	if (mysqli_num_rows($result) > 0)
	{
		$response["success"] = 1;
		$response["orders"] = array();
		while ($orderrow = mysqli_fetch_array($result, MYSQLI_ASSOC))
		{
			$order = array();
			$order["items"] = array();
			$order["order_total"] = $orderrow["order_total"];
			$order["status"] = $orderrow["status"];
			$user_id = $orderrow["user_id"];
			$user_query = mysqli_query($read_con, "SELECT * FROM accountTable WHERE user_id='$user_id'");
			$user_info = mysqli_fetch_assoc($user_query);
			$order["username"] = $user_info["name"];
			$order["mobile"] = $user_info["mobile"];
			$order["order_id"] = $orderrow["order_id"];
			$order_id = $order["order_id"];
			
			//$order["delivery"] = $orderrow["delivery"];
			
			$order["service"] = $orderrow["service"];
			$order["service_charge"] = $orderrow["service_charge"];
			$order["discount_amount"] = $orderrow["discount_amount"];
			$order["bill_total"] = $orderrow["bill_total"];
			
			$order["payment"] = $orderrow["payment"];
			$items = mysqli_query($read_con, "SELECT * FROM orderitemTable WHERE order_id='$order_id'");
			while ($row = mysqli_fetch_array($items, MYSQLI_ASSOC))
			{
				$product = array();
				$product["name"] = $row["name"];
				$product["quantity"] = $row["quantity"];
				$product["sub_total"] = $row["sub_total"];
				array_push($order["items"], $product);
			}
                    
                        if($order["service_charge"]!=0){
                        $product["name"] = "Service Charge";
			$product["quantity"] = "";
			$product["sub_total"] =$order["service_charge"];
                	array_push($order["items"], $product);             
                        }
                         
                        if($order["discount_amount"]!=0){
                        $product["name"] = "Discounts";
			$product["quantity"] = "";
			$product["sub_total"] ='-'.$order["discount_amount"];
               		array_push($order["items"], $product);             
                        }
			array_push($response["orders"], $order);
		}

		$response["message"] = "Orders Read";
		echo json_encode($response);
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No Orders";
		echo json_encode($response);
	}
}

function searchpastordersbyorderid($json_array)
{
	global $read_con;
	$response = array();
	$id = $json_array["id"];
	$result = mysqli_query($read_con, "SELECT * FROM ordersTable WHERE order_id LIKE '%{$id}%' ORDER BY order_id");
	if (mysqli_num_rows($result) > 0)
	{
		$response["success"] = 1;
		$response["orders"] = array();
		while ($orderrow = mysqli_fetch_array($result, MYSQLI_ASSOC))
		{
			$order = array();
			$order["items"] = array();
			$order["order_total"] = $orderrow["order_total"];
			$order["status"] = $orderrow["status"];
			$user_id = $orderrow["user_id"];
			$user_query = mysqli_query($read_con, "SELECT * FROM accountTable WHERE user_id='$user_id'");
			$user_info = mysqli_fetch_assoc($user_query);
			$order["username"] = $user_info["name"];
			$order["mobile"] = $user_info["mobile"];
			$order["order_id"] = $orderrow["order_id"];
			$order_id = $order["order_id"];
			//$order["delivery"] = $orderrow["delivery"];
			
			$order["service"] = $orderrow["service"];
			$order["service_charge"] = $orderrow["service_charge"];
			$order["discount_amount"] = $orderrow["discount_amount"];
			$order["bill_total"] = $orderrow["bill_total"];
			
			
			$order["payment"] = $orderrow["payment"];
			$items = mysqli_query($read_con, "SELECT * FROM orderitemTable WHERE order_id='$order_id'");
			while ($row = mysqli_fetch_array($items, MYSQLI_ASSOC))
			{
				$product = array();
				$product["name"] = $row["name"];
				$product["quantity"] = $row["quantity"];
				$product["sub_total"] = $row["sub_total"];
				array_push($order["items"], $product);
			}
                   
                        if($order["service_charge"]!=0){
                        $product["name"] = "Service Charge";
			$product["quantity"] = "";
			$product["sub_total"] =$order["service_charge"];
                	array_push($order["items"], $product);             
                        }
                         
                        if($order["discount_amount"]!=0){
                        $product["name"] = "Discounts";
			$product["quantity"] = "";
			$product["sub_total"] ='-'.$order["discount_amount"];
               		array_push($order["items"], $product);             
                        }
			array_push($response["orders"], $order);
		}

		$response["message"] = "Orders Read";
		echo json_encode($response);
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No Orders";
		echo json_encode($response);
	}
}

function searchpastordersbyusername($json_array)
{
	global $read_con;
	$response = array();
	$name = $json_array["name"];
	$result = mysqli_query($read_con, "SELECT * FROM ordersTable WHERE user_name LIKE '%{$name}%' ORDER BY order_id");
	if (mysqli_num_rows($result) > 0)
	{
		$response["success"] = 1;
		$response["orders"] = array();
		while ($orderrow = mysqli_fetch_array($result, MYSQLI_ASSOC))
		{
			$order = array();
			$order["items"] = array();
			$order["order_total"] = $orderrow["order_total"];
			$order["status"] = $orderrow["status"];
			$user_id = $orderrow["user_id"];
			$user_query = mysqli_query($read_con, "SELECT * FROM accountTable WHERE user_id='$user_id'");
			$user_info = mysqli_fetch_assoc($user_query);
			$order["username"] = $user_info["name"];
			$order["mobile"] = $user_info["mobile"];
			$order["order_id"] = $orderrow["order_id"];
			$order_id = $order["order_id"];
			//$order["delivery"] = $orderrow["delivery"];
			
			$order["service"] = $orderrow["service"];
			$order["service_charge"] = $orderrow["service_charge"];
			$order["discount_amount"] = $orderrow["discount_amount"];
			$order["bill_total"] = $orderrow["bill_total"];
			
			$order["payment"] = $orderrow["payment"];
			$items = mysqli_query($read_con, "SELECT * FROM orderitemTable WHERE order_id='$order_id'");
			while ($row = mysqli_fetch_array($items, MYSQLI_ASSOC))
			{
				$product = array();
				$product["name"] = $row["name"];
				$product["quantity"] = $row["quantity"];
				$product["sub_total"] = $row["sub_total"];
				array_push($order["items"], $product);
			}
                       
                        if($order["service_charge"]!=0){
                        $product["name"] = "Service Charge";
			$product["quantity"] = "";
			$product["sub_total"] =$order["service_charge"];
                	array_push($order["items"], $product);             
                        }
                         
                        if($order["discount_amount"]!=0){
                        $product["name"] = "Discounts";
			$product["quantity"] = "";
			$product["sub_total"] ='-'.$order["discount_amount"];
               		array_push($order["items"], $product);             
                        }
			array_push($response["orders"], $order);
		}

		$response["message"] = "Orders Read";
		echo json_encode($response);
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "No Orders";
		echo json_encode($response);
	}
}

?>
