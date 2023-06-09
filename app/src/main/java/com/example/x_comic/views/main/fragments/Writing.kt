package com.example.x_comic.views.main.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.x_comic.R
import com.example.x_comic.adapters.*
import com.example.x_comic.models.*
import com.example.x_comic.viewmodels.FirebaseAuthManager
import com.example.x_comic.viewmodels.ProductViewModel
import com.example.x_comic.viewmodels.UserViewModel
import com.example.x_comic.views.main.MainActivity
import com.example.x_comic.views.post.NewChapterActivity
import com.example.x_comic.views.post.PostNewActivity
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.material.tabs.TabLayout


class Writing : Fragment() {

    var REQUEST_CODE_PICK_PRODUCT = 1111
    var REQUEST_CODE_PICK_UPDATE_PRODUCT = 2222
    val bookList: MutableList<BookSneek> = mutableListOf()


    var customBookListView: RecyclerView? = null;
    var scrollView: NestedScrollView? = null;
    var btnEditBook : Button? = null;
    var btnNewBook : Button? = null;
    var _user : User = User()

    private lateinit var productViewModel: ProductViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_writing, container, false)

        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        customBookListView = view.findViewById(R.id.listViewWriting) as RecyclerView;
        scrollView = view.findViewById(R.id.nestedScrollView);
        btnNewBook = view.findViewById(R.id.btnWriteNewBook)

        userViewModel.callApi(FirebaseAuthManager.getUser()!!.uid)
            .observe(this, Observer { user ->
                run {
                    _user = user
                    productViewModel.getAuthBook(_user)
                        .observe(this, Observer { products ->
                            run {
                                val bookListAdapter = BooksAuthAdapter(products);
                                customBookListView!!.adapter = bookListAdapter;
                                customBookListView!!.layoutManager = LinearLayoutManager(this.context);
                                val itemDecoration: RecyclerView.ItemDecoration =
                                    DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
                                customBookListView?.addItemDecoration(itemDecoration)

                                bookListAdapter!!.onItemClick = {book, position ->
                                    // TODO: Edit activity
                                    book.chapters = ArrayList()
                                    val intent = Intent(requireContext(), PostNewActivity::class.java)
                                    intent.putExtra(Product.MESSAGE1, book)
                                    startActivityForResult(intent, REQUEST_CODE_PICK_UPDATE_PRODUCT)
                                }
                            }
                        })
                }
            })

        btnNewBook?.setOnClickListener {
            // DO SOMETHING
            val intent = Intent(requireContext(), PostNewActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_PICK_PRODUCT)
//            (activity as MainActivity).replaceFragment(PostNewActivity())
        }





//        return inflater.inflate(R.layout.fragment_writing, container, false)
        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_UPDATE_PRODUCT && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val reply = data!!.getSerializableExtra(Product.MESSAGE2) as Product
            val isDelete = data!!.getBooleanExtra("DELETE", false)


            if (isDelete) {
                productViewModel.deleteProduct(reply)
            } else {
                // TODO: Update book tren BD
                productViewModel.updateAuthor(reply.id, reply.author)
                productViewModel.updateTitle(reply.id, reply.title)
                productViewModel.updateTinyDes(reply.id, reply.tiny_des)
                productViewModel.updateCover(reply.id, reply.cover)
                productViewModel.updateStatus(reply.id, reply.status)
                productViewModel.updateHide(reply.id, reply.hide)
                productViewModel.updateAge(reply.id, reply.age)
                productViewModel.updateCategory(reply.id, reply.categories)

//                productViewModel.updateProduct(reply)
            }
        }

        if (requestCode == REQUEST_CODE_PICK_PRODUCT && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val reply = data!!.getSerializableExtra(Product.MESSAGE2) as Product

            // TODO: Post book len DB
            _user.addToCollection(reply)
            userViewModel.saveCollection(_user)
            productViewModel.updateAuthor(reply.id, reply.author)
            productViewModel.updateTitle(reply.id, reply.title)
            productViewModel.updateTinyDes(reply.id, reply.tiny_des)
            productViewModel.updateCover(reply.id, reply.cover)
            productViewModel.updateStatus(reply.id, reply.status)
            productViewModel.updateHide(reply.id, reply.hide)
            productViewModel.updateAge(reply.id, reply.age)
            productViewModel.updateCategory(reply.id, reply.categories)

//            productViewModel.updateProduct(reply)
        }
    }
}