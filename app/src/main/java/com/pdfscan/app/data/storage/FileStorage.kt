package com.pdfscan.app.data.storage

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val cacheDir: File
        get() = File(context.cacheDir, "scanned_pages").also { it.mkdirs() }

    fun createTempFile(extension: String = "jpg"): File {
        return File.createTempFile("scan_", ".$extension", cacheDir)
    }

    fun copyToCache(uri: Uri): Uri {
        val file = createTempFile()
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return Uri.fromFile(file)
    }

    fun clearCache() {
        cacheDir.listFiles()?.forEach { it.delete() }
    }
}
