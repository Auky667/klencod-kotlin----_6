package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.EncyclopediaRepository
import com.example.model.SearchCategory
import com.example.util.ArabicSearchEngine
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp-xxhdpi")
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `app_name is configured as Tarmeem Al-Islam`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ترميم الإسلام", appName)
  }

  @Test
  fun `authoritative encyclopedia JSON preserves exact counts and 348 searchable entries`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = EncyclopediaRepository(context)
    val payload = repo.loadEncyclopedia()

    assertEquals(55, payload.seerah.size)
    assertEquals(3, payload.seerahSections.size)
    assertEquals(24, payload.seerahSections[0].topics.size)
    assertEquals(27, payload.seerahSections[1].topics.size)
    assertEquals(4, payload.seerahSections[2].topics.size)

    assertEquals(20, payload.aqeedah.size)
    assertEquals(12, payload.quranScience.size)
    assertEquals(12, payload.tajweed.size)

    assertEquals(7, payload.fiqh.size)
    assertEquals(36, payload.fiqh.sumOf { it.topics.size })

    assertEquals(114, payload.surahs.size)
    assertEquals(99, payload.asmaAllah.size)

    val allTopics = repo.getAllTopicEntries()
    assertEquals(135, allTopics.size)
    assertEquals(348, allTopics.size + payload.surahs.size + payload.asmaAllah.size)

    val searchHits =
      ArabicSearchEngine.search(
        query = "الصلاه",
        category = SearchCategory.ALL,
        topics = allTopics,
        surahs = payload.surahs,
        names = payload.asmaAllah,
      )
    assertTrue("Expected search hits for الصلاه", searchHits.isNotEmpty())
  }

  @Test
  fun `cuj home to search to topic detail memorization and settings theme toggle`() {
    composeTestRule.waitUntil(timeoutMillis = 10_000) {
      composeTestRule.onAllNodesWithTag("home_search_button").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.waitForIdle()

    // Home renders featured verse and search CTA
    composeTestRule.onNodeWithTag("home_search_button").assertIsDisplayed()
    composeTestRule.onNodeWithText("الإسراء: 9").performScrollTo().assertIsDisplayed()

    // Open Search and enter normalized Arabic query
    composeTestRule.onNodeWithTag("home_search_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("search_input_field").performTextInput("التوحيد")
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      composeTestRule.onAllNodesWithTag("search_result_count").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("search_result_count").assertIsDisplayed()

    // Open Seerah tab from bottom bar, filter, open first topic, toggle favorite, go back
    composeTestRule.onNodeWithTag("bottom_nav_seerah").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("seerah_filter_seerah-events").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      composeTestRule.onAllNodesWithTag("topic_card_seerah-1").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("topic_card_seerah-1").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("topic_favorite_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("topic_back_button").performClick()
    composeTestRule.waitForIdle()

    // Open Quran tab and jump to Memorization Plan, mark Surah 1 memorized
    composeTestRule.onNodeWithTag("bottom_nav_quran").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("quran_open_memorization_banner").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      composeTestRule.onAllNodesWithTag("memo_surah_row_surah-1").fetchSemanticsNodes().isNotEmpty()
    }
    composeTestRule.onNodeWithTag("memo_surah_row_surah-1").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("memo_status_option_MEMORIZED").performClick()
    composeTestRule.onNodeWithTag("memo_save_button").performClick()
    composeTestRule.waitForIdle()

    // Open Settings and toggle theme
    composeTestRule.onNodeWithTag("top_settings_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("settings_dark_theme_switch").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("settings_dark_theme_switch").performClick()
    composeTestRule.waitForIdle()
  }
}
