package com.acmebank.accountmanager.model

import java.math.BigDecimal

class Transaction(
    val sender: String,
    val receiver: String,
    val amount: BigDecimal
) {
    constructor() : this("", "", BigDecimal.ZERO) {
    }

    /** True when the amount is strictly positive and has at most 2 decimal places. */
    fun hasValidAmount(): Boolean =
        amount.signum() > 0 && amount.stripTrailingZeros().scale() <= 2
}
