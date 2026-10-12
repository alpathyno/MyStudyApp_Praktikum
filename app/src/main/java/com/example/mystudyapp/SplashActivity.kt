package com.example.mystudyapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val durasiSplash = 1200L   // 1.2 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // baca & terapkan pengaturan SAAT splash tampil
        // (bukan baru pas MainActivity dibuka — jadi perpindahannya mulus)
        SettingsManager.terapkanKeAplikasi(this)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()   // splash tidak boleh bisa ditekan Back untuk kembali
        }, durasiSplash)
    }
}