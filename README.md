# account-manager

A very simple banking REST API: check an account balance and transfer money between accounts. Data is persisted in an H2 database.

## Stack
- Kotlin, Spring Boot 3.2.5, Java 17
- Spring Data JPA + H2
- Maven (wrapper included)

## Initial data
Two accounts, `12345678` and `88888888`, each with a balance of 1,000,000 HKD.

## Run
```bash
./mvnw spring-boot:run
```
Or open the project in IntelliJ and run `AccountManagerApplication`.

## Test
```bash
./mvnw test
```
(`AccountControllerTests`, `AccountManagerApplicationTests`)

## API
| Method | Path | Description |
|---|---|---|
| GET | `/api/account/balance/{account_number}` | Balance of an account |
| POST | `/api/account/transaction` | Transfer between accounts |

```bash
curl -X GET localhost:8080/api/account/balance/88888888
curl -X POST localhost:8080/api/account/transaction \
  -H "Content-Type: application/json" \
  -d '{"sender": "12345678", "receiver": "88888888", "amount": 50000}'
```
After the transfer, `88888888` has 1050000.0 and `12345678` has 950000.0.

## Database
H2 file database stored at `~/account-data` (so balances survive restarts). The H2 console is enabled at `/h2_console` (user `sa`, empty password).

## Structure
`controller/` (REST), `service/` (business logic), `repository/` (JPA), `model/` (`Account`, `Transaction`).

## Status
Archived, no further development planned.
