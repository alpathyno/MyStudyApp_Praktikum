package com.example.mystudyapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager

    // view yang ikut berubah ukurannya
    private lateinit var tvSubJudul: TextView
    private lateinit var tvMateriIsi: TextView
    private lateinit var tvJadwalIsi: TextView
    private lateinit var tvTugasIsi: TextView
    private lateinit var tvRingkasan: TextView
    private lateinit var tvIkonMateri: TextView
    private lateinit var tvIkonJadwal: TextView
    private lateinit var tvIkonTugas: TextView
    private lateinit var btnPengaturan: Button
    private lateinit var ivAvatarUser: ImageView
    private lateinit var tvSapaan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) siapkan pengelola pengaturan
        settings = SettingsManager(this)

        // 2) ambil view
        tvSubJudul  = findViewById(R.id.tvSubJudul)
        tvMateriIsi = findViewById(R.id.tvMateriIsi)
        tvJadwalIsi = findViewById(R.id.tvJadwalIsi)
        tvTugasIsi  = findViewById(R.id.tvTugasIsi)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        ivAvatarUser = findViewById(R.id.ivAvatarUser)
        tvSapaan = findViewById(R.id.tvSapaan)

        findViewById<TextView>(R.id.tvEditProfil).setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }
        tvIkonMateri = findViewById(R.id.tvIkonMateri)
        tvIkonJadwal = findViewById(R.id.tvIkonJadwal)
        tvIkonTugas  = findViewById(R.id.tvIkonTugas)
        btnPengaturan = findViewById(R.id.btnPengaturan)

        // 3) tombol menuju halaman pengaturan
        btnPengaturan.setOnClickListener {
            startActivity(Intent(this, PengaturanActivity::class.java))
        }
    }

    /** Dijalankan setiap kali halaman ini tampil kembali
     *  — termasuk setelah kembali dari halaman pengaturan. */
    override fun onResume() {
        super.onResume()
        terapkanPengaturanTampilan()
    }

    private fun terapkanPengaturanTampilan() {
        // 1) terapkan ukuran teks pengaturan ke semua kalimat isi
        val ukuran = settings.ukuranTeksSp()
        listOf(tvSubJudul, tvMateriIsi, tvJadwalIsi, tvTugasIsi).forEach {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, ukuran)
        }

        // 2) tampilkan ringkasan pengaturan yang sedang aktif
        tvRingkasan.text = RingkasanPengaturan.teks(this, settings)

        // 3) terapkan warna aksen ke tombol & latar ikon
        val warnaAksen = ContextCompat.getColor(this, settings.warnaAksenResId())
        btnPengaturan.backgroundTintList = android.content.res.ColorStateList.valueOf(warnaAksen)
        listOf(tvIkonMateri, tvIkonJadwal, tvIkonTugas).forEach {
            it.backgroundTintList = android.content.res.ColorStateList.valueOf(warnaAksen)
        }

        // 4) tampilkan profil pengguna
        ivAvatarUser.setImageResource(settings.avatarResId())
        val nama = settings.ambilNamaProfil()
        tvSapaan.text = if (nama.isEmpty()) getString(R.string.sapaan_default)
        else getString(R.string.format_sapaan, nama)
    }
}