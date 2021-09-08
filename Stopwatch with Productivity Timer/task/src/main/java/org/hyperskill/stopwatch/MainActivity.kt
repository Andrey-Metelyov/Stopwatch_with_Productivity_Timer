package org.hyperskill.stopwatch

import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException
import java.util.*

const val CHANNEL_ID = "org.hyperskill"

class SettingsDialog(var upperLimit: Int) : DialogFragment() {
    private lateinit var listener: SettingsDialogListener
    private lateinit var upperLimitEditText: EditText

    interface SettingsDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
//        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SettingsDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement SettingsDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_settings, null)
            upperLimitEditText = layout.findViewById(R.id.upperLimitEditText)
            upperLimitEditText.setText(upperLimit.toString())
            builder
                .setView(layout)
                .setTitle("Settings")
                .setPositiveButton(android.R.string.ok
                ) { _, _ ->
                    Log.d(
                        "SettingsDialog",
                        "upperLimit.text = ${upperLimitEditText.text}"
                    )
                    upperLimit = upperLimitEditText.text.toString().toInt()
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity is null")
    }
}

class MainActivity : AppCompatActivity()
    , SettingsDialog.SettingsDialogListener
{
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        timeLimit = (dialog as SettingsDialog).upperLimit
        Log.d("MainActivity", "timeLimit=$timeLimit")
    }

    private lateinit var btnStart: Button
    private lateinit var btnReset: Button
    private lateinit var btnSettings: Button
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var started: Boolean = false
    private var timeStarted = 0L
    private var timeLimit = 0
    private var defaultColor: Int = 0
    private var reminded = false

    private val updateTimer: Runnable = object : Runnable {
        override fun run() {
//            ++timeStarted
            renderTimeAndProgressBar()
            handler.postDelayed(this, 500)
        }
    }

    private fun renderTimeAndProgressBar() {
        if (started) {
            val elapsed = (System.currentTimeMillis() - timeStarted) / 1000
            val minutes = elapsed / 60
            val seconds = elapsed % 60
            if (timeLimit != 0 && elapsed >= timeLimit) {
                remind()
                textView.setTextColor(Color.RED)
            } else {
                textView.setTextColor(defaultColor)
            }
            Log.d("MainActivity","elapsed=$elapsed, timeLimit=$timeLimit ${textView.currentTextColor}")
            textView.text = String.format("%02d:%02d", minutes, seconds)
            val random = Random()
            val color =
                Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)
        } else {
            textView.text = String.format("%02d:%02d", 0, 0)
            textView.setTextColor(defaultColor)
        }
    }

    private fun remind() {
        if (!reminded) {
            val notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(393939, notificationBuilder.build())
            reminded = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNotification()

        btnStart = findViewById(R.id.startButton)
        btnReset = findViewById(R.id.resetButton)
        btnSettings = findViewById(R.id.settingsButton)
        textView = findViewById(R.id.textView)
        defaultColor = textView.currentTextColor
        progressBar = findViewById(R.id.progressBar)

        renderTimeAndProgressBar()
        progressBar.visibility = ProgressBar.INVISIBLE

        btnSettings.setOnClickListener {
            if (!started) {
                val settingsDialog = SettingsDialog(timeLimit)
//                settingsDialog.onDial
                settingsDialog.show(supportFragmentManager, null)
            }
        }

        btnStart.setOnClickListener {
            if (!started) {
                progressBar.visibility = ProgressBar.VISIBLE
                handler.postDelayed(updateTimer, 500)
                btnSettings.isEnabled = false
                timeStarted = System.currentTimeMillis()
                started = true
            }
        }
        btnReset.setOnClickListener {
            handler.removeCallbacks(updateTimer)
            started = false
            reminded = false
            btnSettings.isEnabled = true
            progressBar.visibility = ProgressBar.INVISIBLE
//            timeStarted = 0
            renderTimeAndProgressBar()
        }
    }

    private fun setupNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder"
            val descriptionText = "Time is coming"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = getActivity(applicationContext, 0, intent, FLAG_UPDATE_CURRENT)

        notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.photo)
            .setContentTitle("Reminder")
            .setContentText("Time is coming!")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }
}