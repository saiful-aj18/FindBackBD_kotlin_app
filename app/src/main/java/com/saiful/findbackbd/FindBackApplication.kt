package com.saiful.findbackbd

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FindBackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        runCatching {
            FirebaseMessaging.getInstance().isAutoInitEnabled = false
        }
    }
}
