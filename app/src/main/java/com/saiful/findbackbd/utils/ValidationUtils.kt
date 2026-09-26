package com.saiful.findbackbd.utils

import android.util.Patterns

object ValidationUtils {
    fun email(v: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(v).matches()
    fun password(v: String): Boolean = v.length >= 6
    fun name(v: String): Boolean = v.trim().length >= 2
    fun phone(v: String): Boolean = v.filter { it.isDigit() }.length >= 10
}
