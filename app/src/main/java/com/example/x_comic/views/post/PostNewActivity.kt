package com.example.x_comic.views.post

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.x_comic.R
import com.example.x_comic.adapters.CategoryAdapter
import com.example.x_comic.adapters.CategoryChooseAdapter
import com.example.x_comic.adapters.ChaptersAdapter
import com.example.x_comic.models.Category
import com.example.x_comic.models.Chapter
import com.example.x_comic.models.Product
import com.example.x_comic.models.User
import com.example.x_comic.viewmodels.*
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class PostNewActivity : AppCompatActivity() {
    var REQUEST_CODE_PICK_IMAGE = 1111
    var REQUEST_CODE_PICK_CHAPTER = 2222
    var REQUEST_CODE_UPDATE_CHAPTER = 3333

    var fileNameCover : String = ""
    var age : Int = 0
    var _user : User = User()
    private var curBook : Product = Product()
    var is_new = true
    var is_exists = false

    lateinit var chapterListAdapter : ChaptersAdapter

    var categoryList : ArrayList<Category> = ArrayList()
    var categoryListChoose : ArrayList<Category> = ArrayList()
    var chapterList: MutableList<Chapter> = mutableListOf()

    var customChapterListView: RecyclerView? = null;
    var categoryView : RecyclerView? = null;

    val ageRanges = listOf(
        "0-2 years" to "Board books and picture books" to 2,
        "3-5 years" to "Picture books and early readers" to 5,
        "6-8 years" to "Chapter books and easy readers" to 8,
        "9-12 years" to "Middle-grade novels" to 12,
        "13+ years" to "Young adult and adult novels" to 13,
        "18+ years" to "Adult novels" to 18
    )

    private lateinit var productViewModel: ProductViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var chapterViewModel: ChapterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        chapterViewModel = ViewModelProvider(this).get(ChapterViewModel::class.java)
        setContentView(R.layout.activity_post_new)

        // Spinner Age
        spinnerAgeView()

        var id = Firebase.database.getReference().push().key
        if (id != null) {
            curBook.id = id
        }

        var cover = findViewById<ImageView>(R.id.ivCover)
        cover.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }


        var intent = intent
        val _book = intent.getSerializableExtra(Product.MESSAGE1) as? Product
        _book?.let {
            curBook = _book

            is_new = false
            findViewById<TextView>(R.id.title).text = "Update Book"
            findViewById<Button>(R.id.btnNext).text = "SAVE"

            Glide.with(cover.context)
                .load(curBook.cover)
                .apply(RequestOptions().override(500, 600))
                .into(cover)
            fileNameCover = curBook.cover

            findViewById<EditText>(R.id.etTitle).setText(curBook.title.toString())
            findViewById<EditText>(R.id.etDescription).setText(curBook.tiny_des.toString())
            findViewById<Switch>(R.id.sStatus).isChecked = curBook.status
            findViewById<Switch>(R.id.sIsHide).isChecked =  curBook.hide
            findViewById<Button>(R.id.btnDeleteBook).visibility = View.VISIBLE

            val curIndex = ageRanges.indexOfFirst { it.second == curBook.age }
            val index = if (curIndex == -1) ageRanges.lastIndex else curIndex
            findViewById<Spinner>(R.id.ageSpinner).setSelection(index)

            categoryListChoose = curBook.categories

            productViewModel.getBookById(curBook.id) {book -> kotlin.run {
                chapterList.clear()
                for (i in book.chapters) {
                    if (i != null) {
                        chapterList.add(i)
                    }
                }
                chapterListAdapter.notifyDataSetChanged()
            }
            }
        }

        // Category
        categoryView()
        // Chapter
        chapterView()

        val uid = FirebaseAuthManager.auth.uid
        if (uid != null) {
            userViewModel.callApi(uid)
                .observe(this, Observer { user ->
                    run {
                        _user = user
                    }
                })
        }

        val addCoverBtn = findViewById<ImageButton>(R.id.btnNewCover)
        addCoverBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        val nextButton = findViewById<Button>(R.id.btnNext)
        nextButton.setOnClickListener {

            if (is_new && !is_exists) {
                var temp = Product()
                temp.id = curBook.id

                productViewModel.updateProduct(temp)
            }

            //TODO: Return book
            val replyIntent = Intent()
            replyIntent.putExtra(Product.MESSAGE2, getCurBook())
            setResult(Activity.RESULT_OK, replyIntent)
            finish()
        }

        val backButton = findViewById<TextView>(R.id.imgbtnBack)
        backButton.setOnClickListener {
            finish()
        }

        val newChapterButton = findViewById<Button>(R.id.btnNewChapter)
        newChapterButton.setOnClickListener {

            curBook?.let {
                val intent = Intent(this, NewChapterActivity::class.java)
                intent.putExtra(Chapter.MESSAGE5, curBook.id)
                startActivityForResult(intent, REQUEST_CODE_PICK_CHAPTER)
            }
        }

        val deleteButton = findViewById<Button>(R.id.btnDeleteBook)
        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to delete?")
            builder.setPositiveButton("Yes") { dialog, which ->
                // Handle user's positive response
                val replyIntent = Intent()
                replyIntent.putExtra(Product.MESSAGE2, getCurBook())
                replyIntent.putExtra("DELETE", true)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->
                // Handle user's negative response
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun categoryView () {
        val layoutManager = FlexboxLayoutManager(this);
        categoryView = findViewById(R.id.category_list);
        categoryViewModel.getAll()
            .observe(this, Observer { categories ->
                run {
                    val adapter = CategoryChooseAdapter(categories, categoryListChoose);
                    categoryView!!.adapter = adapter

                    layoutManager!!.flexWrap = FlexWrap.WRAP;
                    layoutManager!!.flexDirection = FlexDirection.ROW;
                    layoutManager!!.alignItems = AlignItems.FLEX_START;
                    categoryView!!.layoutManager = layoutManager;

                    categoryList = adapter!!.getAllItem()
                }
            })
    }

    private fun spinnerAgeView () {
        val ageSpinner = findViewById<Spinner>(R.id.ageSpinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ageRanges.map { it.first.first })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ageSpinner.adapter = adapter
        ageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedAgeRange = ageRanges[position]
                val recommendedReadingLevel = selectedAgeRange.second
                age = selectedAgeRange.second
                // TODO: Chọn tuổi
                Toast.makeText(applicationContext, "Recommended reading level: $recommendedReadingLevel", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun chapterView () {
        customChapterListView = findViewById(R.id.listViewChapter) as RecyclerView;
        chapterListAdapter = ChaptersAdapter(chapterList);
        customChapterListView!!.adapter = chapterListAdapter;
        customChapterListView!!.layoutManager = LinearLayoutManager(this);
        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        customChapterListView?.addItemDecoration(itemDecoration)

        chapterListAdapter!!.onItemClick = {chapter, position ->
            val intent = Intent(this, NewChapterActivity::class.java)
            intent.putExtra(Chapter.MESSAGE1, position)
            intent.putExtra(Chapter.MESSAGE3, chapter)
            intent.putExtra(Chapter.MESSAGE5, curBook.id)
            startActivityForResult(intent, REQUEST_CODE_UPDATE_CHAPTER)
        }
    }

    private fun getCurBook () : Product {

//        curBook.author = _user.penname
        curBook.author = _user.id
        curBook.title = findViewById<EditText>(R.id.etTitle).text.toString()
        curBook.tiny_des = findViewById<EditText>(R.id.etDescription).text.toString()
        curBook.status = findViewById<Switch>(R.id.sStatus).isChecked
        curBook.hide = findViewById<Switch>(R.id.sIsHide).isChecked
        curBook.age = age
        curBook.cover = fileNameCover
        curBook.categories = categoryList
        curBook.chapters = ArrayList()

        return curBook
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            // Lay ten file cua anh
            fileNameCover = curBook.id +".png"
            // Lưu ảnh vào profile
            saveImageToDB(imageUri, fileNameCover)
            val cover = findViewById<ImageView>(R.id.ivCover)
            Glide.with(cover.context)
                .load(imageUri)
                .apply(RequestOptions().override(500, 600))
                .into(cover)
        }

        if (requestCode == REQUEST_CODE_PICK_CHAPTER && resultCode == RESULT_OK && data != null) {
            val reply = data!!.getSerializableExtra(Chapter.MESSAGE2) as Chapter

            if (is_new) {
                var temp = Product()
                temp.id = curBook.id
                is_exists = true
                productViewModel.updateProduct(temp)
            }

            chapterList.add(reply)
            chapterListAdapter?.notifyDataSetChanged()
            for (i in chapterList)
                productViewModel.updateChapter(i.id_book,chapterList.indexOf(i),i)

        }

        if (requestCode == REQUEST_CODE_UPDATE_CHAPTER && resultCode == RESULT_OK && data != null) {
            val reply = data!!.getSerializableExtra(Chapter.MESSAGE2) as Chapter
            val index = data!!.getIntExtra(Chapter.MESSAGE4, -1) as Int
            if (index == -1) {
                var temp : Chapter? = null;
                for (i in chapterList)
                    if (i.id_chapter.equals(reply.id_chapter)) {
                        temp = i
                    }
                temp?.let {
                    chapterList.remove(temp);
                    productViewModel.deleteAllChapter(temp.id_book);
                    for (i in chapterList)
                        productViewModel.updateChapter(i.id_book,chapterList.indexOf(i),i)
                }
            } else {
                productViewModel.updateChapter(reply.id_book, index, reply);
                chapterList[index] = reply
            }

            chapterListAdapter?.notifyDataSetChanged()
        }
    }

    private fun saveImageToDB(imageUri: Uri?, filename : String) {
        try {
            val uid = FirebaseAuthManager.auth.uid
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            if (uid != null) {
                var uploadTask = productViewModel.uploadCover(filename, bitmap, findViewById(R.id.ivCover))
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()

                        fileNameCover = downloadUrl
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
}