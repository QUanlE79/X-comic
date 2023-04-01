package com.example.x_comic.views.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.x_comic.databinding.ActivityLoginBinding
import com.example.x_comic.viewmodels.FirebaseAuthManager
import com.example.x_comic.viewmodels.LoginViewModel
import com.example.x_comic.viewmodels.UserViewModel
import com.example.x_comic.views.main.MainActivity
import com.example.x_comic.views.signup.SignupActivity


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

            progressDialog.show()
            loginViewModel.login(email, password).observe(this, Observer { success ->
                if (success) {
                    val userAuth = FirebaseAuthManager.getUser()

                    if (userAuth != null) {
                        val uid = userAuth.uid

                        userViewModel.getUser(uid).data.observe(this, Observer { User ->
                            binding.textView.text = User.full_name
                        })
                        nextMainActivity()
                    }
                    progressDialog.cancel()
                } else {
                    Toast.makeText(this, "Incorrect username or password!", Toast.LENGTH_LONG)
                        .show()
                    progressDialog.cancel()
                }
            })
        }

        binding.signupTV.setOnClickListener {
            nextSignupActivity()
        }
    }
//
//    private fun isEmailValid(email: String): Boolean {
//        val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
//        return emailRegex.matches(email)
//    }
//    private fun verify(email: String, password: String): Boolean {
//        return isEmailValid(email) && (password.length > 6)
//    }

    private fun nextMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun nextSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }
}