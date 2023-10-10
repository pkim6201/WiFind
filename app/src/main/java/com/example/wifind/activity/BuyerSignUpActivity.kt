package com.example.wifind.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wifind.R
import com.google.android.material.textfield.TextInputEditText
import com.parse.ParseUser

class BuyerSignUpActivity : AppCompatActivity() {
    private var back: ImageView? = null
    private var signup: Button? = null
    private var email: TextInputEditText? = null
    private var username: TextInputEditText? = null
    private var password: TextInputEditText? = null
    private var passwordConfirmation: TextInputEditText? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer_signup)

        progressDialog = ProgressDialog(this)

        back = findViewById(R.id.back)
        signup = findViewById(R.id.signup)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        passwordConfirmation = findViewById(R.id.passwordConfirmation)
        email = findViewById(R.id.email)

        signup?.setOnClickListener {
            val isEmailEmpty = TextUtils.isEmpty(email?.text.toString())
            val isUsernameEmpty = TextUtils.isEmpty(username?.text.toString())
            val passwordMatchesConfirmation = password?.text.toString() == passwordConfirmation?.text.toString()

            if (passwordMatchesConfirmation && !isEmailEmpty && !isUsernameEmpty)
                signup(
                    username = username?.text.toString(),
                    password = password?.text.toString(),
                    email = email?.text.toString()
                )
            else
                Toast.makeText(
                    this,
                    "Make sure that the values you entered are correct.",
                    Toast.LENGTH_SHORT
                ).show()
        }

        back?.setOnClickListener {
            finish()
        }

    }

    fun signup(username: String, password: String, email: String) {
        progressDialog?.show()

        val user = ParseUser()
        user.username = username
        user.setPassword(password)
        user.email = email
        user.put("userType", "buyer")
        user.signUpInBackground {
            progressDialog?.dismiss()
            if (it == null) {
                showAlert("Successful Sign Up!", "Welcome $username !")
            } else {
                ParseUser.logOut()
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.cancel()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        val ok = builder.create()
        ok.show()
    }
}