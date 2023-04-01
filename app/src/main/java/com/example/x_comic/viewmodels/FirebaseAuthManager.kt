package com.example.x_comic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager {
    companion object {
        val auth = FirebaseAuth.getInstance()
        fun getUser() : FirebaseUser? {
            // get current user after logging successes
            return auth.currentUser
        }
    }


    fun login(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result.value = task.isSuccessful
            }

        return result
    }

    fun signup(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                result.value = task.isSuccessful
            }

        return result
    }
}