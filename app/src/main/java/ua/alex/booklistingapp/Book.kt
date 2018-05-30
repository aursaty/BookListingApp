package ua.alex.booklistingapp

import android.graphics.Bitmap

/**
 * Created by aursaty on 5/21/2018.
 */

data class Book(val title: String,
                val author: String,
                val publishYear: String,
                val language: String,
                val image: Bitmap)