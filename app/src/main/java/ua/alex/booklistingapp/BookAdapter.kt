package ua.alex.booklistingapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 *
 * Created by aursaty on 5/21/2018.
 */
class BookAdapter(context: Context?, resource: Int, objects: MutableList<Book>?) : ArrayAdapter<Book>(context, resource, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var listItemView = convertView
        if (listItemView == null)
            listItemView = LayoutInflater.from(context).inflate(
                    R.layout.book_list_view_item, parent, false)

        val book = getItem(position)

        listItemView!!.findViewById<TextView>(R.id.book_title).text = book.title
        listItemView.findViewById<TextView>(R.id.author).text = book.author
        listItemView.findViewById<TextView>(R.id.publishedYear).text = book.publishYear
        listItemView.findViewById<TextView>(R.id.price).text = book.price

        return listItemView
    }
}