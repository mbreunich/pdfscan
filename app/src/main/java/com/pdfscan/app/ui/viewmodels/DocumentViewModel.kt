package com.pdfscan.app.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdfscan.app.data.repository.DocumentRepository
import com.pdfscan.app.domain.model.ImageFilter
import com.pdfscan.app.domain.model.ScannedPage
import com.pdfscan.app.domain.usecase.ExportPdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DocumentUiState(
    val pages: List<ScannedPage> = emptyList(),
    val title: String = "Scan",
)

sealed class ExportState {
    data object Idle : ExportState()
    data object Exporting : ExportState()
    data class Success(val uri: Uri) : ExportState()
    data class Error(val message: String) : ExportState()
}

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val repository: DocumentRepository,
    private val exportPdfUseCase: ExportPdfUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentUiState())
    val uiState: StateFlow<DocumentUiState> = _uiState.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    init {
        refreshState()
    }

    fun addPages(uris: List<Uri>) {
        repository.addPages(uris)
        refreshState()
    }

    fun removePage(pageId: String) {
        repository.removePage(pageId)
        refreshState()
    }

    fun updatePageFilter(pageId: String, filter: ImageFilter) {
        repository.updatePageFilter(pageId, filter)
        refreshState()
    }

    fun reorderPages(fromIndex: Int, toIndex: Int) {
        repository.reorderPages(fromIndex, toIndex)
        refreshState()
    }

    fun exportPdf(fileName: String) {
        val pages = _uiState.value.pages
        if (pages.isEmpty()) return

        viewModelScope.launch {
            _exportState.value = ExportState.Exporting
            try {
                val uri = exportPdfUseCase.export(pages, fileName)
                _exportState.value = ExportState.Success(uri)
            } catch (e: Exception) {
                _exportState.value = ExportState.Error(e.message ?: "Unbekannter Fehler")
            }
        }
    }

    fun reset() {
        repository.reset()
        _exportState.value = ExportState.Idle
        refreshState()
    }

    private fun refreshState() {
        val doc = repository.getDocument()
        _uiState.value = DocumentUiState(
            pages = doc.pages,
            title = doc.title,
        )
    }
}
