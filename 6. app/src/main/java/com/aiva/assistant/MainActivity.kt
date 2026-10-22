
package com.aiva.assistant

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var status: TextView
    private var ready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 48, 36, 36)
            setBackgroundColor(0xFF070B16.toInt())
        }

        val title = TextView(this).apply {
            text = "A I V A"
            textSize = 34f
            setTextColor(0xFF55B9FF.toInt())
        }

        status = TextView(this).apply {
            text = "Good morning, Sir."
            textSize = 18f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(0, 30, 0, 30)
        }

        val listen = Button(this).apply {
            text = "Start voice command"
            setOnClickListener { listenForCommand() }
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(listen)
        setContentView(layout)
    }

    override fun onInit(result: Int) {
        ready = result == TextToSpeech.SUCCESS
        if (ready) {
            tts.language = Locale("en", "IN")
            speak("Good morning, Sir.")
        }
    }

    private fun speak(message: String) {
        status.text = message
        if (ready) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "AIVA")
        }
    }

    private fun listenForCommand() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO), 10
            )
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "AIVA ko command dein")
        }

        try {
            startActivityForResult(intent, 20)
        } catch (e: Exception) {
            speak("Sir, voice recognition is not available.")
        }
    }

    @Deprecated("Legacy result API for simple starter")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 20 && resultCode == RESULT_OK) {
            val words = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )?.firstOrNull()?.lowercase(Locale.ROOT) ?: return

            handleCommand(words)
        }
    }

    private fun handleCommand(command: String) {
        when {
            "time" in command || "समय" in command ||
                "टाइम" in command -> {
                val time = SimpleDateFormat(
                    "hh:mm a", Locale.getDefault()
                ).format(Date())
                speak("Sir, the time is $time")
            }

            "date" in command || "तारीख" in command -> {
                val date = SimpleDateFormat(
                    "dd MMMM yyyy", Locale("hi", "IN")
                ).format(Date())
                speak("Aaj ki tareekh $date hai")
            }

            "youtube" in command -> openApp("com.google.android.youtube")

            "whatsapp" in command || "व्हाट्सऐप" in command ->
                openApp("com.whatsapp")

            "instagram" in command || "इंस्टाग्राम" in command ->
                openApp("com.instagram.android")

            "call" in command || "कॉल" in command -> {
                speak("Sir, please say the phone number.")
            }

            "search" in command || "खोज" in command ||
                "सर्च" in command -> {
                val query = Uri.encode(command)
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/search?q=$query")
                    )
                )
                speak("Opening web search, Sir.")
            }

            else -> speak("Sorry, Sir. Please repeat the command.")
        }
    }

    private fun openApp(packageName: String) {
        val launch = packageManager.getLaunchIntentForPackage(packageName)
        if (launch != null) {
            startActivity(launch)
            speak("Opening app, Sir.")
        } else {
            speak("Sir, this app is not installed.")
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
