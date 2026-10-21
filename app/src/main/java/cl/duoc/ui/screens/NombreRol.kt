package cl.duoc.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cl.duoc.R
import cl.duoc.model.RolCodigo

@Composable
fun nombreDeRol(rolCodigo: String): String = stringResource(
    when (rolCodigo) {
        RolCodigo.USUARIO_FINAL -> R.string.rol_usuario_final
        RolCodigo.AGENTE -> R.string.rol_agente
        else -> R.string.rol_ejecutivo
    }
)