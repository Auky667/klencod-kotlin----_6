package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Balance
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
  val route: String,
  val title: String,
  val shortLabel: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val showInBottomBar: Boolean = false,
) {
  HOME(
    route = "home",
    title = "الرئيسية",
    shortLabel = "الرئيسية",
    selectedIcon = Icons.Rounded.Home,
    unselectedIcon = Icons.Outlined.Home,
    showInBottomBar = true,
  ),
  SEERAH(
    route = "seerah",
    title = "السيرة النبوية",
    shortLabel = "السيرة",
    selectedIcon = Icons.Rounded.AutoStories,
    unselectedIcon = Icons.Outlined.AutoStories,
    showInBottomBar = true,
  ),
  AQEEDAH(
    route = "aqeedah",
    title = "العقيدة الإسلامية",
    shortLabel = "العقيدة",
    selectedIcon = Icons.Rounded.Shield,
    unselectedIcon = Icons.Outlined.Shield,
  ),
  QURAN(
    route = "quran",
    title = "القرآن الكريم وعلومه",
    shortLabel = "القرآن",
    selectedIcon = Icons.AutoMirrored.Rounded.MenuBook,
    unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook,
    showInBottomBar = true,
  ),
  ASMA(
    route = "asma",
    title = "أسماء الله الحسنى",
    shortLabel = "الأسماء",
    selectedIcon = Icons.Rounded.Stars,
    unselectedIcon = Icons.Outlined.Stars,
  ),
  FIQH(
    route = "fiqh",
    title = "الفقه الإسلامي",
    shortLabel = "الفقه",
    selectedIcon = Icons.Rounded.Balance,
    unselectedIcon = Icons.Outlined.Balance,
  ),
  SEARCH(
    route = "search",
    title = "البحث في الموسوعة",
    shortLabel = "البحث",
    selectedIcon = Icons.Rounded.Search,
    unselectedIcon = Icons.Outlined.Search,
    showInBottomBar = true,
  ),
  FAVORITES(
    route = "favorites",
    title = "المفضلة وسجل القراءة",
    shortLabel = "المفضلة",
    selectedIcon = Icons.Rounded.Favorite,
    unselectedIcon = Icons.Outlined.FavoriteBorder,
    showInBottomBar = true,
  ),
  MEMORIZATION(
    route = "memorization",
    title = "خطة الحفظ والمراجعة",
    shortLabel = "الحفظ",
    selectedIcon = Icons.AutoMirrored.Rounded.EventNote,
    unselectedIcon = Icons.AutoMirrored.Outlined.EventNote,
  ),
  ABOUT(
    route = "about",
    title = "عن التطبيق",
    shortLabel = "عن التطبيق",
    selectedIcon = Icons.Rounded.Info,
    unselectedIcon = Icons.Outlined.Info,
  ),
  SETTINGS(
    route = "settings",
    title = "الإعدادات",
    shortLabel = "الإعدادات",
    selectedIcon = Icons.Rounded.Settings,
    unselectedIcon = Icons.Outlined.Settings,
  );

  companion object {
    const val ROUTE_TOPIC_DETAIL = "topic_detail/{topicId}"

    fun topicDetailRoute(topicId: String): String = "topic_detail/$topicId"

    fun fromRoute(route: String?): AppDestination? = entries.find { it.route == route }
  }
}
