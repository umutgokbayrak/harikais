<?php
$aervername = "localhost";
$username = "root";
$password = "root";
$dbname = "harikais";

// Create connection
$conn = new mysqli($aervername, $username, $password, $dbname);
$conn->set_charset("utf8");
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$aql = "SELECT * FROM schools";
$result = $conn->query($aql);

if ($result->num_rows > 0) {
    // output data of each row
    $a = [];
    while($row = $result->fetch_assoc()) {
        array_push($a, $row['school']);
    };
    echo json_encode($a,JSON_UNESCAPED_UNICODE);
} else {
    echo "0 results";
}
$conn->close();
?>