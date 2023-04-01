package com.example.x_comic.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.x_comic.R
import com.example.x_comic.models.Book

class CategoryAdapter (
    private var categoryList: MutableList<String>,
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>()
{
    var onItemClick: ((String) -> Unit)? = null
    var context: Context? = null;
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        var genre = listItemView.findViewById(R.id.genre) as TextView;

        init {
            listItemView.setOnClickListener {
                onItemClick?.invoke(categoryList[adapterPosition])
            }
        }

    }

    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context;
        val inflater = LayoutInflater.from(context)
        var columnView =  inflater.inflate(R.layout.category, parent, false)
        return ViewHolder(columnView)

    }

    override fun getItemCount(): Int {
        return categoryList.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList.get(position);

        val genre = holder.genre;





        genre.setText(category);

        var pick = false;
        genre.setOnClickListener{
            pick = !pick;

            if (pick) {
                genre.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context!!,
                        R.color.azure
                    )
                );
                genre.setTextColor(ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context!!,
                        R.color.white
                    )))

            }
            else {
                genre.backgroundTintList = null;

                genre.setTextColor(ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context!!,
                        R.color.azure
                    )))
            }
        }

    }

}



