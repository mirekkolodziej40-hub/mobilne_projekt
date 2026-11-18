package pl.uczelnia.projekt

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView

private fun Context.znajdzActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.znajdzActivity()
    else -> null
}

@Composable
actual fun PaskiSystemowe(ciemny: Boolean) {
    val widok = LocalView.current
    if (!widok.isInEditMode) {
        SideEffect {
            val okno = widok.context.znajdzActivity()?.window ?: return@SideEffect
            // jasne ikony na ciemnym motywie i odwrotnie
            if (Build.VERSION.SDK_INT >= 30) {
                val maska = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                okno.insetsController?.setSystemBarsAppearance(if (ciemny) 0 else maska, maska)
            } else {
                @Suppress("DEPRECATION")
                var flagi = okno.decorView.systemUiVisibility
                @Suppress("DEPRECATION")
                flagi = if (ciemny) flagi and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                else flagi or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                @Suppress("DEPRECATION")
                okno.decorView.systemUiVisibility = flagi
            }
        }
    }
}
