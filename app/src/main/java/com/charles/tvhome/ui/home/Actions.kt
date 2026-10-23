package com.charles.tvhome.ui.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

fun openSystemSettings(context: Context) {
    try {
        context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: ActivityNotFoundException) {
        showMessage(context, "Settings are not available on this device")
    }
}

fun showMessage(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

/** Used by tiles whose behaviour lands in a later build step. */
fun showComingSoon(context: Context, what: String) {
    showMessage(context, "$what: coming in a later build")
}
