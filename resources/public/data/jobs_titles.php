<?php
$servername = "localhost";
$username = "root";
$password = "root";
$dbname = "harikais";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
$conn->set_charset("utf8");
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT * FROM titles";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    $a = [];
    while($row = $result->fetch_assoc()) {
        array_push($a, $row['title']);
    };
    echo json_encode($a,JSON_UNESCAPED_UNICODE);
} else {
    echo "0 results";
}
$conn->close();
?>