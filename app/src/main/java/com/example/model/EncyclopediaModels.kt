package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Evidence(
  val text: String,
  val source: String,
)

@JsonClass(generateAdapter = true)
data class Topic(
  val id: String,
  val title: String,
  val summary: String = "",
  val content: List<String> = emptyList(),
  val evidences: List<Evidence> = emptyList(),
  val benefits: List<String> = emptyList(),
  val references: List<String> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class TopicGroup(
  val key: String,
  val title: String,
  val description: String,
  val accent: String = "",
  val topics: List<Topic> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class FiqhSection(
  val id: String,
  val title: String,
  val description: String = "",
  val topics: List<Topic> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Surah(
  val id: String,
  val number: Int,
  val name: String,
  val ayahs: Int,
  val revelation: String,
  val virtue: String,
)

@JsonClass(generateAdapter = true)
data class AllahName(
  val id: String,
  val number: Int,
  val name: String,
  val meaning: String,
)

@JsonClass(generateAdapter = true)
data class EncyclopediaStats(
  val sections: Int,
  val topics: Int,
  val names: Int,
  val surahs: Int,
  val fiqhChapters: Int,
  val battles: Int,
)

@JsonClass(generateAdapter = true)
data class FeaturedTopicRef(
  val topic: Topic,
  val group: String,
)

@JsonClass(generateAdapter = true)
data class AboutData(
  val mission: String,
  val goals: List<String>,
  val methodology: List<String>,
  val sources: List<String>,
)

@JsonClass(generateAdapter = true)
data class SectionCardData(
  val key: String,
  val path: String,
  val title: String,
  val description: String,
  val accent: String,
  val icon: String,
  val count: Int,
  val unit: String,
)

@JsonClass(generateAdapter = true)
data class EncyclopediaPayload(
  val seerah: List<Topic>,
  val aqeedah: List<Topic>,
  val fiqh: List<FiqhSection>,
  val fiqhTopicsCount: Int,
  val quranScience: List<Topic>,
  val tajweed: List<Topic>,
  val surahs: List<Surah>,
  val asmaAllah: List<AllahName>,
  val seerahSections: List<TopicGroup>,
  val quranSections: List<TopicGroup>,
  val totalTopics: Int,
  val statistics: EncyclopediaStats,
  val featuredTopics: List<FeaturedTopicRef>,
  val featuredSurahs: List<Surah>,
  val featuredNames: List<AllahName>,
  val HOME_VERSE: String,
  val HOME_VERSE_SOURCE: String,
  val ABOUT: AboutData,
  val MEMORIZATION_PLAN: List<String>,
  val SECTION_CARDS: List<SectionCardData>,
)

data class TopicEntry(
  val topic: Topic,
  val sectionKey: String,
  val sectionTitle: String,
  val subGroupKey: String,
  val subGroupTitle: String,
  val orderIndex: Int,
)

enum class SearchCategory(val label: String) {
  ALL("الكل"),
  SEERAH("السيرة"),
  AQEEDAH("العقيدة"),
  QURAN_TAJWEED("القرآن والتجويد"),
  SURAHS("السور"),
  ASMA("الأسماء الحسنى"),
  FIQH("الفقه"),
}

sealed interface SearchResultItem {
  val id: String
  val score: Int
  val category: SearchCategory
  val groupLabel: String

  data class TopicResult(
    val entry: TopicEntry,
    override val score: Int,
    override val category: SearchCategory,
  ) : SearchResultItem {
    override val id: String = entry.topic.id
    override val groupLabel: String = entry.subGroupTitle
  }

  data class SurahResult(
    val surah: Surah,
    override val score: Int,
  ) : SearchResultItem {
    override val id: String = surah.id
    override val category: SearchCategory = SearchCategory.SURAHS
    override val groupLabel: String = "سور القرآن الكريم"
  }

  data class NameResult(
    val name: AllahName,
    override val score: Int,
  ) : SearchResultItem {
    override val id: String = name.id
    override val category: SearchCategory = SearchCategory.ASMA
    override val groupLabel: String = "أسماء الله الحسنى"
  }
}

enum class MemorizationStatus(val label: String) {
  NONE("لم يُبدأ"),
  IN_PROGRESS("قيد الحفظ"),
  MEMORIZED("محفوظة"),
  REVISED("تمت المراجعة"),
}

@JsonClass(generateAdapter = true)
data class MemorizationRecord(
  val surahId: String,
  val status: MemorizationStatus = MemorizationStatus.NONE,
  val revisionDate: String = "",
)

@JsonClass(generateAdapter = true)
data class ReadingHistoryItem(
  val entryId: String,
  val kind: String, // "topic", "surah", "name"
  val title: String,
  val subtitle: String,
  val timestampMillis: Long,
)

enum class FontScalePreset(val scale: Float, val label: String) {
  SMALL(0.9f, "صغير"),
  MEDIUM(1.0f, "متوسط"),
  LARGE(1.15f, "كبير"),
  EXTRA_LARGE(1.3f, "كبير جداً"),
}

data class UserPreferences(
  val isDarkTheme: Boolean = true,
  val fontScale: Float = 1.0f,
  val comfortLineSpacing: Boolean = true,
  val showSummariesInLists: Boolean = true,
  val favoriteTopicIds: Set<String> = emptySet(),
  val favoriteSurahIds: Set<String> = emptySet(),
  val favoriteNameIds: Set<String> = emptySet(),
  val readingHistory: List<ReadingHistoryItem> = emptyList(),
  val memorizationRecords: Map<String, MemorizationRecord> = emptyMap(),
)
