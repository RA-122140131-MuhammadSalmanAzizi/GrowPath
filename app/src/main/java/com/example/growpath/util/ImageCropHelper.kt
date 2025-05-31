package com.example.growpath.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.UUID
import androidx.activity.ComponentActivity
import android.app.Activity.RESULT_OK

/**
 * Helper class untuk menangani operasi crop gambar
 */
object ImageCropHelper {

    // Ukuran target untuk gambar profil (500x500 px)
    private const val TARGET_WIDTH = 500
    private const val TARGET_HEIGHT = 500

    /**
     * Memulai proses crop gambar dengan rasio 1:1 dan target ukuran 500x500px
     *
     * @param activity Activity tempat hasil crop akan dikembalikan
     * @param sourceUri Uri dari gambar yang akan di-crop
     * @return Intent untuk crop yang dapat digunakan dengan launcher
     */
    fun startCrop(activity: ComponentActivity, sourceUri: Uri): android.content.Intent {
        val destinationUri = Uri.fromFile(
            File(activity.cacheDir, "cropped_${UUID.randomUUID()}.jpg")
        )

        val options = UCrop.Options().apply {
            setCompressionQuality(90) // kualitas kompresi
            setHideBottomControls(false)
            setFreeStyleCropEnabled(false) // disable free crop, enforcing square ratio
            withMaxResultSize(TARGET_WIDTH, TARGET_HEIGHT) // set target size
            setToolbarTitle("Crop Foto Profil")
        }

        // Buat UCrop intent untuk digunakan dengan launcher
        return UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // Gunakan withAspectRatio untuk menetapkan rasio 1:1
            .withOptions(options)
            .getIntent(activity)
    }

    /**
     * Memproses hasil dari aktivitas crop
     *
     * @param resultCode kode hasil dari aktivitas
     * @param data intent data dari hasil aktivitas
     * @return Uri yang sudah di crop, atau null jika proses dibatalkan atau gagal
     */
    fun handleCropResult(resultCode: Int, data: android.content.Intent?): Uri? {
        return if (resultCode == RESULT_OK && data != null) {
            UCrop.getOutput(data)
        } else {
            null
        }
    }
}
