package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Activity.isKeyboardOpen(): Boolean {
    val view = findViewById<View>(android.R.id.content)
    val rect = Rect()
    view.getWindowVisibleDisplayFrame(rect)
    val visible = view.height > rect.height()
    return visible
}

fun Activity.isKeyboardClosed() = !isKeyboardOpen()
