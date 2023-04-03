package com.example.x_comic.views.login

import com.example.x_comic.R
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.x_comic.databinding.ActivityLoginBinding
import com.example.x_comic.viewmodels.FirebaseAuthManager
import com.example.x_comic.viewmodels.LoginViewModel
import com.example.x_comic.viewmodels.UserViewModel
import com.example.x_comic.views.main.MainActivity
import com.example.x_comic.views.signup.SignupActivity
import com.google.android.material.snackbar.Snackbar


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val progressDialog = ProgressDialog(this)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.loginBtn.setOnClickListener {
            val email = binding.usernameET.text.toString().trim()
            val password = binding.passwordET.text.toString().trim()


            val result = verify(email, password)

            val isValid = result.first
            val msg = result.second

            if (!isValid) {
                displayErrorMsg(msg)
            }
            else {
                progressDialog.show()
                loginViewModel.login(email, password).observe(this, Observer { success ->
                    if (success) {
                        val userAuth = FirebaseAuthManager.getUser()

                        if (userAuth != null) {
                            val uid = userAuth.uid
                            val user = userViewModel.getUser(uid)
                            user.data.observe(this, Observer { User ->
                                // TODO: Xử lý việc người dùng vừa đăng nhập xong
                                // Lấy thông tin người dùng từ realtime db
                                //

                                Toast.makeText(this, User.email, Toast.LENGTH_LONG).show()
                            })
                            nextMainActivity()
                        }
                        progressDialog.cancel()
                    } else {
                        displayErrorMsg(FirebaseAuthManager.msg)
                        progressDialog.cancel()
                    }
                })
            }
        }

        binding.signupTV.setOnClickListener {
            nextSignupActivity()
        }
    }

    fun setEditTextBorderColor(editText: EditText, color: Int) {
        val shape = GradientDrawable()
        shape.setStroke(2, color) // set the border width and color
        shape.cornerRadius = 4f // set the corner radius
        shape.setColor(Color.WHITE) // set the background color

        editText.background = shape // set the custom drawable as the background of the EditText
    }
//
    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return emailRegex.matches(email)
    }
//    private fun verify(email: String, password: String): Boolean {
//        return isEmailValid(email) && (password.length > 6)
//    }

    private fun verify(email: String, password: String): Pair<Boolean, String> {
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

        if (email.isEmpty() && password.isEmpty()) {
            msg = "Email and password are not empty!"
        }

        return Pair(isValid, msg)
    }

    private fun displayErrorMsg(msg: String) {
        val snackbar = Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.love))
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.show()
    }

    private fun nextMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun nextSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}