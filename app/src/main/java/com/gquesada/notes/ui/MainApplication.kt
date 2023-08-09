package com.gquesada.notes.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.google.firebase.messaging.FirebaseMessaging
import com.gquesada.notes.core.services.MyFirebaseMessagingService.Companion.NOTIFICATION_CHANNEL_ID
import com.gquesada.notes.core.services.MyFirebaseMessagingService.Companion.NOTIFICATION_CHANNEL_NAME
import com.gquesada.notes.di.module
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(module)
        }
        createChannel()
    }

    private fun createChannel() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        // metodo para setear un topic o canal en firebase y con el cual podemos enviar una notificacion
        // a multiples dispositivos
        //FirebaseMessaging.getInstance().subscribeToTopic(NOTIFICATION_CHANNEL_ID)
    }
}