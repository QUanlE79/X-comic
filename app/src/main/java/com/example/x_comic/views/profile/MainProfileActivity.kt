package com.example.x_comic.views.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.x_comic.R
import com.example.x_comic.adapters.ListAdapterSlideshow
import com.example.x_comic.databinding.ActivityMainProfileBinding
import com.example.x_comic.databinding.ActivityProfileBinding
import com.example.x_comic.models.Product
import com.example.x_comic.viewmodels.FirebaseAuthManager
import com.example.x_comic.viewmodels.ProductViewModel
import com.example.x_comic.viewmodels.UserViewModel
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.IOException

class MainProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainProfileBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var productViewModel: ProductViewModel
    val bookList: MutableList<Product> = mutableListOf()
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


        binding.listView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        productViewModel.getLatestBook().observe(this, Observer {
            products -> run {
            bookList.clear()
            bookList.addAll(products)
            val adapter = ListAdapterSlideshow(this, bookList);

            binding.listView.adapter = adapter
            }
        })

        binding.avtImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        val uid = FirebaseAuthManager.auth.uid
        if (uid != null) {
            userViewModel.callApi(uid)
                .observe(this, Observer {
                    // user nó được thay đổi realtime ở đb
                        user ->
                    run {
                        binding.emailTV.text = user.email
                        binding.usernameTV.text = user.full_name

                        if (user.avatar != "") {
                            Glide.with(binding.avtImg.context)
                                .load(user.avatar)
                                .apply(RequestOptions().transform(CenterCrop()).transform(RoundedCorners(150)))
                                .into(binding.avtImg)

                            Glide.with(binding.background.context)
                                .load(user.avatar)
                                .apply(RequestOptions.bitmapTransform(BlurTransformation(30, 3)))
                                .into(binding.background)
                        } else {
                            Glide.with(binding.avtImg.context)
                                .load(R.drawable.avatar)
                                .apply(RequestOptions().transform(CenterCrop()).transform(RoundedCorners(150)))
                                .into(binding.avtImg)
                        }
                    }
                })
        }
        if (uid != null) {
            userViewModel.getAvt(uid, binding.avtImg, binding.background)
        }
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
                userViewModel.uploadAvt(uid, bitmap, binding.avtImg, binding.background)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun nextSettingActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }
}