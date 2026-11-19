package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.model.FontScalePreset
import com.example.model.UserPreferences
import com.example.ui.components.SectionHeader
import com.example.ui.theme.TarmeemThemeTokens
import kotlin.math.abs

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  userPrefs: UserPreferences,
  onToggleDarkTheme: (Boolean) -> Unit,
  onSelectFontScale: (Float) -> Unit,
  onToggleComfortLineSpacing: (Boolean) -> Unit,
  onToggleShowSummaries: (Boolean) -> Unit,
  onClearHistory: () -> Unit,
  onClearFavorites: () -> Unit,
  onClearMemorization: () -> Unit,
  onNavigateAbout: () -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  var confirmAction by remember { mutableStateOf<String?>(null) }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 840.dp).testTag("settings_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        SectionHeader(
          title = "الإعدادات",
          subtitle = "تخصيص المظهر وحجم الخط وخيارات القراءة وإدارة البيانات المحفوظة",
        )
      }

      // Theme & Display
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.line),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Icon(Icons.Rounded.DarkMode, contentDescription = null, tint = colors.gold)
                Column {
                  Text(
                    text = "الوضع الداكن",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                  Text(
                    text = if (userPrefs.isDarkTheme) "مفعّل (الوضع الافتراضي المريح للعين)" else "مُعطّل (الوضع الفاتح)",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted,
                  )
                }
              }
              Switch(
                checked = userPrefs.isDarkTheme,
                onCheckedChange = onToggleDarkTheme,
                modifier = Modifier.testTag("settings_dark_theme_switch"),
                colors =
                  SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = colors.gold,
                  ),
              )
            }

            HorizontalDivider(color = colors.line)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Icon(Icons.Rounded.FormatSize, contentDescription = null, tint = colors.gold)
                Text(
                  text = "حجم خط القراءة",
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
              }
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                FontScalePreset.entries.forEach { preset ->
                  val isSelected = abs(userPrefs.fontScale - preset.scale) < 0.04f
                  FilterChip(
                    selected = isSelected,
                    onClick = { onSelectFontScale(preset.scale) },
                    label = { Text(preset.label) },
                    modifier =
                      Modifier.minimumInteractiveComponentSize()
                        .testTag("settings_font_${preset.name}"),
                    colors =
                      FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.gold,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                      ),
                  )
                }
              }
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.surface2,
                border = BorderStroke(1.dp, colors.line),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = "نموذج معاينة النص الشرعي: «مَن سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ»",
                  style = MaterialTheme.typography.bodyLarge,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(12.dp),
                )
              }
            }
          }
        }
      }

      // Reading Preferences
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.line),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null, tint = colors.gold)
              Text(
                text = "تفضيلات القراءة والعرض",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "تباعد الأسطر المريح",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = "يوسّع المسافة بين السطور لتيسير القراءة المطوّلة",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.muted,
                )
              }
              Switch(
                checked = userPrefs.comfortLineSpacing,
                onCheckedChange = onToggleComfortLineSpacing,
                modifier = Modifier.testTag("settings_line_spacing_switch"),
              )
            }

            HorizontalDivider(color = colors.line)

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "إظهار الملخصات في القوائم",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                  text = "عرض نبذة مختصرة تحت عنوان كل موضوع داخل الأقسام",
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.muted,
                )
              }
              Switch(
                checked = userPrefs.showSummariesInLists,
                onCheckedChange = onToggleShowSummaries,
                modifier = Modifier.testTag("settings_summaries_switch"),
              )
            }
          }
        }
      }

      // Data Management
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
              text = "إدارة البيانات المحلية",
              style = MaterialTheme.typography.titleMedium,
              color = colors.gold2,
            )
            DataActionRow(
              title = "مسح سجل القراءة (${userPrefs.readingHistory.size})",
              subtitle = "حذف قائمة آخر الموضوعات المقروءة",
              onClick = { confirmAction = "history" },
              testTag = "settings_clear_history",
            )
            HorizontalDivider(color = colors.line)
            val favCount =
              userPrefs.favoriteTopicIds.size +
                userPrefs.favoriteSurahIds.size +
                userPrefs.favoriteNameIds.size
            DataActionRow(
              title = "مسح المفضلة ($favCount)",
              subtitle = "إزالة جميع الموضوعات والسور والأسماء المحفوظة",
              onClick = { confirmAction = "favorites" },
              testTag = "settings_clear_favorites",
            )
            HorizontalDivider(color = colors.line)
            DataActionRow(
              title = "إعادة ضبط خطة الحفظ (${userPrefs.memorizationRecords.size})",
              subtitle = "تصفير حالة الحفظ ومواعيد المراجعة لجميع السور",
              onClick = { confirmAction = "memorization" },
              testTag = "settings_clear_memorization",
            )
          }
        }
      }

      // App Info
      item {
        Card(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(18.dp))
              .clickable(onClick = onNavigateAbout),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.line),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Icon(Icons.Rounded.Info, contentDescription = null, tint = colors.gold)
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "ترميم الإسلام · الإصدار ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                text = "موسوعة إسلامية تعمل بالكامل دون اتصال بالإنترنت · اضغط لعرض المراجع والمنهجية",
                style = MaterialTheme.typography.bodySmall,
                color = colors.muted,
              )
            }
          }
        }
        Spacer(Modifier.height(16.dp))
      }
    }
  }

  confirmAction?.let { action ->
    val (title, body) =
      when (action) {
        "history" -> "مسح سجل القراءة" to "هل تريد بالتأكيد حذف جميع عناصر سجل القراءة؟"
        "favorites" -> "مسح المفضلة" to "هل تريد بالتأكيد إزالة جميع الموضوعات والسور والأسماء من المفضلة؟"
        else -> "إعادة ضبط خطة الحفظ" to "هل تريد بالتأكيد تصفير سجل حفظ السور ومواعيد المراجعة؟"
      }
    ModalBottomSheet(
      onDismissRequest = { confirmAction = null },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = colors.gold2)
        Text(text = body, style = MaterialTheme.typography.bodyMedium)
        Row(
          modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          TextButton(
            onClick = { confirmAction = null },
            modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
          ) {
            Text("إلغاء")
          }
          Button(
            onClick = {
              when (action) {
                "history" -> onClearHistory()
                "favorites" -> onClearFavorites()
                else -> onClearMemorization()
              }
              confirmAction = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = colors.gold),
            modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
          ) {
            Text("تأكيد")
          }
        }
      }
    }
  }
}

@Composable
private fun DataActionRow(
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  testTag: String,
) {
  val colors = TarmeemThemeTokens.colors
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .clickable(onClick = onClick)
        .minimumInteractiveComponentSize()
        .padding(vertical = 8.dp, horizontal = 4.dp)
        .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = colors.muted,
      )
    }
    Icon(
      imageVector = Icons.Rounded.DeleteOutline,
      contentDescription = title,
      tint = colors.gold,
    )
  }
}
