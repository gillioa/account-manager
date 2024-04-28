package com.acmebank.accountmanager

import com.acmebank.accountmanager.model.Transaction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order

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
            String::class.java)).isEqualTo("1000000.0")
    }

    @Test
    @Order(2)
    fun `should get 1000000 balance for account 88888888`() {
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("1000000.0")
    }

    @Test
    @Order(3)
    fun `should transfer 50000 balance from account 88888888 to 12345678`() {
        val transaction = Transaction("88888888", "12345678", 50000.0)
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.sender).isEqualTo(transaction.sender)
        assertThat(response.body!!.receiver).isEqualTo(transaction.receiver)
        assertThat(response.body!!.amount).isEqualTo(transaction.amount)
    }

    @Test
    @Order(4)
    fun `should get 1050000 balance for account 12345678`() {
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1050000.0")
    }

    @Test
    @Order(5)
    fun `should get 950000 balance for account 88888888`() {
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.0")
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
        val transaction = Transaction("88888888", "34229749", 50000.0)
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNull()

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.0")
    }

    @Test
    @Order(8)
    fun `should return a 404 error when transferring from a non-existent account`() {
        val transaction = Transaction("34229749", "88888888", 50000.0)
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(response.body).isNull()

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.0")
    }

    @Test
    @Order(9)
    fun `should return a 406 error when transferring a negative amount`() {
        val transaction = Transaction("88888888", "12345678", -50000.0)
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_ACCEPTABLE)
        assertThat(response.body).isNull()

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.0")
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1050000.0")
    }

    @Test
    @Order(10)
    fun `should return a 406 error when transferring an amount which exceed the balance of the sender`() {
        val transaction = Transaction("88888888", "12345678", 5000000000.0)
        val response = this.restTemplate.postForEntity(
            "http://localhost:$port/api/account/transaction",
            transaction,
            Transaction::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_ACCEPTABLE)
        assertThat(response.body).isNull()

        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/88888888",
            String::class.java)).isEqualTo("950000.0")
        assertThat(this.restTemplate.getForObject(
            "http://localhost:$port/api/account/balance/12345678",
            String::class.java)).isEqualTo("1050000.0")
    }
}