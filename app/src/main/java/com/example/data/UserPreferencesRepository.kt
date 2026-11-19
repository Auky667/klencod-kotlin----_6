package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.MemorizationRecord
import com.example.model.MemorizationStatus
import com.example.model.ReadingHistoryItem
import com.example.model.UserPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tarmeemDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "tarmeem_islam_prefs"
)

class UserPreferencesRepository(private val context: Context) {

  private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
  private val historyListType =
    Types.newParameterizedType(List::class.java, ReadingHistoryItem::class.java)
  private val historyAdapter = moshi.adapter<List<ReadingHistoryItem>>(historyListType)

  private val memoListType =
    Types.newParameterizedType(List::class.java, MemorizationRecord::class.java)
  private val memoAdapter = moshi.adapter<List<MemorizationRecord>>(memoListType)

  private object Keys {
    val DARK_THEME = booleanPreferencesKey("dark_theme")
    val FONT_SCALE = floatPreferencesKey("font_scale")
    val COMFORT_LINE_SPACING = booleanPreferencesKey("comfort_line_spacing")
    val SHOW_SUMMARIES = booleanPreferencesKey("show_summaries")
    val FAV_TOPICS = stringSetPreferencesKey("fav_topics")
    val FAV_SURAHS = stringSetPreferencesKey("fav_surahs")
    val FAV_NAMES = stringSetPreferencesKey("fav_names")
    val READING_HISTORY_JSON = stringPreferencesKey("reading_history_json")
    val MEMORIZATION_JSON = stringPreferencesKey("memorization_json")
  }

  val preferencesFlow: Flow<UserPreferences> =
    context.tarmeemDataStore.data.map { prefs ->
      val history =
        prefs[Keys.READING_HISTORY_JSON]?.let { json ->
          runCatching { historyAdapter.fromJson(json) }.getOrNull()
        } ?: emptyList()

      val memoList =
        prefs[Keys.MEMORIZATION_JSON]?.let { json ->
          runCatching { memoAdapter.fromJson(json) }.getOrNull()
        } ?: emptyList()

      UserPreferences(
        isDarkTheme = prefs[Keys.DARK_THEME] ?: true,
        fontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
        comfortLineSpacing = prefs[Keys.COMFORT_LINE_SPACING] ?: true,
        showSummariesInLists = prefs[Keys.SHOW_SUMMARIES] ?: true,
        favoriteTopicIds = prefs[Keys.FAV_TOPICS] ?: emptySet(),
        favoriteSurahIds = prefs[Keys.FAV_SURAHS] ?: emptySet(),
        favoriteNameIds = prefs[Keys.FAV_NAMES] ?: emptySet(),
        readingHistory = history,
        memorizationRecords = memoList.associateBy { it.surahId },
      )
    }

  suspend fun setDarkTheme(isDark: Boolean) {
    context.tarmeemDataStore.edit { it[Keys.DARK_THEME] = isDark }
  }

  suspend fun setFontScale(scale: Float) {
    context.tarmeemDataStore.edit { it[Keys.FONT_SCALE] = scale.coerceIn(0.85f, 1.45f) }
  }

  suspend fun setComfortLineSpacing(enabled: Boolean) {
    context.tarmeemDataStore.edit { it[Keys.COMFORT_LINE_SPACING] = enabled }
  }

  suspend fun setShowSummariesInLists(enabled: Boolean) {
    context.tarmeemDataStore.edit { it[Keys.SHOW_SUMMARIES] = enabled }
  }

  suspend fun toggleFavoriteTopic(topicId: String): Boolean {
    var added = false
    context.tarmeemDataStore.edit { prefs ->
      val current = prefs[Keys.FAV_TOPICS].orEmpty().toMutableSet()
      added =
        if (current.contains(topicId)) {
          current.remove(topicId)
          false
        } else {
          current.add(topicId)
          true
        }
      prefs[Keys.FAV_TOPICS] = current
    }
    return added
  }

  suspend fun toggleFavoriteSurah(surahId: String): Boolean {
    var added = false
    context.tarmeemDataStore.edit { prefs ->
      val current = prefs[Keys.FAV_SURAHS].orEmpty().toMutableSet()
      added =
        if (current.contains(surahId)) {
          current.remove(surahId)
          false
        } else {
          current.add(surahId)
          true
        }
      prefs[Keys.FAV_SURAHS] = current
    }
    return added
  }

  suspend fun toggleFavoriteName(nameId: String): Boolean {
    var added = false
    context.tarmeemDataStore.edit { prefs ->
      val current = prefs[Keys.FAV_NAMES].orEmpty().toMutableSet()
      added =
        if (current.contains(nameId)) {
          current.remove(nameId)
          false
        } else {
          current.add(nameId)
          true
        }
      prefs[Keys.FAV_NAMES] = current
    }
    return added
  }

  suspend fun recordRead(entryId: String, kind: String, title: String, subtitle: String) {
    context.tarmeemDataStore.edit { prefs ->
      val existing =
        prefs[Keys.READING_HISTORY_JSON]?.let {
          runCatching { historyAdapter.fromJson(it) }.getOrNull()
        } ?: emptyList()
      val updated =
        listOf(
          ReadingHistoryItem(
            entryId = entryId,
            kind = kind,
            title = title,
            subtitle = subtitle,
            timestampMillis = System.currentTimeMillis(),
          )
        ) + existing.filterNot { it.entryId == entryId && it.kind == kind }.take(29)
      prefs[Keys.READING_HISTORY_JSON] = historyAdapter.toJson(updated)
    }
  }

  suspend fun clearReadingHistory() {
    context.tarmeemDataStore.edit { it.remove(Keys.READING_HISTORY_JSON) }
  }

  suspend fun clearAllFavorites() {
    context.tarmeemDataStore.edit {
      it.remove(Keys.FAV_TOPICS)
      it.remove(Keys.FAV_SURAHS)
      it.remove(Keys.FAV_NAMES)
    }
  }

  suspend fun updateMemorization(
    surahId: String,
    status: MemorizationStatus,
    revisionDate: String,
  ) {
    context.tarmeemDataStore.edit { prefs ->
      val existing =
        prefs[Keys.MEMORIZATION_JSON]?.let {
          runCatching { memoAdapter.fromJson(it) }.getOrNull()
        }?.associateBy { it.surahId }?.toMutableMap() ?: mutableMapOf()

      if (status == MemorizationStatus.NONE && revisionDate.isBlank()) {
        existing.remove(surahId)
      } else {
        existing[surahId] =
          MemorizationRecord(
            surahId = surahId,
            status = status,
            revisionDate = revisionDate.trim(),
          )
      }
      prefs[Keys.MEMORIZATION_JSON] = memoAdapter.toJson(existing.values.toList())
    }
  }

  suspend fun clearMemorizationPlan() {
    context.tarmeemDataStore.edit { it.remove(Keys.MEMORIZATION_JSON) }
  }
}
