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
import androidx.compose.foundation.lazy.items
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
fun FiqhScreen(
  payload: EncyclopediaPayload,
  allTopicEntries: List<TopicEntry>,
  userPrefs: UserPreferences,
  onOpenTopic: (String) -> Unit,
  onToggleFavorite: (String) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  var selectedSectionId by rememberSaveable { mutableStateOf("all") }

  val fiqhEntries = allTopicEntries.filter { it.sectionKey == "fiqh" }
  val visibleSections =
    if (selectedSectionId == "all") payload.fiqh
    else payload.fiqh.filter { it.id == selectedSectionId }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 900.dp).testTag("fiqh_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        SectionHeader(
          title = "الفقه الإسلامي",
          subtitle = "7 أقسام رئيسية تضم ${payload.fiqhTopicsCount} بابًا فقهيًا مؤصلاً بالأدلة",
        )
        Spacer(Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          FilterChip(
            selected = selectedSectionId == "all",
            onClick = { selectedSectionId = "all" },
            label = { Text("الكل (${fiqhEntries.size})") },
            modifier = Modifier.minimumInteractiveComponentSize().testTag("fiqh_filter_all"),
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = colors.gold,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
          )
          payload.fiqh.forEach { sec ->
            FilterChip(
              selected = selectedSectionId == sec.id,
              onClick = { selectedSectionId = sec.id },
              label = { Text("${sec.title} (${sec.topics.size})") },
              modifier = Modifier.minimumInteractiveComponentSize().testTag("fiqh_filter_${sec.id}"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = colors.gold,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
          }
        }
      }

      visibleSections.forEach { section ->
        item(key = "header_${section.id}") {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = colors.surface2,
            border = BorderStroke(1.dp, colors.line),
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = section.title,
                  style = MaterialTheme.typography.titleLarge,
                  color = colors.gold2,
                )
                Text(
                  text = "${section.topics.size} أبواب",
                  style = MaterialTheme.typography.labelMedium,
                  color = colors.fiqhAccent,
                )
              }
              if (section.description.isNotBlank()) {
                Text(
                  text = section.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.muted,
                )
              }
            }
          }
        }

        val entriesForSection = fiqhEntries.filter { it.subGroupKey == section.id }
        items(entriesForSection, key = { it.topic.id }) { entry ->
          TopicListCard(
            entry = entry,
            isFavorite = userPrefs.favoriteTopicIds.contains(entry.topic.id),
            showSummary = userPrefs.showSummariesInLists,
            accentColor = colors.fiqhAccent,
            onClick = { onOpenTopic(entry.topic.id) },
            onToggleFavorite = { onToggleFavorite(entry.topic.id) },
          )
        }
      }

      item { Spacer(Modifier.height(16.dp)) }
    }
  }
}
