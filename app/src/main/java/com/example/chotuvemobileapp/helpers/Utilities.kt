package com.example.chotuvemobileapp.helpers

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import java.text.DecimalFormat
import java.util.*

object Utilities {

    fun createDatePicker(field: EditText, context: Context){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        field.setOnClickListener {
            val dialog = DatePickerDialog(context, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                val df = DecimalFormat("00")
                val trueMonth = df.format(mMonth + 1)
                val trueDay = df.format(mDay)
                val text = "$mYear-$trueMonth-$trueDay"
                field.setText(text)
            }, year, month, day)
            dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
            dialog.show()
        }
    }

    fun EditText.watchText(button: Button, validationFun: () -> Boolean) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                button.isEnabled = validationFun()
                if (button.isEnabled) button.alpha = 1f
                else button.alpha = 0.2f
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}