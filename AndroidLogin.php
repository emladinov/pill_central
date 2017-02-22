<?php
define('HOST','pillcentraldev.c9uqhrbiescc.us-east-1.rds.amazonaws.com');
define('USER','PCadmin');
define('PASS','PCadmin1');
define('DB','PillCentral');
 
$con = mysqli_connect(HOST,USER,PASS,DB);
 
$username = $_POST['username'];
$password = $_POST['password'];
 
$sql = "select * from users where username='$username' and password='$password'";
 
$res = mysqli_query($con,$sql);
 
$check = mysqli_fetch_array($res);
 
if(isset($check)){
echo 'success';
}else{
echo 'failure';
}
 
mysqli_close($con);
?>