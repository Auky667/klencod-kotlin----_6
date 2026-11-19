package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.EncyclopediaRepository
import com.example.data.UserPreferencesRepository
import com.example.model.AllahName
import com.example.model.EncyclopediaPayload
import com.example.model.MemorizationStatus
import com.example.model.SearchCategory
import com.example.model.SearchResultItem
import com.example.model.Surah
import com.example.model.TopicEntry
import com.example.model.UserPreferences
import com.example.util.ArabicSearchEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface EncyclopediaUiState {
  data object Loading : EncyclopediaUiState

  data class Ready(
    val payload: EncyclopediaPayload,
    val allTopicEntries: List<TopicEntry>,
  ) : EncyclopediaUiState

  data class Error(val message: String) : EncyclopediaUiState
}

class EncyclopediaViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = EncyclopediaRepository(application.applicationContext)
  private val prefsRepository = UserPreferencesRepository(application.applicationContext)

  private val _uiState = MutableStateFlow<EncyclopediaUiState>(EncyclopediaUiState.Loading)
  val uiState: StateFlow<EncyclopediaUiState> = _uiState.asStateFlow()

  val userPreferences: StateFlow<UserPreferences> =
    prefsRepository.preferencesFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = UserPreferences(),
    )

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _searchCategory = MutableStateFlow(SearchCategory.ALL)
  val searchCategory: StateFlow<SearchCategory> = _searchCategory.asStateFlow()

  val searchResults: StateFlow<List<SearchResultItem>> =
    combine(_uiState, _searchQuery, _searchCategory) { state, query, category ->
      if (state !is EncyclopediaUiState.Ready) {
        emptyList()
      } else {
        ArabicSearchEngine.search(
          query = query,
          category = category,
          topics = state.allTopicEntries,
          surahs = state.payload.surahs,
          names = state.payload.asmaAllah,
        )
      }
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = emptyList(),
    )

  init {
    loadData()
  }

  fun loadData() {
    viewModelScope.launch {
      _uiState.value = EncyclopediaUiState.Loading
      runCatching { repository.loadEncyclopedia() }
        .onSuccess { payload ->
          _uiState.value =
            EncyclopediaUiState.Ready(
              payload = payload,
              allTopicEntries = repository.getAllTopicEntries(),
            )
        }
        .onFailure { err ->
          _uiState.value =
            EncyclopediaUiState.Error(err.localizedMessage ?: "تعذر تحميل بيانات الموسوعة")
        }
    }
  }

  fun getTopicEntry(topicId: String): TopicEntry? = repository.getTopicEntry(topicId)

  fun getRelatedTopics(topicId: String): List<TopicEntry> = repository.getRelatedTopics(topicId)

  fun formatTopicForSharing(entry: TopicEntry): String = repository.formatTopicForSharing(entry)

  fun getSurahById(surahId: String): Surah? {
    val state = _uiState.value as? EncyclopediaUiState.Ready ?: return null
    return state.payload.surahs.find { it.id == surahId }
  }

  fun getNameById(nameId: String): AllahName? {
    val state = _uiState.value as? EncyclopediaUiState.Ready ?: return null
    return state.payload.asmaAllah.find { it.id == nameId }
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun updateSearchCategory(category: SearchCategory) {
    _searchCategory.value = category
  }

  fun setDarkTheme(isDark: Boolean) {
    viewModelScope.launch { prefsRepository.setDarkTheme(isDark) }
  }

  fun setFontScale(scale: Float) {
    viewModelScope.launch { prefsRepository.setFontScale(scale) }
  }

  fun setComfortLineSpacing(enabled: Boolean) {
    viewModelScope.launch { prefsRepository.setComfortLineSpacing(enabled) }
  }

  fun setShowSummariesInLists(enabled: Boolean) {
    viewModelScope.launch { prefsRepository.setShowSummariesInLists(enabled) }
  }

  fun toggleFavoriteTopic(topicId: String) {
    viewModelScope.launch { prefsRepository.toggleFavoriteTopic(topicId) }
  }

  fun toggleFavoriteSurah(surahId: String) {
    viewModelScope.launch { prefsRepository.toggleFavoriteSurah(surahId) }
  }

  fun toggleFavoriteName(nameId: String) {
    viewModelScope.launch { prefsRepository.toggleFavoriteName(nameId) }
  }

  fun recordTopicRead(entry: TopicEntry) {
    viewModelScope.launch {
      prefsRepository.recordRead(
        entryId = entry.topic.id,
        kind = "topic",
        title = entry.topic.title,
        subtitle = "${entry.sectionTitle} · ${entry.subGroupTitle}",
      )
    }
  }

  fun recordSurahRead(surah: Surah) {
    viewModelScope.launch {
      prefsRepository.recordRead(
        entryId = surah.id,
        kind = "surah",
        title = "سورة ${surah.name}",
        subtitle = "${surah.revelation} · ${surah.ayahs} آية",
      )
    }
  }

  fun recordNameRead(name: AllahName) {
    viewModelScope.launch {
      prefsRepository.recordRead(
        entryId = name.id,
        kind = "name",
        title = name.name,
        subtitle = "أسماء الله الحسنى · رقم ${name.number}",
      )
    }
  }

  fun clearReadingHistory() {
    viewModelScope.launch { prefsRepository.clearReadingHistory() }
  }

  fun clearAllFavorites() {
    viewModelScope.launch { prefsRepository.clearAllFavorites() }
  }

  fun updateMemorization(surahId: String, status: MemorizationStatus, revisionDate: String) {
    viewModelScope.launch { prefsRepository.updateMemorization(surahId, status, revisionDate) }
  }

  fun clearMemorizationPlan() {
    viewModelScope.launch { prefsRepository.clearMemorizationPlan() }
  }
}
