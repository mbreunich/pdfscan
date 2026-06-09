package com.pdfscan.app.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.pdfscan.app.domain.model.ScannedPage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExportPdfUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val applyFilterUseCase: ApplyFilterUseCase,
) {

    companion object {
        // A4 at 72 DPI
        private const val A4_WIDTH = 595
        private const val A4_HEIGHT = 842
    }

    suspend fun export(pages: List<ScannedPage>, fileName: String): Uri = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()

        try {
            pages.forEachIndexed { index, page ->
                val bitmap = applyFilterUseCase.apply(page.uri, page.filter)
                val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, index + 1).create()
                val pdfPage = pdfDocument.startPage(pageInfo)

                drawBitmapOnPage(pdfPage.canvas, bitmap)

                pdfDocument.finishPage(pdfPage)
                bitmap.recycle()
            }

            val uri = savePdfToMediaStore(pdfDocument, fileName)
            uri
        } finally {
            pdfDocument.close()
        }
    }

    private fun drawBitmapOnPage(canvas: Canvas, bitmap: Bitmap) {
        val scaleX = A4_WIDTH.toFloat() / bitmap.width
        val scaleY = A4_HEIGHT.toFloat() / bitmap.height
        val scale = minOf(scaleX, scaleY)

        val scaledWidth = bitmap.width * scale
        val scaledHeight = bitmap.height * scale
        val left = (A4_WIDTH - scaledWidth) / 2f
        val top = (A4_HEIGHT - scaledHeight) / 2f

        canvas.save()
        canvas.translate(left, top)
        canvas.scale(scale, scale)
        canvas.drawBitmap(bitmap, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        canvas.restore()
    }

    private fun savePdfToMediaStore(pdfDocument: PdfDocument, fileName: String): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/PdfScan")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            ?: throw IllegalStateException("Failed to create MediaStore entry")

        resolver.openOutputStream(uri)?.use { outputStream ->
            pdfDocument.writeTo(outputStream)
        } ?: throw IllegalStateException("Failed to open output stream")

        return uri
    }
}
