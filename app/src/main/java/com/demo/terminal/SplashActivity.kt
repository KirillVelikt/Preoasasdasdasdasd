package com.demo.terminal

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : Activity() {

    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Музыка на заставке
        player = MediaPlayer.create(this, R.raw.splash_music)
        player?.start()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }

    override fun onDestroy() {
        player?.release()
        super.onDestroy()
    }
}
