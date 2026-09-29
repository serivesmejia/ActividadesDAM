package org.deltacv.myapplication.notifications

import java.util.UUID

data class NotificacionModel(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val mensaje: String,
    val timestamp: Long = System.currentTimeMillis()
)
