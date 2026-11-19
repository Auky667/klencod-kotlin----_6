package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.EncyclopediaPayload
import com.example.model.TopicEntry
import com.example.model.UserPreferences
import com.example.ui.components.SectionHeader
import com.example.ui.components.TopicListCard
import com.example.ui.theme.TarmeemThemeTokens

@Composable
fun SeerahScreen(
  payload: EncyclopediaPayload,
  allTopicEntries: List<TopicEntry>,
  userPrefs: UserPreferences,
  onOpenTopic: (String) -> Unit,
  onToggleFavorite: (String) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  val seerahEntries = allTopicEntries.filter { it.sectionKey == "seerah" }
  var selectedSubGroup by rememberSaveable { mutableStateOf("all") }

  val filteredEntries =
    if (selectedSubGroup == "all") seerahEntries
    else seerahEntries.filter { it.subGroupKey == selectedSubGroup }

  val activeGroupMeta = payload.seerahSections.find { it.key == selectedSubGroup }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 900.dp).testTag("seerah_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        SectionHeader(
          title = "السيرة النبوية",
          subtitle = "55 موضوعًا موثقًا موزعة بين أحداث السيرة والشمائل المحمدية والخلفاء الراشدين",
        )
        Spacer(Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          FilterChip(
            selected = selectedSubGroup == "all",
            onClick = { selectedSubGroup = "all" },
            label = { Text("الكل (${seerahEntries.size})") },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("seerah_filter_all"),
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = colors.gold,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
          )
          payload.seerahSections.forEach { group ->
            FilterChip(
              selected = selectedSubGroup == group.key,
              onClick = { selectedSubGroup = group.key },
              label = { Text("${group.title} (${group.topics.size})") },
              modifier =
                Modifier.minimumInteractiveComponentSize().testTag("seerah_filter_${group.key}"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = colors.gold,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
          }
        }
      }

      if (activeGroupMeta != null) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = colors.surface2,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Text(
                text = activeGroupMeta.title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.gold2,
              )
              Text(
                text = activeGroupMeta.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
              )
              if (activeGroupMeta.key == "seerah-events") {
                Text(
                  text = "مرتبة وفق التسلسل التاريخي لأحداث السيرة النبوية الشريفة.",
                  style = MaterialTheme.typography.labelSmall,
                  color = colors.green,
                )
              }
            }
          }
        }
      }

      itemsIndexed(filteredEntries, key = { _, item -> item.topic.id }) { index, entry ->
        val badge =
          if (entry.subGroupKey == "seerah-events") {
            "محطة ${index + 1} · ${entry.subGroupTitle}"
          } else {
            entry.subGroupTitle
          }
        TopicListCard(
          entry = entry,
          isFavorite = userPrefs.favoriteTopicIds.contains(entry.topic.id),
          showSummary = userPrefs.showSummariesInLists,
          accentColor = colors.seerahAccent,
          badgeText = badge,
          onClick = { onOpenTopic(entry.topic.id) },
          onToggleFavorite = { onToggleFavorite(entry.topic.id) },
        )
      }

      item { Spacer(Modifier.height(16.dp)) }
    }
  }
}
