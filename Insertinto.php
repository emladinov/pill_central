<?php
	//This will return all of the floor information for a specific lot.
	//The link for this file will take the lot ID as its input.

    $host = "pillcentraldev.c9uqhrbiescc.us-east-1.rds.amazonaws.com";
	$username = "PCadmin";
	$password = "PCadmin1";
	$dbname = "PillCentral";


    $con=mysqli_connect($host,$username,$password,$dbname);

	if (mysqli_connect_errno())
	{
		echo "Failed to connect to MySQL: " . mysqli_connect_error();
	}

	$Username = $_GET['username'];
	$Password = $_GET['password'];
	$Email = $_GET['email'];
	$sql = "Insert login(username, password, email) Values ('".$Username."','".$Password."','".$Email."');";

	
	if ($result = mysqli_query($con, $sql))
	{
		// If so, then create a results array and a temporary one
        // to hold the data
        $resultArray = array();
        $tempArray = array();

        // Loop through each row in the result set
        while($row = $result->fetch_object())
        {
            // Add each row into our results array
            $tempArray = $row;
            array_push($resultArray, $tempArray);
        }
        // Finally, encode the array to JSON and output the results
        echo json_encode($resultArray);
	}
	mysqli_close($con); 
?>