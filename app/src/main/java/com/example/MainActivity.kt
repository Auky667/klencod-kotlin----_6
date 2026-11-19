package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TarmeemApp
import com.example.ui.viewmodel.EncyclopediaViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: EncyclopediaViewModel = viewModel()
      val prefs = viewModel.userPreferences.collectAsStateWithLifecycle().value
      val view = LocalView.current
      if (!view.isInEditMode) {
        SideEffect {
          val controller = WindowCompat.getInsetsController(window, view)
          controller.isAppearanceLightStatusBars = !prefs.isDarkTheme
          controller.isAppearanceLightNavigationBars = !prefs.isDarkTheme
        }
      }
      TarmeemApp(viewModel = viewModel)
    }
  }
}