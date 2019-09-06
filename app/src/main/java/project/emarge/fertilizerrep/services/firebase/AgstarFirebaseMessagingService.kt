package project.emarge.fertilizerrep.services.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class AgstarFirebaseMessagingService : FirebaseMessagingService() {

    val TAG : String = "FB"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("FB", "From: ${remoteMessage?.notification}")
        var notificationBody: String? = null
        var notificationTitle: String? = null

        // if (remoteMessage.getNotification() != null) {
        remoteMessage?.notification?.let {
            notificationTitle = remoteMessage.notification!!.title
            notificationBody = remoteMessage.notification!!.body
        }
        sendNotification(notificationBody!!,notificationTitle!!)

    }

    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")
    }


    private fun sendNotification(messageBody: String,messageTitle: String) {
       /* val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build())*/
    }
}