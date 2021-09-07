package org.hyperskill.stopwatch

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var btnStart: Button
    private lateinit var btnReset: Button
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var started: Boolean = false
    private var time = 0
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
            val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart = findViewById(R.id.startButton)
        btnReset = findViewById(R.id.resetButton)
        textView = findViewById(R.id.textView)
        progressBar = findViewById(R.id.progressBar)

        renderTimeAndProgressBar()
        progressBar.visibility = ProgressBar.INVISIBLE

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