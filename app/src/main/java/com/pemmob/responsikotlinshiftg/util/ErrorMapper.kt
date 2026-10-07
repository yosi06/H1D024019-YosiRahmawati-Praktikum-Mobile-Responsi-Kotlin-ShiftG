package com.pemmob.responsikotlinshiftg.util

import retrofit2.HttpException
import java.io.IOException

/**
 * Extension function untuk mengubah Throwable menjadi pesan error yang ramah pengguna.
 */
fun Throwable.toUserMessage(): String = when (this) {
    is IOException -> "Tidak dapat terhubung ke server. Periksa koneksi internet kamu."
    is HttpException -> when (code()) {
        404 -> "Data anime tidak ditemukan."
        429 -> "Terlalu banyak permintaan. Tunggu sebentar lalu coba lagi."
        in 500..599 -> "Server Tenrai sedang bermasalah (${code()}). Coba lagi nanti."
        else -> "Terjadi kesalahan (${code()})."
    }
    else -> message ?: "Terjadi kesalahan yang tidak diketahui."
}
