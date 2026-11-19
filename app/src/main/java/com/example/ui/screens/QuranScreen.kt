package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.EncyclopediaPayload
import com.example.model.MemorizationStatus
import com.example.model.Surah
import com.example.model.TopicEntry
import com.example.model.UserPreferences
import com.example.ui.components.SectionHeader
import com.example.ui.components.SurahCompactCard
import com.example.ui.components.TopicListCard
import com.example.ui.theme.TarmeemThemeTokens
import com.example.util.ArabicSearchEngine

@Composable
fun QuranScreen(
  payload: EncyclopediaPayload,
  allTopicEntries: List<TopicEntry>,
  userPrefs: UserPreferences,
  onOpenTopic: (String) -> Unit,
  onToggleFavoriteTopic: (String) -> Unit,
  onOpenSurah: (Surah) -> Unit,
  onToggleFavoriteSurah: (String) -> Unit,
  onOpenMemorizationPlan: () -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  var selectedTab by rememberSaveable { mutableStateOf("science") } // "science", "tajweed", "surahs"
  var revelationFilter by rememberSaveable { mutableStateOf("all") } // "all", "مكية", "مدنية"
  var surahQuery by rememberSaveable { mutableStateOf("") }

  val scienceEntries = allTopicEntries.filter { it.subGroupKey == "quran-science" }
  val tajweedEntries = allTopicEntries.filter { it.subGroupKey == "quran-tajweed" }

  val filteredSurahs =
    payload.surahs.filter { s ->
      val matchesRev = revelationFilter == "all" || s.revelation == revelationFilter
      val normQ = ArabicSearchEngine.normalize(surahQuery)
      val matchesQ =
        normQ.isEmpty() ||
          ArabicSearchEngine.normalize("${s.name} ${s.virtue} ${s.number}").contains(normQ)
      matchesRev && matchesQ
    }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 900.dp).testTag("quran_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        SectionHeader(
          title = "القرآن الكريم وعلومه",
          subtitle = "علوم القرآن (12) · التجويد والوقف (12) · السور القرآنية (114)",
        )
        Spacer(Modifier.height(8.dp))

        // Quick Memorization Plan Banner
        Card(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .clickable(onClick = onOpenMemorizationPlan)
              .testTag("quran_open_memorization_banner"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.green.copy(alpha = 0.45f)),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.green.copy(alpha = 0.16f),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.EventNote,
                  contentDescription = null,
                  tint = colors.green,
                  modifier = Modifier.padding(10.dp).size(22.dp),
                )
              }
              Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                  text = "خطة الحفظ والمراجعة التفاعلية",
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                val memorizedCount =
                  userPrefs.memorizationRecords.values.count {
                    it.status == MemorizationStatus.MEMORIZED ||
                      it.status == MemorizationStatus.REVISED
                  }
                Text(
                  text = "أنجزت حفظ/مراجعة $memorizedCount من أصل 114 سورة · اضغط لإدارة خطتك",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.muted,
                )
              }
            }
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = "فتح خطة الحفظ",
              tint = colors.green,
            )
          }
        }

        Spacer(Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          FilterChip(
            selected = selectedTab == "science",
            onClick = { selectedTab = "science" },
            label = { Text("علوم القرآن (${scienceEntries.size})") },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("quran_tab_science"),
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = colors.gold,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
          )
          FilterChip(
            selected = selectedTab == "tajweed",
            onClick = { selectedTab = "tajweed" },
            label = { Text("التجويد (${tajweedEntries.size})") },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("quran_tab_tajweed"),
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = colors.gold,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
          )
          FilterChip(
            selected = selectedTab == "surahs",
            onClick = { selectedTab = "surahs" },
            label = { Text("قائمة السور (${payload.surahs.size})") },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("quran_tab_surahs"),
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = colors.gold,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
          )
        }
      }

      when (selectedTab) {
        "science" -> {
          items(scienceEntries, key = { it.topic.id }) { entry ->
            TopicListCard(
              entry = entry,
              isFavorite = userPrefs.favoriteTopicIds.contains(entry.topic.id),
              showSummary = userPrefs.showSummariesInLists,
              accentColor = colors.quranAccent,
              onClick = { onOpenTopic(entry.topic.id) },
              onToggleFavorite = { onToggleFavoriteTopic(entry.topic.id) },
            )
          }
        }

        "tajweed" -> {
          items(tajweedEntries, key = { it.topic.id }) { entry ->
            TopicListCard(
              entry = entry,
              isFavorite = userPrefs.favoriteTopicIds.contains(entry.topic.id),
              showSummary = userPrefs.showSummariesInLists,
              accentColor = colors.quranAccent,
              onClick = { onOpenTopic(entry.topic.id) },
              onToggleFavorite = { onToggleFavoriteTopic(entry.topic.id) },
            )
          }
        }

        "surahs" -> {
          item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              OutlinedTextField(
                value = surahQuery,
                onValueChange = { surahQuery = it },
                placeholder = { Text("ابحث باسم السورة أو فضلها...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                  if (surahQuery.isNotEmpty()) {
                    IconButton(onClick = { surahQuery = "" }) {
                      Icon(Icons.Rounded.Close, contentDescription = "مسح")
                    }
                  }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("surah_filter_input"),
              )
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("all" to "الكل (114)", "مكية" to "مكية", "مدنية" to "مدنية").forEach {
                  (key, label) ->
                  FilterChip(
                    selected = revelationFilter == key,
                    onClick = { revelationFilter = key },
                    label = { Text(label) },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                  )
                }
              }
              Text(
                text = "تنبيه: تعرض القائمة بيانات السور وفضلها كما وردت في الموسوعة، ولا تشمل نص المصحف كاملاً.",
                style = MaterialTheme.typography.labelSmall,
                color = colors.muted,
              )
            }
          }
          items(filteredSurahs, key = { it.id }) { surah ->
            SurahCompactCard(
              surah = surah,
              isFavorite = userPrefs.favoriteSurahIds.contains(surah.id),
              memorizationStatus =
                userPrefs.memorizationRecords[surah.id]?.status ?: MemorizationStatus.NONE,
              onClick = { onOpenSurah(surah) },
              onToggleFavorite = { onToggleFavoriteSurah(surah.id) },
            )
          }
        }
      }

      item { Spacer(Modifier.height(16.dp)) }
    }
  }
}
