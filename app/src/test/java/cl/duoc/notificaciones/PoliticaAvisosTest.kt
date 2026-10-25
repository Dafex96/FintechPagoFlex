package cl.duoc.notificaciones

import cl.duoc.model.Compromiso
import cl.duoc.model.EstadoCompromisoId
import cl.duoc.notificaciones.PoliticaAvisos.TipoAviso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PoliticaAvisosTest {

    private fun compromiso(estado: Int) = Compromiso(
        id = 1, folio = "F-1", idUsuarioFinal = 1, idEmpresa = 1, empresaNombre = "Empresa",
        concepto = "Cuota", monto = 10000, fechaVencimiento = "2026-10-30",
        idEstadoCompromiso = estado.toLong(), estadoNombre = ""
    )

    @Test
    fun noSeAvisaEntreLas22YLas08() {
        assertTrue(PoliticaAvisos.enHorarioSilencioso(22))
        assertTrue(PoliticaAvisos.enHorarioSilencioso(23))
        assertTrue(PoliticaAvisos.enHorarioSilencioso(0))
        assertTrue(PoliticaAvisos.enHorarioSilencioso(7))
        assertFalse(PoliticaAvisos.enHorarioSilencioso(8))
        assertFalse(PoliticaAvisos.enHorarioSilencioso(21))
    }

    @Test
    fun avisoPrevioDentroDeLaAnticipacionElegida() {
        val pendiente = compromiso(EstadoCompromisoId.PENDIENTE)
        assertEquals(TipoAviso.PREVIO, PoliticaAvisos.tipoDeAviso(pendiente, 3, 3))
        assertEquals(TipoAviso.PREVIO, PoliticaAvisos.tipoDeAviso(pendiente, 3, 1))
        assertNull(PoliticaAvisos.tipoDeAviso(pendiente, 3, 4))
        assertNull(PoliticaAvisos.tipoDeAviso(pendiente, 1, 2))
    }

    @Test
    fun avisoElMismoDiaDelVencimiento() {
        assertEquals(TipoAviso.HOY, PoliticaAvisos.tipoDeAviso(compromiso(EstadoCompromisoId.PENDIENTE), 3, 0))
    }

    @Test
    fun avisoDeVencidoSinImportarLaAnticipacion() {
        assertEquals(TipoAviso.VENCIDO, PoliticaAvisos.tipoDeAviso(compromiso(EstadoCompromisoId.VENCIDO), 1, -4))
    }

    @Test
    fun noSeAvisaDeCompromisosPagadosAnuladosOEnRevision() {
        listOf(EstadoCompromisoId.PAGADO, EstadoCompromisoId.ANULADO, EstadoCompromisoId.EN_REVISION).forEach {
            assertNull(PoliticaAvisos.tipoDeAviso(compromiso(it), 5, 0))
            assertNull(PoliticaAvisos.tipoDeAviso(compromiso(it), 5, 2))
        }
    }
}
