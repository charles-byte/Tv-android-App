package com.charles.tvhome.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import java.util.Locale

/** A shelf tile matched against what is installed on this TV. */
data class ResolvedApp(
    val tile: AppTile,
    /** Null when no matching app is installed. */
    val packageName: String?,
    /** The app's own TV banner (16:9), if it has one. */
    val banner: ImageBitmap?,
    /** Fallback when there is no banner. */
    val icon: ImageBitmap?,
) {
    val installed: Boolean get() = packageName != null
}

/**
 * Finds the shelf apps on the device. Artwork comes from the installed apps at runtime,
 * so no third-party logos are bundled. Call off the main thread: loading banners is slow.
 */
object InstalledApps {
    private const val TAG = "TvHome"

    fun resolve(context: Context, tiles: List<AppTile>): List<ResolvedApp> {
        val pm = context.packageManager
        val launchable = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER),
            0,
        ).filter { it.activityInfo.packageName != context.packageName }

        return tiles.map { tile ->
            val match = findMatch(pm, tile, launchable)
            if (match == null) {
                Log.i(TAG, "${tile.label}: not installed")
                ResolvedApp(tile, null, null, null)
            } else {
                val pkg = match.activityInfo.packageName
                Log.i(TAG, "${tile.label}: $pkg")
                ResolvedApp(
                    tile = tile,
                    packageName = pkg,
                    banner = (match.activityInfo.loadBanner(pm) ?: runCatching { pm.getApplicationBanner(pkg) }.getOrNull())
                        ?.toImage(640, 360),
                    icon = match.activityInfo.loadIcon(pm)?.toImage(192, 192),
                )
            }
        }
    }

    /** Known package names first, then the app's label (e.g. "DStv"), exact before prefix. */
    private fun findMatch(pm: PackageManager, tile: AppTile, apps: List<ResolveInfo>): ResolveInfo? {
        tile.packages.forEach { pkg ->
            apps.firstOrNull { it.activityInfo.packageName == pkg }?.let { return it }
        }
        val labelled = apps.map { it to normalise(it.loadLabel(pm).toString()) }
        val key = normalise(tile.label)
        return labelled.firstOrNull { it.second == key }?.first
            ?: labelled.firstOrNull { it.second.startsWith(key) }?.first
    }

    fun launch(context: Context, packageName: String): Boolean {
        val pm = context.packageManager
        val intent = pm.getLeanbackLaunchIntentForPackage(packageName) ?: pm.getLaunchIntentForPackage(packageName)
            ?: return false
        return try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Cannot open $packageName", e)
            false
        } catch (e: SecurityException) {
            Log.w(TAG, "Cannot open $packageName", e)
            false
        }
    }

    private fun normalise(s: String) = s.lowercase(Locale.ROOT).filter { it.isLetterOrDigit() }

    private fun Drawable.toImage(w: Int, h: Int): ImageBitmap? =
        runCatching { toBitmap(w, h).asImageBitmap() }.getOrNull()
}
