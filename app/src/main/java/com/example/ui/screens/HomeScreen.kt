package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AllahName
import com.example.model.EncyclopediaPayload
import com.example.model.ReadingHistoryItem
import com.example.model.Surah
import com.example.model.TopicEntry
import com.example.model.UserPreferences
import com.example.ui.components.AllahNameCard
import com.example.ui.components.QuranVerseBanner
import com.example.ui.components.RubElHizbEmblem
import com.example.ui.components.SectionHeader
import com.example.ui.components.SurahCompactCard
import com.example.ui.components.TopicListCard
import com.example.ui.components.islamicGeometricBackdrop
import com.example.ui.navigation.AppDestination
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.TarmeemThemeTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
  payload: EncyclopediaPayload,
  allTopicEntries: List<TopicEntry>,
  userPrefs: UserPreferences,
  onNavigate: (AppDestination) -> Unit,
  onOpenTopic: (String) -> Unit,
  onOpenSurah: (Surah) -> Unit,
  onOpenName: (AllahName) -> Unit,
  onToggleFavoriteTopic: (String) -> Unit,
  onToggleFavoriteSurah: (String) -> Unit,
  onToggleFavoriteName: (String) -> Unit,
  onOpenHistoryItem: (ReadingHistoryItem) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  val topicMap = allTopicEntries.associateBy { it.topic.id }

  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      modifier = Modifier.fillMaxSize().widthIn(max = 960.dp).testTag("home_screen_list"),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
      // 1. Hero Brand + Welcome + Quick Access (Search & Favorites)
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.45f)),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        ) {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .background(
                  Brush.verticalGradient(
                    listOf(
                      colors.gold.copy(alpha = 0.14f),
                      MaterialTheme.colorScheme.surface,
                    )
                  )
                )
                .islamicGeometricBackdrop(lineColor = colors.gold, alpha = 0.07f)
                .padding(20.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = colors.surface2,
                  border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.5f)),
                  modifier = Modifier.size(52.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    RubElHizbEmblem(modifier = Modifier.size(34.dp), color = colors.gold)
                  }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                    text = "ترميم الإسلام",
                    style = MaterialTheme.typography.displaySmall.copy(fontFamily = AmiriFontFamily),
                    color = colors.gold2,
                  )
                  Text(
                    text = "«علمٌ يُبصّر، وإيمانٌ يُعمّر، وعملٌ يُصلح»",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.gold,
                  )
                }
              }

              Text(
                text = "مرحبًا بك في رحاب العلم الشرعي الموثّق. استكشف السيرة النبوية والعقيدة والقرآن والفقه وأسماء الله الحسنى في تجربة قراءة هادئة ومترابطة.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Button(
                  onClick = { onNavigate(AppDestination.SEARCH) },
                  modifier =
                    Modifier.weight(1f).minimumInteractiveComponentSize().testTag("home_search_button"),
                  colors =
                    ButtonDefaults.buttonColors(
                      containerColor = colors.gold,
                      contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                  Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(Modifier.width(6.dp))
                  Text("البحث الشامل")
                }
                OutlinedButton(
                  onClick = { onNavigate(AppDestination.FAVORITES) },
                  modifier =
                    Modifier.weight(1f)
                      .minimumInteractiveComponentSize()
                      .testTag("home_favorites_button"),
                  border = BorderStroke(1.dp, colors.gold.copy(alpha = 0.6f)),
                ) {
                  Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = colors.gold,
                    modifier = Modifier.size(18.dp),
                  )
                  Spacer(Modifier.width(6.dp))
                  val favTotal =
                    userPrefs.favoriteTopicIds.size +
                      userPrefs.favoriteSurahIds.size +
                      userPrefs.favoriteNameIds.size
                  Text("المفضلة ($favTotal)")
                }
              }
            }
          }
        }
      }

      // 2. Featured Quranic Verse
      item {
        QuranVerseBanner(
          verse = payload.HOME_VERSE,
          source = payload.HOME_VERSE_SOURCE,
        )
      }

      // 3. Continue Reading Section
      if (userPrefs.readingHistory.isNotEmpty()) {
        val lastItem = userPrefs.readingHistory.first()
        item {
          SectionHeader(
            title = "متابعة القراءة",
            subtitle = "عد مباشرةً إلى آخر ما توقفت عنده",
          )
          Spacer(Modifier.height(8.dp))
          Card(
            modifier =
              Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { onOpenHistoryItem(lastItem) }
                .testTag("continue_reading_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, colors.green.copy(alpha = 0.45f)),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Text(
                  text = lastItem.subtitle,
                  style = MaterialTheme.typography.labelSmall,
                  color = colors.green,
                )
                Text(
                  text = lastItem.title,
                  style = MaterialTheme.typography.titleLarge,
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
              }
              Spacer(Modifier.width(12.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.green.copy(alpha = 0.16f),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                  Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = "متابعة القراءة",
                    tint = colors.green,
                    modifier = Modifier.size(18.dp),
                  )
                  Text(
                    text = "متابعة",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.green,
                  )
                }
              }
            }
          }
        }
      }

      // 4. Encyclopedia Statistics
      item {
        SectionHeader(
          title = "إحصائيات الموسوعة",
          subtitle = "الأرقام الموثقة لمحتويات موسوعة ترميم الإسلام",
        )
        Spacer(Modifier.height(8.dp))
        val stats =
          listOf(
            "أقسام كبرى" to payload.statistics.sections,
            "موضوعًا علميًا" to payload.statistics.topics,
            "بابًا فقهيًا" to payload.statistics.fiqhChapters,
            "سورة قرآنية" to payload.statistics.surahs,
            "اسمًا لله الحسنى" to payload.statistics.names,
            "غزوة ومحطة كبرى" to payload.statistics.battles,
          )
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val cols = if (maxWidth >= 600.dp) 3 else 2
          val spacing = 10.dp
          val cardWidth = (maxWidth - spacing * (cols - 1)) / cols
          FlowRow(
            maxItemsInEachRow = cols,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
          ) {
            stats.forEach { (label, value) ->
              Surface(
                modifier = Modifier.width(cardWidth),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, colors.line),
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                  Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.gold2,
                    fontWeight = FontWeight.Bold,
                  )
                  Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.muted,
                  )
                }
              }
            }
          }
        }
      }

      // 5. Main Section Cards + Asma & Memorization Plan cards
      item {
        SectionHeader(
          title = "أقسام الموسوعة الرئيسية",
          subtitle = "تصفّح العلوم الإسلامية في تبويبٍ منظم",
        )
        Spacer(Modifier.height(8.dp))
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
          val cols = if (maxWidth >= 680.dp) 2 else 1
          val spacing = 12.dp
          val cardWidth = (maxWidth - spacing * (cols - 1)) / cols
          FlowRow(
            maxItemsInEachRow = cols,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
          ) {
            payload.SECTION_CARDS.forEach { sec ->
              val (dest, icon, accent) =
                when (sec.key) {
                  "seerah" -> Triple(AppDestination.SEERAH, Icons.Rounded.AutoStories, colors.seerahAccent)
                  "aqeedah" -> Triple(AppDestination.AQEEDAH, Icons.Rounded.Shield, colors.aqeedahAccent)
                  "quran" -> Triple(AppDestination.QURAN, Icons.AutoMirrored.Rounded.MenuBook, colors.quranAccent)
                  else -> Triple(AppDestination.FIQH, Icons.Rounded.Balance, colors.fiqhAccent)
                }
              MainSectionCard(
                title = sec.title,
                description = sec.description,
                countText = "${sec.count} ${sec.unit}",
                icon = icon,
                accentColor = accent,
                onClick = { onNavigate(dest) },
                modifier = Modifier.width(cardWidth).testTag("section_card_${sec.key}"),
              )
            }

            MainSectionCard(
              title = "أسماء الله الحسنى",
              description = "الأسماء الحسنى التسعة والتسعون مرتبة مع بيان معانيها ودلالاتها الإيمانية.",
              countText = "99 اسمًا",
              icon = Icons.Rounded.Stars,
              accentColor = colors.asmaAccent,
              onClick = { onNavigate(AppDestination.ASMA) },
              modifier = Modifier.width(cardWidth).testTag("section_card_asma"),
            )

            MainSectionCard(
              title = "خطة الحفظ والمراجعة",
              description = "خطتك التفاعلية لتتبع حفظ السور ومراجعتها وتسجيل نسبة الإنجاز ومواعيد المراجعة.",
              countText = "${userPrefs.memorizationRecords.size} / 114 سورة",
              icon = Icons.AutoMirrored.Rounded.EventNote,
              accentColor = colors.green,
              onClick = { onNavigate(AppDestination.MEMORIZATION) },
              modifier = Modifier.width(cardWidth).testTag("section_card_memorization"),
            )
          }
        }
      }

      // 6. Featured Topics
      item {
        SectionHeader(
          title = "الموضوعات المميزة",
          subtitle = "مختارات علمية من أركان الموسوعة",
        )
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          payload.featuredTopics.forEach { feat ->
            val entry = topicMap[feat.topic.id]
            if (entry != null) {
              TopicListCard(
                entry = entry,
                isFavorite = userPrefs.favoriteTopicIds.contains(entry.topic.id),
                showSummary = userPrefs.showSummariesInLists,
                accentColor = colors.gold,
                badgeText = feat.group,
                onClick = { onOpenTopic(entry.topic.id) },
                onToggleFavorite = { onToggleFavoriteTopic(entry.topic.id) },
              )
            }
          }
        }
      }

      // 7. Suggested Surahs
      item {
        SectionHeader(
          title = "سور قرآنية مقترحة",
          subtitle = "بطاقات مختارة من سور القرآن الكريم",
          trailingContent = {
            TextButton(
              onClick = { onNavigate(AppDestination.QURAN) },
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Text("عرض الكل", color = colors.gold)
            }
          },
        )
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          payload.featuredSurahs.forEach { surah ->
            SurahCompactCard(
              surah = surah,
              isFavorite = userPrefs.favoriteSurahIds.contains(surah.id),
              memorizationStatus =
                userPrefs.memorizationRecords[surah.id]?.status
                  ?: com.example.model.MemorizationStatus.NONE,
              onClick = { onOpenSurah(surah) },
              onToggleFavorite = { onToggleFavoriteSurah(surah.id) },
            )
          }
        }
      }

      // 8. Featured Names of Allah
      item {
        SectionHeader(
          title = "أسماء الله الحسنى المميزة",
          subtitle = "مختارات من الأسماء الحسنى ومعانيها",
          trailingContent = {
            TextButton(
              onClick = { onNavigate(AppDestination.ASMA) },
              modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
              Text("الكل (99)", color = colors.gold)
            }
          },
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          items(payload.featuredNames, key = { it.id }) { name ->
            AllahNameCard(
              allahName = name,
              isFavorite = userPrefs.favoriteNameIds.contains(name.id),
              onClick = { onOpenName(name) },
              onToggleFavorite = { onToggleFavoriteName(name.id) },
              modifier = Modifier.width(210.dp),
            )
          }
        }
      }

      // 9. Recently Read Topics
      if (userPrefs.readingHistory.isNotEmpty()) {
        item {
          SectionHeader(
            title = "آخر ما قرأته",
            subtitle = "سجل مطالعتك الأخيرة في الموسوعة",
          )
          Spacer(Modifier.height(8.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            userPrefs.readingHistory.take(5).forEach { history ->
              Surface(
                modifier =
                  Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenHistoryItem(history) },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, colors.line),
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth().padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                  Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = colors.gold,
                    modifier = Modifier.size(20.dp),
                  )
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = history.title,
                      style = MaterialTheme.typography.titleSmall,
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                      text = history.subtitle,
                      style = MaterialTheme.typography.labelSmall,
                      color = colors.muted,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                  Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "فتح",
                    tint = colors.muted,
                    modifier = Modifier.size(16.dp),
                  )
                }
              }
            }
          }
        }
      }

      item { Spacer(Modifier.height(16.dp)) }
    }
  }
}

@Composable
private fun MainSectionCard(
  title: String,
  description: String,
  countText: String,
  icon: ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = TarmeemThemeTokens.colors
  Card(
    modifier = modifier.clip(RoundedCornerShape(18.dp)).clickable(onClick = onClick),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, colors.line),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = accentColor.copy(alpha = 0.16f),
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accentColor,
            modifier = Modifier.padding(10.dp).size(24.dp),
          )
        }
        Surface(
          shape = RoundedCornerShape(50),
          color = colors.surface2,
          border = BorderStroke(1.dp, colors.line),
        ) {
          Text(
            text = countText,
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
      }
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontFamily = AmiriFontFamily, fontSize = 20.sp),
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = colors.muted,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}
