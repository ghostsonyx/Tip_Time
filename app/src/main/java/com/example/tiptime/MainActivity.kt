package com.example.tiptime

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.tiptime.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if cost of service changes to enable calculate button
        binding.costOfServiceEditText.addTextChangedListener { isCalculateButtonAvailable() }

        // check if other tip changes to enable calculate button
        binding.optionOtherEditText.addTextChangedListener { isCalculateButtonAvailable() }

        // check if radio button for tip was clicked, if so disable others
        binding.tipOptions.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(binding.tipOptions.checkedRadioButtonId) {
                R.id.option_twenty_percent -> binding.optionOtherEditText.isEnabled = false
                R.id.option_eighteen_percent -> binding.optionOtherEditText.isEnabled = false
                R.id.option_fifteen_percent -> binding.optionOtherEditText.isEnabled = false
                R.id.option_other_percent -> binding.optionOtherEditText.isEnabled = true
            }

            isCalculateButtonAvailable()
        }

        // calculate tip when calculate button is pressed
        binding.calculateButton.setOnClickListener { calculateTip() }

        // hide keyboard on enter key for editable text fields
        binding.costOfServiceEditText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
        binding.optionOtherEditText.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode) }
    }

    private fun calculateTip() {
        // validate cost of service input
        val costOfService = binding.costOfServiceEditText.text.toString().toDoubleOrNull()

        if (costOfService == null || costOfService == 0.00) {
            displayTip(0.00)
            return
        }

        // validate tip
        if (!isTipPercentageKnown()) {
            displayTip(0.00)
            return
        }

        val tipPercentage = getTipPercentage()

        var tip = costOfService * tipPercentage

        if (binding.roundUpSwitch.isChecked)
            tip = ceil(costOfService * tipPercentage)

        displayTip(tip)
    }

    private fun displayTip(tip: Double) {
        val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
        binding.tipResult.text = getString(R.string.tip_amount, formattedTip)
    }

    private fun getTipPercentage(): Double {
        return when(binding.tipOptions.checkedRadioButtonId) {
            R.id.option_twenty_percent -> 0.20
            R.id.option_eighteen_percent -> 0.18
            R.id.option_fifteen_percent -> 0.15
            R.id.option_other_percent -> binding.optionOtherEditText.text.toString().toDouble() / 100.00
            else -> 0.00
        }
    }

    private fun isTipPercentageKnown(): Boolean {
        val tipPercentage = when(binding.tipOptions.checkedRadioButtonId) {
            R.id.option_twenty_percent -> 0.20
            R.id.option_eighteen_percent -> 0.18
            R.id.option_fifteen_percent -> 0.15
            R.id.option_other_percent -> {
                if (binding.optionOtherEditText.text?.isNotEmpty() == true) {
                    binding.optionOtherEditText.text.toString().toDouble()
                } else {
                    return false
                }
            }
            else -> null
        }

        return (tipPercentage is Double)
    }

    private fun isCalculateButtonAvailable() {
        binding.calculateButton.isEnabled = binding.costOfServiceEditText.text?.isNotEmpty() == true && isTipPercentageKnown()
    }

    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}