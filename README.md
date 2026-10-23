# TV Home

A custom home screen (launcher) for Android TV and Google TV, built with Kotlin and Jetpack Compose for TV. Everything works with the remote: D-pad, OK, Back and Home.

Package name: `com.charles.tvhome`

## Status

| Step | What | State |
|---|---|---|
| 1 | Project skeleton, manifest, launcher registration, dark screen | Done |
| 2 | Static layout with sample data and working focus | Done, waiting for TV test |
| 3 to 10 | App launching, live clock/weather, sports, Continue watching, HDMI, wallpaper, screensaver, Home Button Fix, config | Not started |

In this build:

- The clock and date are live (24-hour, British English, device timezone).
- Settings opens Android system settings.
- Weather, Continue watching, sports and inputs show **sample data** (marked "SAMPLE" on screen). Selecting a tile shows a short "coming in a later build" message.
- The sports rows show all three time states at once so you can check them: F1 shows a countdown, MotoGP Sprint shows LIVE, Man Utd shows a day and time.

## Getting the APK

Every push to `main` is built by GitHub Actions (`.github/workflows/build.yml`).

- **Easiest:** open the repository's **Releases** page and download `tvhome-debug.apk` from the release named **Latest build**. The direct link is
  `https://github.com/charles-byte/Tv-android-App/releases/download/latest/tvhome-debug.apk`
  (if the repository is private you must be signed in to GitHub).
- Or open **Actions**, pick the latest **Build APK** run and download the `tvhome-debug-apk` artifact (a zip containing `app-debug.apk`).

All builds are signed with the same debug key (`app/debug.keystore`), so a new APK installs over the old one without uninstalling.

### Building locally (optional)

Needs JDK 17 and the Android SDK (Android Studio installs both).

```sh
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Installing on the TV

You need `adb` on a computer on the same Wi-Fi as the TV. It comes with Android's "SDK Platform Tools" (download from developer.android.com/tools/releases/platform-tools).

1. **Enable Developer Options on the TV.** Settings > System > About (on some TVs: Settings > Device Preferences > About). Select **Build** (or "Android TV OS build") 7 times until it says you are a developer.
2. **Turn on debugging.** Settings > System > Developer options: turn on **USB debugging**, and **Network debugging** / **Wireless debugging** if your TV shows it.
3. **Find the TV's IP address.** Settings > Network & Internet > your Wi-Fi network.
4. **Connect and install** from the computer:

   ```sh
   adb connect TV_IP:5555
   # Accept the "Allow debugging?" prompt on the TV (tick "Always allow").
   adb install -r tvhome-debug.apk
   ```

   On newer Google TV builds with **Wireless debugging**, you may need to pair first: in Wireless debugging choose "Pair device with pairing code", then run `adb pair TV_IP:PAIR_PORT` with the code shown, then `adb connect TV_IP:PORT` using the port shown on that screen.

### Setting it as the default home screen

Try these in order:

1. Press **Home** on the remote. If the TV asks which home app to use, choose **TV Home** and then **Always**.
2. Settings > Apps > Default apps > Home app (only some TVs have this), choose **TV Home**.
3. On Google TV, from the computer:

   ```sh
   adb shell cmd package set-home-activity com.charles.tvhome/.MainActivity
   ```

Some Google TV and Fire TV builds still send Home back to the stock launcher. Step 9 of the build plan adds a fix for that (an accessibility service plus an ADB option). Until then you can open TV Home from the TV's app list.

### Reverting to the stock home screen

Any of these:

- Uninstall: `adb uninstall com.charles.tvhome` (or Settings > Apps > TV Home > Uninstall). The TV falls back to its own launcher.
- Settings > Apps > TV Home > **Clear defaults**, then press Home and choose the stock launcher.
- If you later disable the stock launcher with ADB (step 9), re-enable it with `adb shell pm enable PACKAGE`. Common stock launcher packages: `com.google.android.apps.tv.launcherx` (Google TV), `com.google.android.tvlauncher` (Android TV), `com.amazon.tv.launcher` (Fire TV). Confirm yours with `adb shell pm list packages | grep -i launcher`.

## What to check on the TV (steps 1 and 2)

- [ ] TV Home appears in the TV's app list with its banner, and is offered when you press Home.
- [ ] The screen is dark (`#0B0E13`) with the clock top left, weather and the Settings button top right.
- [ ] Focus starts on the **YouTube** tile.
- [ ] The focused tile grows slightly and shows a dark inner ring plus an amber outer ring.
- [ ] D-pad moves naturally: apps row, up to Continue watching, up again to Settings, down to the sports card and inputs, left and right within rows. Nothing gets stuck.
- [ ] Settings opens Android settings; Back returns to TV Home.
- [ ] Back on the home screen does nothing (the app does not close).
- [ ] Pressing Home while on TV Home puts focus back on the first app tile.
- [ ] Layout fits the screen with no clipped text (try on both 1080p and 4K if you can).

Please also note any spacing or sizing you want changed before step 3.

## Project layout

```
app/src/main/
  AndroidManifest.xml            launcher registration (HOME + LEANBACK_LAUNCHER)
  java/com/charles/tvhome/
    MainActivity.kt              swallows Back, refocuses on Home press
    data/                        models, sample data, LIVE/countdown/date logic
    ui/theme/Theme.kt            colours (accent is one constant), fonts, 1920x1080 scaling
    ui/components/FocusTile.kt   focus scale and double ring
    ui/home/                     header, rows, sports and inputs cards
  res/font/                      Sora and Inter (SIL Open Font License, see assets/licenses)
```

Layout sizes are written in pixels of a 1920x1080 design and scaled to the screen (`dpx` / `spx` in `Theme.kt`), so the screen looks the same on 1080p and 4K panels.

## Privacy

No ads, analytics or tracking. No third-party logos are bundled; app tiles use text labels.
