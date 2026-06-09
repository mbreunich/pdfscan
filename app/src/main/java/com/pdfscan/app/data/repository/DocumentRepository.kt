package com.pdfscan.app.data.repository

import android.net.Uri
import com.pdfscan.app.data.storage.FileStorage
import com.pdfscan.app.domain.model.Document
import com.pdfscan.app.domain.model.ImageFilter
import com.pdfscan.app.domain.model.ScannedPage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val fileStorage: FileStorage,
) {
    private var currentDocument = Document()

    fun getDocument(): Document = currentDocument

    fun addPages(uris: List<Uri>) {
        val newPages = uris.map { uri ->
            val cachedUri = fileStorage.copyToCache(uri)
            ScannedPage(uri = cachedUri)
        }
        currentDocument = currentDocument.copy(
            pages = currentDocument.pages + newPages
        )
    }

    fun removePage(pageId: String) {
        currentDocument = currentDocument.copy(
            pages = currentDocument.pages.filter { it.id != pageId }
        )
    }

    fun updatePageFilter(pageId: String, filter: ImageFilter) {
        currentDocument = currentDocument.copy(
            pages = currentDocument.pages.map { page ->
                if (page.id == pageId) page.copy(filter = filter) else page
            }
        )
    }

    fun reorderPages(fromIndex: Int, toIndex: Int) {
        val pages = currentDocument.pages.toMutableList()
        val page = pages.removeAt(fromIndex)
        pages.add(toIndex, page)
        currentDocument = currentDocument.copy(pages = pages)
    }

    fun updateTitle(title: String) {
        currentDocument = currentDocument.copy(title = title)
    }

    fun reset() {
        fileStorage.clearCache()
        currentDocument = Document()
    }
}
