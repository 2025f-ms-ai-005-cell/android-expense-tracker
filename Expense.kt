package dev.sadia.pocketledger

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth

data class Expense(val id: String, val title: String, val cents: Long, val category: String, val date: LocalDate)

object ExpenseRules {
    val categories = listOf("Food", "Transport", "Learning", "Other")
    fun parseAmount(text: String): Long? = try {
        val amount = BigDecimal(text.trim())
        if (amount <= BigDecimal.ZERO || amount.scale() > 2 || amount > BigDecimal("1000000000")) null
        else amount.movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
    } catch (_: Exception) { null }
    fun monthlyTotal(expenses: List<Expense>, month: YearMonth): Long =
        expenses.filter { YearMonth.from(it.date) == month }.sumOf { it.cents }
    fun display(cents: Long): String = BigDecimal.valueOf(cents, 2).setScale(2).toPlainString()
}
