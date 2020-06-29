package com.example.chotuvemobileapp.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat.startActivityForResult
import java.text.DecimalFormat
import java.util.*

object Utilities {

    const val DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss"
    const val DATE_FORMAT_SHORT = "dd/MM/yyyy"
    const val REQUEST_CODE_EDIT_PROFILE = 22
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val EMAIL = "email"
    const val BIRTH_DATE = "birthDate"
    const val USERNAME = "username"
    const val PIC_URL = "pic_url"
    const val REQUEST_LOCATION_PERMISSION = 34
    const val REQUEST_GALLERY_PERMISSION = 35

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

    fun startSelectActivity(activity: Activity, selectType: String, title: String, responseCode: PickRequest) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = selectType
        }
        startActivityForResult(activity, Intent.createChooser(intent, title), responseCode.value, null)
    }

    @SuppressLint("Recycle")
    fun getFileName(uri: Uri, contentResolver: ContentResolver) : String {
        var result = null as String?
        if (uri.scheme.equals("content")) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}