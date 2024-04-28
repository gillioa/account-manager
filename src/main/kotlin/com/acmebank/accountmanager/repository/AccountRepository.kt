package com.acmebank.accountmanager.repository

import com.acmebank.accountmanager.model.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, String> {
}