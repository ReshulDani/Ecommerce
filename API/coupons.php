<?php

$configs = include('config.php');
$read_con = mysqli_connect($configs["mysql_host"],$configs["user_read"], $configs["password_read"],$configs["database_name"]);

// Check connection
if (mysqli_connect_errno()) {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

$json       = file_get_contents('php://input');
$json_array = json_decode($json, true);

if ($json_array["action"] == "applyCoupon") {
    applycoupon($json_array);
}

function applycoupon($json_array)
{
    global $read_con;
    $response = array();
    $code     = $json_array["code"];
    
    $coupon_query = mysqli_query($read_con, "SELECT * FROM couponsTable WHERE code ='$code'");
    if (mysqli_num_rows($coupon_query) == 1) {
        $coupon = mysqli_fetch_array($coupon_query, MYSQLI_ASSOC);
        if ($coupon["is_active"] == 1) {
            $type = $coupon["type"];
            switch ($type) {
                case 1:
                    //BUY above 'min_requirement' GET 'value' off
                    $response["reqItemId"]  = 0;
                    $response["reqAmount"]  = $coupon["min_requirement"];
                    $response["getItemId"]  = 0;
                    $response["getAmount"]  = $coupon["value"];
                    $response["getPercent"] = 0;
                    break;
                case 2:
                    //BUY above 'min_requirement' GET 'value' % off
                    $response["reqItemId"]  = 0;
                    $response["reqAmount"]  = $coupon["min_requirement"];
                    $response["getItemId"]  = 0;
                    $response["getAmount"]  = 0;
                    $response["getPercent"] = $coupon["value"];
                    break;
                case 3:
                    //BUY above 'min_requirement' GET 'value' item_id FREE
                    $response["reqItemId"]  = 0;
                    $response["reqAmount"]  = $coupon["min_requirement"];
                    $response["getItemId"]  = $coupon["value"];
                    $response["getAmount"]  = 0;
                    $response["getPercent"] = 0;
                    break;
                case 4:
                    //BUY 'min_requirement' item_id GET 'value' item_id FREE
                    $response["reqItemId"]  = $coupon["min_requirement"];
                    $response["reqAmount"]  = 0;
                    $response["getItemId"]  = $coupon["value"];
                    $response["getAmount"]  = 0;
                    $response["getPercent"] = 0;
                    break;
                case 5:
                    //GET 'value' off on item_id 'min_requirement'
                    $response["reqItemId"]  = $coupon["min_requirement"];
                    $response["reqAmount"]  = 0;
                    $response["getItemId"]  = 0;
                    $response["getAmount"]  = $coupon["value"];
                    $response["getPercent"] = 0;
                    break;

            }
            $response["type"]    = $type;
            $response["success"] = 1;
        } else {
            $response["message"] = "Coupon Inactive";
            $response["success"] = 0;
            
        }
    } else {
        $response["message"] = "Invalid Coupon";
        $response["success"] = 0;
    }
    
    echo json_encode($response);
}

?>	
