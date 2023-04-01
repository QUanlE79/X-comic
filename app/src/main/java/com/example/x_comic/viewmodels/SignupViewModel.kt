package com.example.x_comic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SignupViewModel : ViewModel(){
    private val firebaseAuthManager = FirebaseAuthManager()

    fun signup(email: String, password: String): LiveData<Boolean> {
        return firebaseAuthManager.signup(email, password)
    }
}