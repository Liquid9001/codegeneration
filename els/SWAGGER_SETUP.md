# Swagger UI Setup & Usage Guide

## 📋 Overview

This document provides a complete guide to accessing and using the Swagger UI for the ELS (Electronic Banking System) API.

## 🚀 Quick Start

### Step 1: Start the Application

Navigate to the project directory and run:

```bash
cd C:\Users\enesb\Videos\github\codegeneration\els
./mvnw spring-boot:run
```

Or if using jar file:

```bash
java -jar target/els-0.0.1-SNAPSHOT.jar
```

### Step 2: Access Swagger UI

Once the application is running, open your browser and navigate to:

```
http://localhost:8081/swagger-ui.html
```

You should see the interactive Swagger UI interface.

## 🎯 Using Swagger UI

### 1. **Browsing Endpoints**

The Swagger UI displays all available endpoints organized by tags:
- **Users** - User management operations
- **Accounts** - Account management operations
- **Transactions** - Transaction operations
- **ATM Sessions** - ATM session operations

### 2. **Testing an Endpoint**

#### To test any endpoint:

1. **Click on the endpoint** to expand it
2. **Click the "Try it out" button** (right side of the endpoint)
3. **Fill in any required parameters** (if applicable)
4. **Click the "Execute" button**
5. **View the response** in the Response section

### 3. **Understanding the UI Elements**

#### Response Section
After executing a request, you'll see:
- **Response body** - The JSON response from the server
- **Response headers** - HTTP headers returned
- **Response code** - HTTP status code (200, 400, 500, etc.)
- **curl command** - Equivalent curl command for the request

#### Schema Section
Each endpoint shows:
- **Parameters** - Query, path, or body parameters
- **Request body schema** - Structure of the request payload
- **Response schema** - Structure of the response

### 4. **Trying Different Endpoints**

#### Get All Users
```
GET /users
```
- No parameters required
- Returns a list of user objects

#### Get All Accounts
```
GET /accounts
```
- No parameters required
- Returns a list of account objects

#### Get All Transactions
```
GET /transactions
```
- No parameters required
- Returns a list of transaction objects

#### Get All ATM Sessions
```
GET /atm-sessions
```
- No parameters required
- Returns a list of ATM session objects

## 📄 Alternative Endpoints

### OpenAPI Specification Files

The API provides machine-readable specifications in multiple formats:

#### JSON Format
```
http://localhost:8081/v3/api-docs
```
Response format: Standard OpenAPI 3.0 JSON

#### YAML Format
```
http://localhost:8081/v3/api-docs.yaml
```
Response format: OpenAPI 3.0 YAML

### Using these files:

1. **Import into Postman**
   - Open Postman
   - Click "Import" → "Link"
   - Paste: `http://localhost:8081/v3/api-docs`
   - Postman automatically creates all endpoints

2. **Import into API Clients**
   - Most API clients support OpenAPI 3.0
   - Use the JSON or YAML endpoint URL
   - Client automatically parses and creates requests

## 🔍 Understanding the Response Models

### User DTO
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "first_name": "John",
  "last_name": "Doe",
  "phone_number": "0612345678",
  "bsn": 123456789,
  "role": 0,
  "approved": true,
  "created_at": "2024-01-15T10:30:00"
}
```

### Account DTO
```json
{
  "id": 1,
  "customer_id": 1,
  "iban": "NL91 ABNA 0417 1643 00",
  "account_type": "CHECKING",
  "balance": "1500.50",
  "absolute_transfer_limit": "5000.00",
  "daily_transfer_limit": "10000.00",
  "active": true,
  "created_at": "2024-01-15T10:30:00"
}
```

### Transaction DTO
```json
{
  "id": 1,
  "from_account_id": 1,
  "to_account_id": 2,
  "initiated_by_user_id": 1,
  "amount": "100.50",
  "transaction_type": "TRANSFER",
  "currency": "EUR",
  "timestamp": "2024-01-15T10:30:00",
  "status": "COMPLETED",
  "description": "Payment for invoice #12345"
}
```

### ATM Session DTO
```json
{
  "id": 1,
  "customer_id": 1,
  "login_time": "2024-01-15T10:30:00",
  "logout_time": "2024-01-15T11:30:00",
  "active": false
}
```

## 🛠️ Using cURL Commands

### Export from Swagger UI

When you execute a request in Swagger UI, it shows the equivalent cURL command. You can copy this command and run it in your terminal:

```bash
curl -X GET "http://localhost:8081/users" -H "accept: application/json"
```

### Common cURL Examples

#### Get All Users
```bash
curl -X GET http://localhost:8081/users \
  -H "Content-Type: application/json"
```

#### Get All Accounts
```bash
curl -X GET http://localhost:8081/accounts \
  -H "Content-Type: application/json"
```

#### Get All Transactions
```bash
curl -X GET http://localhost:8081/transactions \
  -H "Content-Type: application/json"
```

#### Get All ATM Sessions
```bash
curl -X GET http://localhost:8081/atm-sessions \
  -H "Content-Type: application/json"
```

## 📱 Using with Postman

### Option 1: Import from OpenAPI URL

1. Open Postman
2. Click **Import** button
3. Select **Link** tab
4. Enter: `http://localhost:8081/v3/api-docs`
5. Click **Continue**
6. All endpoints are automatically created

### Option 2: Manual Collection Creation

1. Create a new collection called "ELS API"
2. Add requests for each endpoint:
   - GET /users
   - GET /accounts
   - GET /transactions
   - GET /atm-sessions
3. Set the URL with base: `{{base_url}}`
4. Add environment variable: `base_url = http://localhost:8081`
5. Test each endpoint

## 🔐 Security Headers

When making requests, you may need to include authentication headers:

```bash
curl -X GET http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>"
```

## 🐛 Troubleshooting

### Issue: Cannot access http://localhost:8081/swagger-ui.html

**Solution:**
- Verify the application is running on port 8081
- Check if another application is using port 8081
- Look for error messages in the application logs
- Try accessing http://localhost:8081 directly

### Issue: 403 Forbidden Error

**Solution:**
- Check if security is enabled in the application
- Verify you have proper authentication credentials
- Check the SecurityConfig for any restrictions

### Issue: 404 Not Found Error

**Solution:**
- Verify the endpoint path is correct
- Check the Swagger UI for the correct endpoint
- Ensure the application is fully started

### Issue: Swagger UI Shows "No operations defined"

**Solution:**
- Verify Spring Boot is running with Spring Web
- Check that controllers are in the correct package structure
- Restart the application
- Clear browser cache

## 📚 API Documentation Structure

### Organized by Tags

All endpoints are organized under these tags:

1. **Users** - User management
   - GET /users

2. **Accounts** - Account management
   - GET /accounts

3. **Transactions** - Transaction operations
   - GET /transactions

4. **ATM Sessions** - ATM session management
   - GET /atm-sessions

Each tag groups related endpoints together.

## 🔗 Related Documentation

- **Main README:** README.md
- **API Documentation (HTML):** API_DOCUMENTATION.html
- **ERD Diagram:** ERD ELS.drawio

## ✨ Key Features of Swagger UI

### 1. **Interactive Testing**
- Try out endpoints directly from the browser
- No need for external tools

### 2. **Request/Response Schemas**
- See exactly what data each endpoint accepts
- Understand the response structure

### 3. **Auto-Generated Documentation**
- Always synchronized with your code
- Changes are reflected immediately

### 4. **cURL Command Export**
- Copy-paste ready commands
- Perfect for scripting

### 5. **Example Values**
- Each field shows example data
- Helps understand data types and formats

## 📝 Configuration File Locations

- **OpenAPI Config:** `/src/main/java/nl/codegeneratie/els/config/OpenApiConfig.java`
- **Application Properties:** `/src/main/resources/application.properties`
- **Controllers:** `/src/main/java/nl/codegeneratie/els/controller/`
- **DTOs:** `/src/main/java/nl/codegeneratie/els/dtos/`

## 🎓 Learning Resources

### OpenAPI/Swagger Learning
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [Swagger Documentation](https://swagger.io/docs/)
- [SpringDoc OpenAPI](https://springdoc.org/)

### Spring Boot Learning
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)

## 🤝 Support

For issues, questions, or improvements:
- Email: support@els.nl
- Website: https://els.nl/support

---

**Last Updated:** May 2, 2026  
**Version:** 1.0.0  
**License:** Apache License 2.0

