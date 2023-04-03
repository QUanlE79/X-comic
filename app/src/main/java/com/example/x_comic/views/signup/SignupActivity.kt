package com.example.x_comic.views.signup

import android.R
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.x_comic.databinding.ActivitySignupBinding
import com.example.x_comic.models.User
import com.example.x_comic.viewmodels.FirebaseAuthManager
import com.example.x_comic.viewmodels.SignupViewModel
import com.example.x_comic.viewmodels.UserViewModel
import com.example.x_comic.views.login.LoginActivity
import com.example.x_comic.views.main.MainActivity
import com.google.android.material.snackbar.Snackbar

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        signupViewModel = ViewModelProvider(this).get(SignupViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        setContentView(binding.root)
        val progressDialog = ProgressDialog(this)

        binding.loginTV.setOnClickListener {
            nextSignupActivity()
        }


        binding.signupBtn.setOnClickListener {
            val email = binding.usernameET.text.toString().trim()
            val password = binding.passwordET.text.toString().trim()
            val confirmPassword = binding.confirmPasswordET.text.toString().trim()

            val result = verify(email, password, confirmPassword)

            val isValid = result.first
            val msg = result.second

            if (!isValid) {
                displayErrorMsg(msg)
            } else {
                progressDialog.show()

                signupViewModel.signup(email, password).observe(this, Observer { success  ->
                    if (success) {
                        val userAuth = FirebaseAuthManager.getUser()

                        val user = User()
                        if (userAuth != null) {
                            user.id = userAuth.uid
                            user.email = userAuth.email.toString()
                        }

                        userViewModel.addUser(user)

                        nextMainActivity()
                        progressDialog.cancel()
                    } else {
                        displayErrorMsg(FirebaseAuthManager.msg)
                        progressDialog.cancel()
                    }
                })
            }
        }
    }

    private fun displayErrorMsg(msg: String) {
        val snackbar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.holo_red_light))
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    fun setEditTextBorderColor(editText: EditText, color: Int) {
        val shape = GradientDrawable()
        shape.setStroke(2, color) // set the border width and color
        shape.cornerRadius = 4f // set the corner radius
        shape.setColor(Color.WHITE) // set the background color

        editText.background = shape // set the custom drawable as the background of the EditText
    }

    private fun verify(email: String, password: String, confirmPassword: String): Pair<Boolean, String> {
        var isValid = true
        var msg = ""

        if (email.isEmpty()) {
            isValid = false
            setEditTextBorderColor(binding.usernameET, Color.RED)
            msg = "Email is not empty!"
        }
        if (password.isEmpty()) {
            isValid = false
            setEditTextBorderColor(binding.passwordET, Color.RED)
            msg = "Password is not empty!"
        }
        if (confirmPassword.isEmpty()) {
            isValid = false
            setEditTextBorderColor(binding.confirmPasswordET, Color.RED)
            msg = "Confirm password is not empty!"
        }

        if (password.length < 6) {
            isValid = false
            msg = "Your password is too short!"
        }

        if (password != confirmPassword) {
            isValid = false
            msg = "Confirm password does not match!"
        }

        return Pair(isValid, msg)
    }

    private fun nextMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun nextSignupActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}