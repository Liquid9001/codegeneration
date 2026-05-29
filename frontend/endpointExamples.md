APPROVING USERS AS AN EMPLOYEE / ADMIN, sets user.approved to true AND creates their accounts
example url: http://localhost:8080/users/3/approve

example json:
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

example result:
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