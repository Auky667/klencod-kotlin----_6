package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.model.EncyclopediaPayload
import com.example.ui.components.RubElHizbEmblem
import com.example.ui.components.SectionHeader
import com.example.ui.components.islamicGeometricBackdrop
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.TarmeemThemeTokens

@Composable
fun AboutScreen(payload: EncyclopediaPayload) {
  val colors = TarmeemThemeTokens.colors
  val about = payload.ABOUT

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 880.dp).testTag("about_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Brand Mission Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.45f)),
        ) {
          Column(
            modifier =
              Modifier.fillMaxWidth()
                .islamicGeometricBackdrop(lineColor = colors.gold, alpha = 0.06f)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              RubElHizbEmblem(modifier = Modifier.size(28.dp), color = colors.gold)
              Text(
                text = "ترميم الإسلام",
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = AmiriFontFamily),
                color = colors.gold2,
              )
            }
            Text(
              text = "«علمٌ يُبصّر، وإيمانٌ يُعمّر، وعملٌ يُصلح»",
              style = MaterialTheme.typography.labelLarge,
              color = colors.gold,
            )
            Text(
              text = about.mission,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }

      // Goals
      item {
        SectionHeader(title = "أهداف الموسوعة")
        Spacer(Modifier.height(6.dp))
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.line),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            about.goals.forEachIndexed { idx, goal ->
              Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
              ) {
                Surface(shape = CircleShape, color = colors.surface2) {
                  Text(
                    text = "${idx + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.gold2,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  )
                }
                Text(
                  text = goal,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1f),
                )
              }
            }
          }
        }
      }

      // Methodology
      item {
        SectionHeader(title = "المنهجية العلمية")
        Spacer(Modifier.height(6.dp))
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.line),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            about.methodology.forEachIndexed { idx, method ->
              Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
              ) {
                Surface(shape = CircleShape, color = colors.green.copy(alpha = 0.16f)) {
                  Text(
                    text = "${idx + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.green,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  )
                }
                Text(
                  text = method,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1f),
                )
              }
            }
          }
        }
      }

      // Sections Summary
      item {
        SectionHeader(title = "أبواب الموسوعة وأقسامها")
        Spacer(Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          payload.SECTION_CARDS.forEach { sec ->
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surface,
              border = BorderStroke(1.dp, colors.line),
              modifier = Modifier.fillMaxWidth(),
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Text(
                  text = "${sec.title} (${sec.count} ${sec.unit})",
                  style = MaterialTheme.typography.titleMedium,
                  color = colors.gold2,
                )
                Text(
                  text = sec.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = colors.muted,
                )
              }
            }
          }
        }
      }

      // Authentic Sources List
      item {
        SectionHeader(
          title = "المراجع المعتمدة في الموسوعة",
          subtitle = "تُعرض هنا أمهات الكتب والمصادر المرجعية التي أُسندت إليها الموضوعات بحسب كل باب",
        )
        Spacer(Modifier.height(6.dp))
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, colors.line),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            about.sources.forEachIndexed { idx, source ->
              Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
              ) {
                Text(
                  text = "${idx + 1}.",
                  style = MaterialTheme.typography.labelLarge,
                  color = colors.gold,
                )
                Text(
                  text = source,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.weight(1f),
                )
              }
            }
          }
        }
      }

      // Copyright & Version
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = colors.surface2,
          border = BorderStroke(1.dp, colors.line),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text = "ترميم الإسلام · الإصدار ${BuildConfig.VERSION_NAME}",
              style = MaterialTheme.typography.titleSmall,
              color = colors.gold2,
            )
            Text(
              text = "جميع الحقوق محفوظة لموسوعة ترميم الإسلام العلمية",
              style = MaterialTheme.typography.bodySmall,
              color = colors.muted,
            )
          }
        }
        Spacer(Modifier.height(16.dp))
      }
    }
  }
}
