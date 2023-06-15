package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.sendNotification

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        // learned viewBinding from here https://www.youtube.com/watch?v=z0F2QTAKsWU
        // hope to be as good as Philipp Lackner in Android. He is very knowledgeable about so many aspects of Android development.
        // also I see here what was described in the previous lecture. LoadingButton has default behavior in performClick() and
        // we add additional behaviors by adding a click listener, which is called inside performClick().
        binding.contentMainLayout.customButton.setOnClickListener {
            download()
        }

        createChannel(
            getString(R.string.detail_notification_channel_id),
            getString(R.string.detail_notification_channel_name)
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.i("Notification", "Notification channel being created")
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply{
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Show download result details"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            Log.i("Notification", "Notification channel was created")
        }
    }


    fun onRadioButtonClicked(view: View){
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radio_button_glide ->
                    if (checked) {
                        url = "https://github.com/bumptech/glide"
                    }
                R.id.radio_button_starter ->
                    if (checked) {
                        url = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                    }
                R.id.radio_button_retrofit ->
                    if (checked) {
                        url = "https://github.com/square/retrofit"
                    }
            }
        }
    }



    //Learned about this from Philipp Lackner's youtube channel.
    // https://www.youtube.com/watch?v=4t8EevQSYK4
    // Across the internet and in his video many people create a class to extend BroadCastReceiver() and then they
    // register it in the manifest. Looks like here we dont need to register it!  Why?
    // Nevermind, I see it is registered iwth a call to registerReceiver()
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID){ // then this broadcast was about the file we were trying to download just now.
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val manager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    val colIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(colIndex)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val notificationManager = ContextCompat.getSystemService(
                            context,
                            NotificationManager::class.java
                        ) as NotificationManager

                        notificationManager.sendNotification(
                            "Success",
                            context
                        )
                    } else {
                        val notificationManager = ContextCompat.getSystemService(
                            context,
                            NotificationManager::class.java
                        ) as NotificationManager

                        notificationManager.sendNotification(
                            "Fail",
                            context
                        )
                    }
                }
                cursor.close()
            }
        }
    }

    private fun download() {
        if(url == ""){
            // show toast, then return
            Toast.makeText(this, resources.getString(R.string.nothing_selected_for_download_message), Toast.LENGTH_SHORT).show()
            return
        }
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private var url : String = ""
        private const val CHANNEL_ID = "channelId"
    }

}