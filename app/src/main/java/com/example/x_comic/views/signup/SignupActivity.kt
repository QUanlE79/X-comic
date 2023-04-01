package com.example.x_comic.views.signup

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.x_comic.databinding.ActivitySignupBinding
import com.example.x_comic.models.User
import com.example.x_comic.viewmodels.FirebaseAuthManager
import com.example.x_comic.viewmodels.SignupViewModel
import com.example.x_comic.viewmodels.UserViewModel
import com.example.x_comic.views.login.LoginActivity
import com.example.x_comic.views.main.MainActivity

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
            progressDialog.show()
            signupViewModel.signup(email, password).observe(this, Observer { success ->
                if (success) {
                    var userAuth = FirebaseAuthManager.getUser()

                    var user = User()
                    if (userAuth != null) {
                        user.id = userAuth.uid
                        user.email = userAuth.email.toString()
                    }

                    userViewModel.addUser(user)

                    nextMainActivity()
                    progressDialog.cancel()
                } else {
                    Toast.makeText(this, "Incorrect username or password!", Toast.LENGTH_LONG)
                        .show()
                    progressDialog.cancel()
                }
            })
        }
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