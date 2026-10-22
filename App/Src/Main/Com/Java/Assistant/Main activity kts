
package com.aiva.assistant

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var status: TextView
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)
        buildScreen()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 40, 28, 40)
            setBackgroundColor(Color.rgb(5, 10, 24))
        }

        val title = TextView(this).apply {
            text = "A I V A"
            textSize = 36f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(70, 180, 255))
        }

        val subtitle = TextView(this).apply {
            text = "PERSONAL VOICE ASSISTANT"
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(Color.LTGRAY)
        }

        status = TextView(this).apply {
            text = "Good morning, Sir."
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, 36, 0, 36)
        }

        val listenButton = Button(this).apply {
            text = "VOICE COMMAND"
            setOnClickListener { listenForCommand() }
        }

        val timeButton = Button(this).apply {
            text = "TIME AND DATE"
            setOnClickListener { tellTime() }
        }

        val webButton = Button(this).apply {
            text = "WEB SEARCH"
            setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com")
                    )
                )
            }
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(status)
        root.addView(listenButton)
        root.addView(timeButton)
        root.addView(webButton)

        setContentView(root)
    }

    override fun onInit(result: Int) {
        ttsReady = result == TextToSpeech.SUCCESS

        if (ttsReady) {
            val languageResult = tts.setLanguage(Locale("hi", "IN"))
            if (languageResult == TextToSpeech.LANG_MISSING_DATA ||
                languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.language = Locale.US
            }
            speak("Good morning, Sir.")
        }
    }

    private fun speak(message: String) {
        status.text = message
        if (ttsReady) {
            tts.speak(
                message,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "AIVA_REPLY"
            )
        }
    }

    private fun tellTime() {
        val time = SimpleDateFormat(
            "hh:mm a", Locale.getDefault()
        ).format(Date())

        val date = SimpleDateFormat(
            "dd MMMM yyyy", Locale.getDefault()
        ).format(Date())

        speak("Sir, time is $time. Date is $date.")
    }

    private fun listenForCommand() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO), 100
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
            startActivityForResult(intent, 200)
        } catch (e: Exception) {
            speak("Sir, voice recognition is not available.")
        }
    }

    @Deprecated("Legacy speech result callback")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200 && resultCode == RESULT_OK) {
            val command = data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                ?.lowercase(Locale.ROOT) ?: return

            handleCommand(command)
        }
    }

    private fun handleCommand(command: String) {
        when {
            "समय" in command || "टाइम" in command ||
                "time" in command -> tellTime()

            "youtube" in command || "यूट्यूब" in command ->
                openApp("com.google.android.youtube")

            "whatsapp" in command || "व्हाट्सएप" in command ->
                openApp("com.whatsapp")

            "instagram" in command || "इंस्टाग्राम" in command ->
                openApp("com.instagram.android")

            "call" in command || "कॉल" in command ->
                speak("Sir, please tell me the contact name.")

            "search" in command || "खोजो" in command ||
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

            else -> speak("Sorry, Sir. Please repeat your command.")
        }
    }

    private fun openApp(packageName: String) {
        val launchIntent =
            packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            startActivity(launchIntent)
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
