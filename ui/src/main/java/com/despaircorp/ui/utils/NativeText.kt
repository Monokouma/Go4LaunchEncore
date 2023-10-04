package com.despaircorp.ui.utils

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

sealed class NativeText {

    abstract fun toCharSequence(context: Context): CharSequence

    data class Simple(val text: String) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = text
    }

    data class Resource(@StringRes val id: Int) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = context.getString(id)
    }

    data class Plural(@PluralsRes val id: Int, val number: Int, val args: List<Any>) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = context.resources.getQuantityString(id, number, *args.toTypedArray())
    }

    data class Argument(@StringRes val id: Int, val arg: Any) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = context.getString(id, arg)
    }

    data class Arguments(@StringRes val id: Int, val args: List<Any>) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = context.getString(id, *args.toTypedArray())
    }

    data class Multi(val text: List<NativeText>) : NativeText() {
        override fun toCharSequence(context: Context): CharSequence = StringBuilder().apply {
            for (item in text) {
                append(item.toCharSequence(context))
            }
        }
    }
}

fun TextView.setText(nativeText: NativeText?) {
    text = nativeText?.toCharSequence(context)
}

/**
 * Show the NativeText as Toast and return the instance of the shown toast, should you cancel it
 */
fun NativeText.showAsToast(context: Context, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(context, toCharSequence(context), duration).also {
        it.show()
    }