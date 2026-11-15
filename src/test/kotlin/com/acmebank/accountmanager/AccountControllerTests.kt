package com.acmebank.accountmanager

import com.acmebank.accountmanager.model.Transaction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import java.math.BigDecimal

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AccountControllerTests {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    @Order(1)
    fun `should get 1000000 balance for account 12345678`() {
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1000000.00")
    }

    @Test
    @Order(2)
    fun `should get 1000000 balance for account 88888888`() {
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("1000000.00")
    }

    @Test
    @Order(3)
    fun `should transfer 50000 balance from account 88888888 to 12345678`() {
        val transaction = Transaction("88888888", "12345678", BigDecimal("50000.00"))
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.sender).isEqualTo(transaction.sender)
        assertThat(response.body!!.receiver).isEqualTo(transaction.receiver)
        assertThat(response.body!!.amount).isEqualByComparingTo(transaction.amount)
    }

    @Test
    @Order(4)
    fun `should get 1050000 balance for account 12345678`() {
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1050000.00")
    }

    @Test
    @Order(5)
    fun `should get 950000 balance for account 88888888`() {
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.00")
    }

    @Test
    @Order(6)
    fun `should return a 404 error when fetching balance for a non-existent account`() {
        val response = this.restTemplate.getForEntity(
            "http://localhost:$port/api/account/balance/34229749",
            String::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNull()
    }

    @Test
    @Order(7)
    fun `should return a 404 error when transferring to a non-existent account`() {
        val transaction = Transaction("88888888", "34229749", BigDecimal("50000.00"))
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNull()

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.00")
    }

    @Test
    @Order(8)
    fun `should return a 404 error when transferring from a non-existent account`() {
        val transaction = Transaction("34229749", "88888888", BigDecimal("50000.00"))
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNull()

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.00")
    }

    @Test
    @Order(9)
    fun `should return a 406 error when transferring a negative amount`() {
        val transaction = Transaction("88888888", "12345678", BigDecimal("-50000.00"))
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_ACCEPTABLE)
        assertThat(response.body).isNull()

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.00")
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1050000.00")
    }

    @Test
    @Order(10)
    fun `should return a 406 error when transferring an amount which exceed the balance of the sender`() {
        val transaction = Transaction("88888888", "12345678", BigDecimal("5000000000.00"))
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_ACCEPTABLE)
        assertThat(response.body).isNull()

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.00")
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1050000.00")
    }

    @Test
    @Order(11)
    fun `should return a 406 error when the amount has more than 2 decimals`() {
        val transaction = Transaction("88888888", "12345678", BigDecimal("10.005"))
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_ACCEPTABLE)

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.00")
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1050000.00")
    }

    @Test
    @Order(12)
    fun `should transfer an amount with 2 decimals`() {
        val transaction = Transaction("88888888", "12345678", BigDecimal("0.25"))
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("949999.75")
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1050000.25")
    }
}
