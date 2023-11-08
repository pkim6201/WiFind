package com.example.wifind.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.wifind.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parse.ParseUser


class SettingsActivity : AppCompatActivity() {

    lateinit var btReport: Button
    lateinit var btLogOut: Button
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btReport = findViewById(R.id.bt_report)
        btLogOut = findViewById(R.id.bt_logout)
        bottomNav = findViewById(R.id.bottom_nav)

        findViewById<TextView>(R.id.tv_signed_in_as).text = "Signed in as: " + ParseUser.getCurrentUser().username
        btReport.setOnClickListener { onReportClicked() }
        btLogOut.setOnClickListener {
            ParseUser.logOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.marketplace -> {
                    finish()
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.settings -> true

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNav.selectedItemId = R.id.settings
    }

    private fun onReportClicked() {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_report_dialog, null)
        MaterialAlertDialogBuilder(this)
            .setView(view)
            .setTitle("Report an Issue")
            .setMessage("Please describe the issue you are having")
            .setPositiveButton("Send Email") { _, _ ->
                val userIssue = view.findViewById<EditText>(R.id.et_issue).text.toString()
                openReportInEmailApp(emailBody = userIssue)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun openReportInEmailApp(emailBody: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("wifind@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Issue from ${ParseUser.getCurrentUser().username}")
            putExtra(Intent.EXTRA_TEXT, emailBody)
        }
        startActivity(intent)
    }

}