package com.acmebank.accountmanager.model


class Transaction(
    val sender: String,
    val receiver: String,
    val amount: Double
) {
    constructor() : this("","", 0.0) {
    }
}