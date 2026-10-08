package com.sipe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sipe.app.ui.nav.AppNav
import com.sipe.app.ui.theme.SipeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as SipeApplication
        setContent {
            val dark by app.session.dark.collectAsState(initial = false)
            SipeTheme(dark = dark) {
                // La app siempre pide login al abrir (decisión de producto:
                // la sesión se usa para el saludo, no para omitir el acceso).
                AppNav(app, startLogged = false)
            }
        }
    }
}
