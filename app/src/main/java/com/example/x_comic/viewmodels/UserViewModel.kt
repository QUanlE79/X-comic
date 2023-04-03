package com.example.x_comic.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.x_comic.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserViewModel: ViewModel() {
    private val _user = MutableLiveData<User>()
    val data: LiveData<User>
        get() = _user

    fun getUser(uid: String) : UserViewModel {
        val database = Firebase.database
        val ref = database.reference.child("users").child(uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var id = snapshot.child("id").value as String
                var full_name = snapshot.child("full_name").value as String
                var age = snapshot.child("age").value as Long
                var avatar = snapshot.child("avatar").value as String
                var bio = snapshot.child("bio").value as String
                var dob = snapshot.child("dob").value as String
                var email = snapshot.child("email").value as String
                var follow = snapshot.child("follow").value as Long
                var hide = snapshot.child("hide").value as Boolean
                var penname = snapshot.child("penname").value as String
                var phone = snapshot.child("phone").value as String
                var gender = snapshot.child("gender").value as String
                var role = snapshot.child("role").value as Long

                _user.value = User(id, full_name, age, avatar, bio, dob, email, follow, hide, penname, phone, gender, role)
            }
            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi
            }
        })
        return this
    }

    fun addUser(user: User) {
        val database = Firebase.database
        database.reference
            .child("users")
            .child(user.id)
            .setValue(user)
    }
}