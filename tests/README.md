# Tests

This directory contains integration and end-to-end tests for the Eagles Academy application.

## Test Structure

```
tests/
├── integration/        # Integration tests
├── e2e/               # End-to-end tests
├── api/               # API testing
└── frontend/          # Frontend testing
```

## Running Tests

### Integration Tests
```bash
# Run all integration tests
./run-integration-tests.sh

# Run specific test suite
./run-integration-tests.sh --suite=enrollment
```

### End-to-End Tests
```bash
# Run E2E tests
./run-e2e-tests.sh

# Run E2E tests with specific browser
./run-e2e-tests.sh --browser=chrome
```

### API Tests
```bash
# Test backend API endpoints
./test-api.sh

# Test specific API endpoints
./test-api.sh --endpoint=enrollment
```

## Test Development

### Adding New Tests

1. Choose appropriate test type (unit, integration, e2e)
2. Create test files following naming conventions
3. Include both positive and negative test cases
4. Document test purposes and expected outcomes

### Test Naming Conventions

- Integration tests: `test_*.py` or `*_test.js`
- E2E tests: `*.e2e.js` or `*.spec.js`
- API tests: `api_*.py` or `*_api_test.js`

### Test Data

- Use test-specific data that doesn't affect production
- Clean up test data after test completion
- Use factories or fixtures for consistent test data

## Continuous Integration

Tests are automatically run in CI/CD pipeline:
- On pull requests
- On pushes to main branch
- Scheduled daily runs

## Future Test Implementation

- [ ] Frontend unit tests with Jest
- [ ] Backend unit tests with PHPUnit
- [ ] API integration tests
- [ ] End-to-end tests with Playwright
- [ ] Performance tests
- [ ] Security tests
- [ ] Accessibility tests