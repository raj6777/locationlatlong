import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class LocationForegroundService : Service() {
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = createNotification()

        startForeground(1, notification)
    }

    private fun createNotification(): Notification {
        val builder: NotificationCompat.Builder
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, "channel_id")
        } else {
            NotificationCompat.Builder(this,)
        }
        builder.setSmallIcon(R.drawable.ic_notification_overlay)
            .setContentTitle("Foreground Service")
            .setContentText("Running")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return builder.build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}