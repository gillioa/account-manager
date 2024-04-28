## Account-manager Project

The goal of this project is to be able to simulate a very simple banking system where users can check on their balance using their account number and transfer money between those accounts.
The data must be persisted in an H2 database so it remains the same in case of a restart of the service.

The intial data set is two accounts with number 12345678 and 88888888 with both a balance of 1,000,000 HKD for each of them

To make it run, simply open the project in IntelliJ and run the `AccountManagerApplication` file.
To run the tests, simply open the project in IntelliJ and run the `AccountControllerTests` file.

Please find below usage examples using `curl` :

```
curl -X GET localhost:8080/api/account/balance/88888888
curl -X GET localhost:8080/api/account/balance/12345678
```

The response should be 1000000.0 for both requests.

```
curl -X POST localhost:8080/api/account/transaction -H "Content-Type: application/json" -d '{"sender": "12345678", "receiver": "88888888", "amount": 50000}'
curl -X GET localhost:8080/api/account/balance/88888888
curl -X GET localhost:8080/api/account/balance/12345678
```

In the example above, we send an amount of 50,000 HKD from the account 12345678 to 88888888 and recheck the balance of both accounts.
The response to the first `GET` should be now 1050000.0 and the response to the second `GET` should be now 950000.0.
