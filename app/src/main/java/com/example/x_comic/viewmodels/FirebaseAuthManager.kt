package com.example.x_comic.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.x_comic.views.signup.SignupActivity
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

        var msg = ""
    }

    fun login(email: String, password: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                    e ->
                run {
                    msg = e.message.toString()
                    result.value = false
                }
            }
            .addOnSuccessListener {
                result.value = true
            }

        return result
    }

    fun signup(email: String, password: String): LiveData<Boolean>{
        val result = MutableLiveData<Boolean>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener {
                    e ->
                run {
                    msg = e.message.toString()
                    result.value = false
                }
            }
            .addOnSuccessListener {
                result.value = true
            }


        return result
    }
}