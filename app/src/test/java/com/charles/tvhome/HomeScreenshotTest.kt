package com.charles.tvhome

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.Density
import com.android.resources.ScreenOrientation
import com.charles.tvhome.ui.home.HomeScreen
import com.charles.tvhome.ui.theme.TvHomeTheme
import org.junit.Rule
import org.junit.Test

/**
 * Renders the home screen as a 1080p TV would draw it.
 * Run `./gradlew :app:recordPaparazziDebug`; images land in app/src/test/snapshots/images.
 */
class HomeScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.NEXUS_5.copy(
            screenWidth = 1920,
            screenHeight = 1080,
            xdpi = 320,
            ydpi = 320,
            density = Density.XHIGH,
            orientation = ScreenOrientation.LANDSCAPE,
        ),
        theme = "android:Theme.DeviceDefault.NoActionBar",
    )

    @Test
    fun home() {
        paparazzi.snapshot {
            TvHomeTheme {
                HomeScreen(homePresses = 0, resumes = 0)
            }
        }
    }
}
