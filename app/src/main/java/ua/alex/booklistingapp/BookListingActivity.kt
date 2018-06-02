package ua.alex.booklistingapp

import android.app.LoaderManager
import android.app.SearchManager
import android.content.AsyncTaskLoader
import android.content.Context
import android.content.Loader
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SearchView

class BookListingActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<Book>> {


    companion object {
        val BOOKS_REQUEST = "https://www.googleapis.com/books/v1/volumes?q=%s&maxResults=10"
        val QUERY_TEXT_BUNDLE_KEY = "QUERY_TEXT_BUNDLE_KEY"
        val LOG_TAG: String = BookListingActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_listing)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = menu!!.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnSearchClickListener {
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (!checkConnection()) {
                    Snackbar.make(findViewById<LinearLayout>(R.id.parentLinearLayout),
                            "Connection error!",
                            Snackbar.LENGTH_SHORT).show()
                    return false
                }
//                updateUi(listOf(Book("Angels and Demons", "Dan Brown", "1996", "150", Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)),
//                        Book("The Da Vinci Code", "Dan Brown", "1996", "150", Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)),
//                        Book("Lord of the Rings", "J. R. Tolkien", "1996", "150", Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)),
//                        Book("Harry Potter", "J. K. Rowling", "1996", "150", Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888))))
                val bundle = Bundle()
                bundle.putString(QUERY_TEXT_BUNDLE_KEY, p0)

                if (loaderManager.getLoader<List<Book>>(0) == null)
                    loaderManager.initLoader<List<Book>>(0, bundle, this@BookListingActivity).forceLoad()
                else
                    loaderManager.restartLoader(0, bundle, this@BookListingActivity)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        return true
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<List<Book>> {
        return BookLoader(this, String.format(BOOKS_REQUEST, p1!![QUERY_TEXT_BUNDLE_KEY]))
    }

    override fun onLoadFinished(p0: Loader<List<Book>>?, p1: List<Book>?) {
        if (p1 != null) {
            if (p1.isEmpty()) {

            } else {
                updateUi(p1)
            }
        }
    }

    override fun onLoaderReset(p0: Loader<List<Book>>?) {
    }

    private fun updateUi(books: List<Book>) {
        val bookListView = findViewById<ListView>(R.id.book_list_view)

        val bookAdapter = BookAdapter(this, books)

        bookListView.adapter = bookAdapter
    }

    private fun checkConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private class BookLoader(context: Context?, stringUrl: String) : AsyncTaskLoader<List<Book>>(context) {
        val requestUrl = stringUrl

        override fun onStartLoading() {
            forceLoad()
        }

        override fun loadInBackground(): List<Book> {
            val books = QueryUtils.fetchBookData(requestUrl)
            return books
        }

    }
}
