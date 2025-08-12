<?php
/**
 * Eagles Academy and Adults Education - Backend API Entry Point
 * 
 * This serves as the main entry point for the backend API.
 * For now, it provides a simple API endpoint and serves the frontend.
 */

// Set CORS headers for frontend-backend communication
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Simple routing
$request_uri = $_SERVER['REQUEST_URI'];
$path = parse_url($request_uri, PHP_URL_PATH);

// Remove leading slash and 'public' from path if present
$path = ltrim($path, '/');
if (strpos($path, 'public/') === 0) {
    $path = substr($path, 7);
}

switch ($path) {
    case '':
    case 'api':
        // Basic API info endpoint
        header('Content-Type: application/json');
        echo json_encode([
            'message' => 'Eagles Academy API',
            'version' => '1.0.0',
            'status' => 'active',
            'endpoints' => [
                'GET /api' => 'API information',
                'GET /api/health' => 'Health check',
                'POST /api/enroll' => 'Student enrollment (coming soon)',
                'POST /api/contact' => 'Contact form submission (coming soon)'
            ]
        ]);
        break;
        
    case 'api/health':
        // Health check endpoint
        header('Content-Type: application/json');
        echo json_encode([
            'status' => 'healthy',
            'timestamp' => date('c'),
            'server' => $_SERVER['HTTP_HOST']
        ]);
        break;
        
    case 'api/enroll':
        // Placeholder for enrollment endpoint
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            header('Content-Type: application/json');
            echo json_encode([
                'message' => 'Enrollment endpoint - coming soon',
                'status' => 'not_implemented'
            ]);
        } else {
            http_response_code(405);
            echo json_encode(['error' => 'Method not allowed']);
        }
        break;
        
    case 'api/contact':
        // Placeholder for contact form endpoint
        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            header('Content-Type: application/json');
            echo json_encode([
                'message' => 'Contact form endpoint - coming soon',
                'status' => 'not_implemented'
            ]);
        } else {
            http_response_code(405);
            echo json_encode(['error' => 'Method not allowed']);
        }
        break;
        
    default:
        // 404 for unknown API endpoints
        http_response_code(404);
        header('Content-Type: application/json');
        echo json_encode([
            'error' => 'Endpoint not found',
            'path' => $path
        ]);
        break;
}
?>