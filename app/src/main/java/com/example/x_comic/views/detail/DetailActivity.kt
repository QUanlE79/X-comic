package com.example.x_comic.views.detail

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.x_comic.R
import com.example.x_comic.models.Book
import com.example.x_comic.views.main.fragments.*

class DetailActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intent = intent
        val stringData = intent.getStringExtra("book_data")
        val bookData = Book.fromString(stringData!!)

        var title = findViewById(R.id.book_title) as TextView;
        var author = findViewById(R.id.book_author) as TextView;
        var cover = findViewById(R.id.book_cover) as ImageView;
        var view = findViewById(R.id.viewTextView) as TextView;
        var favorite = findViewById(R.id.favoriteTextView) as TextView;
        var chapter = findViewById(R.id.numOfChapterTextView) as TextView;
        var category1 = findViewById(R.id.category_tag) as TextView;
        var category2 = findViewById(R.id.category_tag2) as TextView;
        var category3 = findViewById(R.id.category_tag3) as TextView;
        var rest = findViewById(R.id.rest) as TextView;
        var status = findViewById(R.id.statusTextView) as TextView;
        var age = findViewById(R.id.ageTextView) as TextView;
        var ratingTextView = findViewById(R.id.ratingTextView) as TextView;
        var chooseChapterBtn = findViewById(R.id.chooseChapterBtn) as EditText;


        title.text = bookData.book.title
        author.text = bookData.book.author
        cover.setImageResource(bookData.book.cover)
        view.text = bookData.view.toString()
        favorite.text = bookData.favorite.toString()
        chapter.text = bookData.chapter.toString()
        status.text = "Satatus: Done"
        age.text = "Age: 16+"
        ratingTextView.text = "Rating: " + bookData.book.rating
        chooseChapterBtn.setText("☰ " +  bookData.chapter.toString()  +" Chapters")

        category1.text = bookData.genres[0]
        category2.text = bookData.genres[1]
        category3.text = bookData.genres[2]
        var category_holder = arrayListOf(category1,category2,category3)
        val category = bookData.genres.take(3);
        for (i in category) {
            when (i){
                "Romance" -> {
                    category_holder[category.indexOf(i)].setText(i)
                    category_holder[category.indexOf(i)].backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this,
                        R.color.love
                    ));
                }
                "Fiction" -> {
                    category_holder[category.indexOf(i)].setText(i)
                    category_holder[category.indexOf(i)].backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this,
                        R.color.yellow_green
                    ));
                }
                "Short Story" -> {
                    category_holder[category.indexOf(i)].setText(i)
                    category_holder[category.indexOf(i)].backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this,
                        R.color.light_blue
                    ));
                }
                "Mystery" -> {
                    category_holder[category.indexOf(i)].setText(i)
                    category_holder[category.indexOf(i)].backgroundTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(this, R.color.violet));
                }
                "Thriller" -> {
                    category_holder[category.indexOf(i)].setText(i)
                    category_holder[category.indexOf(i)].backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this,
                        R.color.golden
                    ));
                }
                "Horror" -> {
                    category_holder[category.indexOf(i)].setText(i)
                    category_holder[category.indexOf(i)].backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this,
                        R.color.purple_500
                    ));
                }
                "Humor" -> {
                    category_holder[category.indexOf(i)].setText(i)
                    category_holder[category.indexOf(i)].backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this,
                        R.color.pink
                    ));
                }
                else -> {
                    category_holder[category.indexOf(i)].setText(i)
                    category_holder[category.indexOf(i)].backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this,
                        R.color.lightgrey
                    ));
                }


            }
        }

        rest.setText(if ((bookData.genres.size -3)>0) "+ ${bookData.genres.size -3} more" else "");

        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}