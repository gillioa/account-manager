package com.acmebank.accountmanager.controller

import com.acmebank.accountmanager.model.Transaction
import com.acmebank.accountmanager.repository.AccountRepository
import com.acmebank.accountmanager.service.AccountService

import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/account")
class AccountController(accountRepository: AccountRepository) {
    private val accountService: AccountService = AccountService(accountRepository)

    @GetMapping("/balance/{account_number}")
    fun fetchBalanceByAccountNumber(@PathVariable("account_number") number: String): ResponseEntity<Double> {
        val account = accountService.getAccountById(number)
        if (account != null) {
            return ResponseEntity<Double>(account.balance, HttpStatus.OK)
        }
        return ResponseEntity<Double>(HttpStatus.NOT_FOUND)
    }

    @PostMapping("/transaction")
    fun transaction(@RequestBody transactionContent: Transaction): ResponseEntity<Transaction> {
        val sender = accountService.getAccountById(transactionContent.sender)
        val receiver = accountService.getAccountById(transactionContent.receiver)
        if (sender == null || receiver == null) {
            return ResponseEntity<Transaction>(HttpStatus.NOT_FOUND)
        }
        if (transactionContent.amount <= 0 || sender.balance < transactionContent.amount) {
            return ResponseEntity<Transaction>(HttpStatus.NOT_ACCEPTABLE)
        }
        try {
            accountService.executeTransaction(sender, receiver, transactionContent.amount)
        } catch (e: Exception) {
            return ResponseEntity<Transaction>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity<Transaction>(transactionContent, HttpStatus.OK)
    }
}