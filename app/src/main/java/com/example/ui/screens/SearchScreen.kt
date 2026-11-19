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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AllahName
import com.example.model.MemorizationStatus
import com.example.model.SearchCategory
import com.example.model.SearchResultItem
import com.example.model.Surah
import com.example.model.UserPreferences
import com.example.ui.components.AllahNameCard
import com.example.ui.components.EmptyStateBox
import com.example.ui.components.SectionHeader
import com.example.ui.components.SurahCompactCard
import com.example.ui.components.TopicListCard
import com.example.ui.theme.TarmeemThemeTokens

@Composable
fun SearchScreen(
  query: String,
  selectedCategory: SearchCategory,
  results: List<SearchResultItem>,
  totalEntriesCount: Int,
  userPrefs: UserPreferences,
  onQueryChange: (String) -> Unit,
  onCategoryChange: (SearchCategory) -> Unit,
  onOpenTopic: (String) -> Unit,
  onOpenSurah: (Surah) -> Unit,
  onOpenName: (AllahName) -> Unit,
  onToggleFavoriteTopic: (String) -> Unit,
  onToggleFavoriteSurah: (String) -> Unit,
  onToggleFavoriteName: (String) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 900.dp).testTag("search_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item {
        SectionHeader(
          title = "البحث في الموسوعة",
          subtitle = "بحث فوري يدعم تجاهل التشكيل وتوحيد الهمزات في $totalEntriesCount مدخلاً",
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = query,
          onValueChange = onQueryChange,
          placeholder = { Text("ابحث في السيرة، العقيدة، القرآن، الفقه، السور، الأسماء...") },
          leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
          trailingIcon = {
            if (query.isNotEmpty()) {
              IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("search_clear_button"),
              ) {
                Icon(Icons.Rounded.Close, contentDescription = "مسح البحث")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth().testTag("search_input_field"),
        )
        Spacer(Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          SearchCategory.entries.forEach { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { onCategoryChange(cat) },
              label = { Text(cat.label) },
              modifier = Modifier.minimumInteractiveComponentSize().testTag("search_cat_${cat.name}"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = colors.gold,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
          }
        }
      }

      if (query.isBlank()) {
        item {
          EmptyStateBox(
            icon = Icons.Rounded.Search,
            title = "ابدأ بكتابة كلمة البحث",
            message = "يمكنك البحث مثلًا عن: التوحيد، غزوة بدر، الصلاة، الفاتحة، الرحمن، التجويد، أو أي اسم أو مسألة.",
          )
        }
      } else {
        item {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = colors.surface2,
            border = BorderStroke(1.dp, colors.line),
          ) {
            Text(
              text = "عدد النتائج: ${results.size} نتيجة",
              style = MaterialTheme.typography.labelLarge,
              color = colors.gold2,
              modifier =
                Modifier.padding(horizontal = 12.dp, vertical = 6.dp).testTag("search_result_count"),
            )
          }
        }

        if (results.isEmpty()) {
          item {
            EmptyStateBox(
              icon = Icons.Rounded.SearchOff,
              title = "لا توجد نتائج مطابقة",
              message = "جرّب كلمة أعمّ أو بدّل قسم التصفية أعلاه.",
            )
          }
        } else {
          items(results, key = { "${it.category.name}_${it.id}" }) { result ->
            when (result) {
              is SearchResultItem.TopicResult -> {
                TopicListCard(
                  entry = result.entry,
                  isFavorite = userPrefs.favoriteTopicIds.contains(result.entry.topic.id),
                  showSummary = userPrefs.showSummariesInLists,
                  accentColor = colors.gold,
                  badgeText = "${result.entry.sectionTitle} · ${result.entry.subGroupTitle}",
                  onClick = { onOpenTopic(result.entry.topic.id) },
                  onToggleFavorite = { onToggleFavoriteTopic(result.entry.topic.id) },
                )
              }

              is SearchResultItem.SurahResult -> {
                SurahCompactCard(
                  surah = result.surah,
                  isFavorite = userPrefs.favoriteSurahIds.contains(result.surah.id),
                  memorizationStatus =
                    userPrefs.memorizationRecords[result.surah.id]?.status
                      ?: MemorizationStatus.NONE,
                  onClick = { onOpenSurah(result.surah) },
                  onToggleFavorite = { onToggleFavoriteSurah(result.surah.id) },
                )
              }

              is SearchResultItem.NameResult -> {
                AllahNameCard(
                  allahName = result.name,
                  isFavorite = userPrefs.favoriteNameIds.contains(result.name.id),
                  onClick = { onOpenName(result.name) },
                  onToggleFavorite = { onToggleFavoriteName(result.name.id) },
                )
              }
            }
          }
        }
      }

      item { Spacer(Modifier.height(16.dp)) }
    }
  }
}
