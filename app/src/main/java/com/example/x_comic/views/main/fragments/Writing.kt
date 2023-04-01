package com.example.x_comic.views.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.x_comic.R
import com.example.x_comic.adapters.BookListAdapter
import com.example.x_comic.models.Avatar
import com.example.x_comic.models.Book
import com.example.x_comic.models.BookAuthor
import com.example.x_comic.models.BookSneek
import com.example.x_comic.views.main.MainActivity
import com.example.x_comic.views.post.PostNewActivity

/**
 * A simple [Fragment] subclass.
 * Use the [Writing.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class Writing : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val bookList: MutableList<BookSneek> = mutableListOf(
        BookSneek("How to Burn The Bad Boy", "alsophanie", R.drawable.bookcover, 4.9),
        BookSneek("Temporarily", "bbiboo123", R.drawable.book_cover_1, 4.5),
        BookSneek("Rome Is Us", "ann_beyond", R.drawable.book_cover_2, 4.0),
        BookSneek("Fool Man", "landyshere", R.drawable.book_cover_3, 4.7),
        BookSneek("The Mind Of A Leader", "vivianneee", R.drawable.book_cover_4, 4.3),
        BookSneek("The Light Beyond The Garden Wall", "pixiequinn", R.drawable.book_cover_5, 4.5),
        BookSneek("The Secret At The Joneses", "rhjulxie", R.drawable.book_cover_6, 3.9),
        BookSneek("The Victim's Picture", "gizashey", R.drawable.book_cover_7, 4.2)

    )

    var bookDetailList: MutableList<Book> = mutableListOf(

        Book(
            bookList[0],
            253.2,
            125.5,
            20,
            arrayListOf("Romance", "Thriller", "Short Story", "Humor")
        ),
        Book(bookList[1], 154.4, 100.3, 50, arrayListOf("Fiction", "Horror", "Mystery", "Humor")),
        Book(bookList[2], 179.6, 122.2, 13, arrayListOf("School Life", "Humor", "Short Story")),
        Book(
            bookList[3],
            211.3,
            112.6,
            7,
            arrayListOf("Romance", "Mystery", "Short Story", "Humor")
        ),
        Book(
            bookList[4],
            236.2,
            109.7,
            36,
            arrayListOf("Fiction", "Thriller", "Mystery", "Horror", "Humor", "Romance")
        )
    )

    var bookAuthorlList: MutableList<BookAuthor> = mutableListOf(

        BookAuthor(bookDetailList[0], 1, 0),
        BookAuthor(bookDetailList[1], 2, 1),
        BookAuthor(bookDetailList[2], 3, 1),
        BookAuthor(bookDetailList[3], 4, 0),
        BookAuthor(bookDetailList[4], 5, 1),
    )

    var customBookListView: RecyclerView? = null;
    var scrollView: NestedScrollView? = null;
    var btnEditBook : Button? = null;
    var btnNewBook : Button? = null;

    class BooksAdapter (private val books: MutableList<BookAuthor>) : RecyclerView.Adapter<BooksAdapter.ViewHolder>() {
        var onItemClick: ((book: BookAuthor, position: Int) -> Unit)? = null
        var onButtonClick: ((BookAuthor) -> Unit)? = null
        inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
            val title = listItemView.findViewById(R.id.book_list_title) as TextView
            val imageView: ImageView = listItemView.findViewById(R.id.cover_book) as ImageView
            val status = listItemView.findViewById(R.id.book_list_status) as TextView
            val chapter = listItemView.findViewById(R.id.book_list_chapter) as TextView

            init {
                listItemView.setOnClickListener { onItemClick?.invoke(books[adapterPosition], adapterPosition) }

            }

        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            // Inflate the custom layout
            val studentView = inflater.inflate(R.layout.book_status_writting, parent, false)
            // Return a new holder instance
            return ViewHolder(studentView)
        }

        override fun getItemCount(): Int {
            return books.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Get the data model based on position
            val s: BookAuthor = books.get(position)
            // Set item views based on your views and data model

            holder.title.text = books[position].book.book.title
            if (books[position].status == 0) {
                holder.status.text = "In progress"
                holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.yellow));
            } else {
                holder.status.text = "Done"
                holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            }
            holder.chapter.text = "${books[position].chapterIsPosted} of ${books[position].book.chapter} Chapter was posted"
            holder.imageView.setImageResource(books[position].book.book.cover)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_writing, container, false)
        customBookListView = view.findViewById(R.id.listViewWriting) as RecyclerView;
        scrollView = view.findViewById(R.id.nestedScrollView);
        btnEditBook = view.findViewById(R.id.btnEditBook)
        btnNewBook = view.findViewById(R.id.btnWriteNewBook)

        val bookListAdapter = BooksAdapter(bookAuthorlList);
        customBookListView!!.adapter = bookListAdapter;
        customBookListView!!.layoutManager = LinearLayoutManager(this.context);
        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        customBookListView?.addItemDecoration(itemDecoration)

        bookListAdapter!!.onItemClick = {book, position ->
            // DO SOMETHING
        }

        btnNewBook?.setOnClickListener {
            // DO SOMETHING

//            val intent = Intent(requireContext(), PostNewActivity::class.java)
//            startActivity(intent)
            (activity as MainActivity).replaceFragment(PostNewActivity())
        }

        btnEditBook?.setOnClickListener {
            // DO SOMETHING
        }

//        return inflater.inflate(R.layout.fragment_writing, container, false)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Writing.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Writing().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}