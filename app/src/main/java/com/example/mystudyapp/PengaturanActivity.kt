package com.example.mystudyapp

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class PengaturanActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager
    private lateinit var tvRingkasan: TextView
    private lateinit var tvStatusNotifikasi: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengaturan)

        settings = SettingsManager(this)

        tvRingkasan = findViewById(R.id.tvRingkasan)
        tvStatusNotifikasi = findViewById(R.id.tvStatusNotifikasi)

        val rgBahasa = findViewById<RadioGroup>(R.id.rgBahasa)
        val rgTema = findViewById<RadioGroup>(R.id.rgTema)
        val rgUkuran = findViewById<RadioGroup>(R.id.rgUkuran)
        val swNotifikasi = findViewById<SwitchCompat>(R.id.swNotifikasi)
        val rgAksen = findViewById<RadioGroup>(R.id.rgAksen)

        // 1) TAMPILKAN KEADAAN AWAL dari pengaturan yang tersimpan
        isiKeadaanAwal(rgBahasa, rgTema, rgUkuran, rgAksen, swNotifikasi)

        // 2) BARU pasang listener: setiap perubahan disimpan & diterapkan
        rgBahasa.setOnCheckedChangeListener { group, id ->
            // abaikan kalau perubahan ini dipicu oleh kode sendiri (saat membatalkan)
            if (sedangMemprosesBahasa) return@setOnCheckedChangeListener

            val bahasaBaru = if (id == R.id.rbEnglish) SettingsManager.BAHASA_EN else SettingsManager.BAHASA_ID
            val bahasaLama = settings.ambilBahasa()

            // kalau sama dengan yang sudah tersimpan, tidak perlu konfirmasi apa-apa
            if (bahasaBaru == bahasaLama) return@setOnCheckedChangeListener

            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_ganti_bahasa_judul)
                .setMessage(R.string.dialog_ganti_bahasa_pesan)
                .setCancelable(false)
                .setNegativeButton(R.string.dialog_batal) { _, _ ->
                    // batalkan: kembalikan radio ke pilihan lama TANPA memicu konfirmasi lagi
                    sedangMemprosesBahasa = true
                    group.check(if (bahasaLama == SettingsManager.BAHASA_EN) R.id.rbEnglish else R.id.rbIndonesia)
                    sedangMemprosesBahasa = false
                }
                .setPositiveButton(R.string.dialog_ya) { _, _ ->
                    settings.simpanBahasa(bahasaBaru)
                    SettingsManager.terapkanKeAplikasi(this)
                    recreate()
                }
                .show()
        }

        rgTema.setOnCheckedChangeListener { _, id ->
            val nilai = when (id) {
                R.id.rbGelap  -> SettingsManager.TEMA_GELAP
                R.id.rbSistem -> SettingsManager.TEMA_SISTEM
                else          -> SettingsManager.TEMA_TERANG
            }
            settings.simpanTema(nilai)
            SettingsManager.terapkanKeAplikasi(this)
        }

        rgUkuran.setOnCheckedChangeListener { _, id ->
            val nilai = when (id) {
                R.id.rbKecil -> SettingsManager.UKURAN_KECIL
                R.id.rbBesar -> SettingsManager.UKURAN_BESAR
                else         -> SettingsManager.UKURAN_SEDANG
            }
            settings.simpanUkuran(nilai)
            perbaruiRingkasan()
        }

        rgAksen.setOnCheckedChangeListener { _, id ->
            val nilai = when (id) {
                R.id.rbHijau -> SettingsManager.AKSEN_HIJAU
                R.id.rbUngu  -> SettingsManager.AKSEN_UNGU
                else         -> SettingsManager.AKSEN_BIRU
            }
            settings.simpanAksen(nilai)
            terapkanWarnaAksen()
        }

        swNotifikasi.setOnCheckedChangeListener { _, aktif ->
            settings.simpanNotifikasi(aktif)
            perbaruiRingkasan()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener { konfirmasiReset() }
    }

    override fun onResume() {
        super.onResume()
        perbaruiRingkasan()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // sengaja TIDAK memanggil super.onSaveInstanceState(outState)
        // supaya status RadioGroup/Switch yang lama tidak ikut disimpan & dipulihkan
        // saat recreate() dipanggil — sumber data yang benar selalu dari SharedPreferences
    }

    // ================= keadaan awal =================
    private fun isiKeadaanAwal(
        rgBahasa: RadioGroup, rgTema: RadioGroup, rgUkuran: RadioGroup,
        rgAksen: RadioGroup, swNotifikasi: SwitchCompat
    ) {
        rgBahasa.check(
            if (settings.ambilBahasa() == SettingsManager.BAHASA_EN) R.id.rbEnglish
            else R.id.rbIndonesia
        )
        rgTema.check(
            when (settings.ambilTema()) {
                SettingsManager.TEMA_GELAP  -> R.id.rbGelap
                SettingsManager.TEMA_SISTEM -> R.id.rbSistem
                else -> R.id.rbTerang
            }
        )
        rgUkuran.check(
            when (settings.ambilUkuran()) {
                SettingsManager.UKURAN_KECIL -> R.id.rbKecil
                SettingsManager.UKURAN_BESAR -> R.id.rbBesar
                else -> R.id.rbSedang
            }
        )
        rgAksen.check(
            when (settings.ambilAksen()) {
                SettingsManager.AKSEN_HIJAU -> R.id.rbHijau
                SettingsManager.AKSEN_UNGU  -> R.id.rbUngu
                else -> R.id.rbBiru
            }
        )
        terapkanWarnaAksen()
        swNotifikasi.isChecked = settings.ambilNotifikasi()
        perbaruiRingkasan()
    }

    // ===== LANJUTAN dari PengaturanActivity.kt - bagian 1 di atas =====

    // ================= ringkasan pengaturan aktif =================

    private var sedangMemprosesBahasa = false
    private fun perbaruiRingkasan() {
        tvRingkasan.text = RingkasanPengaturan.teks(this, settings)
        tvStatusNotifikasi.text = RingkasanPengaturan.statusNotifikasi(this, settings)
    }

    // ================= reset dengan konfirmasi =================
    private fun konfirmasiReset() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_reset_judul)
            .setMessage(R.string.dialog_reset_pesan)
            .setNegativeButton(R.string.dialog_batal, null)
            .setPositiveButton(R.string.dialog_ya) { _, _ ->
                settings.reset()                              // hapus semua pengaturan
                Toast.makeText(
                    this, getString(R.string.toast_reset), Toast.LENGTH_SHORT
                ).show()
                SettingsManager.terapkanKeAplikasi(this)       // kembali ke bahasa & tema awal
                recreate()                                    // segarkan halaman ini
            }
            .show()
    }
    private fun terapkanWarnaAksen() {
        val warna = androidx.core.content.ContextCompat.getColor(this, settings.warnaAksenResId())
        findViewById<Button>(R.id.btnReset).backgroundTintList =
            android.content.res.ColorStateList.valueOf(warna)
    }
}