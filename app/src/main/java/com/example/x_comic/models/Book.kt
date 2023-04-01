package com.example.x_comic.models

data class Book(var book: BookSneek, var view: Double, var favorite: Double, var chapter: Int, var genres: ArrayList<String>)  {
    override fun toString(): String {
        return "[${book.title}, ${book.author}, ${book.cover}, ${book.rating}], $view, $favorite, $chapter, ${genres.joinToString(", ", "[", "]")}"
    }
    companion object{
        fun fromString(bookStr: String): Book {
            val pattern = """\[(.+),\s(.+),\s(\d+),\s([\d.]+)\],\s([\d.]+),\s([\d.]+),\s(\d+),\s\[(.+)]""".toRegex()
            val matchResult = pattern.find(bookStr) ?: throw IllegalArgumentException("Invalid Book string")
            val (title, author, cover, ratingStr, viewStr, favoriteStr, chapterStr, genresStr) = matchResult.destructured

            val bookSneek = BookSneek(
                title,
                author,
                cover.toInt(),
                ratingStr.toDouble()
            )
            val view = viewStr.toDouble()
            val favorite = favoriteStr.toDouble()
            val chapter = chapterStr.toInt()
            val genres = genresStr.split(", ")

            return Book(bookSneek, view, favorite, chapter, ArrayList(genres))
        }

    }
}