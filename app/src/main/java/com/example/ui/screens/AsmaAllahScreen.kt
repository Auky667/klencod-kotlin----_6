package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AllahName
import com.example.model.EncyclopediaPayload
import com.example.model.UserPreferences
import com.example.ui.components.AllahNameCard
import com.example.ui.components.EmptyStateBox
import com.example.ui.components.SectionHeader
import com.example.ui.theme.TarmeemThemeTokens
import com.example.util.ArabicSearchEngine

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AsmaAllahScreen(
  payload: EncyclopediaPayload,
  userPrefs: UserPreferences,
  onOpenName: (AllahName) -> Unit,
  onToggleFavoriteName: (String) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  var filterQuery by rememberSaveable { mutableStateOf("") }

  val normQuery = ArabicSearchEngine.normalize(filterQuery)
  val filteredNames =
    if (normQuery.isEmpty()) payload.asmaAllah
    else {
      payload.asmaAllah.filter { item ->
        ArabicSearchEngine.normalize("${item.name} ${item.meaning} ${item.number}").contains(normQuery)
      }
    }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 960.dp).testTag("asma_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item {
        SectionHeader(
          title = "أسماء الله الحسنى",
          subtitle = "99 اسمًا لله تعالى مرتبةً مع بيان المعنى والدلالة",
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = filterQuery,
          onValueChange = { filterQuery = it },
          placeholder = { Text("ابحث في الأسماء الحسنى أو معانيها...") },
          leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
          trailingIcon = {
            if (filterQuery.isNotEmpty()) {
              IconButton(onClick = { filterQuery = "" }) {
                Icon(Icons.Rounded.Close, contentDescription = "مسح البحث")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().testTag("asma_search_input"),
        )
        Spacer(Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = colors.surface2,
          border = BorderStroke(1.dp, colors.line),
        ) {
          Text(
            text = "عدد الأسماء المعروضة: ${filteredNames.size} من أصل ${payload.asmaAllah.size}",
            style = MaterialTheme.typography.labelMedium,
            color = colors.gold2,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          )
        }
      }

      if (filteredNames.isEmpty()) {
        item {
          EmptyStateBox(
            icon = Icons.Rounded.Stars,
            title = "لا توجد أسماء مطابقة",
            message = "جرّب البحث بكلمة أخرى أو امسح حقل التصفية لعرض الأسماء الـ 99 كاملة.",
          )
        }
      } else {
        item {
          BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cols =
              when {
                maxWidth >= 760.dp -> 3
                maxWidth >= 460.dp -> 2
                else -> 1
              }
            val spacing = 10.dp
            val cardWidth = (maxWidth - spacing * (cols - 1)) / cols
            FlowRow(
              maxItemsInEachRow = cols,
              horizontalArrangement = Arrangement.spacedBy(spacing),
              verticalArrangement = Arrangement.spacedBy(spacing),
            ) {
              filteredNames.forEach { allahName ->
                AllahNameCard(
                  allahName = allahName,
                  isFavorite = userPrefs.favoriteNameIds.contains(allahName.id),
                  onClick = { onOpenName(allahName) },
                  onToggleFavorite = { onToggleFavoriteName(allahName.id) },
                  modifier = Modifier.width(cardWidth),
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
