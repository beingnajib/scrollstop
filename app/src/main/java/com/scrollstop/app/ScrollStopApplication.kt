package com.scrollstop.app

import android.app.Application
import com.scrollstop.app.util.NotificationHelper

class ScrollStopApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
    }
}
