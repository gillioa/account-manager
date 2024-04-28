package com.acmebank.accountmanager.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column

@Entity
@Table(name = "ACCOUNT")
class Account(
    @Id
    @Column(length=8)
    val number: String,

    @Column(nullable = false)
    val balance: Double
) {
    constructor() : this("",0.0) {
    }
}