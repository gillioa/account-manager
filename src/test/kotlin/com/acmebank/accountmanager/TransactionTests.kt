package com.acmebank.accountmanager

import com.acmebank.accountmanager.model.Transaction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class TransactionTests {
    private fun valid(amount: String) = Transaction("a", "b", BigDecimal(amount)).hasValidAmount()

    @Test
    fun `accepts positive amounts with up to 2 decimals`() {
        assertThat(valid("50000")).isTrue()
        assertThat(valid("50000.00")).isTrue()
        assertThat(valid("12.5")).isTrue()
        assertThat(valid("0.01")).isTrue()
    }

    @Test
    fun `accepts large amounts`() {
        assertThat(valid("10000000.00")).isTrue()
        assertThat(valid("1E+7")).isTrue()
        assertThat(valid("5000000000.00")).isTrue()
    }

    @Test
    fun `rejects zero, negative and more than 2 decimals`() {
        assertThat(valid("0")).isFalse()
        assertThat(valid("-5.00")).isFalse()
        assertThat(valid("1.005")).isFalse()
    }
}
