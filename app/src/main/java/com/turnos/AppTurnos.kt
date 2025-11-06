package com.turnos

import android.app.Application
import com.turnos.di.DefaultAppContainer

class AppTurnos : Application() {


    lateinit var appContainer: DefaultAppContainer

    override fun onCreate() {
        super.onCreate()

        appContainer = DefaultAppContainer(applicationContext)
    }
}

