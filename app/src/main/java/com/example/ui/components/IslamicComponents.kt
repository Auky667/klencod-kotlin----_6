package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AllahName
import com.example.model.MemorizationRecord
import com.example.model.MemorizationStatus
import com.example.model.Surah
import com.example.model.TopicEntry
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.ScheherazadeFontFamily
import com.example.ui.theme.TarmeemThemeTokens
import kotlin.math.min

/**
 * Draws a subtle Islamic eight-pointed star geometric lattice behind banners/headers.
 */
fun Modifier.islamicGeometricBackdrop(
  lineColor: Color,
  alpha: Float = 0.08f,
): Modifier = drawBehind {
  val stroke = Stroke(width = 1.2f)
  val tint = lineColor.copy(alpha = alpha)
  val step = min(size.width, size.height).coerceAtLeast(120f) * 0.65f
  val cols = (size.width / step).toInt() + 2
  val rows = (size.height / step).toInt() + 2

  for (r in 0..rows) {
    for (c in 0..cols) {
      val cx = c * step
      val cy = r * step
      val half = step * 0.32f
      drawRect(
        color = tint,
        topLeft = Offset(cx - half, cy - half),
        size = androidx.compose.ui.geometry.Size(half * 2f, half * 2f),
        style = stroke,
      )
      rotate(degrees = 45f, pivot = Offset(cx, cy)) {
        drawRect(
          color = tint,
          topLeft = Offset(cx - half, cy - half),
          size = androidx.compose.ui.geometry.Size(half * 2f, half * 2f),
          style = stroke,
        )
      }
    }
  }
}

@Composable
fun RubElHizbEmblem(
  modifier: Modifier = Modifier,
  color: Color = TarmeemThemeTokens.colors.gold,
) {
  Canvas(modifier = modifier) {
    val s = min(size.width, size.height) * 0.64f
    val cx = size.width / 2f
    val cy = size.height / 2f
    val half = s / 2f
    val stroke = Stroke(width = 2.5f)
    drawRect(
      color = color,
      topLeft = Offset(cx - half, cy - half),
      size = androidx.compose.ui.geometry.Size(s, s),
      style = stroke,
    )
    rotate(degrees = 45f, pivot = Offset(cx, cy)) {
      drawRect(
        color = color,
        topLeft = Offset(cx - half, cy - half),
        size = androidx.compose.ui.geometry.Size(s, s),
        style = stroke,
      )
    }
    drawCircle(color = color.copy(alpha = 0.7f), radius = s * 0.16f, center = Offset(cx, cy))
  }
}

@Composable
fun SectionHeader(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  val colors = TarmeemThemeTokens.colors
  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      RubElHizbEmblem(modifier = Modifier.size(22.dp), color = colors.gold)
      Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onBackground,
        )
        if (!subtitle.isNullOrBlank()) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = colors.muted,
          )
        }
      }
    }
    if (trailingContent != null) {
      trailingContent()
    }
  }
}

@Composable
fun QuranVerseBanner(
  verse: String,
  source: String,
  modifier: Modifier = Modifier,
) {
  val colors = TarmeemThemeTokens.colors
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.45f)),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Box(
      modifier =
        Modifier.fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                colors.gold.copy(alpha = 0.12f),
                MaterialTheme.colorScheme.surface,
              )
            )
          )
          .islamicGeometricBackdrop(lineColor = colors.gold, alpha = 0.06f)
          .padding(horizontal = 20.dp, vertical = 20.dp),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          HorizontalDivider(modifier = Modifier.width(32.dp), color = colors.gold.copy(alpha = 0.6f))
          Text(
            text = "آيةٌ مختارة",
            style = MaterialTheme.typography.labelMedium,
            color = colors.gold,
          )
          HorizontalDivider(modifier = Modifier.width(32.dp), color = colors.gold.copy(alpha = 0.6f))
        }
        Text(
          text = "﴿ $verse ﴾",
          fontFamily = ScheherazadeFontFamily,
          fontSize = 23.sp,
          lineHeight = 40.sp,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Surface(
          shape = RoundedCornerShape(50),
          color = colors.surface2,
          border = BorderStroke(1.dp, colors.line),
        ) {
          Text(
            text = source,
            style = MaterialTheme.typography.labelMedium,
            color = colors.gold2,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
          )
        }
      }
    }
  }
}

@Composable
fun TopicListCard(
  entry: TopicEntry,
  isFavorite: Boolean,
  showSummary: Boolean,
  accentColor: Color,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit,
  modifier: Modifier = Modifier,
  badgeText: String? = null,
) {
  val colors = TarmeemThemeTokens.colors
  Card(
    modifier =
      modifier
        .fillMaxWidth()
        .testTag("topic_card_${entry.topic.id}")
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, colors.line),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier =
          Modifier.padding(top = 4.dp)
            .width(4.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(accentColor)
      )
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = colors.surface2,
          ) {
            Text(
              text = badgeText ?: entry.subGroupTitle,
              style = MaterialTheme.typography.labelSmall,
              color = accentColor,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          if (entry.topic.evidences.isNotEmpty()) {
            Text(
              text = "${entry.topic.evidences.size} دليل",
              style = MaterialTheme.typography.labelSmall,
              color = colors.muted,
            )
          }
        }
        Text(
          text = entry.topic.title,
          style = MaterialTheme.typography.titleMedium.copy(fontFamily = AmiriFontFamily, fontSize = 18.sp),
          color = MaterialTheme.colorScheme.onSurface,
        )
        if (showSummary && entry.topic.summary.isNotBlank()) {
          Text(
            text = entry.topic.summary,
            style = MaterialTheme.typography.bodySmall,
            color = colors.muted,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        IconButton(
          onClick = onToggleFavorite,
          modifier =
            Modifier.minimumInteractiveComponentSize().testTag("fav_btn_${entry.topic.id}"),
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
            tint = if (isFavorite) colors.gold else colors.muted,
          )
        }
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
          contentDescription = "فتح الموضوع",
          tint = colors.muted.copy(alpha = 0.7f),
          modifier = Modifier.size(16.dp),
        )
      }
    }
  }
}

@Composable
fun SurahCompactCard(
  surah: Surah,
  isFavorite: Boolean,
  memorizationStatus: MemorizationStatus,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = TarmeemThemeTokens.colors
  Card(
    modifier =
      modifier
        .fillMaxWidth()
        .testTag("surah_card_${surah.id}")
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, colors.line),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier =
          Modifier.size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface2)
            .border(1.dp, colors.gold.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = surah.number.toString(),
          style = MaterialTheme.typography.titleSmall,
          color = colors.gold2,
          fontWeight = FontWeight.Bold,
        )
      }
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = "سورة ${surah.name}",
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = AmiriFontFamily),
            color = MaterialTheme.colorScheme.onSurface,
          )
          if (memorizationStatus != MemorizationStatus.NONE) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = colors.green.copy(alpha = 0.18f),
            ) {
              Text(
                text = memorizationStatus.label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.green,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              )
            }
          }
        }
        Text(
          text = "${surah.revelation} · ${surah.ayahs} آية",
          style = MaterialTheme.typography.labelSmall,
          color = colors.gold,
        )
        Text(
          text = surah.virtue,
          style = MaterialTheme.typography.bodySmall,
          color = colors.muted,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }
      IconButton(
        onClick = onToggleFavorite,
        modifier = Modifier.minimumInteractiveComponentSize().testTag("fav_btn_${surah.id}"),
      ) {
        Icon(
          imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
          contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
          tint = if (isFavorite) colors.gold else colors.muted,
        )
      }
    }
  }
}

@Composable
fun AllahNameCard(
  allahName: AllahName,
  isFavorite: Boolean,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = TarmeemThemeTokens.colors
  Card(
    modifier =
      modifier
        .fillMaxWidth()
        .testTag("asma_card_${allahName.id}")
        .clip(RoundedCornerShape(16.dp))
        .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, colors.line),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = CircleShape,
          color = colors.surface2,
          border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.4f)),
        ) {
          Text(
            text = allahName.number.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = colors.gold2,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
          )
        }
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("fav_btn_${allahName.id}"),
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
            tint = if (isFavorite) colors.gold else colors.muted,
          )
        }
      }
      Text(
        text = allahName.name,
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = colors.gold2,
      )
      Text(
        text = allahName.meaning,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailBottomSheet(
  surah: Surah,
  isFavorite: Boolean,
  memorizationRecord: MemorizationRecord?,
  onDismiss: () -> Unit,
  onToggleFavorite: () -> Unit,
  onCopy: () -> Unit,
  onShare: () -> Unit,
  onOpenMemorizationPlan: () -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "سورة ${surah.name}",
            style = MaterialTheme.typography.headlineMedium,
            color = colors.gold2,
          )
          Text(
            text = "الترتيب: ${surah.number} · ${surah.revelation} · ${surah.ayahs} آية",
            style = MaterialTheme.typography.bodySmall,
            color = colors.muted,
          )
        }
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("sheet_surah_fav"),
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
            tint = if (isFavorite) colors.gold else colors.muted,
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surface2,
        border = BorderStroke(1.dp, colors.line),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "من فضل السورة وموضوعها",
            style = MaterialTheme.typography.labelLarge,
            color = colors.gold,
          )
          Text(
            text = surah.virtue,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Text(
        text = "تنبيه علمي: تعرض الموسوعة هنا بطاقة تعريف السورة وفضلها كما وردت في بيانات المشروع، ولا تتضمن نص المصحف كاملاً.",
        style = MaterialTheme.typography.bodySmall,
        color = colors.muted,
      )

      val status = memorizationRecord?.status ?: MemorizationStatus.NONE
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.green.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, colors.green.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Rounded.BookmarkAdded,
              contentDescription = null,
              tint = colors.green,
            )
            Column {
              Text(
                text = "حالة الحفظ: ${status.label}",
                style = MaterialTheme.typography.labelLarge,
                color = colors.green,
              )
              if (!memorizationRecord?.revisionDate.isNullOrBlank()) {
                Text(
                  text = "تاريخ المراجعة: ${memorizationRecord?.revisionDate}",
                  style = MaterialTheme.typography.labelSmall,
                  color = colors.muted,
                )
              }
            }
          }
          OutlinedButton(
            onClick = {
              onDismiss()
              onOpenMemorizationPlan()
            },
            modifier = Modifier.minimumInteractiveComponentSize(),
          ) {
            Text("خطة الحفظ")
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllahNameDetailBottomSheet(
  allahName: AllahName,
  isFavorite: Boolean,
  onDismiss: () -> Unit,
  onToggleFavorite: () -> Unit,
  onCopy: () -> Unit,
  onShare: () -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = allahName.name,
            fontFamily = AmiriFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = colors.gold2,
          )
          Text(
            text = "من أسماء الله الحسنى · الاسم رقم ${allahName.number}",
            style = MaterialTheme.typography.bodySmall,
            color = colors.muted,
          )
        }
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier.minimumInteractiveComponentSize().testTag("sheet_asma_fav"),
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "إزالة من المفضلة" else "إضافة إلى المفضلة",
            tint = if (isFavorite) colors.gold else colors.muted,
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surface2,
        border = BorderStroke(1.dp, colors.line),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "المعنى والدلالة",
            style = MaterialTheme.typography.labelLarge,
            color = colors.gold,
          )
          Text(
            text = allahName.meaning,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Text(
        text = "ملاحظة التوثيق: الفوائد السلوكية والشواهد التفصيلية المفردة لهذا الاسم غير مدرجة بصورة مستقلة في مصدر البيانات الأساسي.",
        style = MaterialTheme.typography.bodySmall,
        color = colors.muted,
      )

      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
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

@Composable
fun EmptyStateBox(
  icon: ImageVector,
  title: String,
  message: String,
  modifier: Modifier = Modifier,
) {
  val colors = TarmeemThemeTokens.colors
  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, colors.line),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = colors.gold,
        modifier = Modifier.size(40.dp),
      )
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
      )
      Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = colors.muted,
        textAlign = TextAlign.Center,
      )
    }
  }
}
