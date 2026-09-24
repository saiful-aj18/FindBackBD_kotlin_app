package com.saiful.findbackbd.data.remote
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
class FindBackMessagingService:FirebaseMessagingService(){
 override fun onMessageReceived(message:RemoteMessage){val title=message.notification?.title?:"FindBack BD";val body=message.notification?.body?:"You have a new notification";val manager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager;if(Build.VERSION.SDK_INT>=26)manager.createNotificationChannel(NotificationChannel("findback","FindBack Notifications",NotificationManager.IMPORTANCE_DEFAULT));manager.notify(System.currentTimeMillis().toInt(),NotificationCompat.Builder(this,"findback").setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body).setAutoCancel(true).build())}
}
