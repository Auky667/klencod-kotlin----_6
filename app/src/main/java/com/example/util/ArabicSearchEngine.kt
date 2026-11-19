package com.example.util

import com.example.model.AllahName
import com.example.model.SearchCategory
import com.example.model.SearchResultItem
import com.example.model.Surah
import com.example.model.TopicEntry
import java.text.Normalizer

object ArabicSearchEngine {

  private val DIACRITICS_REGEX = Regex("[\u064B-\u065F\u0670\u06D6-\u06ED\u0640]")
  private val WHITESPACE_REGEX = Regex("\\s+")

  fun normalize(input: String?): String {
    if (input.isNullOrBlank()) return ""
    val decomposed = Normalizer.normalize(input, Normalizer.Form.NFKD)
    val stripped = DIACRITICS_REGEX.replace(decomposed, "")
    val sb = StringBuilder(stripped.length)
    for (ch in stripped) {
      val mapped =
        when (ch) {
          'أ', 'إ', 'آ', 'ٱ' -> 'ا'
          'ى' -> 'ي'
          'ة' -> 'ه'
          'ؤ' -> 'و'
          'ئ' -> 'ي'
          else -> ch.lowercaseChar()
        }
      sb.append(mapped)
    }
    return WHITESPACE_REGEX.replace(sb.toString(), " ").trim()
  }

  fun search(
    query: String,
    category: SearchCategory,
    topics: List<TopicEntry>,
    surahs: List<Surah>,
    names: List<AllahName>,
  ): List<SearchResultItem> {
    val normQuery = normalize(query)
    if (normQuery.isEmpty()) return emptyList()

    val tokens = normQuery.split(" ").filter { it.isNotEmpty() }
    val results = mutableListOf<SearchResultItem>()

    for (entry in topics) {
      val entryCategory = mapSectionToCategory(entry.sectionKey)
      if (category != SearchCategory.ALL && category != entryCategory) continue

      val normTitle = normalize(entry.topic.title)
      val normSummary = normalize(entry.topic.summary)
      val normGroup = normalize("${entry.sectionTitle} ${entry.subGroupTitle}")
      val normBody =
        normalize(
          buildString {
            entry.topic.content.forEach { append(it).append(' ') }
            entry.topic.evidences.forEach {
              append(it.text).append(' ').append(it.source).append(' ')
            }
            entry.topic.benefits.forEach { append(it).append(' ') }
            entry.topic.references.forEach { append(it).append(' ') }
          }
        )

      val combined = "$normTitle $normSummary $normGroup $normBody"
      if (tokens.all { combined.contains(it) }) {
        val score =
          when {
            normTitle == normQuery -> 0
            normTitle.startsWith(normQuery) || normTitle.contains(normQuery) -> 1
            tokens.all { normTitle.contains(it) } -> 2
            normSummary.contains(normQuery) || normGroup.contains(normQuery) -> 3
            else -> 4
          }
        results.add(SearchResultItem.TopicResult(entry = entry, score = score, category = entryCategory))
      }
    }

    if (category == SearchCategory.ALL || category == SearchCategory.SURAHS) {
      for (surah in surahs) {
        val normName = normalize(surah.name)
        val normFull = normalize("سورة ${surah.name} ${surah.revelation} ${surah.virtue} ${surah.number}")
        if (tokens.all { normFull.contains(it) }) {
          val score =
            when {
              normName == normQuery || normName == normQuery.removePrefix("سوره ") -> 0
              normName.contains(normQuery) -> 1
              else -> 3
            }
          results.add(SearchResultItem.SurahResult(surah = surah, score = score))
        }
      }
    }

    if (category == SearchCategory.ALL || category == SearchCategory.ASMA) {
      for (allahName in names) {
        val normName = normalize(allahName.name)
        val normMeaning = normalize(allahName.meaning)
        val normFull = "$normName $normMeaning"
        if (tokens.all { normFull.contains(it) }) {
          val score =
            when {
              normName == normQuery -> 0
              normName.contains(normQuery) -> 1
              else -> 3
            }
          results.add(SearchResultItem.NameResult(name = allahName, score = score))
        }
      }
    }

    return results.sortedWith(compareBy<SearchResultItem> { it.score }.thenBy { it.id })
  }

  private fun mapSectionToCategory(sectionKey: String): SearchCategory =
    when (sectionKey) {
      "seerah" -> SearchCategory.SEERAH
      "aqeedah" -> SearchCategory.AQEEDAH
      "quran" -> SearchCategory.QURAN_TAJWEED
      "fiqh" -> SearchCategory.FIQH
      else -> SearchCategory.ALL
    }
}
