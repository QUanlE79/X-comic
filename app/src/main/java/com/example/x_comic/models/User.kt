package com.example.x_comic.models

class User
{
    var id: String = ""
    var full_name: String = ""
    var age: Long = 0
    var avatar: String = ""
    var bio: String = ""
    var dob: String = ""
    var email: String = ""
    var follow: Long = 0
    var is_hide: Boolean = true
    var penname: String = ""
    var phone: String = ""
    var gender: String = "male"
    var role: Long = 1
    constructor(
        id: String = "",
        full_name: String = "",
        age: Long = 0,
        avatar: String = "",
        bio: String = "",
        dob: String = "",
        email: String = "",
        follow: Long = 0,
        is_hide: Boolean = true,
        penname: String = "",
        phone: String = "",
        gender: String = "male",
        role: Long = 1, // Role = 1: là đọc giả; 2: là người đọc giả có thể đăng truyện; 3: là admin
    ) {
        this.id = id
        this.full_name = full_name
        this.age = age
        this.avatar = avatar
        this.bio = bio
        this.dob = dob
        this.email = email
        this.follow = follow
        this.is_hide = is_hide
        this.penname = penname
        this.phone = phone
        this.gender = gender
        this.role = role
    }
    constructor() {

    }
}