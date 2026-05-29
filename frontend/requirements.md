# Banking System Requirements

## 🧑‍💻 Developer

### Requirements
- Must write backend code using **Java and Spring Boot**
- Must persist data using a database (in-memory DB allowed for POC)
- Must use a frontend JavaScript framework Specifically Vue SPA, Bootstrap, Pinia, Vite
- Frontend must communicate with backend via **REST API only**
- Application must be tested and stable (bug-free expectation)
- Must include **unit tests (JUnit)**

### Deployment
- Backend deployed to platforms such as **Render.com**
- Frontend deployed to **GitHub Pages**
- Automated deployments are preferred

### Additional Constraints
- All backend code must use Java + Spring Boot
- REST API architecture must be followed
- Functional + unit testing required
- UI frameworks (Vue/React) require approval if different
- Must contribute meaningfully to both frontend and backend

---

## 🔌 API Consumer

### User Stories
- Can view complete API documentation
- Must authenticate using **JWT**

### System Requirements
- API documentation provided via **Swagger UI**
- APIs must follow REST best practices
- All protected routes require JWT authentication

---

## 👤 User

### User Stories
- Can log in securely and access resources (authentication + authorization)
- Can view all application interfaces with good UX

### System Requirements
- Login via username/email + password
- Role-based access control (employee or customer)
- All APIs secured properly
- JWT issued upon login
- UI should be functional and user-friendly
- CSS/UI framework recommended for development efficiency

---

## 🧑‍💼 Customer

### User Stories
- Can register an account
- Can transfer funds between own accounts
- Can find IBAN of another customer by name
- Can transfer funds to other customers
- Can view account details
- Can view transaction history
- Can log in to ATM
- Can withdraw money from ATM
- Can deposit money into ATM
- Can search and filter transactions

### System Rules & Constraints
- Registration requires:
  - First name
  - Last name
  - Email
  - BSN number
  - Phone number
- No bank account is created until employee approval
- Validation required (e.g., duplicate email handling)
- After registration, user sees a basic welcome page until approval
- Customers can transfer between checking and savings accounts
- Customers can transfer to other customers (checking → checking)
- Transaction limits enforced (absolute + daily limits)
- All transactions are recorded
- ATM uses email + password authentication
- Account balances are updated after each transaction
- Transaction filtering supported:
  - Date range (start/end)
  - Amount comparisons (<, >, =)
  - IBAN-based filtering
- Transaction history is paginated

---

## 🧑‍🏫 Employee

### User Stories
- Can view all customer accounts
- Can view customer transaction history
- Can transfer funds between customer accounts
- Can view customers without accounts
- Can approve customer signups and create accounts
- Can close customer accounts
- Can set absolute transfer limits
- Can set daily transfer limits
- Can view all system transactions

### System Rules & Constraints
- Customer data is paginated
- Account creation includes:
  - Both checking and savings accounts
- IBAN format must follow:
  - `NLxxINHOxxxxxxxx`
- Each account must have a unique IBAN
- Transfer limits are enforced and editable
- Transactions cannot violate absolute limits
- Daily transfer limits are strictly enforced
- All transactions are logged:
  - ATM transactions
  - Employee actions
  - Customer transfers
- Transaction data includes:
  - From account
  - To account
  - Amount
  - Timestamp
  - Initiator
- Transaction lists are paginated

---

## 🏦 Bank

### System Rule
- All transactions must be in **EUR**
- No currency conversion is required