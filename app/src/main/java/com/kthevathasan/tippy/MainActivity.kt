package com.kthevathasan.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val DEFAULT_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var sbTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var etNumPeople: EditText
    private lateinit var tvSplitAmount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBaseAmount = findViewById(R.id.etBaseAmount)
        sbTip = findViewById(R.id.sbTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        etNumPeople = findViewById(R.id.etNumPeople)
        tvSplitAmount = findViewById(R.id.tvSplitAmount)

        sbTip.progress = DEFAULT_TIP_PERCENT
        tvTipPercentLabel.text = "$DEFAULT_TIP_PERCENT%"
        updateTipDescription(DEFAULT_TIP_PERCENT)

        sbTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged|sbTip $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged|etBaseAmount $s")
                computeTipAndTotal()
            }
        })

        etNumPeople.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged|etNumPeople $s")
                computeTipAndTotal()
            }

        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }

        tvTipDescription.text = tipDescription

        //update color based on tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / sbTip.max,
            ContextCompat.getColor(this, R.color.tip_bad),
            ContextCompat.getColor(this, R.color.tip_good)
        ) as Int

        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            tvSplitAmount.text = ""
            return
        }

        //get value of base, tip percent, and number of people
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = sbTip.progress
        val numPeople: Int = if (etNumPeople.text.isNullOrEmpty()) {
            1
        } else {
            etNumPeople.text.toString().toInt()
        }

        //compute tip, total, and split amount
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount

        //update UI
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)

        //if there is only 1 person or the field is empty
        if (numPeople != 1) {
            val splitAmount = totalAmount / numPeople
            tvSplitAmount.text = "%.2f".format(splitAmount)
        } else {
            tvSplitAmount.text = null
        }

    }
}