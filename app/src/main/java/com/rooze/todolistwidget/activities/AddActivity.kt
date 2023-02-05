package com.rooze.todolistwidget.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class AddActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "AddActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
        bindAddDialog()
    }

    private fun bindAddDialog() {
        AddDialogFragment().show(supportFragmentManager, "AddDialog")
    }
}