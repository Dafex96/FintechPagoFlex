package cl.duoc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cl.duoc.navigation.AppNavegacion
import cl.duoc.notificaciones.AvisosScheduler
import cl.duoc.notificaciones.NotificacionesHelper
import cl.duoc.ui.theme.PagoFlexTheme

class MainActivity : ComponentActivity() {

    private var destinoNotificacion by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificacionesHelper.crearCanales(this)
        AvisosScheduler.programarPeriodico(this)
        destinoNotificacion = intent?.getStringExtra(NotificacionesHelper.EXTRA_DESTINO)

        setContent {
            PagoFlexTheme {
                AppNavegacion(
                    destinoNotificacion = destinoNotificacion,
                    onDestinoAtendido = {
                        destinoNotificacion = null
                        intent?.removeExtra(NotificacionesHelper.EXTRA_DESTINO)
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        destinoNotificacion = intent.getStringExtra(NotificacionesHelper.EXTRA_DESTINO)
    }
}