package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.TopicEntry
import com.example.model.UserPreferences
import com.example.ui.components.SectionHeader
import com.example.ui.components.TopicListCard
import com.example.ui.theme.TarmeemThemeTokens

@Composable
fun AqeedahScreen(
  allTopicEntries: List<TopicEntry>,
  userPrefs: UserPreferences,
  onOpenTopic: (String) -> Unit,
  onToggleFavorite: (String) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  val aqeedahEntries = allTopicEntries.filter { it.sectionKey == "aqeedah" }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 900.dp).testTag("aqeedah_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        SectionHeader(
          title = "العقيدة الإسلامية",
          subtitle = "20 موضوعًا علميًا في أصول الاعتقاد والتوحيد وأركان الإيمان",
        )
        Spacer(Modifier.height(8.dp))
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
              text = "منهج التقرير العلمي",
              style = MaterialTheme.typography.titleMedium,
              color = colors.aqeedahAccent,
            )
            Text(
              text = "تُعرض أبواب التوحيد وأركان الإيمان وقواعد الأسماء والصفات والقدر واليوم الآخر ببيانٍ متدرّج يستند إلى الكتاب والسنة.",
              style = MaterialTheme.typography.bodySmall,
              color = colors.muted,
            )
          }
        }
      }

      itemsIndexed(aqeedahEntries, key = { _, item -> item.topic.id }) { index, entry ->
        TopicListCard(
          entry = entry,
          isFavorite = userPrefs.favoriteTopicIds.contains(entry.topic.id),
          showSummary = userPrefs.showSummariesInLists,
          accentColor = colors.aqeedahAccent,
          badgeText = "الباب ${index + 1} · العقيدة",
          onClick = { onOpenTopic(entry.topic.id) },
          onToggleFavorite = { onToggleFavorite(entry.topic.id) },
        )
      }

      item { Spacer(Modifier.height(16.dp)) }
    }
  }
}
