package com.saiful.findbackbd.utils
import android.util.Patterns
object ValidationUtils{fun email(v:String)=Patterns.EMAIL_ADDRESS.matcher(v).matches();fun password(v:String)=v.length>=6;fun name(v:String)=v.trim().length>=2;fun phone(v:String)=v.filter{it.isDigit()}.length>=10}
