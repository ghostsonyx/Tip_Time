package com.example.tiptime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
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

    private var tipPercentage = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if cost of service changes to enable calculate button
        binding.costOfServiceEditText.addTextChangedListener {
            isCalculateButtonAvailable()
        }

        // check if other tip changes to enable calculate button
        binding.optionOtherEditText.addTextChangedListener {
            isCalculateButtonAvailable()
        }

        binding.tipOptions.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(binding.tipOptions.checkedRadioButtonId) {
                R.id.option_twenty_percent -> binding.optionOtherEditText.isEnabled = false
                R.id.option_eighteen_percent -> binding.optionOtherEditText.isEnabled = false
                R.id.option_fifteen_percent -> binding.optionOtherEditText.isEnabled = false
                R.id.option_other_percent -> binding.optionOtherEditText.isEnabled = true
            }

            isCalculateButtonAvailable()
        }

        binding.calculateButton.setOnClickListener {
            calculateTip()
        }
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

        val tipPercentage = when(binding.tipOptions.checkedRadioButtonId) {
            R.id.option_twenty_percent -> 0.20
            R.id.option_eighteen_percent -> 0.18
            R.id.option_fifteen_percent -> 0.15
            R.id.option_other_percent -> binding.optionOtherEditText.text.toString().toDouble() / 100.00
            else -> 0.00
        }

        var tip = costOfService * tipPercentage

        if (binding.roundUpSwitch.isChecked)
            tip = ceil(costOfService * tipPercentage)

        displayTip(tip)
    }

    private fun displayTip(tip: Double) {
        val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
        binding.tipResult.text = getString(R.string.tip_amount, formattedTip)
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
}