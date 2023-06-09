package com.example.x_comic.views.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.x_comic.R
import com.example.x_comic.adapters.*
import com.example.x_comic.databinding.ActivityMainProfileBinding
import com.example.x_comic.models.Product
import com.example.x_comic.models.Reading
import com.example.x_comic.models.User
import com.example.x_comic.viewmodels.FirebaseAuthManager
import com.example.x_comic.viewmodels.ProductViewModel
import com.example.x_comic.viewmodels.UserViewModel
import com.example.x_comic.views.detail.DetailActivity
import com.example.x_comic.views.more.AuthorListActivity
import com.example.x_comic.views.more.FavoriteActivity
import com.example.x_comic.views.more.ReadingActivity
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.IOException

class MainProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainProfileBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var productViewModel: ProductViewModel
    val bookList: MutableList<Product> = mutableListOf()
    val readingList: MutableList<Product> = mutableListOf()
    val followList: MutableList<User> = mutableListOf()
    private var REQUEST_CODE_PICK_IMAGE = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.settingImg.setOnClickListener {
            nextSettingActivity()
        }


        binding.FavoriteListView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.profileListView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.readingListView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        val favoriteAdapter = ProfileBookListAdapter(bookList)
        val readingAdapter = ProfileBookListAdapter(readingList)
        val avatarAdapter = AvatarListAdapter(followList)

        readingAdapter.onItemClick = {
            book -> nextBookDetailActivity(book)
        }

        favoriteAdapter.onItemClick = {
                book -> nextBookDetailActivity(book)
        }

        avatarAdapter.onItemClick = {
            author -> nextAuthorProfileActivity(author)
        }

        binding.FavoriteListView.adapter = favoriteAdapter
        binding.profileListView.adapter = avatarAdapter
        binding.readingListView.adapter = readingAdapter

        binding.avtImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        val uid = FirebaseAuthManager.auth.uid

        binding.favoriteLL.setOnClickListener {
            nextFavoriteActivity()
        }
        binding.readingLL.setOnClickListener {
            nextReadingActivity()
        }
        binding.followLL.setOnClickListener {
            nextFollowActivity()
        }

        if (uid != null) {
            userViewModel.getAllUserIsFollowed(uid) {
                usersID -> run {
                    followList.clear()
                    var cnt: Int = 0
                    for (userid in usersID.children) {
                        userViewModel.getUserById(userid.value as String) {
                            user -> run {
                                if (user.isfollowed(uid)) {
                                    cnt++
                                    followList.add(user)
                                }
                            }
                            avatarAdapter.notifyDataSetChanged()
                            binding.followProfileTV.text = "${cnt} Profiles"
                        }
                        avatarAdapter.notifyDataSetChanged()
                    }
                }
            }

            productViewModel.getAllBookIsLoved(uid) {
                booksID -> run {
                    bookList.clear()
                    var cnt: Int = 0
                    for (bookid in booksID.children) {
                        productViewModel.getBookById(bookid.value as String) {
                            book -> run {
                                Log.d("BOOKKKKK", book.title)
                                if (book.islove(uid) && !book.hide) {
                                    cnt++
                                    bookList.add(book)
                                }
                            }
                            favoriteAdapter.notifyDataSetChanged()
                            binding.loveTV.text = "${cnt} Stories"
//                            binding.followProfileTV.text = "${cnt} book"
                        }
                        favoriteAdapter.notifyDataSetChanged()
                    }
                }
            }

            productViewModel.getAllReadingBook(uid) {
                    list -> run {
                    readingList.clear()
                    val readings = ArrayList<Reading>()
                    var cnt: Int = 0
                    for (item in list.children) {
                        var reading = item.getValue(Reading::class.java)
                        if (reading != null) {
                            readings.add(reading)
                        }
                        productViewModel.getBookById(reading?.id_book as String) {
                            book -> run {
                                if (!book.hide) {
                                    cnt++
                                    readingList.add(book)
                                }
                                }
                            readingAdapter.notifyDataSetChanged()
                            binding.readingTV.text = "${cnt} Stories"
                        }
                        readingAdapter.notifyDataSetChanged()
                    }
                }
            }

            userViewModel.callApi(uid)
                .observe(this, Observer {
                    // user nó được thay đổi realtime ở đb
                        user ->
                    run {
                        binding.favoriteTextView.text = "Favorites of ${user.full_name}"
                        binding.emailTV.text = user.email
                        binding.usernameTV.text = user.full_name
                        binding.aboutme.text = user.aboutme

                        if (user.avatar != "") {
                            Glide.with(binding.avtImg.context)
                                .load(user.avatar)
                                .apply(RequestOptions().override(100, 100))
                                .circleCrop()
                                .into(binding.avtImg)

                            Glide.with(binding.background.context)
                                .load(user.avatar)
                                .apply(RequestOptions.bitmapTransform(BlurTransformation(30, 3)))
                                .into(binding.background)
                        } else {
                            // nếu mà chưa có avt thì mặc định render vầy :v
                            Glide.with(binding.avtImg.context)
                                .load(R.drawable.avatar)
                                .apply(RequestOptions().override(100, 100))
                                .circleCrop()
                                .into(binding.avtImg)
                        }

                        binding.followNumber.text = user.have_followed.size.toString()
                        binding.readListNumber.text = user.reading.size.toString()
                    }
                })
        }
        if (uid != null) {
            userViewModel.getAvt(uid, binding.avtImg, binding.background)
        }
    }

    private fun nextFollowActivity() {
        val intent = Intent(this, AuthorListActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            // Lưu ảnh vào profile
            saveImageToProfile(imageUri)
        }
    }

    private fun saveImageToProfile(imageUri: Uri?) {
        try {
            val uid = FirebaseAuthManager.auth.uid
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            if (uid != null) {
                // upload avt lên storage
                var uploadTask = userViewModel.uploadAvt(uid, bitmap)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()

                        // change avt ở realtime db
                        userViewModel.changeAvt(downloadUrl)
                    }
                }.addOnFailureListener { exception ->
                    // Tải lên ảnh thất bại
                    exception.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun nextSettingActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun nextFavoriteActivity() {
        val intent = Intent(this, FavoriteActivity::class.java)
        startActivity(intent)
    }

    private fun nextReadingActivity() {
        val intent = Intent(this, ReadingActivity::class.java)
        startActivity(intent)
    }
    fun nextAuthorProfileActivity(author: User) {
        val intent = Intent(this, AuthorProfileActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("authorKey", author)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun nextBookDetailActivity(book: Product) {
        val intent = Intent(this, DetailActivity::class.java)
//        val bundle = Bundle()
//        bundle.putSerializable("productKey", book)
        intent.putExtra("book_data", book.id)

        startActivity(intent)
    }
}