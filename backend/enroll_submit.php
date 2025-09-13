<?php
require 'connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $fullname = $_POST['fullname'] ?? '';
    $dob = $_POST['dob'] ?? '';
    $gender = $_POST['gender'] ?? '';
    $address = $_POST['address'] ?? '';
    $guardianName = $_POST['guardianName'] ?? '';
    $relationship = $_POST['relationship'] ?? '';
    $guardianPhone = $_POST['guardianPhone'] ?? '';
    $guardianEmail = $_POST['guardianEmail'] ?? '';
    $agree = isset($_POST['agree']) ? 1 : 0;

    // Handle photo upload
    $photoPath = '';
    if (isset($_FILES['photo']) && $_FILES['photo']['error'] === UPLOAD_ERR_OK) {
        $targetDir = "../uploads/";
        if (!is_dir($targetDir)) mkdir($targetDir, 0777, true);
        $photoPath = $targetDir . basename($_FILES['photo']['name']);
        move_uploaded_file($_FILES['photo']['tmp_name'], $photoPath);
    }

    $stmt = $conn->prepare("INSERT INTO enrollments 
      (fullname, dob, gender, address, photo, guardianName, relationship, guardianPhone, guardianEmail, agree) 
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    $result = $stmt->execute([
      $fullname, $dob, $gender, $address, $photoPath,
      $guardianName, $relationship, $guardianPhone, $guardianEmail, $agree
    ]);
    echo $result ? "success" : "error";
}
?>
