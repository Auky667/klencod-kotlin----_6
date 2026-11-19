package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TopicEntry
import com.example.ui.components.EmptyStateBox
import com.example.ui.components.RubElHizbEmblem
import com.example.ui.components.islamicGeometricBackdrop
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.TarmeemThemeTokens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TopicDetailScreen(
  entry: TopicEntry?,
  relatedTopics: List<TopicEntry>,
  isFavorite: Boolean,
  onBack: () -> Unit,
  onToggleFavorite: () -> Unit,
  onCopy: () -> Unit,
  onShare: () -> Unit,
  onOpenRelatedTopic: (String) -> Unit,
  onRecordRead: (TopicEntry) -> Unit,
) {
  BackHandler(onBack = onBack)

  val colors = TarmeemThemeTokens.colors

  LaunchedEffect(entry?.topic?.id) {
    if (entry != null) {
      onRecordRead(entry)
    }
  }

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = entry?.sectionTitle ?: "عرض الموضوع",
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.minimumInteractiveComponentSize().testTag("topic_back_button"),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
              contentDescription = "العودة",
              tint = colors.gold,
            )
          }
        },
        actions = {
          if (entry != null) {
            IconButton(
              onClick = onToggleFavorite,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("topic_favorite_button"),
            ) {
              Icon(
                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
                tint = if (isFavorite) colors.gold else colors.muted,
              )
            }
            IconButton(
              onClick = onCopy,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("topic_copy_button"),
            ) {
              Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = "نسخ الموضوع",
                tint = colors.muted,
              )
            }
            IconButton(
              onClick = onShare,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("topic_share_button"),
            ) {
              Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "مشاركة الموضوع",
                tint = colors.muted,
              )
            }
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
          ),
      )
    },
  ) { innerPadding ->
    if (entry == null) {
      Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(20.dp),
        contentAlignment = Alignment.Center,
      ) {
        EmptyStateBox(
          icon = Icons.AutoMirrored.Outlined.MenuBook,
          title = "الموضوع غير متاح",
          message = "لم يتم العثور على الموضوع المطلوب.",
        )
      }
      return@Scaffold
    }

    val topic = entry.topic
    Box(
      modifier = Modifier.fillMaxSize().padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize().widthIn(max = 820.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        // Header Banner
        item {
          Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.4f)),
          ) {
            Column(
              modifier =
                Modifier.fillMaxWidth()
                  .islamicGeometricBackdrop(lineColor = colors.gold, alpha = 0.05f)
                  .padding(20.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                RubElHizbEmblem(modifier = Modifier.size(18.dp), color = colors.gold)
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = colors.surface2,
                  border = BorderStroke(1.dp, colors.line),
                ) {
                  Text(
                    text = "${entry.sectionTitle} · ${entry.subGroupTitle}",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.gold2,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  )
                }
              }

              Text(
                text = topic.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = AmiriFontFamily),
                color = MaterialTheme.colorScheme.onSurface,
              )

              if (topic.summary.isNotBlank()) {
                Text(
                  text = topic.summary,
                  style = MaterialTheme.typography.bodyMedium,
                  color = colors.muted,
                )
              }

              HorizontalDivider(color = colors.line)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Button(
                  onClick = onToggleFavorite,
                  modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
                  colors =
                    ButtonDefaults.buttonColors(
                      containerColor = if (isFavorite) colors.gold else colors.surface2,
                      contentColor = if (isFavorite) MaterialTheme.colorScheme.onPrimary else colors.gold2,
                    ),
                ) {
                  Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                  )
                  Spacer(Modifier.width(6.dp))
                  Text(if (isFavorite) "في المفضلة" else "حفظ")
                }
                OutlinedButton(
                  onClick = onCopy,
                  modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
                ) {
                  Icon(Icons.Outlined.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(Modifier.width(6.dp))
                  Text("نسخ")
                }
                OutlinedButton(
                  onClick = onShare,
                  modifier = Modifier.weight(1f).minimumInteractiveComponentSize(),
                ) {
                  Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(Modifier.width(6.dp))
                  Text("مشاركة")
                }
              }
            }
          }
        }

        // Body Content Paragraphs
        if (topic.content.isNotEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, colors.line),
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
              ) {
                Text(
                  text = "عرض الموضوع",
                  style = MaterialTheme.typography.titleMedium,
                  color = colors.gold,
                )
                topic.content.forEach { paragraph ->
                  Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                }
              }
            }
          }
        }

        // Evidences Section
        if (topic.evidences.isNotEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, colors.line),
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.FormatQuote,
                    contentDescription = null,
                    tint = colors.gold,
                  )
                  Text(
                    text = "الأدلة من الكتاب والسنة",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.gold,
                  )
                }
                topic.evidences.forEach { ev ->
                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colors.surface2,
                    border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth(),
                  ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                      Box(
                        modifier =
                          Modifier.width(3.dp)
                            .height(48.dp)
                            .background(colors.gold, RoundedCornerShape(3.dp))
                      )
                      Spacer(Modifier.width(12.dp))
                      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                          text = ev.text,
                          style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                          color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (ev.source.isNotBlank()) {
                          Text(
                            text = "التخريج والمصدر: ${ev.source}",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.gold2,
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // Benefits & Lessons
        if (topic.benefits.isNotEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, colors.line),
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = colors.green,
                  )
                  Text(
                    text = "الفوائد والدروس المستفادة",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.green,
                  )
                }
                topic.benefits.forEachIndexed { idx, benefit ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top,
                  ) {
                    Surface(
                      shape = CircleShape,
                      color = colors.green.copy(alpha = 0.16f),
                    ) {
                      Text(
                        text = "${idx + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.green,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                      )
                    }
                    Text(
                      text = benefit,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurface,
                      modifier = Modifier.weight(1f),
                    )
                  }
                }
              }
            }
          }
        }

        // References
        if (topic.references.isNotEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = BorderStroke(1.dp, colors.line),
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Bookmark,
                    contentDescription = null,
                    tint = colors.gold2,
                  )
                  Text(
                    text = "المراجع والمصادر",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.gold2,
                  )
                }
                FlowRow(
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                  topic.references.forEach { ref ->
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = colors.surface2,
                      border = BorderStroke(1.dp, colors.line),
                    ) {
                      Text(
                        text = ref,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // Related Topics
        if (relatedTopics.isNotEmpty()) {
          item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "موضوعات ذات صلة",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
              )
              relatedTopics.forEach { rel ->
                Surface(
                  modifier =
                    Modifier.fillMaxWidth()
                      .clip(RoundedCornerShape(14.dp))
                      .clickable { onOpenRelatedTopic(rel.topic.id) }
                      .testTag("related_topic_${rel.topic.id}"),
                  shape = RoundedCornerShape(14.dp),
                  color = MaterialTheme.colorScheme.surface,
                  border = BorderStroke(1.dp, colors.line),
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                  ) {
                    Column(
                      modifier = Modifier.weight(1f),
                      verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                      Text(
                        text = rel.topic.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = AmiriFontFamily),
                        color = MaterialTheme.colorScheme.onSurface,
                      )
                      Text(
                        text = rel.subGroupTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.gold,
                      )
                    }
                    Icon(
                      imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                      contentDescription = "انتقال إلى الموضوع",
                      tint = colors.muted,
                      modifier = Modifier.size(18.dp),
                    )
                  }
                }
              }
            }
          }
        }

        // Bottom back to section button
        item {
          OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize(),
          ) {
            Text("العودة إلى ${entry.sectionTitle}")
          }
          Spacer(Modifier.height(24.dp))
        }
      }
    }
  }
}
