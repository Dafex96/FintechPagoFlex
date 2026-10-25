package cl.duoc.ui.components

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cl.duoc.R
import cl.duoc.permisos.Permisos
import cl.duoc.ui.styles.EstilosTarjeta
import cl.duoc.ui.theme.Dimens

/**
 * Pide el permiso de notificaciones (obligatorio desde Android 13) explicando antes para qué sirve.
 * No ocupa espacio si el permiso ya está concedido, si el teléfono es anterior a Android 13 o si
 * la persona eligió "Ahora no".
 */
@Composable
fun SolicitudPermisoNotificaciones(modifier: Modifier = Modifier) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    var concedido by remember { mutableStateOf(Permisos.tiene(context, Permisos.NOTIFICACIONES)) }
    var descartado by rememberSaveable { mutableStateOf(false) }
    var rechazado by rememberSaveable { mutableStateOf(false) }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { otorgado ->
        concedido = otorgado
        if (!otorgado) rechazado = true
    }

    if (concedido || descartado) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = EstilosTarjeta.forma,
        colors = EstilosTarjeta.coloresDestacada()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            Text(
                text = stringResource(R.string.permiso_notif_titulo),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(
                    if (rechazado) R.string.permiso_notif_denegado else R.string.permiso_notif_explicacion
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            if (rechazado) {
                BotonPrincipal(
                    texto = stringResource(R.string.permiso_notif_btn_ajustes),
                    onClick = {
                        Permisos.abrirAjustes(context)
                        descartado = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                BotonPrincipal(
                    texto = stringResource(R.string.permiso_notif_btn_permitir),
                    onClick = { pedirPermiso.launch(Permisos.NOTIFICACIONES) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            TextButton(
                onClick = { descartado = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.permiso_notif_btn_ahora_no))
            }
        }
    }
}
