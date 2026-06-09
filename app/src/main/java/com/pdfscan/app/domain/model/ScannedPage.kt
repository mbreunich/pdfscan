package com.pdfscan.app.domain.model

import android.net.Uri

data class ScannedPage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val uri: Uri,
    val filter: ImageFilter = ImageFilter.ORIGINAL,
)

enum class ImageFilter {
    ORIGINAL,
    GRAYSCALE,
    BLACK_WHITE,
}
