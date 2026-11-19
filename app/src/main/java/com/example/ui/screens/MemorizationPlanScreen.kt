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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.EncyclopediaPayload
import com.example.model.MemorizationRecord
import com.example.model.MemorizationStatus
import com.example.model.Surah
import com.example.model.UserPreferences
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.TarmeemThemeTokens
import com.example.util.ArabicSearchEngine
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorizationPlanScreen(
  payload: EncyclopediaPayload,
  userPrefs: UserPreferences,
  onUpdateMemorization: (surahId: String, status: MemorizationStatus, revisionDate: String) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  var statusFilter by rememberSaveable { mutableStateOf("all") }
  var query by rememberSaveable { mutableStateOf("") }
  var editingSurah by remember { mutableStateOf<Surah?>(null) }

  val totalSurahs = payload.surahs.size
  val records = userPrefs.memorizationRecords
  val inProgressCount = records.values.count { it.status == MemorizationStatus.IN_PROGRESS }
  val memorizedCount = records.values.count { it.status == MemorizationStatus.MEMORIZED }
  val revisedCount = records.values.count { it.status == MemorizationStatus.REVISED }
  val completedCount = memorizedCount + revisedCount
  val progressFraction = if (totalSurahs > 0) completedCount.toFloat() / totalSurahs else 0f
  val progressPercent = (progressFraction * 100f).roundToInt()

  val upcomingTasks =
    payload.surahs.mapNotNull { s ->
      val rec = records[s.id]
      if (rec != null && (rec.status == MemorizationStatus.IN_PROGRESS || rec.revisionDate.isNotBlank())) {
        s to rec
      } else null
    }

  val filteredSurahs =
    payload.surahs.filter { surah ->
      val status = records[surah.id]?.status ?: MemorizationStatus.NONE
      val matchesStatus =
        when (statusFilter) {
          "in_progress" -> status == MemorizationStatus.IN_PROGRESS
          "memorized" -> status == MemorizationStatus.MEMORIZED
          "revised" -> status == MemorizationStatus.REVISED
          else -> true
        }
      val normQ = ArabicSearchEngine.normalize(query)
      val matchesQ =
        normQ.isEmpty() ||
          ArabicSearchEngine.normalize("${surah.name} ${surah.number} ${surah.revelation}")
            .contains(normQ)
      matchesStatus && matchesQ
    }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 900.dp).testTag("memorization_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      item {
        SectionHeader(
          title = "خطة الحفظ والمراجعة",
          subtitle = "تابع إنجازك في حفظ سور القرآن الكريم وجدول المراجعة الأسبوعي",
        )
        Spacer(Modifier.height(8.dp))

        // Progress Overview Card
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.45f)),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                  text = "نسبة الإنجاز الكلية",
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = "$completedCount سورة منجز حفظها/مراجعتها من أصل $totalSurahs",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.muted,
                )
              }
              Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.gold2,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("memorization_progress_percent"),
              )
            }

            LinearProgressIndicator(
              progress = { progressFraction },
              modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
              color = colors.green,
              trackColor = colors.surface2,
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              MemoStatPill(
                label = "قيد الحفظ",
                count = inProgressCount,
                color = colors.gold,
                modifier = Modifier.weight(1f),
              )
              MemoStatPill(
                label = "محفوظة",
                count = memorizedCount,
                color = colors.green,
                modifier = Modifier.weight(1f),
              )
              MemoStatPill(
                label = "تمت المراجعة",
                count = revisedCount,
                color = colors.gold2,
                modifier = Modifier.weight(1f),
              )
            }
          }
        }
      }

      // Upcoming tasks queue
      if (upcomingTasks.isNotEmpty()) {
        item {
          SectionHeader(
            title = "المهام القادمة والمراجعات",
            subtitle = "السور الجارية في الحفظ أو المحددة بموعد مراجعة",
          )
          Spacer(Modifier.height(6.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            upcomingTasks.forEach { (surah, rec) ->
              Surface(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { editingSurah = surah },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, colors.green.copy(alpha = 0.4f)),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                      text = "سورة ${surah.name} (${surah.ayahs} آية)",
                      style = MaterialTheme.typography.titleSmall,
                      color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                      text =
                        buildString {
                          append(rec.status.label)
                          if (rec.revisionDate.isNotBlank()) {
                            append(" · موعد المراجعة: ${rec.revisionDate}")
                          }
                        },
                      style = MaterialTheme.typography.labelSmall,
                      color = colors.green,
                    )
                  }
                  Icon(
                    imageVector = Icons.Rounded.EditCalendar,
                    contentDescription = "تعديل خطة السورة",
                    tint = colors.gold,
                  )
                }
              }
            }
          }
        }
      }

      // General Memorization Guidelines from DATA.MEMORIZATION_PLAN
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.line),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Text(
              text = "إرشادات خطة الحفظ والمراجعة",
              style = MaterialTheme.typography.titleMedium,
              color = colors.gold2,
            )
            payload.MEMORIZATION_PLAN.forEachIndexed { idx, tip ->
              Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
              ) {
                Surface(shape = CircleShape, color = colors.surface2) {
                  Text(
                    text = "${idx + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.gold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  )
                }
                Text(
                  text = tip,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1f),
                )
              }
            }
          }
        }
      }

      // Surah selection & status controls
      item {
        SectionHeader(
          title = "اختيار السور وتحديث الحالة",
          subtitle = "اضغط على أي سورة لتعيين حالة الحفظ أو تحديد تاريخ المراجعة",
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = query,
          onValueChange = { query = it },
          placeholder = { Text("ابحث عن سورة لإضافتها إلى خطتك...") },
          leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
          trailingIcon = {
            if (query.isNotEmpty()) {
              IconButton(onClick = { query = "" }) {
                Icon(Icons.Rounded.Close, contentDescription = "مسح")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.fillMaxWidth().testTag("memo_surah_search"),
        )
        Spacer(Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          listOf(
            "all" to "الكل ($totalSurahs)",
            "in_progress" to "قيد الحفظ ($inProgressCount)",
            "memorized" to "محفوظة ($memorizedCount)",
            "revised" to "تمت المراجعة ($revisedCount)",
          ).forEach { (key, label) ->
            FilterChip(
              selected = statusFilter == key,
              onClick = { statusFilter = key },
              label = { Text(label) },
              modifier = Modifier.minimumInteractiveComponentSize().testTag("memo_filter_$key"),
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = colors.gold,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
          }
        }
      }

      items(filteredSurahs, key = { it.id }) { surah ->
        val rec = records[surah.id]
        val status = rec?.status ?: MemorizationStatus.NONE
        Surface(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .clickable { editingSurah = surah }
              .testTag("memo_surah_row_${surah.id}"),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, colors.line),
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
                shape = RoundedCornerShape(10.dp),
                color = colors.surface2,
                modifier = Modifier.size(40.dp),
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = surah.number.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.gold2,
                  )
                }
              }
              Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                  text = "سورة ${surah.name}",
                  style = MaterialTheme.typography.titleMedium.copy(fontFamily = AmiriFontFamily),
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = "${surah.revelation} · ${surah.ayahs} آية" +
                    if (!rec?.revisionDate.isNullOrBlank()) " · مراجعة: ${rec?.revisionDate}" else "",
                  style = MaterialTheme.typography.labelSmall,
                  color = colors.muted,
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color =
                when (status) {
                  MemorizationStatus.NONE -> colors.surface2
                  MemorizationStatus.IN_PROGRESS -> colors.gold.copy(alpha = 0.2f)
                  MemorizationStatus.MEMORIZED,
                  MemorizationStatus.REVISED -> colors.green.copy(alpha = 0.2f)
                },
            ) {
              Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                color =
                  when (status) {
                    MemorizationStatus.NONE -> colors.muted
                    MemorizationStatus.IN_PROGRESS -> colors.gold2
                    MemorizationStatus.MEMORIZED,
                    MemorizationStatus.REVISED -> colors.green
                  },
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              )
            }
          }
        }
      }

      item { Spacer(Modifier.height(16.dp)) }
    }
  }

  editingSurah?.let { surah ->
    val current = records[surah.id] ?: MemorizationRecord(surah.id)
    var selectedStatus by remember(surah.id) { mutableStateOf(current.status) }
    var revisionDate by remember(surah.id) { mutableStateOf(current.revisionDate) }

    ModalBottomSheet(
      onDismissRequest = { editingSurah = null },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
          text = "خطة حفظ سورة ${surah.name}",
          style = MaterialTheme.typography.headlineSmall,
          color = colors.gold2,
        )
        Text(
          text = "اختر حالة الحفظ:",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          MemorizationStatus.entries.forEach { st ->
            val isSelected = selectedStatus == st
            Surface(
              modifier =
                Modifier.fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { selectedStatus = st }
                  .testTag("memo_status_option_${st.name}"),
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) colors.gold.copy(alpha = 0.18f) else colors.surface2,
              border = BorderStroke(1.dp, if (isSelected) colors.gold else colors.line),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  text = st.label,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                if (isSelected) {
                  Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = colors.gold,
                    modifier = Modifier.size(18.dp),
                  )
                }
              }
            }
          }
        }

        OutlinedTextField(
          value = revisionDate,
          onValueChange = { revisionDate = it },
          label = { Text("تاريخ المراجعة (اختياري)") },
          placeholder = { Text("مثال: الجمعة القادمة أو 2026-05-01") },
          leadingIcon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = null) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("memo_date_input"),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf("اليوم", "بعد 3 أيام", "الأسبوع القادم").forEach { preset ->
            OutlinedButton(
              onClick = { revisionDate = preset },
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            ) {
              Text(preset, style = MaterialTheme.typography.labelSmall)
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          TextButton(
            onClick = { editingSurah = null },
            modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
          ) {
            Text("إلغاء")
          }
          Button(
            onClick = {
              onUpdateMemorization(surah.id, selectedStatus, revisionDate)
              editingSurah = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = colors.gold),
            modifier = Modifier.weight(1f).minimumInteractiveComponentSize().testTag("memo_save_button"),
          ) {
            Text("حفظ")
          }
        }
      }
    }
  }
}

@Composable
private fun MemoStatPill(
  label: String,
  count: Int,
  color: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier,
) {
  val colors = TarmeemThemeTokens.colors
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    color = colors.surface2,
    border = BorderStroke(1.dp, colors.line),
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = count.toString(),
        style = MaterialTheme.typography.titleLarge,
        color = color,
        fontWeight = FontWeight.Bold,
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = colors.muted,
      )
    }
  }
}
