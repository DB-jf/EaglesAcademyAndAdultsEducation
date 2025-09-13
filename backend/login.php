<?php
require 'connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = $_POST['username'];  // or username
    $password = $_POST['password'];

    // Get user
    $stmt = $conn->prepare("SELECT * FROM users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user && password_verify($password, $user['password_hash'])) {
        session_start();
        $_SESSION['user_id'] = $user['user_id'];
        $_SESSION['role'] = $user['role'];

        // Redirect by role
        if ($user['role'] === 'admin') {
            header("Location: admin_dashboard.php");
        } else {
            header("Location: student_portal.php");
        }
        exit;
    } else {
        echo "Invalid credentials.";
    }
}
?>
