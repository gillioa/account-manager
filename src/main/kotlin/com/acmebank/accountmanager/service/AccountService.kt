package com.acmebank.accountmanager.service

import com.acmebank.accountmanager.model.Account
import com.acmebank.accountmanager.repository.AccountRepository
import jakarta.transaction.Transactional
import java.math.BigDecimal
import org.springframework.stereotype.Service

@Service
class AccountService(private val accountRepository: AccountRepository) {
    @Transactional
    fun getAccountById(accountId: String): Account? {
        val account = accountRepository.findById(accountId)
        if (account.isPresent) {
            return account.get()
        }
        return null
    }

    @Transactional(rollbackOn = [Exception::class])
    fun executeTransaction(sender: Account, receiver: Account, amount: BigDecimal) {
        val updatedSender = Account(sender.number, sender.balance.subtract(amount))
        accountRepository.save(updatedSender)
        val updatedReceiver = Account(receiver.number, receiver.balance.add(amount))
        accountRepository.save(updatedReceiver)
    }
}