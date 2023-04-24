package com.example.x_comic.models

class User
{
    var id: String = ""
    var full_name: String = "no data yet"
    var age: Long = 0
    var avatar: String = ""
    var bio: String = "no data yet"
    var dob: String = "no data yet"
    var email: String = ""
    var hide: Boolean = true
    var penname: String = "no data yet"
    var phone: String = "no data yet"
    var gender: String = "male"
    var aboutme: String = ""
    var follow: ArrayList<String> = ArrayList()
    var have_followed: ArrayList<String> = ArrayList()
    var role: Long = 1
    constructor(
        id: String = "",
        full_name: String = "no data yet",
        age: Long = 0,
        avatar: String = "",
        bio: String = "no data yet",
        dob: String = "no data yet",
        email: String = "",
        hide: Boolean = true,
        penname: String = "no data yet",
        phone: String = "no data yet",
        gender: String = "male",
        aboutme: String = "",
        follow: ArrayList<String> = ArrayList(),
        have_followed: ArrayList<String> = ArrayList(),
        role: Long = 1, // Role = 1: là đọc giả; 2: là người đọc giả có thể đăng truyện; 3: là admin
    ) {
        this.id = id
        this.full_name = full_name
        this.age = age
        this.avatar = avatar
        this.bio = bio
        this.dob = dob
        this.email = email
        this.hide = hide
        this.penname = penname
        this.phone = phone
        this.gender = gender
        this.role = role
        this.follow = follow
        // is_followed nó bug :)))
        this.have_followed = have_followed
        this.aboutme = aboutme
    }

    fun follow(other: User) {
        this.follow.add(other.id)
    }

    fun haveFollowed(other: User) {
        this.have_followed.add(other.id)
    }

    fun unHaveFollowed(other: User) {
        var index: Int? = null

        for (i in 0 until this.have_followed.size) {
            if (this.have_followed[i] == other.id) {
                index = i
            }
        }

        if (index != null)
            this.have_followed.removeAt(index)
    }


    fun unfollow(other: User) {
        var index: Int? = null

        for (i in 0 until this.follow.size) {
            if (this.follow[i] == other.id) {
                index = i
            }
        }

        if (index != null)
            this.follow.removeAt(index)
    }

    fun following(other: User): Boolean {
        return this.follow.contains(other.id)
    }


    constructor() {

    }
}