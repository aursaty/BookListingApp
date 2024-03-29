package ua.alex.booklistingapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 *
 * Created by aursaty on 5/25/2018.
 */
class QueryUtils {

    companion object {
        private val LOG_TAG = QueryUtils::class.java.simpleName

        fun fetchBookData(requestUrl: String): List<Book> {
            val url = createUrl(requestUrl)

            val responseString = makeHttpRequest(url)

            return extractBooks(responseString)
        }

        private fun createUrl(stringUrl: String): URL {
            return URL(stringUrl)
        }

        private fun makeHttpRequest(url: URL): String {
            val urlConnection = url.openConnection() as HttpURLConnection?
            urlConnection!!.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            var jsonResponse = ""

            if (urlConnection.responseCode == 200) {
                val inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.e(LOG_TAG, "Problem retrieving the books")
            }
            return jsonResponse
        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("utf-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }
            return output.toString()
        }

        private fun extractBooks(booksJson: String): List<Book> {
            if (booksJson.isEmpty())
                return ArrayList()

            val baseJsonResponse = JSONObject(booksJson)
            val bookItemsArray = try {
                baseJsonResponse.getJSONArray("items")
            } catch (e: JSONException) {
                return emptyList()
            }

            val bookList = ArrayList<Book>()
            var currentBookJson: JSONObject
            var volumeInfo: JSONObject
            var title: String
            var author: String
            var bitmap: Bitmap
//            var listPrice: JSONObject
            var imageLinks: JSONObject
            var urlThumbnail: URL
            var language: String
//            var saleability: String
            var publishYear: String
            for (i in 0 until bookItemsArray.length()) {
                currentBookJson = bookItemsArray.getJSONObject(i)
                volumeInfo = currentBookJson.getJSONObject("volumeInfo")
                title = volumeInfo.getString("title")
                author = try {
                    volumeInfo.getJSONArray("authors").get(0) as String
                } catch (e: JSONException) {
                    "Unknown"
                }
                try {
                    imageLinks = volumeInfo.getJSONObject("imageLinks")
                    urlThumbnail = URL(imageLinks.getString("thumbnail"))
                    bitmap = BitmapFactory.decodeStream(urlThumbnail.openConnection().getInputStream())
                } catch (e: JSONException) {
                    bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                }
                language = volumeInfo.getString("language")
//                saleability = currentBookJson.getJSONObject("saleInfo")
//                        .getString("saleability")
//                if (saleability == "FOR_SALE") {
//                    listPrice = currentBookJson.getJSONObject("saleInfo")
//                            .getJSONObject("listPrice")
//                    price = listPrice.getString("amount") +
//                            listPrice.getString("currencyCode")
//                } else {
//                    price = saleability
//                }

//                } catch (e: JSONException) {
//                    price = ""
//                }
                publishYear = try {
                    volumeInfo.getString("publishedDate").split('-')[0]
                } catch (e: JSONException) {
                    "UNKNOWN"
                }
                bookList.add(Book(title, author, publishYear, language, bitmap))
            }
            return bookList
        }

    }
}
