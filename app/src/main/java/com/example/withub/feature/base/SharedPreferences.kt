package com.example.withub.feature.base

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences (context:Context){
    val prefs: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    var accountToken: String?
        get() = prefs.getString("token",null)
        set(value){
            prefs.edit().putString("token",value).apply()
        }

    var githubToken: String?
        get() = prefs.getString("githubToken",null)
        set(value){
            prefs.edit().putString("githubToken",value).apply()
        }
}