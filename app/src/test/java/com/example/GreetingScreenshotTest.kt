package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Deck
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun home_screen_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        HomeScreen(
          decks = listOf(
            Deck(
              id = 1L,
              title = "علم الأعصاب والذاكرة",
              category = "علوم إدراكية",
              description = "كيف يخزن الدماغ الذكريات ويعالج السينابس العصبية",
              iconName = "brain",
              colorHex = "#06B6D4"
            )
          ),
          totalCards = 12,
          masteredCards = 5,
          onSelectDeck = {},
          onLaunchGlobalMethod = {},
          onCreateDeck = { _, _, _, _, _ -> },
          isAudioPlaying = false,
          onToggleAudio = {},
          onOpenAudioSettings = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/home_screen.png")
  }
}
