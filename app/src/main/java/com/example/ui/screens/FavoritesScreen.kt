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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AllahName
import com.example.model.EncyclopediaPayload
import com.example.model.MemorizationStatus
import com.example.model.ReadingHistoryItem
import com.example.model.Surah
import com.example.model.TopicEntry
import com.example.model.UserPreferences
import com.example.ui.components.AllahNameCard
import com.example.ui.components.EmptyStateBox
import com.example.ui.components.SectionHeader
import com.example.ui.components.SurahCompactCard
import com.example.ui.components.TopicListCard
import com.example.ui.theme.TarmeemThemeTokens

@Composable
fun FavoritesScreen(
  payload: EncyclopediaPayload,
  allTopicEntries: List<TopicEntry>,
  userPrefs: UserPreferences,
  onOpenTopic: (String) -> Unit,
  onOpenSurah: (Surah) -> Unit,
  onOpenName: (AllahName) -> Unit,
  onOpenHistoryItem: (ReadingHistoryItem) -> Unit,
  onToggleFavoriteTopic: (String) -> Unit,
  onToggleFavoriteSurah: (String) -> Unit,
  onToggleFavoriteName: (String) -> Unit,
  onClearReadingHistory: () -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  var selectedTab by rememberSaveable { mutableStateOf("topics") } // "topics", "surahs", "names", "history"

  val favTopics = allTopicEntries.filter { userPrefs.favoriteTopicIds.contains(it.topic.id) }
  val favSurahs = payload.surahs.filter { userPrefs.favoriteSurahIds.contains(it.id) }
  val favNames = payload.asmaAllah.filter { userPrefs.favoriteNameIds.contains(it.id) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 900.dp).testTag("favorites_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        SectionHeader(
          title = "المفضلة وسجل القراءة",
          subtitle = "الوصول السريع إلى الموضوعات والسور والأسماء المحفوظة وسجل المطالعة",
        )
        Spacer(Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          listOf(
            "topics" to "الموضوعات (${favTopics.size})",
            "surahs" to "السور (${favSurahs.size})",
            "names" to "الأسماء (${favNames.size})",
            "history" to "سجل القراءة (${userPrefs.readingHistory.size})",
          ).forEach { (key, label) ->
            FilterChip(
              selected = selectedTab == key,
              onClick = { selectedTab = key },
              label = { Text(label) },
              modifier = Modifier.minimumInteractiveComponentSize().testTag("fav_tab_$key"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = colors.gold,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
          }
        }
      }

      when (selectedTab) {
        "topics" -> {
          if (favTopics.isEmpty()) {
            item {
              EmptyStateBox(
                icon = Icons.Outlined.FavoriteBorder,
                title = "لا توجد موضوعات محفوظة بعد",
                message = "اضغط على أيقونة القلب في أي موضوع من السيرة أو العقيدة أو القرآن أو الفقه لإضافته هنا.",
              )
            }
          } else {
            items(favTopics, key = { it.topic.id }) { entry ->
              TopicListCard(
                entry = entry,
                isFavorite = true,
                showSummary = userPrefs.showSummariesInLists,
                accentColor = colors.gold,
                badgeText = "${entry.sectionTitle} · ${entry.subGroupTitle}",
                onClick = { onOpenTopic(entry.topic.id) },
                onToggleFavorite = { onToggleFavoriteTopic(entry.topic.id) },
              )
            }
          }
        }

        "surahs" -> {
          if (favSurahs.isEmpty()) {
            item {
              EmptyStateBox(
                icon = Icons.Outlined.FavoriteBorder,
                title = "لا توجد سور في المفضلة",
                message = "يمكنك حفظ السور المفضلة من قسم القرآن الكريم أو من نتائج البحث.",
              )
            }
          } else {
            items(favSurahs, key = { it.id }) { surah ->
              SurahCompactCard(
                surah = surah,
                isFavorite = true,
                memorizationStatus =
                  userPrefs.memorizationRecords[surah.id]?.status ?: MemorizationStatus.NONE,
                onClick = { onOpenSurah(surah) },
                onToggleFavorite = { onToggleFavoriteSurah(surah.id) },
              )
            }
          }
        }

        "names" -> {
          if (favNames.isEmpty()) {
            item {
              EmptyStateBox(
                icon = Icons.Outlined.FavoriteBorder,
                title = "لا توجد أسماء محفوظة",
                message = "احفظ الأسماء التي تتدبّر معانيها من قسم أسماء الله الحسنى.",
              )
            }
          } else {
            items(favNames, key = { it.id }) { name ->
              AllahNameCard(
                allahName = name,
                isFavorite = true,
                onClick = { onOpenName(name) },
                onToggleFavorite = { onToggleFavoriteName(name.id) },
              )
            }
          }
        }

        "history" -> {
          if (userPrefs.readingHistory.isEmpty()) {
            item {
              EmptyStateBox(
                icon = Icons.Rounded.History,
                title = "سجل القراءة فارغ",
                message = "سيظهر هنا آخر ما قرأته في الموسوعة لتتمكن من المتابعة في أي وقت.",
              )
            }
          } else {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
              ) {
                TextButton(
                  onClick = onClearReadingHistory,
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Text("مسح السجل", color = colors.gold)
                }
              }
            }
            items(
              userPrefs.readingHistory,
              key = { "${it.kind}_${it.entryId}_${it.timestampMillis}" },
            ) { item ->
              Surface(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenHistoryItem(item) },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, colors.line),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = colors.gold,
                    modifier = Modifier.size(20.dp),
                  )
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = item.title,
                      style = MaterialTheme.typography.titleMedium,
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                      text = item.subtitle,
                      style = MaterialTheme.typography.bodySmall,
                      color = colors.muted,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                  Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "فتح",
                    tint = colors.muted,
                    modifier = Modifier.size(16.dp),
                  )
                }
              }
            }
          }
        }
      }

      item { Spacer(Modifier.height(16.dp)) }
    }
  }
}
