package com.example.mystudyapp

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class SettingsManager(context: Context) {

    // satu objek SharedPreferences untuk seluruh aplikasi
    private val prefs = context.applicationContext
        .getSharedPreferences(NAMA_PREFS, Context.MODE_PRIVATE)

    companion object {
        private const val NAMA_PREFS = "pengaturan_app"

        // ===== kunci penyimpanan =====
        const val KEY_BAHASA     = "language"
        const val KEY_TEMA       = "theme"
        const val KEY_UKURAN     = "text_size"
        const val KEY_NOTIFIKASI = "notifikasi"
        const val KEY_AKSEN = "accent"
        const val KEY_PROFIL_NAMA   = "profil_nama"
        const val KEY_PROFIL_AVATAR = "profil_avatar"

        // ===== nilai yang sah =====
        const val BAHASA_ID   = "id"
        const val BAHASA_EN   = "en"
        const val TEMA_TERANG = "light"
        const val TEMA_GELAP  = "dark"
        const val UKURAN_KECIL  = "small"
        const val UKURAN_SEDANG = "medium"
        const val UKURAN_BESAR  = "large"
        const val AKSEN_HIJAU = "green"
        const val AKSEN_BIRU  = "blue"
        const val AKSEN_UNGU  = "purple"

        val DAFTAR_AVATAR = listOf(
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
        )

        /** Menerapkan tema & bahasa ke SELURUH aplikasi.
         *  Dipanggil dari MyStudyApp dan setiap kali pengaturan diubah. */
        fun terapkanKeAplikasi(context: Context) {
            val s = SettingsManager(context)

            // 1) tema: paksa mode terang atau gelap
            AppCompatDelegate.setDefaultNightMode(
                if (s.ambilTema() == TEMA_GELAP) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            // 2) bahasa: pakai file res/values atau res/values-en
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(s.ambilBahasa())
            )
        }
    }

    // ================= BAHASA =================
    fun ambilBahasa(): String = prefs.getString(KEY_BAHASA, BAHASA_ID) ?: BAHASA_ID
    fun simpanBahasa(nilai: String) = prefs.edit().putString(KEY_BAHASA, nilai).apply()

    // ================= TEMA =================
    fun ambilTema(): String = prefs.getString(KEY_TEMA, TEMA_TERANG) ?: TEMA_TERANG
    fun simpanTema(nilai: String) = prefs.edit().putString(KEY_TEMA, nilai).apply()

    // ================= UKURAN TEKS =================
    fun ambilUkuran(): String = prefs.getString(KEY_UKURAN, UKURAN_SEDANG) ?: UKURAN_SEDANG
    fun simpanUkuran(nilai: String) = prefs.edit().putString(KEY_UKURAN, nilai).apply()

    // ================= WARNA AKSEN =================
    fun ambilAksen(): String = prefs.getString(KEY_AKSEN, AKSEN_BIRU) ?: AKSEN_BIRU
    fun simpanAksen(nilai: String) = prefs.edit().putString(KEY_AKSEN, nilai).apply()

    /** id resource warna sesuai aksen yang sedang aktif */
    fun warnaAksenResId(): Int = when (ambilAksen()) {
        AKSEN_HIJAU -> R.color.aksen_hijau
        AKSEN_UNGU  -> R.color.aksen_ungu
        else        -> R.color.aksen_biru
    }

    /** ukuran teks halaman utama dalam satuan sp */
    fun ukuranTeksSp(): Float = when (ambilUkuran()) {
        UKURAN_KECIL -> 12f
        UKURAN_BESAR -> 17f
        else         -> 14f
    }

    // ================= PROFIL PENGGUNA =================
    fun ambilNamaProfil(): String = prefs.getString(KEY_PROFIL_NAMA, "") ?: ""
    fun simpanNamaProfil(nilai: String) = prefs.edit().putString(KEY_PROFIL_NAMA, nilai).apply()

    fun ambilIndeksAvatar(): Int = prefs.getInt(KEY_PROFIL_AVATAR, 0)
    fun simpanIndeksAvatar(nilai: Int) = prefs.edit().putInt(KEY_PROFIL_AVATAR, nilai).apply()

    fun avatarResId(): Int = DAFTAR_AVATAR[ambilIndeksAvatar()]


    // ================= NOTIFIKASI =================
    fun ambilNotifikasi(): Boolean = prefs.getBoolean(KEY_NOTIFIKASI, false)
    fun simpanNotifikasi(nilai: Boolean) = prefs.edit().putBoolean(KEY_NOTIFIKASI, nilai).apply()

    // ================= RESET =================
    fun reset() = prefs.edit().clear().apply()
}

/** Ringkasan pengaturan aktif — dipakai halaman utama DAN halaman pengaturan,
 *  jadi tulisannya tidak perlu diulang di dua tempat. */
object RingkasanPengaturan {

    fun teks(context: Context, s: SettingsManager): String {
        val bahasa = context.getString(
            if (s.ambilBahasa() == SettingsManager.BAHASA_EN) R.string.singkat_en
            else R.string.singkat_id
        )
        val tema = context.getString(
            if (s.ambilTema() == SettingsManager.TEMA_GELAP) R.string.singkat_gelap
            else R.string.singkat_terang
        )
        val ukuran = context.getString(
            R.string.singkat_teks,
            context.getString(
                when (s.ambilUkuran()) {
                    SettingsManager.UKURAN_KECIL -> R.string.singkat_kecil
                    SettingsManager.UKURAN_BESAR -> R.string.singkat_besar
                    else -> R.string.singkat_sedang
                }
            )
        )
        return context.getString(R.string.ringkasan_format, bahasa, tema, ukuran)
    }

    fun statusNotifikasi(context: Context, s: SettingsManager): String = context.getString(
        R.string.status_format,
        context.getString(R.string.singkat_notifikasi),
        context.getString(
            if (s.ambilNotifikasi()) R.string.status_aktif else R.string.status_nonaktif
        )
    )
}