# ELS - Electronic Banking System API

## Overview

The Electronic Banking System (ELS) API is a comprehensive RESTful web service that manages banking operations including users, accounts, transactions, and ATM sessions. The API is fully documented with interactive Swagger UI for easy exploration and testing.

## Quick Start

### Running the Application

1. **Start the application:**
   ```bash
   cd C:\Users\enesb\Videos\github\codegeneration\els
   ./mvnw spring-boot:run
   ```

2. **Access the Swagger UI:**
   - Open your browser and navigate to: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

3. **Access the OpenAPI specification:**
   - JSON format: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)
   - YAML format: [http://localhost:8081/v3/api-docs.yaml](http://localhost:8081/v3/api-docs.yaml)

## API Endpoints

### Users (`/users`)
Manage user accounts and authentication in the system.

- **GET /users** - Retrieve all users
  - Response: List of UserDTO objects
  - Status codes: 200 (Success), 500 (Server Error)

### Accounts (`/accounts`)
Manage bank accounts with balance and transfer limit tracking.

- **GET /accounts** - Retrieve all accounts
  - Response: List of AccountDTO objects
  - Status codes: 200 (Success), 500 (Server Error)

### Transactions (`/transactions`)
Track and retrieve all financial transactions.

- **GET /transactions** - Retrieve all transactions
  - Response: List of TransactionDTO objects
  - Status codes: 200 (Success), 500 (Server Error)

### ATM Sessions (`/atm-sessions`)
Monitor ATM login sessions and user activities.

- **GET /atm-sessions** - Retrieve all ATM sessions
  - Response: List of AtmSessionDTO objects
  - Status codes: 200 (Success), 500 (Server Error)

## Data Models

### User
Represents a user in the ELS system.

**Fields:**
- `id` (Long): Unique identifier
- `email` (String): Email address (unique constraint)
- `first_name` (String): User's first name
- `last_name` (String): User's last name
- `phone_number` (Integer): Contact phone number
- `bsn` (Integer): Dutch BSN (Burgerservicenummer)
- `role` (Integer): User role (0=customer, 1=employee, 2=admin)
- `approved` (Boolean): Approval status
- `created_at` (LocalDateTime): Account creation timestamp

### Account
Represents a bank account.

**Fields:**
- `id` (Long): Unique identifier
- `customer_id` (Long): Owner of the account
- `iban` (String): International Bank Account Number (unique constraint)
- `account_type` (String): Type of account (SAVINGS, CHECKING, etc.)
- `balance` (BigDecimal): Current balance
- `absolute_transfer_limit` (BigDecimal): Max transfer per transaction
- `daily_transfer_limit` (BigDecimal): Max transfers per day
- `active` (Boolean): Account status
- `created_at` (LocalDateTime): Account creation timestamp

### Transaction
Represents a financial transaction.

**Fields:**
- `id` (Long): Unique identifier
- `from_account_id` (Long): Source account
- `to_account_id` (Long): Destination account
- `initiated_by_user_id` (Long): User who initiated the transaction
- `amount` (BigDecimal): Transaction amount
- `transaction_type` (String): Type (TRANSFER, DEPOSIT, WITHDRAWAL, etc.)
- `currency` (String): Currency code (EUR, USD, etc.)
- `timestamp` (LocalDateTime): Transaction time
- `status` (String): Status (PENDING, COMPLETED, FAILED, REJECTED)
- `description` (String): Transaction notes

### ATM Session
Represents an ATM login session.

**Fields:**
- `id` (Long): Unique identifier
- `customer_id` (Long): User who logged in
- `login_time` (LocalDateTime): Login timestamp
- `logout_time` (LocalDateTime): Logout timestamp
- `active` (Boolean): Current session status

## Entity Relationships

```
User (1) ──owns──> (N) Account
User (1) ──initiates──> (N) Transaction
User (1) ──uses──> (N) ATM_Session
Account (1) ──sends──> (N) Transaction
Account (1) ──receives──> (N) Transaction
```

## Configuration

The application is configured with the following settings in `application.properties`:

- **Server Port:** 8081
- **Swagger UI Path:** /swagger-ui.html
- **OpenAPI JSON Path:** /v3/api-docs
- **Database:** H2 (in-memory)
- **Security:** Spring Security enabled
- **Validation:** JSR-303 Bean Validation enabled

## Technologies Used

- **Java:** 21
- **Spring Boot:** 4.0.6
- **Spring Security:** For authentication & authorization
- **Spring Data JPA:** For database operations
- **SpringDoc OpenAPI:** For Swagger/OpenAPI documentation
- **Lombok:** For reducing boilerplate code
- **H2 Database:** For development/testing
- **Maven:** Build tool

## Testing the API

### Using Swagger UI
1. Navigate to [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
2. Click on any endpoint to expand it
3. Click "Try it out"
4. Click "Execute" to test the endpoint
5. View the response in the Response section

### Using cURL
```bash
# Get all users
curl http://localhost:8081/users

# Get all accounts
curl http://localhost:8081/accounts

# Get all transactions
curl http://localhost:8081/transactions

# Get all ATM sessions
curl http://localhost:8081/atm-sessions
```

### Using Postman
1. Import the OpenAPI specification from: http://localhost:8081/v3/api-docs
2. Create requests for each endpoint
3. Send and inspect responses

## Security Notes

- The application has Spring Security enabled
- Configure authentication and authorization as needed
- Sensitive data like passwords should never be exposed
- Use HTTPS in production environments
- Implement rate limiting for API endpoints

## Build & Deployment

### Build the Application
```bash
./mvnw clean package
```

### Run the Application
```bash
./mvnw spring-boot:run
```

### Build Docker Image (optional)
```bash
docker build -t els-api:1.0 .
docker run -p 8081:8081 els-api:1.0
```

## Documentation Links

- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8081/v3/api-docs
- **OpenAPI YAML:** http://localhost:8081/v3/api-docs.yaml
- **H2 Console:** http://localhost:8081/h2-console (if enabled)

## Example Requests

### Get Users
```bash
curl -X GET http://localhost:8081/users \
  -H "Content-Type: application/json"
```

**Response (200 OK):**
```json
[
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
]
```

## Support & Maintenance

For issues, questions, or feature requests, please contact the support team:
- **Email:** support@els.nl
- **Website:** https://els.nl/support

## License

This project is licensed under the Apache License 2.0. See the LICENSE file for details.

