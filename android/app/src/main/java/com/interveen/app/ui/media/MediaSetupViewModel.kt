package com.interveen.app.ui.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interveen.app.domain.model.Goal
import com.interveen.app.domain.model.GoalKind
import com.interveen.app.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MediaSetupUiState(
    val title: String = "",
    val body: String = "",
    val pendingCaptureUri: Uri? = null,
    val savedGoal: Goal = Goal(),
    val saved: Boolean = false,
)

@HiltViewModel
class MediaSetupViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val draft = MutableStateFlow(Draft())

    val uiState: StateFlow<MediaSetupUiState> = combine(
        preferencesRepository.preferences,
        draft,
    ) { prefs, current ->
        MediaSetupUiState(
            title = current.title ?: prefs.goal.title,
            body = current.body ?: prefs.goal.body,
            pendingCaptureUri = current.pendingCaptureUri,
            savedGoal = prefs.goal,
            saved = current.saved,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MediaSetupUiState())

    fun onTitleChange(value: String) {
        draft.update { it.copy(title = value, saved = false) }
    }

    fun onBodyChange(value: String) {
        draft.update { it.copy(body = value, saved = false) }
    }

    fun createCaptureUri(extension: String): Uri {
        val dir = File(context.cacheDir, "media").apply { mkdirs() }
        val file = File(dir, "capture_${System.currentTimeMillis()}.$extension")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        draft.update { it.copy(pendingCaptureUri = uri) }
        return uri
    }

    fun onVideoPicked(uri: Uri?) {
        persistMedia(uri, GoalKind.VIDEO)
    }

    fun onImagePicked(uri: Uri?) {
        persistMedia(uri, GoalKind.IMAGE)
    }

    fun saveTextGoal() {
        viewModelScope.launch {
            val state = uiState.value
            preferencesRepository.setGoal(
                Goal(
                    kind = GoalKind.TEXT,
                    title = state.title,
                    body = state.body,
                    mediaUri = null,
                ),
            )
            draft.update { it.copy(saved = true) }
        }
    }

    private fun persistMedia(source: Uri?, kind: GoalKind) {
        if (source == null) return
        viewModelScope.launch {
            val stored = withContext(Dispatchers.IO) { copyIntoGoals(source, kind) }
            val state = uiState.value
            preferencesRepository.setGoal(
                Goal(
                    kind = kind,
                    title = state.title,
                    body = state.body,
                    mediaUri = stored,
                ),
            )
            draft.update { it.copy(pendingCaptureUri = null, saved = true) }
        }
    }

    private fun copyIntoGoals(source: Uri, kind: GoalKind): String {
        val dir = File(context.filesDir, "goals").apply { mkdirs() }
        val extension = if (kind == GoalKind.VIDEO) "mp4" else "jpg"
        val target = File(dir, "goal.$extension")
        context.contentResolver.openInputStream(source).use { input ->
            requireNotNull(input) { "Unable to read selected media." }
            target.outputStream().use { output -> input.copyTo(output) }
        }
        return Uri.fromFile(target).toString()
    }

    private data class Draft(
        val title: String? = null,
        val body: String? = null,
        val pendingCaptureUri: Uri? = null,
        val saved: Boolean = false,
    )
}
