package com.charles.tvhome

import com.charles.tvhome.ui.home.HomeScreen
import com.charles.tvhome.ui.theme.TvHomeTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Renders the home screen as a 1080p TV draws it (960x540 dp at xhdpi).
 * Run `./gradlew :app:testDebugUnitTest`; the image lands in app/build/screenshots.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w960dp-h540dp-land-xhdpi")
class HomeScreenshotTest {

    @Test
    fun home() {
        captureRoboImage("build/screenshots/home.png") {
            TvHomeTheme {
                HomeScreen(homePresses = 0, resumes = 0)
            }
        }
    }
}
