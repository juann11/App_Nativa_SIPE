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
            val logged by app.session.logged.collectAsState(initial = false)
            SipeTheme(dark = dark) {
                AppNav(app, startLogged = logged)
            }
        }
    }
}
