package com.example.ui

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.model.AllahName
import com.example.model.ReadingHistoryItem
import com.example.model.Surah
import com.example.ui.components.AllahNameDetailBottomSheet
import com.example.ui.components.RubElHizbEmblem
import com.example.ui.components.SurahDetailBottomSheet
import com.example.ui.navigation.AppDestination
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AqeedahScreen
import com.example.ui.screens.AsmaAllahScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.FiqhScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemorizationPlanScreen
import com.example.ui.screens.QuranScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SeerahScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TopicDetailScreen
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.TarmeemTheme
import com.example.ui.theme.TarmeemThemeTokens
import com.example.ui.viewmodel.EncyclopediaUiState
import com.example.ui.viewmodel.EncyclopediaViewModel
import kotlinx.coroutines.launch

@Composable
fun TarmeemApp(
  viewModel: EncyclopediaViewModel = viewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()

  TarmeemTheme(
    darkTheme = userPrefs.isDarkTheme,
    fontScale = userPrefs.fontScale,
    comfortLineSpacing = userPrefs.comfortLineSpacing,
  ) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var activeSurahSheet by remember { mutableStateOf<Surah?>(null) }
    var activeNameSheet by remember { mutableStateOf<AllahName?>(null) }

    fun showMessage(msg: String) {
      scope.launch {
        snackbarHostState.currentSnackbarData?.dismiss()
        snackbarHostState.showSnackbar(msg)
      }
    }

    fun copyText(text: String) {
      clipboard.setText(AnnotatedString(text))
      showMessage(context.getString(R.string.copied_to_clipboard))
    }

    fun shareText(title: String, text: String) {
      val sendIntent =
        Intent(Intent.ACTION_SEND).apply {
          type = "text/plain"
          putExtra(Intent.EXTRA_SUBJECT, title)
          putExtra(Intent.EXTRA_TEXT, text)
        }
      val chooser = Intent.createChooser(sendIntent, context.getString(R.string.share_topic_chooser))
      runCatching { context.startActivity(chooser) }
        .onFailure {
          clipboard.setText(AnnotatedString(text))
          showMessage("تعذر فتح نافذة المشاركة، تم نسخ النص إلى الحافظة")
        }
    }

    fun navigateTo(dest: AppDestination) {
      navController.navigate(dest.route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
      }
    }

    fun openTopic(topicId: String) {
      navController.navigate(AppDestination.topicDetailRoute(topicId)) {
        launchSingleTop = true
      }
    }

    fun openSurah(surah: Surah) {
      viewModel.recordSurahRead(surah)
      activeSurahSheet = surah
    }

    fun openName(name: AllahName) {
      viewModel.recordNameRead(name)
      activeNameSheet = name
    }

    fun openHistoryItem(item: ReadingHistoryItem) {
      when (item.kind) {
        "topic" -> openTopic(item.entryId)
        "surah" -> viewModel.getSurahById(item.entryId)?.let { openSurah(it) }
        "name" -> viewModel.getNameById(item.entryId)?.let { openName(it) }
      }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val isWideScreen = maxWidth >= 840.dp
      val backStackEntry by navController.currentBackStackEntryAsState()
      val currentRoute = backStackEntry?.destination?.route
      val currentDestination = AppDestination.fromRoute(currentRoute)
      val isTopicDetailRoute = currentRoute == AppDestination.ROUTE_TOPIC_DETAIL

      val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

      BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
      }

      val drawerSheetBody: @Composable () -> Unit = {
        DrawerMenuBody(
          currentDestination = currentDestination,
          onSelect = { dest ->
            scope.launch { drawerState.close() }
            navigateTo(dest)
          },
        )
      }

      val mainScaffoldContent: @Composable () -> Unit = {
        MainAppScaffold(
          isWideScreen = isWideScreen,
          isTopicDetailRoute = isTopicDetailRoute,
          currentDestination = currentDestination,
          isDarkTheme = userPrefs.isDarkTheme,
          snackbarHostState = snackbarHostState,
          onOpenDrawer = { scope.launch { drawerState.open() } },
          onToggleTheme = { viewModel.setDarkTheme(!userPrefs.isDarkTheme) },
          onNavigate = ::navigateTo,
        ) { contentModifier ->
          when (val state = uiState) {
            is EncyclopediaUiState.Loading -> {
              Box(
                modifier = contentModifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                  CircularProgressIndicator(color = TarmeemThemeTokens.colors.gold)
                  Text(
                    text = "جارٍ تهيئة موسوعة ترميم الإسلام...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TarmeemThemeTokens.colors.muted,
                  )
                }
              }
            }

            is EncyclopediaUiState.Error -> {
              Box(
                modifier = contentModifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center,
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                  Text(
                    text = state.message,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                  )
                  Button(onClick = { viewModel.loadData() }) {
                    Text("إعادة المحاولة")
                  }
                }
              }
            }

            is EncyclopediaUiState.Ready -> {
              val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
              val searchCategory by viewModel.searchCategory.collectAsStateWithLifecycle()
              val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

              TarmeemNavHost(
                modifier = contentModifier,
                navController = navController,
                state = state,
                userPrefs = userPrefs,
                searchQuery = searchQuery,
                searchCategory = searchCategory,
                searchResults = searchResults,
                viewModel = viewModel,
                onNavigate = ::navigateTo,
                onOpenTopic = ::openTopic,
                onOpenSurah = ::openSurah,
                onOpenName = ::openName,
                onOpenHistoryItem = ::openHistoryItem,
                onToggleFavTopic = { id ->
                  val willAdd = !userPrefs.favoriteTopicIds.contains(id)
                  viewModel.toggleFavoriteTopic(id)
                  showMessage(
                    context.getString(
                      if (willAdd) R.string.added_to_favorites else R.string.removed_from_favorites
                    )
                  )
                },
                onToggleFavSurah = { id ->
                  val willAdd = !userPrefs.favoriteSurahIds.contains(id)
                  viewModel.toggleFavoriteSurah(id)
                  showMessage(
                    context.getString(
                      if (willAdd) R.string.added_to_favorites else R.string.removed_from_favorites
                    )
                  )
                },
                onToggleFavName = { id ->
                  val willAdd = !userPrefs.favoriteNameIds.contains(id)
                  viewModel.toggleFavoriteName(id)
                  showMessage(
                    context.getString(
                      if (willAdd) R.string.added_to_favorites else R.string.removed_from_favorites
                    )
                  )
                },
                onCopyText = ::copyText,
                onShareText = ::shareText,
                onShowMessage = ::showMessage,
              )
            }
          }
        }
      }

      if (isWideScreen) {
        PermanentNavigationDrawer(
          drawerContent = {
            PermanentDrawerSheet(
              modifier = Modifier.width(280.dp),
              drawerContainerColor = MaterialTheme.colorScheme.surface,
              drawerContentColor = MaterialTheme.colorScheme.onSurface,
            ) {
              drawerSheetBody()
            }
          },
          content = mainScaffoldContent,
        )
      } else {
        ModalNavigationDrawer(
          drawerState = drawerState,
          gesturesEnabled = !isTopicDetailRoute,
          drawerContent = {
            ModalDrawerSheet(
              modifier = Modifier.width(290.dp),
              drawerContainerColor = MaterialTheme.colorScheme.surface,
              drawerContentColor = MaterialTheme.colorScheme.onSurface,
            ) {
              drawerSheetBody()
            }
          },
          content = mainScaffoldContent,
        )
      }

      activeSurahSheet?.let { surah ->
        val shareBody =
          "سورة ${surah.name} (رقم ${surah.number})\n${surah.revelation} · ${surah.ayahs} آية\n\n${surah.virtue}\n\n— موسوعة ترميم الإسلام"
        SurahDetailBottomSheet(
          surah = surah,
          isFavorite = userPrefs.favoriteSurahIds.contains(surah.id),
          memorizationRecord = userPrefs.memorizationRecords[surah.id],
          onDismiss = { activeSurahSheet = null },
          onToggleFavorite = {
            val willAdd = !userPrefs.favoriteSurahIds.contains(surah.id)
            viewModel.toggleFavoriteSurah(surah.id)
            showMessage(
              context.getString(
                if (willAdd) R.string.added_to_favorites else R.string.removed_from_favorites
              )
            )
          },
          onCopy = { copyText(shareBody) },
          onShare = { shareText("سورة ${surah.name}", shareBody) },
          onOpenMemorizationPlan = { navigateTo(AppDestination.MEMORIZATION) },
        )
      }

      activeNameSheet?.let { name ->
        val shareBody =
          "${name.name} (الاسم رقم ${name.number} من أسماء الله الحسنى)\n\nالمعنى: ${name.meaning}\n\n— موسوعة ترميم الإسلام"
        AllahNameDetailBottomSheet(
          allahName = name,
          isFavorite = userPrefs.favoriteNameIds.contains(name.id),
          onDismiss = { activeNameSheet = null },
          onToggleFavorite = {
            val willAdd = !userPrefs.favoriteNameIds.contains(name.id)
            viewModel.toggleFavoriteName(name.id)
            showMessage(
              context.getString(
                if (willAdd) R.string.added_to_favorites else R.string.removed_from_favorites
              )
            )
          },
          onCopy = { copyText(shareBody) },
          onShare = { shareText(name.name, shareBody) },
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainAppScaffold(
  isWideScreen: Boolean,
  isTopicDetailRoute: Boolean,
  currentDestination: AppDestination?,
  isDarkTheme: Boolean,
  snackbarHostState: SnackbarHostState,
  onOpenDrawer: () -> Unit,
  onToggleTheme: () -> Unit,
  onNavigate: (AppDestination) -> Unit,
  content: @Composable (Modifier) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      if (!isTopicDetailRoute) {
        TopAppBar(
          modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
          title = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              RubElHizbEmblem(modifier = Modifier.size(20.dp), color = colors.gold)
              Text(
                text = currentDestination?.title ?: "ترميم الإسلام",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = AmiriFontFamily),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          },
          navigationIcon = {
            if (!isWideScreen) {
              IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier.minimumInteractiveComponentSize().testTag("drawer_menu_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Menu,
                  contentDescription = "فتح القائمة الجانبية",
                  tint = colors.gold,
                )
              }
            }
          },
          actions = {
            IconButton(
              onClick = onToggleTheme,
              modifier = Modifier.minimumInteractiveComponentSize().testTag("top_theme_toggle"),
            ) {
              Icon(
                imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                contentDescription = if (isDarkTheme) "تفعيل الوضع الفاتح" else "تفعيل الوضع الداكن",
                tint = colors.gold,
              )
            }
            if (currentDestination != AppDestination.SEARCH) {
              IconButton(
                onClick = { onNavigate(AppDestination.SEARCH) },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("top_search_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Search,
                  contentDescription = "البحث",
                  tint = colors.muted,
                )
              }
            }
            if (currentDestination != AppDestination.SETTINGS) {
              IconButton(
                onClick = { onNavigate(AppDestination.SETTINGS) },
                modifier = Modifier.minimumInteractiveComponentSize().testTag("top_settings_button"),
              ) {
                Icon(
                  imageVector = Icons.Rounded.Settings,
                  contentDescription = "الإعدادات",
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
      }
    },
    bottomBar = {
      if (!isWideScreen && !isTopicDetailRoute) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = colors.gold,
          windowInsets = WindowInsets.navigationBars,
        ) {
          AppDestination.entries.filter { it.showInBottomBar }.forEach { dest ->
            val selected = currentDestination == dest
            NavigationBarItem(
              selected = selected,
              onClick = { onNavigate(dest) },
              icon = {
                Icon(
                  imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                  contentDescription = dest.title,
                )
              },
              label = {
                Text(
                  text = dest.shortLabel,
                  style = MaterialTheme.typography.labelSmall,
                  maxLines = 1,
                )
              },
              modifier = Modifier.testTag("bottom_nav_${dest.route}"),
              colors =
                NavigationBarItemDefaults.colors(
                  selectedIconColor = colors.gold2,
                  selectedTextColor = colors.gold2,
                  indicatorColor = colors.gold.copy(alpha = 0.16f),
                  unselectedIconColor = colors.muted,
                  unselectedTextColor = colors.muted,
                ),
            )
          }
        }
      }
    },
  ) { innerPadding ->
    content(Modifier.padding(innerPadding))
  }
}

@Composable
private fun DrawerMenuBody(
  currentDestination: AppDestination?,
  onSelect: (AppDestination) -> Unit,
) {
  val colors = TarmeemThemeTokens.colors
  Column(
    modifier =
      Modifier.fillMaxHeight()
        .windowInsetsPadding(WindowInsets.statusBars)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 12.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = colors.surface2,
      border = BorderStroke(1.dp, colors.line),
      modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        RubElHizbEmblem(modifier = Modifier.size(32.dp), color = colors.gold)
        Column {
          Text(
            text = "ترميم الإسلام",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = AmiriFontFamily),
            color = colors.gold2,
          )
          Text(
            text = "موسوعة علمية إسلامية",
            style = MaterialTheme.typography.labelSmall,
            color = colors.muted,
          )
        }
      }
    }

    AppDestination.entries.forEach { dest ->
      if (dest == AppDestination.ABOUT) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colors.line)
      }
      val selected = currentDestination == dest
      NavigationDrawerItem(
        selected = selected,
        onClick = { onSelect(dest) },
        icon = {
          Icon(
            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
            contentDescription = null,
          )
        },
        label = {
          Text(
            text = dest.title,
            style = MaterialTheme.typography.bodyMedium,
          )
        },
        modifier = Modifier.testTag("drawer_item_${dest.route}"),
        colors =
          NavigationDrawerItemDefaults.colors(
            selectedContainerColor = colors.gold.copy(alpha = 0.16f),
            selectedIconColor = colors.gold2,
            selectedTextColor = colors.gold2,
            unselectedIconColor = colors.muted,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
          ),
      )
    }

    Spacer(Modifier.height(12.dp))
  }
}

@Composable
private fun TarmeemNavHost(
  modifier: Modifier,
  navController: NavHostController,
  state: EncyclopediaUiState.Ready,
  userPrefs: com.example.model.UserPreferences,
  searchQuery: String,
  searchCategory: com.example.model.SearchCategory,
  searchResults: List<com.example.model.SearchResultItem>,
  viewModel: EncyclopediaViewModel,
  onNavigate: (AppDestination) -> Unit,
  onOpenTopic: (String) -> Unit,
  onOpenSurah: (Surah) -> Unit,
  onOpenName: (AllahName) -> Unit,
  onOpenHistoryItem: (ReadingHistoryItem) -> Unit,
  onToggleFavTopic: (String) -> Unit,
  onToggleFavSurah: (String) -> Unit,
  onToggleFavName: (String) -> Unit,
  onCopyText: (String) -> Unit,
  onShareText: (String, String) -> Unit,
  onShowMessage: (String) -> Unit,
) {
  NavHost(
    navController = navController,
    startDestination = AppDestination.HOME.route,
    modifier = modifier.fillMaxSize(),
    enterTransition = { fadeIn() },
    exitTransition = { fadeOut() },
  ) {
    composable(AppDestination.HOME.route) {
      HomeScreen(
        payload = state.payload,
        allTopicEntries = state.allTopicEntries,
        userPrefs = userPrefs,
        onNavigate = onNavigate,
        onOpenTopic = onOpenTopic,
        onOpenSurah = onOpenSurah,
        onOpenName = onOpenName,
        onToggleFavoriteTopic = onToggleFavTopic,
        onToggleFavoriteSurah = onToggleFavSurah,
        onToggleFavoriteName = onToggleFavName,
        onOpenHistoryItem = onOpenHistoryItem,
      )
    }

    composable(AppDestination.SEERAH.route) {
      SeerahScreen(
        payload = state.payload,
        allTopicEntries = state.allTopicEntries,
        userPrefs = userPrefs,
        onOpenTopic = onOpenTopic,
        onToggleFavorite = onToggleFavTopic,
      )
    }

    composable(AppDestination.AQEEDAH.route) {
      AqeedahScreen(
        allTopicEntries = state.allTopicEntries,
        userPrefs = userPrefs,
        onOpenTopic = onOpenTopic,
        onToggleFavorite = onToggleFavTopic,
      )
    }

    composable(AppDestination.QURAN.route) {
      QuranScreen(
        payload = state.payload,
        allTopicEntries = state.allTopicEntries,
        userPrefs = userPrefs,
        onOpenTopic = onOpenTopic,
        onToggleFavoriteTopic = onToggleFavTopic,
        onOpenSurah = onOpenSurah,
        onToggleFavoriteSurah = onToggleFavSurah,
        onOpenMemorizationPlan = { onNavigate(AppDestination.MEMORIZATION) },
      )
    }

    composable(AppDestination.ASMA.route) {
      AsmaAllahScreen(
        payload = state.payload,
        userPrefs = userPrefs,
        onOpenName = onOpenName,
        onToggleFavoriteName = onToggleFavName,
      )
    }

    composable(AppDestination.FIQH.route) {
      FiqhScreen(
        payload = state.payload,
        allTopicEntries = state.allTopicEntries,
        userPrefs = userPrefs,
        onOpenTopic = onOpenTopic,
        onToggleFavorite = onToggleFavTopic,
      )
    }

    composable(AppDestination.SEARCH.route) {
      SearchScreen(
        query = searchQuery,
        selectedCategory = searchCategory,
        results = searchResults,
        totalEntriesCount =
          state.allTopicEntries.size + state.payload.surahs.size + state.payload.asmaAllah.size,
        userPrefs = userPrefs,
        onQueryChange = viewModel::updateSearchQuery,
        onCategoryChange = viewModel::updateSearchCategory,
        onOpenTopic = onOpenTopic,
        onOpenSurah = onOpenSurah,
        onOpenName = onOpenName,
        onToggleFavoriteTopic = onToggleFavTopic,
        onToggleFavoriteSurah = onToggleFavSurah,
        onToggleFavoriteName = onToggleFavName,
      )
    }

    composable(AppDestination.FAVORITES.route) {
      FavoritesScreen(
        payload = state.payload,
        allTopicEntries = state.allTopicEntries,
        userPrefs = userPrefs,
        onOpenTopic = onOpenTopic,
        onOpenSurah = onOpenSurah,
        onOpenName = onOpenName,
        onOpenHistoryItem = onOpenHistoryItem,
        onToggleFavoriteTopic = onToggleFavTopic,
        onToggleFavoriteSurah = onToggleFavSurah,
        onToggleFavoriteName = onToggleFavName,
        onClearReadingHistory = {
          viewModel.clearReadingHistory()
          onShowMessage("تم مسح سجل القراءة")
        },
      )
    }

    composable(AppDestination.MEMORIZATION.route) {
      MemorizationPlanScreen(
        payload = state.payload,
        userPrefs = userPrefs,
        onUpdateMemorization = { surahId, status, date ->
          viewModel.updateMemorization(surahId, status, date)
          onShowMessage("تم تحديث خطة الحفظ")
        },
      )
    }

    composable(AppDestination.ABOUT.route) {
      AboutScreen(payload = state.payload)
    }

    composable(AppDestination.SETTINGS.route) {
      SettingsScreen(
        userPrefs = userPrefs,
        onToggleDarkTheme = viewModel::setDarkTheme,
        onSelectFontScale = viewModel::setFontScale,
        onToggleComfortLineSpacing = viewModel::setComfortLineSpacing,
        onToggleShowSummaries = viewModel::setShowSummariesInLists,
        onClearHistory = {
          viewModel.clearReadingHistory()
          onShowMessage("تم مسح سجل القراءة")
        },
        onClearFavorites = {
          viewModel.clearAllFavorites()
          onShowMessage("تم مسح جميع عناصر المفضلة")
        },
        onClearMemorization = {
          viewModel.clearMemorizationPlan()
          onShowMessage("تمت إعادة ضبط خطة الحفظ")
        },
        onNavigateAbout = { onNavigate(AppDestination.ABOUT) },
      )
    }

    composable(
      route = AppDestination.ROUTE_TOPIC_DETAIL,
      arguments = listOf(navArgument("topicId") { type = NavType.StringType }),
    ) { entry ->
      val topicId = entry.arguments?.getString("topicId").orEmpty()
      val topicEntry = viewModel.getTopicEntry(topicId)
      val related = viewModel.getRelatedTopics(topicId)
      TopicDetailScreen(
        entry = topicEntry,
        relatedTopics = related,
        isFavorite = userPrefs.favoriteTopicIds.contains(topicId),
        onBack = { navController.popBackStack() },
        onToggleFavorite = { onToggleFavTopic(topicId) },
        onCopy = {
          topicEntry?.let { onCopyText(viewModel.formatTopicForSharing(it)) }
        },
        onShare = {
          topicEntry?.let {
            onShareText(it.topic.title, viewModel.formatTopicForSharing(it))
          }
        },
        onOpenRelatedTopic = onOpenTopic,
        onRecordRead = viewModel::recordTopicRead,
      )
    }
  }
}
