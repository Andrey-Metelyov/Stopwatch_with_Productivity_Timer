package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException
import java.util.*

class SettingsDialog(var upperLimit: Int) : DialogFragment() {
//    internal lateinit var listener: SettingsDialogListener
    lateinit var upperLimitEditText: EditText
//
//    interface SettingsDialogListener {
//        fun onDialogPositiveClick(dialog: DialogFragment)
//        fun onDialogNegativeClick(dialog: DialogFragment)
//    }
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            listener = context as SettingsDialogListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException((context.toString() +
//                    " must implement SettingsDialogListener"))
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_settings, null)
            upperLimitEditText = layout.findViewById(R.id.upperLimit)
            upperLimitEditText.setText(upperLimit.toString())
            builder
                .setView(layout)
                .setTitle("Settings")
                .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    Log.d("SettingsDialog", "upperLimit.text = ${upperLimitEditText.text.toString()}")
                    upperLimit = upperLimitEditText.text.toString().toInt()
//                    dialog
//                    timeLimit = find
                })
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity is null")
    }
}

class MainActivity : AppCompatActivity()
//    , SettingsDialog.SettingsDialogListener
{
//    override fun onDialogPositiveClick(dialog: DialogFragment) {
//        timeLimit = (dialog as SettingsDialog).timeLimit
//    }
//
//    override fun onDialogNegativeClick(dialog: DialogFragment) {
//        TODO("Not yet implemented")
//    }

    private lateinit var btnStart: Button
    private lateinit var btnReset: Button
    private lateinit var btnSettings: Button
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var started: Boolean = false
    private var time = 0
    private var timeLimit = 0
    private val updateTimer: Runnable = object : Runnable {
        override fun run() {
            time++
            renderTimeAndProgressBar()
            handler.postDelayed(this, 1000)
        }
    }

    private fun renderTimeAndProgressBar() {
        val minutes = time / 60
        val seconds = time % 60
        textView.text = String.format("%02d:%02d", minutes, seconds)
        if (started) {
            val random = Random()
            val color =
                Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart = findViewById(R.id.startButton)
        btnReset = findViewById(R.id.resetButton)
        btnSettings = findViewById(R.id.settingsButton)
        textView = findViewById(R.id.textView)
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
                handler.postDelayed(updateTimer, 1000)
                started = true
            }
        }
        btnReset.setOnClickListener {
            handler.removeCallbacks(updateTimer)
            started = false
            progressBar.visibility = ProgressBar.INVISIBLE
            time = 0
            renderTimeAndProgressBar()
        }
    }
}