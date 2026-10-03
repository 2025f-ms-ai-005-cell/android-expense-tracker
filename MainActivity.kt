package dev.sadia.pocketledger

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

class MainActivity : Activity() {
    private val expenses = mutableListOf<Expense>()
    private lateinit var content: LinearLayout
    private var storageWarning = false
    private val preferences by lazy { getSharedPreferences("ledger", MODE_PRIVATE) }
    override fun onCreate(state: Bundle?) { super.onCreate(state); load(); render() }
    private fun load() {
        try {
            val data = JSONArray(preferences.getString("expenses", "[]"))
            for (i in 0 until data.length()) {
                val item = data.getJSONObject(i)
                expenses.add(Expense(item.getString("id"), item.getString("title"), item.getLong("cents"), item.getString("category"), LocalDate.parse(item.getString("date"))))
            }
        } catch (_: Exception) { expenses.clear(); storageWarning = true }
    }
    private fun save(): Boolean {
        val data = JSONArray()
        expenses.forEach { data.put(JSONObject().put("id", it.id).put("title", it.title).put("cents", it.cents).put("category", it.category).put("date", it.date.toString())) }
        return preferences.edit().putString("expenses", data.toString()).commit()
    }
    private fun label(text: String, size: Float = 16f): TextView = TextView(this).apply { this.text = text; textSize = size; setPadding(0, 12, 0, 12); setTextColor(Color.rgb(20, 40, 50)) }
    private fun input(hint: String): EditText = EditText(this).apply { this.hint = hint; setSingleLine(true); content.addView(this) }
    private fun notifyUser(message: String) { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
    private fun render() {
        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 36, 32, 36) }
        setContentView(ScrollView(this).apply { addView(content) })
        content.addView(label("PocketLedger", 30f))
        content.addView(label("Offline expense tracker · Portfolio demo\nPKR · Data stays on this device"))
        if (storageWarning) {
            content.addView(label("Saved data could not be read. It has not been overwritten. Export/recover it before adding entries."))
            return
        }
        val month = YearMonth.now()
        content.addView(label("$month total: PKR ${ExpenseRules.display(ExpenseRules.monthlyTotal(expenses, month))}", 22f))
        val title = input("Expense title")
        val amount = input("Amount in PKR, e.g. 250.50").apply { inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val date = input("Date YYYY-MM-DD").apply { setText(LocalDate.now().toString()) }
        val category = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, ExpenseRules.categories) }
        content.addView(category)
        content.addView(Button(this).apply { text = "Add expense"; setOnClickListener {
            val cents = ExpenseRules.parseAmount(amount.text.toString())
            val enteredDate = try { LocalDate.parse(date.text.toString().trim()) } catch (_: Exception) { null }
            val name = title.text.toString().trim()
            when {
                name.length !in 2..80 -> title.error = "Use 2–80 characters"
                cents == null -> amount.error = "Use a positive amount, max 2 decimals, up to 1 billion PKR"
                enteredDate == null || enteredDate.isAfter(LocalDate.now()) -> date.error = "Use a valid date, not in the future"
                else -> {
                    val expense = Expense(UUID.randomUUID().toString(), name, cents, category.selectedItem.toString(), enteredDate)
                    expenses.add(expense)
                    if (save()) { render(); notifyUser("Saved locally") } else { expenses.remove(expense); notifyUser("Could not save. Nothing added.") }
                }
            }
        } })
        content.addView(label("Expense history", 22f))
        if (expenses.isEmpty()) content.addView(label("No expenses yet. Add your first entry above."))
        expenses.sortedByDescending { it.date }.forEach { expense ->
            content.addView(label("${expense.title}\nPKR ${ExpenseRules.display(expense.cents)} · ${expense.category} · ${expense.date}"))
            content.addView(Button(this).apply { text = "Delete ${expense.title}"; setOnClickListener {
                AlertDialog.Builder(this@MainActivity).setTitle("Delete this expense?").setMessage(expense.title)
                    .setNegativeButton("Keep", null).setPositiveButton("Delete") { _, _ ->
                        val position = expenses.indexOf(expense)
                        expenses.remove(expense)
                        if (save()) render() else { expenses.add(position, expense); notifyUser("Could not save deletion. Entry kept.") }
                    }.show()
            } })
        }
    }
}
