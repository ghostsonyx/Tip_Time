package com.example.tiptime

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.example.tiptime.databinding.ActivityMainBinding
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if cost of service has been added to enable calculate button
        binding.costOfService.addTextChangedListener {
            binding.calculateButton.isEnabled = binding.costOfService.text.isNotEmpty()
        }

        binding.calculateButton.setOnClickListener {
            calculateTip()
        }
    }

    private fun calculateTip() {
        val costOfService = binding.costOfService.text.toString().toDoubleOrNull()

        if (costOfService == null || costOfService == 0.00) {
            displayTip(0.00)
            return
        }

        val tipPercentage = when(binding.tipOptions.checkedRadioButtonId) {
            R.id.option_twenty_percent -> 0.20
            R.id.option_eighteen_percent -> 0.18
            R.id.option_fifteen_percent -> 0.15
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
}
