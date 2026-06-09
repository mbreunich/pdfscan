package com.pdfscan.app.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.pdfscan.app.domain.model.ImageFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApplyFilterUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
) {

    suspend fun apply(uri: Uri, filter: ImageFilter): Bitmap = withContext(Dispatchers.Default) {
        val sourceBitmap = loadBitmap(uri)
        when (filter) {
            ImageFilter.ORIGINAL -> sourceBitmap
            ImageFilter.GRAYSCALE -> applyColorMatrix(sourceBitmap, grayscaleMatrix())
            ImageFilter.BLACK_WHITE -> applyBlackWhite(sourceBitmap)
        }
    }

    private suspend fun loadBitmap(uri: Uri): Bitmap {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false)
            .build()
        val result = imageLoader.execute(request)
        return result.drawable?.toBitmap() ?: throw IllegalStateException("Failed to load image")
    }

    private fun applyColorMatrix(source: Bitmap, matrix: ColorMatrix): Bitmap {
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }
        canvas.drawBitmap(source, 0f, 0f, paint)
        return result
    }

    private fun applyBlackWhite(source: Bitmap): Bitmap {
        val grayscale = applyColorMatrix(source, grayscaleMatrix())
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val threshold = 128
        for (x in 0 until grayscale.width) {
            for (y in 0 until grayscale.height) {
                val pixel = grayscale.getPixel(x, y)
                val gray = pixel and 0xFF
                val bw = if (gray > threshold) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
                result.setPixel(x, y, bw)
            }
        }
        return result
    }

    private fun grayscaleMatrix(): ColorMatrix {
        return ColorMatrix().apply { setSaturation(0f) }
    }
}
