package org.hyperskill.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnStart: Button
    private lateinit var btnReset: Button
    private lateinit var textView: TextView
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var started: Boolean = false
    private var time = 0
    private val updateTimer: Runnable = object : Runnable {
        override fun run() {
            time++
            renderTime()
            handler.postDelayed(this, 1000)
        }
    }

    private fun renderTime() {
        val minutes = time / 60
        val seconds = time % 60
        textView.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStart = findViewById(R.id.startButton)
        btnReset = findViewById(R.id.resetButton)
        textView = findViewById(R.id.textView)
        renderTime()
        btnStart.setOnClickListener {
            if (!started) {
                handler.postDelayed(updateTimer, 1000)
                started = true
            }
        }
        btnReset.setOnClickListener {
            handler.removeCallbacks(updateTimer)
            started = false
            time = 0
            renderTime()
        }
    }
}