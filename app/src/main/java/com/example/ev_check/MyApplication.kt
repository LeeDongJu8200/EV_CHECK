package com.example.ev_check

import android.app.Application

class MyApplication : Application()
{
    companion object
    {
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate()
    {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}

