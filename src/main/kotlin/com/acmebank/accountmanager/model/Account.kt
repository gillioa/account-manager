package com.acmebank.accountmanager.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import java.math.BigDecimal

@Entity
@Table(name = "ACCOUNT")
class Account(
    @Id
    @Column(length=8)
    val number: String,

    @Column(nullable = false, precision = 19, scale = 2)
    val balance: BigDecimal
) {
    constructor() : this("", BigDecimal.ZERO.setScale(2)) {
    }
}
