package com.example.withub.feature.base

import android.app.Application


class MyApp: Application() {
    companion object {
        lateinit var prefs : SharedPreferences
    }
    /* prefs라는 이름의 MySharedPreferences 하나만 생성할 수 있도록 설정. */

    override fun onCreate() {
        prefs = SharedPreferences(applicationContext)
        super.onCreate()
    }

}