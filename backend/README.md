# Eagles Academy Backend

This is the backend API for the Eagles Academy and Adults Education website.

## Stack

- **Language**: PHP 8.0+
- **Architecture**: Simple REST API
- **Entry Point**: `public/index.php`

## Setup

### Prerequisites

- PHP 8.0 or higher
- Web server (Apache/Nginx) or PHP built-in server

### Development

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Start the PHP development server:
   ```bash
   php -S localhost:8000 -t public
   ```

3. The API will be available at `http://localhost:8000`

### Production

For production deployment, configure your web server to serve files from the `public/` directory.

#### Apache Configuration

Create a `.htaccess` file in the `public/` directory:

```apache
RewriteEngine On
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule ^(.*)$ index.php [QSA,L]
```

#### Nginx Configuration

```nginx
location / {
    try_files $uri $uri/ /index.php?$query_string;
}

location ~ \.php$ {
    fastcgi_pass unix:/var/run/php/php8.0-fpm.sock;
    fastcgi_index index.php;
    fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
    include fastcgi_params;
}
```

## API Endpoints

### Base URL
- Development: `http://localhost:8000`
- Production: `https://yourdomain.com/api`

### Available Endpoints

- `GET /api` - API information and available endpoints
- `GET /api/health` - Health check endpoint
- `POST /api/enroll` - Student enrollment (placeholder)
- `POST /api/contact` - Contact form submission (placeholder)

### Example Requests

#### Health Check
```bash
curl http://localhost:8000/api/health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "2024-01-15T10:30:00+00:00",
  "server": "localhost:8000"
}
```

#### API Information
```bash
curl http://localhost:8000/api
```

## Development

### Adding New Endpoints

1. Edit `public/index.php`
2. Add new case to the switch statement
3. Implement your endpoint logic
4. Update this README with documentation

### Database Integration

When ready to add database functionality:

1. Install a database (MySQL, PostgreSQL, SQLite)
2. Add database configuration
3. Consider using a lightweight ORM or database abstraction layer
4. Update the composer.json file for dependency management

### Future Enhancements

- [ ] Database integration
- [ ] User authentication/authorization
- [ ] Student enrollment processing
- [ ] Contact form email handling
- [ ] File upload handling
- [ ] Admin dashboard API
- [ ] Payment processing integration
- [ ] Logging and monitoring
- [ ] Rate limiting
- [ ] Input validation and sanitization

## Testing

Run the test suite (when tests are added):

```bash
# PHP Unit tests (to be implemented)
./vendor/bin/phpunit tests/
```

## Security Considerations

- Implement proper input validation
- Use prepared statements for database queries
- Implement rate limiting for API endpoints
- Use HTTPS in production
- Implement proper error handling without exposing sensitive information
- Regular security updates for PHP and dependencies