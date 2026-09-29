package org.deltacv.myapplication.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationHelper {
    private const val CHANNEL_ID = "rover_volunteer_notifications"
    private const val CHANNEL_NAME = "Notificaciones de Voluntarios"

    fun crearCanalDeNotificaciones(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifica al autor cuando alguien se une como voluntario a su proyecto"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun mostrarNotificacionNuevoVoluntario(
        context: Context,
        nombreVoluntario: String,
        tituloProyecto: String
    ) {
        crearCanalDeNotificaciones(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Nuevo voluntario en tu proyecto!")
            .setContentText("$nombreVoluntario se ha unido a '$tituloProyecto'.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$nombreVoluntario se ha registrado como voluntario en tu proyecto '$tituloProyecto'.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationId = (System.currentTimeMillis() % 100000).toInt()
        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, builder.build())
            } catch (e: SecurityException) {
                // Permission not granted
            }
        }
    }
}
