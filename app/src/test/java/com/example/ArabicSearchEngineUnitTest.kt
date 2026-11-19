package com.example

import com.example.model.AllahName
import com.example.model.Evidence
import com.example.model.SearchCategory
import com.example.model.SearchResultItem
import com.example.model.Surah
import com.example.model.Topic
import com.example.model.TopicEntry
import com.example.util.ArabicSearchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArabicSearchEngineUnitTest {

  private val sampleTopics =
    listOf(
      TopicEntry(
        topic =
          Topic(
            id = "aqeedah-1",
            title = "أصل التوحيد والإيمان",
            summary = "بيان معنى توحيد الله وإفراده بالعبادة.",
            content = listOf("التوحيد هو أساس الدين الإسلامي وركنه الأعظم."),
            evidences = listOf(Evidence(text = "اعبدوا الله ما لكم من إله غيره", source = "الأعراف: 59")),
            benefits = listOf("تحقيق العبودية الخالصة"),
            references = listOf("العقيدة الواسطية"),
          ),
        sectionKey = "aqeedah",
        sectionTitle = "العقيدة الإسلامية",
        subGroupKey = "aqeedah",
        subGroupTitle = "أصول العقيدة",
        orderIndex = 0,
      ),
      TopicEntry(
        topic =
          Topic(
            id = "seerah-1",
            title = "الهجرة إلى المدينة المنورة",
            summary = "خروج النبي ﷺ وصاحبه أبي بكر الصديق.",
            content = listOf("كان فيها ترسيخ مبدأ التوحيد وبناء دولة الإسلام."),
          ),
        sectionKey = "seerah",
        sectionTitle = "السيرة النبوية",
        subGroupKey = "seerah-events",
        subGroupTitle = "أحداث السيرة",
        orderIndex = 1,
      ),
    )

  private val sampleSurahs =
    listOf(
      Surah(
        id = "surah-1",
        number = 1,
        name = "الفاتحة",
        ayahs = 7,
        revelation = "مكية",
        virtue = "أعظم سورة في القرآن، وبها تُفتتح الصلاة.",
      )
    )

  private val sampleNames =
    listOf(
      AllahName(
        id = "asma-1",
        number = 1,
        name = "الرَّحْمَن",
        meaning = "الواسع الرحمة لجميع الخلق.",
      )
    )

  @Test
  fun `normalize strips diacritics and unifies hamza taa marbuta and alef maksura`() {
    assertEquals("الرحمن", ArabicSearchEngine.normalize("الرَّحْمَـٰن"))
    assertEquals("اسراء", ArabicSearchEngine.normalize("إِسْرَاء"))
    assertEquals("الصلاه", ArabicSearchEngine.normalize("الصَّلَاة"))
    assertEquals("موسي", ArabicSearchEngine.normalize("مُوسَى"))
    assertEquals("مساله", ArabicSearchEngine.normalize("مَسْأَلَة"))
  }

  @Test
  fun `search ignores tashkeel and prioritizes title matches over body matches`() {
    val results =
      ArabicSearchEngine.search(
        query = "التوحيد",
        category = SearchCategory.ALL,
        topics = sampleTopics,
        surahs = sampleSurahs,
        names = sampleNames,
      )
    assertEquals(2, results.size)
    assertTrue(results.first() is SearchResultItem.TopicResult)
    assertEquals("aqeedah-1", results.first().id)
  }

  @Test
  fun `search matches surahs and names with normalized hamza and taa marbuta`() {
    val surahResults =
      ArabicSearchEngine.search(
        query = "الفاتحه",
        category = SearchCategory.SURAHS,
        topics = sampleTopics,
        surahs = sampleSurahs,
        names = sampleNames,
      )
    assertEquals(1, surahResults.size)
    assertEquals("surah-1", surahResults.first().id)

    val nameResults =
      ArabicSearchEngine.search(
        query = "الرحمن",
        category = SearchCategory.ASMA,
        topics = sampleTopics,
        surahs = sampleSurahs,
        names = sampleNames,
      )
    assertEquals(1, nameResults.size)
    assertEquals("asma-1", nameResults.first().id)
  }
}
