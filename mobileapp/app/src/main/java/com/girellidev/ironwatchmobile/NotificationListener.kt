package com.girellidev.ironwatchmobile

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.PrintWriter
import java.net.Socket

class WhatsAppListener : NotificationListenerService() {

    private val SERVER_IP = "Colocar_a_merda_do_ip_aqui_gordao"
    private val SERVER_PORT = 5500

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != "com.whatsapp") return

        val msg = sbn.notification.extras.getCharSequence("android.text")?.toString() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Socket(SERVER_IP, SERVER_PORT).use { socket ->
                    val out = PrintWriter(socket.getOutputStream(), true)
                    out.println(msg)
                }
            } catch (_: Exception) {}
        }
    }
}
