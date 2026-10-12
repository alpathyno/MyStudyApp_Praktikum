package com.example.mystudyapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfilActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager
    private lateinit var ivAvatarProfil: ImageView
    private lateinit var etNamaProfil: EditText
    private var indeksAvatar = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        settings = SettingsManager(this)

        ivAvatarProfil = findViewById(R.id.ivAvatarProfil)
        etNamaProfil = findViewById(R.id.etNamaProfil)

        // 1) tampilkan data yang sudah tersimpan
        indeksAvatar = settings.ambilIndeksAvatar()
        ivAvatarProfil.setImageResource(SettingsManager.DAFTAR_AVATAR[indeksAvatar])
        etNamaProfil.setText(settings.ambilNamaProfil())

        // 2) tombol ganti avatar: geser ke avatar berikutnya
        findViewById<Button>(R.id.btnGantiAvatar).setOnClickListener {
            indeksAvatar = (indeksAvatar + 1) % SettingsManager.DAFTAR_AVATAR.size
            ivAvatarProfil.setImageResource(SettingsManager.DAFTAR_AVATAR[indeksAvatar])
        }

        // 3) tombol simpan
        findViewById<Button>(R.id.btnSimpanProfil).setOnClickListener {
            settings.simpanNamaProfil(etNamaProfil.text.toString().trim())
            settings.simpanIndeksAvatar(indeksAvatar)
            Toast.makeText(this, getString(R.string.toast_profil_tersimpan), Toast.LENGTH_SHORT).show()
            finish()   // balik ke halaman utama, otomatis ter-refresh lewat onResume()
        }
    }
}