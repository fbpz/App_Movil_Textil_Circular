package com.example.textilcircular

import android.app.Application
import com.example.textilcircular.di.AppContainer

class TextilCircularApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
