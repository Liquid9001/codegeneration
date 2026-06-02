[POST] LOGGING IN AS A USER, returns token
example url: http://localhost:8080/users/login

example json request: 
{
	"email": "michael.johnson@example.com",
	"password": "password123"
}

example json response:
{
	"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsInVzZXJJZCI6NSwicm9sZSI6IkFETUlOIiwiYXBwcm92ZWQiOnRydWUsImlhdCI6MTc4MDA4NDU5MCwiZXhwIjoxNzgwMTcwOTkwfQ.WWR9MCo31lACUU5Fw-HDsQwdyG2FPojd049DkD-E8jA"
}

[POST] APPROVING USERS AS AN EMPLOYEE / ADMIN, sets user.approved to true AND creates their accounts
example url: http://localhost:8080/users/3/approve

example json request:
{
	"savingsAccount": {
		"absoluteTransferLimit": "5000.00",
		"dailyTransferLimit": "500.00"
	},
	"checkingAccount": {
		"absoluteTransferLimit": "5000.00",
		"dailyTransferLimit": "500.00"		
	}
}

example json response:
{
	"accounts": [
		{
			"absoluteTransferLimit": 5000.00,
			"accountType": "CHECKING",
			"active": true,
			"balance": 0.00,
			"createdAt": "2026-05-29T22:51:03.626022",
			"customerId": null,
			"dailyTransferLimit": 500.00,
			"iban": "NL95ELSE27749D536E5",
			"id": 5
		},
		{
			"absoluteTransferLimit": 5000.00,
			"accountType": "SAVINGS",
			"active": true,
			"balance": 0.00,
			"createdAt": "2026-05-29T22:51:03.627022",
			"customerId": null,
			"dailyTransferLimit": 500.00,
			"iban": "NL97ELS573674975110",
			"id": 6
		}
	],
	"approved": true,
	"bsn": 555666777,
	"createdAt": "2024-01-17T09:15:00",
	"email": "michael.johnson@example.com",
	"firstName": "Michael",
	"id": 3,
	"lastName": "Johnson",
	"password": null,
	"phoneNumber": 698765432,
	"role": "CUSTOMER"
}

[GET] RETRIEVING user info (requires admin or employee, or requires authorizing with jwt token as the user)
URL: http://localhost:8080/users/{userid} 

example response: {
	"accounts": [
		{
			"absoluteTransferLimit": 10000.00,
			"accountType": "CHECKING",
			"active": true,
			"balance": 5000.00,
			"createdAt": "2024-01-15T10:30:00",
			"customerId": null,
			"dailyTransferLimit": 20000.00,
			"iban": "NL91ABNA0417164300",
			"id": 1
		},
		{
			"absoluteTransferLimit": 5000.00,
			"accountType": "SAVINGS",
			"active": true,
			"balance": 15000.00,
			"createdAt": "2024-02-01T14:20:00",
			"customerId": null,
			"dailyTransferLimit": 10000.00,
			"iban": "NL47ABNA0123456789",
			"id": 2
		}
	],
	"approved": true,
	"bsn": 123456789,
	"createdAt": "2024-01-15T10:30:00",
	"email": "john.doe@example.com",
	"firstName": "John",
	"id": 1,
	"lastName": "Doe",
	"password": null,
	"phoneNumber": 612345678,
	"role": "CUSTOMER"
}

[GET] RETRIEVING all users (only admin or employee)
URL: http://localhost:8080/users

additional parameters:
offset
limit

example response (no parameters):
[
	{
		"accounts": [
			{
				"absoluteTransferLimit": 10000.00,
				"accountType": "CHECKING",
				"active": true,
				"balance": 5000.00,
				"createdAt": "2024-01-15T10:30:00",
				"customerId": null,
				"dailyTransferLimit": 20000.00,
				"iban": "NL91ABNA0417164300",
				"id": 1
			},
			{
				"absoluteTransferLimit": 5000.00,
				"accountType": "SAVINGS",
				"active": true,
				"balance": 15000.00,
				"createdAt": "2024-02-01T14:20:00",
				"customerId": null,
				"dailyTransferLimit": 10000.00,
				"iban": "NL47ABNA0123456789",
				"id": 2
			}
		],
		"approved": true,
		"bsn": 123456789,
		"createdAt": "2024-01-15T10:30:00",
		"email": "john.doe@example.com",
		"firstName": "John",
		"id": 1,
		"lastName": "Doe",
		"password": null,
		"phoneNumber": 612345678,
		"role": "CUSTOMER"
	},
	{
		"accounts": [
			{
				"absoluteTransferLimit": 10000.00,
				"accountType": "CHECKING",
				"active": true,
				"balance": 8500.50,
				"createdAt": "2024-01-16T14:45:00",
				"customerId": null,
				"dailyTransferLimit": 20000.00,
				"iban": "NL61ABNA0123456790",
				"id": 3
			}
		],
		"approved": true,
		"bsn": 987654321,
		"createdAt": "2024-01-16T14:45:00",
		"email": "jane.smith@example.com",
		"firstName": "Jane",
		"id": 2,
		"lastName": "Smith",
		"password": null,
		"phoneNumber": 621234567,
		"role": "CUSTOMER"
	},
	{
		"accounts": [],
		"approved": false,
		"bsn": 555666777,
		"createdAt": "2024-01-17T09:15:00",
		"email": "michael.johnson@example.com",
		"firstName": "Michael",
		"id": 3,
		"lastName": "Johnson",
		"password": null,
		"phoneNumber": 698765432,
		"role": "CUSTOMER"
	},
	{
		"accounts": [
			{
				"absoluteTransferLimit": 50000.00,
				"accountType": "SAVINGS",
				"active": true,
				"balance": 50000.00,
				"createdAt": "2024-01-18T11:30:00",
				"customerId": null,
				"dailyTransferLimit": 100000.00,
				"iban": "NL81ABNA0123456792",
				"id": 4
			}
		],
		"approved": true,
		"bsn": 111222333,
		"createdAt": "2024-01-18T11:30:00",
		"email": "sara.williams@example.com",
		"firstName": "Sara",
		"id": 4,
		"lastName": "Williams",
		"password": null,
		"phoneNumber": 643210987,
		"role": "EMPLOYEE"
	},
	{
		"accounts": [],
		"approved": true,
		"bsn": 999999999,
		"createdAt": "2024-01-01T08:00:00",
		"email": "admin@example.com",
		"firstName": "Admin",
		"id": 5,
		"lastName": "User",
		"password": null,
		"phoneNumber": 600000000,
		"role": "ADMIN"
	}
]

Example decoded JWT token (eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsInVzZXJJZCI6NSwicm9sZSI6IkFETUlOIiwiYXBwcm92ZWQiOnRydWUsImlhdCI6MTc4MDA4NDU5MCwiZXhwIjoxNzgwMTcwOTkwfQ.WWR9MCo31lACUU5Fw-HDsQwdyG2FPojd049DkD-E8jA) : 
{
  "sub": "admin@example.com",
  "userId": 5,
  "role": "ADMIN",
  "approved": true,
  "iat": 1780084590,
  "exp": 1780170990
}

role could be "ADMIN", "CHECKING", "SAVINGS"

[POST] Registering user 
URL: http://localhost:8080/users

example request:
{
  "email": "john.doe@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": 612345678,
  "bsn": 123456789,
  "role": "CUSTOMER"
}

example response:
{
  "id": 1,
  "email": "john.doe@example.com",
  "password": null,
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": 612345678,
  "bsn": 123456789,
  "role": "CUSTOMER",
  "approved": false,
  "createdAt": "2026-05-30T15:42:18.123"
}

[GET] Get bank account details
Example URL: http://localhost:8080/accounts/1
example response:
{
	"absoluteTransferLimit": 10000.00,
	"accountType": "CHECKING",
	"active": true,
	"balance": 5000.00,
	"createdAt": "2024-01-15T10:30:00",
	"customerId": null,
	"dailyTransferLimit": 20000.00,
	"iban": "NL91ABNA0417164300",
	"id": 1
}