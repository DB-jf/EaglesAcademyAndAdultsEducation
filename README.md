# Eagles Academy and Adults Education

A modern web application for Eagles Academy and Adults Education complex, featuring a responsive frontend and PHP backend API.

## 🏗️ Architecture

This project follows a clean separation between frontend and backend:

- **Frontend**: Static HTML/CSS/JavaScript website
- **Backend**: PHP REST API for dynamic functionality

## 📁 Project Structure

```
├── frontend/           # Frontend web application
│   ├── index.html     # Main homepage
│   ├── signin.html    # Student sign-in page
│   ├── enroll.html    # Enrollment form
│   ├── buy-pin.html   # PIN purchase page
│   ├── StyleSheet.css # Main stylesheet
│   └── images/        # Image assets
├── backend/           # PHP backend API
│   ├── public/        # Web-accessible files
│   │   └── index.php  # API entry point
│   ├── src/           # PHP source code (future)
│   ├── tests/         # Backend tests
│   ├── composer.json  # PHP dependencies
│   └── README.md      # Backend documentation
├── docs/              # Project documentation
├── tests/             # Integration tests
├── .github/           # GitHub workflows
│   └── workflows/
│       └── php-ci.yml # PHP CI/CD pipeline
└── .gitignore         # Git ignore rules
```

## 🚀 Quick Start

### Frontend Development

The frontend is a static website that can be served directly:

1. Navigate to the frontend directory:
   ```bash
   cd frontend/
   ```

2. Serve with any static web server:
   ```bash
   # Using Python
   python -m http.server 3000
   
   # Using Node.js (if you have http-server installed)
   npx http-server -p 3000
   
   # Using PHP
   php -S localhost:3000
   ```

3. Open `http://localhost:3000` in your browser

### Backend Development

The backend provides REST API endpoints:

1. Navigate to the backend directory:
   ```bash
   cd backend/
   ```

2. Install PHP dependencies (optional for basic functionality):
   ```bash
   composer install
   ```

3. Start the development server:
   ```bash
   php -S localhost:8000 -t public
   ```

4. Test the API:
   ```bash
   curl http://localhost:8000/api/health
   ```

## 🛠️ Technology Stack

### Frontend
- **HTML5** - Semantic markup
- **CSS3** - Modern styling with flexbox/grid
- **JavaScript** - Interactive functionality
- **Google Fonts** - Typography (Inter)

### Backend
- **PHP 8.0+** - Server-side logic
- **Composer** - Dependency management
- **REST API** - JSON-based API endpoints

### DevOps
- **GitHub Actions** - CI/CD pipeline
- **PHPUnit** - Backend testing
- **PHP CodeSniffer** - Code style checking

## 📖 Documentation

- [Backend API Documentation](backend/README.md)
- [Frontend Documentation](docs/frontend.md) *(coming soon)*
- [Deployment Guide](docs/deployment.md) *(coming soon)*

## 🧪 Testing

### Backend Tests
```bash
cd backend/
composer test
```

### API Testing
```bash
# Health check
curl http://localhost:8000/api/health

# API information
curl http://localhost:8000/api
```

## 🚀 Deployment

### Frontend Deployment

The frontend can be deployed to any static hosting service:
- GitHub Pages
- Netlify
- Vercel
- AWS S3 + CloudFront
- Traditional web hosting

### Backend Deployment

The backend can be deployed to any PHP hosting environment:
- Shared hosting with PHP support
- VPS with Apache/Nginx + PHP-FPM
- Cloud platforms (AWS, Google Cloud, Azure)
- Containerized deployment with Docker

## 📝 Features

- **Responsive Design** - Works on all devices
- **Modern UI/UX** - Clean and professional interface
- **Student Enrollment** - Online registration system
- **PIN Purchase** - Integrated payment system *(coming soon)*
- **User Authentication** - Secure sign-in system *(coming soon)*
- **Admin Dashboard** - Management interface *(coming soon)*

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

For more information about Eagles Academy and Adults Education, please visit our website or contact us through the contact form.
