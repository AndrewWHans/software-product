package com.example.loancalculatoramortizationapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private var isDarkModeEnabled = false // State tracker for dark mode
    private var calculatedResult: String = "" // State to store the result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val printReceiptButton = findViewById<Button>(R.id.printReceiptButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)

        // Settings button to show popup menu
        settingsButton.setOnClickListener { view ->
            showSettingsMenu(view)
        }

        // Calculate button logic with amortization formula
        calculateButton.setOnClickListener {
            val principalInput = findViewById<EditText>(R.id.principalInput).text.toString().toDoubleOrNull()
            val rateInput = findViewById<EditText>(R.id.interestRateInput).text.toString().toDoubleOrNull()
            val termInput = findViewById<EditText>(R.id.termInput).text.toString().toIntOrNull()

            if (principalInput != null && rateInput != null && termInput != null) {
                val monthlyRate = rateInput / 12 / 100 // Convert annual rate to monthly
                val totalPayments = termInput * 12 // Total months

                // Amortization formula
                val monthlyPayment = principalInput * (monthlyRate * (1 + monthlyRate).pow(totalPayments)) /
                        ((1 + monthlyRate).pow(totalPayments) - 1)

                val totalPayable = monthlyPayment * totalPayments
                val totalInterest = totalPayable - principalInput

                // Store the calculated result
                calculatedResult = """
                    Loan Amount: $$principalInput
                    Monthly Payment: $${"%.2f".format(monthlyPayment)}
                    Total Interest: $${"%.2f".format(totalInterest)}
                    Total Payable: $${"%.2f".format(totalPayable)}
                """.trimIndent()

                Toast.makeText(this, "Calculation completed! Click Print Receipt.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter valid inputs!", Toast.LENGTH_SHORT).show()
            }
        }

        // Print receipt button logic
        printReceiptButton.setOnClickListener {
            if (calculatedResult.isNotEmpty()) {
                resultTextView.text = calculatedResult // Display the result
            } else {
                Toast.makeText(this, "No calculation found! Please calculate first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSettingsMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

        // Set initial check status
        popupMenu.menu.findItem(R.id.darkMode).isChecked = isDarkModeEnabled

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.darkMode -> {
                    isDarkModeEnabled = !isDarkModeEnabled
                    item.isChecked = isDarkModeEnabled
                    toggleDarkMode(isDarkModeEnabled)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun toggleDarkMode(enable: Boolean) {
        val mode = if (enable) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        recreate() // Apply the mode change
    }
}