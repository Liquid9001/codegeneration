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
		"accountType": "SAVINGS",
		"absoluteTransferLimit": "5000.00",
		"dailyTransferLimit": "500.00"
	},
	"checkingAccount": {
		"accountType": "CHECKING",
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
