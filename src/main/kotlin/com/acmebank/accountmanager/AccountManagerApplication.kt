package com.acmebank.accountmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
@RestController
class AccountManagerApplication

fun main(args: Array<String>) {
	runApplication<AccountManagerApplication>(*args)
}
