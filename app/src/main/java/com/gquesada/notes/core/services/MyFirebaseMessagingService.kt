package com.gquesada.notes.core.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gquesada.notes.R
import com.gquesada.notes.data.datasources.SharedPreferencesDataSource
import com.gquesada.notes.ui.main.views.MainActivity
import org.koin.android.ext.android.inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "channel_id"
        const val NOTIFICATION_CHANNEL_NAME = "New Notes"
    }

    private val sharedPreferencesDataSource: SharedPreferencesDataSource by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sharedPreferencesDataSource.saveDeviceToken(token)
        Log.d("NOT_TOKEN", "Token = $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        /**
         * Funcion que se ejecuta cuando recibimos un push notification y el app esta en primer plano (foreground)
         * aca podemos decidir si mostramos un local notification
         * o manejamos de forma silenciosa el mensaje remoto que estamos recibiendo desde el server
         * por ejemplo cuando la notificacion no requiere mostratr una alerta al usuario y basta
         * con una simple sincronizacion de nuestros datos locales
         *
         * En este caso decidimos mostrar una notificiacion local
         */

        showLocalNotification(message)
    }

    private fun showLocalNotification(message: RemoteMessage) {

        // Mostrar un Local notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("note_id", message.data["note_id"])
        intent.putExtra("action", message.data["action"])
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )


        //Proceso para mostrar un local notification
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(message.notification?.title)
            .setContentTitle(message.notification?.body)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}