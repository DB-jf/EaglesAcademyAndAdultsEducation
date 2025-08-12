<?php

namespace EaglesAcademy\Tests\Feature;

use PHPUnit\Framework\TestCase;

class ApiEndpointsTest extends TestCase
{
    private static $serverProcess;
    private string $baseUrl = 'http://localhost:8001';

    public static function setUpBeforeClass(): void
    {
        // Start PHP development server for testing
        $command = 'php -S localhost:8001 -t public > /dev/null 2>&1 & echo $!';
        $pid = shell_exec($command);
        static::$serverProcess = trim($pid);
        
        // Wait for server to start
        sleep(2);
    }

    public static function tearDownAfterClass(): void
    {
        // Stop the development server
        if (static::$serverProcess) {
            shell_exec("kill " . static::$serverProcess);
        }
    }

    public function testHealthEndpoint()
    {
        $response = $this->makeRequest('/api/health');
        
        $this->assertNotFalse($response, 'Health endpoint should be accessible');
        
        $data = json_decode($response, true);
        $this->assertIsArray($data, 'Response should be valid JSON');
        $this->assertEquals('healthy', $data['status'], 'Health status should be healthy');
        $this->assertArrayHasKey('timestamp', $data, 'Response should include timestamp');
    }

    public function testApiInfoEndpoint()
    {
        $response = $this->makeRequest('/api');
        
        $this->assertNotFalse($response, 'API info endpoint should be accessible');
        
        $data = json_decode($response, true);
        $this->assertIsArray($data, 'Response should be valid JSON');
        $this->assertEquals('Eagles Academy API', $data['message'], 'API message should match');
        $this->assertArrayHasKey('endpoints', $data, 'Response should include endpoints list');
    }

    public function testNotFoundEndpoint()
    {
        $response = $this->makeRequest('/api/nonexistent');
        
        $this->assertNotFalse($response, 'Should return response for non-existent endpoint');
        
        $data = json_decode($response, true);
        $this->assertIsArray($data, 'Response should be valid JSON');
        $this->assertArrayHasKey('error', $data, 'Should return error for non-existent endpoint');
    }

    public function testEnrollEndpointMethodNotAllowed()
    {
        $response = $this->makeRequest('/api/enroll', 'GET');
        
        $data = json_decode($response, true);
        $this->assertIsArray($data, 'Response should be valid JSON');
        $this->assertArrayHasKey('error', $data, 'Should return error for wrong method');
    }

    private function makeRequest(string $endpoint, string $method = 'GET'): string|false
    {
        $context = stream_context_create([
            'http' => [
                'method' => $method,
                'header' => 'Content-Type: application/json',
                'timeout' => 10
            ]
        ]);

        return @file_get_contents($this->baseUrl . $endpoint, false, $context);
    }
}