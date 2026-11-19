package com.example.data

import android.content.Context
import com.example.model.EncyclopediaPayload
import com.example.model.TopicEntry
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EncyclopediaRepository(private val context: Context) {

  private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
  private val adapter = moshi.adapter(EncyclopediaPayload::class.java)

  @Volatile
  private var cachedPayload: EncyclopediaPayload? = null

  @Volatile
  private var cachedTopicEntries: List<TopicEntry>? = null

  @Volatile
  private var cachedTopicMap: Map<String, TopicEntry>? = null

  suspend fun loadEncyclopedia(): EncyclopediaPayload =
    cachedPayload ?: withContext(Dispatchers.IO) {
      cachedPayload?.let { return@withContext it }
      val json = context.assets.open("encyclopedia.json").bufferedReader(Charsets.UTF_8).use {
        it.readText()
      }
      val parsed =
        requireNotNull(adapter.fromJson(json)) { "Failed to parse encyclopedia.json" }
      buildIndexes(parsed)
      cachedPayload = parsed
      parsed
    }

  private fun buildIndexes(payload: EncyclopediaPayload) {
    val entries = mutableListOf<TopicEntry>()
    var order = 0

    payload.seerahSections.forEach { group ->
      group.topics.forEach { topic ->
        entries.add(
          TopicEntry(
            topic = topic,
            sectionKey = "seerah",
            sectionTitle = "السيرة النبوية",
            subGroupKey = group.key,
            subGroupTitle = group.title,
            orderIndex = order++,
          )
        )
      }
    }

    payload.aqeedah.forEach { topic ->
      entries.add(
        TopicEntry(
          topic = topic,
          sectionKey = "aqeedah",
          sectionTitle = "العقيدة الإسلامية",
          subGroupKey = "aqeedah",
          subGroupTitle = "أصول العقيدة",
          orderIndex = order++,
        )
      )
    }

    payload.quranSections.forEach { group ->
      group.topics.forEach { topic ->
        entries.add(
          TopicEntry(
            topic = topic,
            sectionKey = "quran",
            sectionTitle = "القرآن الكريم وعلومه",
            subGroupKey = group.key,
            subGroupTitle = group.title,
            orderIndex = order++,
          )
        )
      }
    }

    payload.fiqh.forEach { fiqhSec ->
      fiqhSec.topics.forEach { topic ->
        entries.add(
          TopicEntry(
            topic = topic,
            sectionKey = "fiqh",
            sectionTitle = "الفقه الإسلامي",
            subGroupKey = fiqhSec.id,
            subGroupTitle = fiqhSec.title,
            orderIndex = order++,
          )
        )
      }
    }

    cachedTopicEntries = entries
    cachedTopicMap = entries.associateBy { it.topic.id }
  }

  fun getAllTopicEntries(): List<TopicEntry> = cachedTopicEntries.orEmpty()

  fun getTopicEntry(topicId: String): TopicEntry? = cachedTopicMap?.get(topicId)

  fun getRelatedTopics(topicId: String, limit: Int = 4): List<TopicEntry> {
    val current = getTopicEntry(topicId) ?: return emptyList()
    val all = getAllTopicEntries()
    val sameSubGroup =
      all.filter { it.topic.id != topicId && it.subGroupKey == current.subGroupKey }
    val sameSection =
      all.filter {
        it.topic.id != topicId &&
          it.sectionKey == current.sectionKey &&
          it.subGroupKey != current.subGroupKey
      }
    return (sameSubGroup + sameSection).take(limit)
  }

  fun formatTopicForSharing(entry: TopicEntry): String {
    val t = entry.topic
    return buildString {
      appendLine("【${t.title}】")
      appendLine("${entry.sectionTitle} · ${entry.subGroupTitle}")
      if (t.summary.isNotBlank()) {
        appendLine()
        appendLine(t.summary)
      }
      t.content.forEach { para ->
        appendLine()
        appendLine(para)
      }
      if (t.evidences.isNotEmpty()) {
        appendLine()
        appendLine("الأدلة الشرعية:")
        t.evidences.forEach { ev ->
          appendLine("• ${ev.text}")
          if (ev.source.isNotBlank()) appendLine("  (${ev.source})")
        }
      }
      if (t.benefits.isNotEmpty()) {
        appendLine()
        appendLine("الفوائد والدروس:")
        t.benefits.forEachIndexed { idx, b -> appendLine("${idx + 1}. $b") }
      }
      if (t.references.isNotEmpty()) {
        appendLine()
        appendLine("المراجع: ${t.references.joinToString("، ")}")
      }
      appendLine()
      append("— موسوعة ترميم الإسلام")
    }.trim()
  }
}
