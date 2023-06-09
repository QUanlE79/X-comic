package com.example.x_comic.views.main.fragments


import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.Klaxon
import com.example.x_comic.R
import com.example.x_comic.adapters.BookReadingAdapter

import com.example.x_comic.models.BookReading

import com.example.x_comic.viewmodels.FirebaseAuthManager
import com.example.x_comic.viewmodels.ProductViewModel
import com.example.x_comic.viewmodels.UserViewModel
import com.example.x_comic.views.detail.DetailActivity
import com.example.x_comic.views.read.ReadBookActivity

var listReading: MutableList<BookReading> = mutableListOf();
lateinit var online_number: TextView
class Reading : Fragment() {

    var listReadingOffline: MutableList<BookReading> = mutableListOf()

    private lateinit var userViewModel: UserViewModel
    private lateinit var productViewModel: ProductViewModel



    var customOnlineBookList: RecyclerView? = null;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_reading, container, false)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)


        customOnlineBookList = view.findViewById(R.id.online_books);
        online_number = view.findViewById<TextView>(R.id.number_online)


        val OnlineAdapter = BookReadingAdapter(listReading, online_number);




        customOnlineBookList!!.adapter = OnlineAdapter;



        customOnlineBookList!!.layoutManager = GridLayoutManager(this.context,3);
        listReading.clear();



        val uid = FirebaseAuthManager.auth.uid
        if (uid != null) {

            productViewModel.getAllReadingBook(uid) { booksID ->
                run {
                    listReading.clear();
                    var cnt: Int = 0


                    for (snapshot in booksID.children) {

                                var bookid = snapshot.getValue(com.example.x_comic.models.Reading::class.java)

                                productViewModel.getBookById(bookid!!.id_book) { bookInner ->
                                    run {

                                        if ( bookInner!=null && !bookInner.hide) {

                                            cnt++
                                            listReading.add(
                                                BookReading(
                                                    bookInner,
                                                    bookid.posChap,
                                                    bookid.numChap
                                                )

                                            )

                                            println(listReading);

                                      //OnlineAdapter.notifyItemInserted(listReading.size);
                                        OnlineAdapter.notifyDataSetChanged()
                                        }
                                   //     OnlineAdapter.notifyDataSetChanged()

                                    }
                                  //
                                    val storyText = if (cnt == 1) "Story" else "Stories"
                                    online_number.text = "${cnt} $storyText"





                                }




                    }

                    OnlineAdapter.notifyDataSetChanged();



                }


            }

        }


        OnlineAdapter.onItemClick = {
                book -> nextBookDetailActivity(book)
        }

        OnlineAdapter.setOnItemLongClickListener {
            book, position ->
            val dialog = BookDialog(book, position, OnlineAdapter);
            dialog.show((context as? FragmentActivity)!!.supportFragmentManager,"dbchau10");
        }


        return view
    }

    fun nextBookDetailActivity(book: BookReading) {

        if (book != null) {

            val intent = Intent(context, ReadBookActivity::class.java)
            intent.putExtra("book", book.book!!.id)
            intent.putExtra("id_chapter", book.book!!.chapters[book.current - 1].id_chapter)
            ActivityCompat.startActivityForResult(requireActivity(), intent, 302, null)
        } else {
            // Handle the case when the book is null
        }
    }





}


class BookDialog(private val book: BookReading, private val position: Int, private val adapter: BookReadingAdapter) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { // Use the Builder class for convenient dialog construction
            var builder = AlertDialog.Builder(it)
            builder.setTitle(book.book?.title)

                builder.setItems(arrayOf("Read", "Story Info", "Remove from Library"),
                    DialogInterface.OnClickListener { dialog, which ->
                        when(which){
                            0 -> {
                                nextBookDetailActivity(book)
                            }
                            1-> {
                                nextBookInfoActivity(book)
                            }
                            2-> {
                                removeBook(position, adapter)
                            }
                            else -> {

                            }
                        }

                    }) // Create the AlertDialog object and return it


            builder.create() } ?: throw IllegalStateException("Activity cannot be null") }

    private fun nextBookDetailActivity(book: BookReading) {
        val intent = Intent(context, ReadBookActivity::class.java)
        intent.putExtra("book", book.book!!.id)
        intent.putExtra("id_chapter", book.book!!.chapters[book.current - 1].id_chapter)
        ActivityCompat.startActivityForResult(requireActivity(), intent, 302, null)
    }

    private fun nextBookInfoActivity(book: BookReading) {
        if (book.book!=null) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("book_data", book.book!!.id)
            startActivity(intent)
        }
    }


    private fun removeBook(position: Int, adapter: BookReadingAdapter){
        var userViewModel: UserViewModel
        var productViewModel: ProductViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        val uid = FirebaseAuthManager.auth.uid
        if (uid != null) {
            productViewModel.getAllReadingBook(uid) { booksID ->
                run {
                    //  productViewModel.removeBookReading(uid,position)
                    val childrenList: ArrayList<com.example.x_comic.models.Reading> = arrayListOf();
                    for (snapshot in booksID.children){
                        var bookid = snapshot.getValue(com.example.x_comic.models.Reading::class.java)
                        if (bookid!=null) {
                            childrenList.add(bookid!!)
                        }
                    }
                    childrenList.removeAt(position);
                    listReading.removeAt(position)
                    val storyText = if (listReading.size == 1) "Story" else "Stories"
                    online_number.text = "${listReading.size} $storyText"
                    userViewModel.updateReadingUserList(childrenList)
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

}