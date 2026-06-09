package com.pdfscan.app.domain.model

data class Document(
    val pages: List<ScannedPage> = emptyList(),
    val title: String = "Scan",
)
