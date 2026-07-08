package com.example.luciano.chanchuno

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.example.luciano.chanchuno.MainActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.Timer
import java.util.TimerTask

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.loadscreen)
        // Dispositivo de desarrollo: recibe anuncios de TEST en vez de reales.
        // Los usuarios finales no están en esta lista, así que ven anuncios reales normalmente.
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("5D8698564B18E082861C34C22F750A14"))
                .build()
        )
        MobileAds.initialize(this) {}
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                val mainIntent = Intent().setClass(
                    this@SplashActivity, MainActivity::class.java
                )
                startActivity(mainIntent)
                finish()
            }
        }
        val timer = Timer()
        timer.schedule(task, SPLASH_SCREEN_DELAY)
    }

    companion object {
        private const val SPLASH_SCREEN_DELAY: Long = 2000
    }
}
