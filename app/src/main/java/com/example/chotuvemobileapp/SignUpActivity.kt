package com.example.chotuvemobileapp

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        RegDateText.setOnClickListener {
            val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                val text = "$mDay/$mMonth/$mYear"
                RegDateText.setText(text)

            }, year, month, day)
            dialog.show()
        }
    }
}
