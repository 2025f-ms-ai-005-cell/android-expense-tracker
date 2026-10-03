package dev.sadia.pocketledger

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class ExpenseRulesTest {
    @Test fun exactDecimalAmounts() {
        assertEquals(25050L, ExpenseRules.parseAmount("250.50"))
        assertEquals(10L, ExpenseRules.parseAmount("0.10"))
        assertEquals("250.50", ExpenseRules.display(25050))
    }
    @Test fun rejectsInvalidAmounts() {
        listOf("", "-1", "0", "abc", "1.234", "1000000001").forEach { assertNull(ExpenseRules.parseAmount(it)) }
    }
    @Test fun monthFilter() {
        val rows = listOf(Expense("1", "Food", 100, "Food", LocalDate.of(2030, 1, 1)), Expense("2", "Bus", 200, "Transport", LocalDate.of(2030, 2, 1)))
        assertEquals(100L, ExpenseRules.monthlyTotal(rows, YearMonth.of(2030, 1)))
        assertEquals(0L, ExpenseRules.monthlyTotal(rows, YearMonth.of(2030, 3)))
    }
}
