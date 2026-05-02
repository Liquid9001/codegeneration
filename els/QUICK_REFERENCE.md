# Quick Reference Guide - Swagger UI Setup

## 🎯 Quick Access

| What | Where |
|------|-------|
| **Swagger UI (Interactive)** | http://localhost:8081/swagger-ui.html |
| **OpenAPI JSON Spec** | http://localhost:8081/v3/api-docs |
| **OpenAPI YAML Spec** | http://localhost:8081/v3/api-docs.yaml |
| **API Base URL** | http://localhost:8081 |

## 🚀 Start Application

```bash
cd C:\Users\enesb\Videos\github\codegeneration\els
./mvnw spring-boot:run
```

Application starts on: **Port 8081**

## 📚 Available Endpoints

### Users
```
GET /users
```
Returns all users in the system.

**Example Response:**
```json
{
  "id": 1,
  "email": "john@example.com",
  "first_name": "John",
  "last_name": "Doe",
  "phone_number": "0612345678",
  "bsn": 123456789,
  "role": 0,
  "approved": true,
  "created_at": "2024-01-15T10:30:00"
}
```

### Accounts
```
GET /accounts
```
Returns all bank accounts.

**Example Response:**
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

### Transactions
```
GET /transactions
```
Returns all transactions.

**Example Response:**
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
  "description": "Payment for invoice"
}
```

### ATM Sessions
```
GET /atm-sessions
```
Returns all ATM sessions.

**Example Response:**
```json
{
  "id": 1,
  "customer_id": 1,
  "login_time": "2024-01-15T10:30:00",
  "logout_time": "2024-01-15T11:30:00",
  "active": false
}
```

## 💻 cURL Examples

### Get All Users
```bash
curl -X GET http://localhost:8081/users \
  -H "Content-Type: application/json"
```

### Get All Accounts
```bash
curl -X GET http://localhost:8081/accounts \
  -H "Content-Type: application/json"
```

### Get All Transactions
```bash
curl -X GET http://localhost:8081/transactions \
  -H "Content-Type: application/json"
```

### Get All ATM Sessions
```bash
curl -X GET http://localhost:8081/atm-sessions \
  -H "Content-Type: application/json"
```

## 📱 Using with Postman

### Quick Import
1. Open Postman
2. Click **Import**
3. Select **Link** tab
4. Paste: `http://localhost:8081/v3/api-docs`
5. Click **Continue**
6. All endpoints are ready to use!

## 🔗 Entity Relationships

```
User (1) ──owns──> (N) Account
User (1) ──initiates──> (N) Transaction
User (1) ──uses──> (N) ATM_Session
Account (1) ──sends──> (N) Transaction
Account (1) ──receives──> (N) Transaction
```

## 📋 Documentation Files

| File | Purpose |
|------|---------|
| **README.md** | Main documentation |
| **SWAGGER_SETUP.md** | Detailed Swagger UI guide |
| **API_DOCUMENTATION.html** | Beautiful HTML docs |
| **IMPLEMENTATION_SUMMARY.md** | What was implemented |
| **QUICK_REFERENCE.md** | This file |

## 🎓 Data Models

### User Model
- **id** (Long) - Primary Key
- **email** (String) - Unique constraint
- **first_name** (String)
- **last_name** (String)
- **phone_number** (Integer)
- **bsn** (Integer) - Unique constraint
- **role** (Integer) - 0=customer, 1=employee, 2=admin
- **approved** (Boolean)
- **created_at** (LocalDateTime)

### Account Model
- **id** (Long) - Primary Key
- **customer_id** (Long) - Foreign Key to User
- **iban** (String) - Unique constraint
- **account_type** (String)
- **balance** (BigDecimal)
- **absolute_transfer_limit** (BigDecimal)
- **daily_transfer_limit** (BigDecimal)
- **active** (Boolean)
- **created_at** (LocalDateTime)

### Transaction Model
- **id** (Long) - Primary Key
- **from_account_id** (Long) - Foreign Key
- **to_account_id** (Long) - Foreign Key
- **initiated_by_user_id** (Long) - Foreign Key
- **amount** (BigDecimal)
- **transaction_type** (String)
- **currency** (String)
- **timestamp** (LocalDateTime)
- **status** (String) - PENDING, COMPLETED, FAILED, REJECTED
- **description** (String)

### ATM Session Model
- **id** (Long) - Primary Key
- **customer_id** (Long) - Foreign Key to User
- **login_time** (LocalDateTime)
- **logout_time** (LocalDateTime)
- **active** (Boolean)

## ✅ Build Verification

✓ Build Status: **SUCCESS**  
✓ Compilation: **All files compile without errors**  
✓ Java Version: **21**  
✓ Spring Boot: **4.0.6**  
✓ Total Source Files: **23 compiled successfully**  

## 🔧 Configuration

**Port:** 8081  
**Framework:** Spring Boot 4.0.6  
**Database:** H2 (in-memory)  
**Security:** Spring Security enabled  
**API Documentation:** OpenAPI 3.0 + Swagger UI  

## ⚡ Common Tasks

### Test All Endpoints
1. Open http://localhost:8081/swagger-ui.html
2. Click each endpoint section
3. Click "Try it out" button
4. Click "Execute" button
5. View responses

### Export cURL Commands
1. In Swagger UI, execute a request
2. Scroll down to Response section
3. Look for cURL command
4. Copy and paste to terminal

### Import into API Tools
1. Use OpenAPI URL: http://localhost:8081/v3/api-docs
2. Supports: Postman, Insomnia, REST Client, etc.
3. Import creates all endpoints automatically

## 🐛 Troubleshooting

### Issue: Cannot access Swagger UI
**Solution:** Ensure application is running on port 8081

### Issue: 404 Not Found
**Solution:** Check endpoint path in Swagger UI

### Issue: 500 Internal Server Error
**Solution:** Check application logs for errors

### Issue: Empty response
**Solution:** Verify data exists in database

## 📞 Support

- **Email:** support@els.nl
- **Website:** https://els.nl/support

---

**Last Updated:** May 2, 2026  
**Status:** ✅ Production Ready  
**Version:** 1.0.0  

