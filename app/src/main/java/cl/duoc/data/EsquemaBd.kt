package cl.duoc.data

object EsquemaBd {

    val TABLAS: List<String> = listOf(
        "rol",
        "comuna",
        "area",
        "rubro",
        "estado_compromiso",
        "motivo_anulacion",
        "canal",
        "medio_pago",
        "motivo_reporte",
        "estado_reporte",
        "estado_recomendacion",
        "reconocimiento",
        "empresa_cliente",
        "usuario",
        "compromiso",
        "anulacion",
        "pago",
        "reporte",
        "recomendacion",
        "contacto_recomendacion",
        "historial_recomendacion",
        "usuario_reconocimiento"
    )

    val CREAR_TABLAS: List<String> = listOf(
        """CREATE TABLE rol (
    id_rol INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT NOT NULL UNIQUE,
    nombre TEXT NOT NULL,
    descripcion TEXT
)""",
        """CREATE TABLE comuna (
    id_comuna INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE area (
    id_area INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE rubro (
    id_rubro INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE estado_compromiso (
    id_estado_compromiso INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE motivo_anulacion (
    id_motivo_anulacion INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE canal (
    id_canal INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE medio_pago (
    id_medio_pago INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE motivo_reporte (
    id_motivo_reporte INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE estado_reporte (
    id_estado_reporte INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
)""",
        """CREATE TABLE estado_recomendacion (
    id_estado_recomendacion INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    orden INTEGER NOT NULL
)""",
        """CREATE TABLE reconocimiento (
    id_reconocimiento INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT NOT NULL
)""",
        """CREATE TABLE empresa_cliente (
    id_empresa INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT NOT NULL UNIQUE,
    razon_social TEXT NOT NULL,
    nombre_fantasia TEXT NOT NULL,
    rut TEXT NOT NULL UNIQUE,
    id_rubro INTEGER NOT NULL REFERENCES rubro(id_rubro),
    correo_atencion TEXT NOT NULL,
    telefono_atencion TEXT NOT NULL,
    recargo_atraso INTEGER NOT NULL CHECK (recargo_atraso >= 0),
    activa INTEGER NOT NULL DEFAULT 1
)""",
        """CREATE TABLE usuario (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    id_rol INTEGER NOT NULL REFERENCES rol(id_rol),
    rut TEXT NOT NULL UNIQUE,
    nombres TEXT NOT NULL,
    apellidos TEXT NOT NULL,
    correo TEXT NOT NULL UNIQUE,
    contrasena_hash TEXT NOT NULL,
    telefono TEXT,
    activo INTEGER NOT NULL DEFAULT 1,
    fecha_creacion TEXT NOT NULL,
    id_comuna INTEGER REFERENCES comuna(id_comuna),
    fecha_incorporacion TEXT,
    avisos_activos INTEGER,
    dias_anticipacion INTEGER CHECK (dias_anticipacion IS NULL OR dias_anticipacion IN (1, 3, 5)),
    id_area INTEGER REFERENCES area(id_area),
    id_empresa INTEGER REFERENCES empresa_cliente(id_empresa),
    cargo TEXT
)""",
        """CREATE TABLE compromiso (
    id_compromiso INTEGER PRIMARY KEY AUTOINCREMENT,
    folio TEXT NOT NULL UNIQUE,
    id_usuario_final INTEGER NOT NULL REFERENCES usuario(id_usuario),
    id_empresa INTEGER NOT NULL REFERENCES empresa_cliente(id_empresa),
    concepto TEXT NOT NULL,
    numero_cuota INTEGER,
    total_cuotas INTEGER,
    monto INTEGER NOT NULL CHECK (monto > 0),
    fecha_vencimiento TEXT NOT NULL,
    id_estado_compromiso INTEGER NOT NULL REFERENCES estado_compromiso(id_estado_compromiso),
    recargo_aplicado INTEGER NOT NULL DEFAULT 0 CHECK (recargo_aplicado >= 0),
    fecha_cambio_estado TEXT NOT NULL,
    id_ejecutivo_registro INTEGER REFERENCES usuario(id_usuario),
    fecha_registro TEXT NOT NULL,
    UNIQUE (id_usuario_final, id_empresa, concepto, numero_cuota),
    CHECK (numero_cuota IS NULL OR total_cuotas IS NULL OR numero_cuota <= total_cuotas)
)""",
        """CREATE TABLE anulacion (
    id_compromiso INTEGER PRIMARY KEY REFERENCES compromiso(id_compromiso),
    id_ejecutivo INTEGER NOT NULL REFERENCES usuario(id_usuario),
    id_motivo_anulacion INTEGER NOT NULL REFERENCES motivo_anulacion(id_motivo_anulacion),
    comentario TEXT,
    fecha TEXT NOT NULL
)""",
        """CREATE TABLE pago (
    id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
    numero_comprobante TEXT NOT NULL UNIQUE,
    id_compromiso INTEGER NOT NULL UNIQUE REFERENCES compromiso(id_compromiso),
    fecha_hora TEXT NOT NULL,
    monto_pagado INTEGER NOT NULL CHECK (monto_pagado > 0),
    id_canal INTEGER NOT NULL REFERENCES canal(id_canal),
    id_medio_pago INTEGER NOT NULL REFERENCES medio_pago(id_medio_pago)
)""",
        """CREATE TABLE reporte (
    id_reporte INTEGER PRIMARY KEY AUTOINCREMENT,
    numero TEXT NOT NULL UNIQUE,
    id_compromiso INTEGER NOT NULL REFERENCES compromiso(id_compromiso),
    id_motivo_reporte INTEGER NOT NULL REFERENCES motivo_reporte(id_motivo_reporte),
    comentario TEXT,
    fecha_creacion TEXT NOT NULL,
    id_estado_reporte INTEGER NOT NULL REFERENCES estado_reporte(id_estado_reporte),
    id_agente INTEGER REFERENCES usuario(id_usuario),
    fecha_toma TEXT,
    respuesta TEXT,
    fecha_resolucion TEXT
)""",
        """CREATE TABLE recomendacion (
    id_recomendacion INTEGER PRIMARY KEY AUTOINCREMENT,
    numero TEXT NOT NULL UNIQUE,
    id_usuario_final INTEGER NOT NULL REFERENCES usuario(id_usuario),
    nombre_organizacion TEXT NOT NULL,
    rut_organizacion TEXT,
    id_rubro INTEGER NOT NULL REFERENCES rubro(id_rubro),
    id_comuna INTEGER NOT NULL REFERENCES comuna(id_comuna),
    fecha TEXT NOT NULL,
    id_estado_recomendacion INTEGER NOT NULL REFERENCES estado_recomendacion(id_estado_recomendacion),
    UNIQUE (id_usuario_final, nombre_organizacion)
)""",
        """CREATE TABLE contacto_recomendacion (
    id_recomendacion INTEGER PRIMARY KEY REFERENCES recomendacion(id_recomendacion),
    nombre TEXT NOT NULL,
    correo TEXT,
    telefono TEXT,
    autoriza_contacto INTEGER NOT NULL CHECK (autoriza_contacto = 1)
)""",
        """CREATE TABLE historial_recomendacion (
    id_historial INTEGER PRIMARY KEY AUTOINCREMENT,
    id_recomendacion INTEGER NOT NULL REFERENCES recomendacion(id_recomendacion),
    id_estado_recomendacion INTEGER NOT NULL REFERENCES estado_recomendacion(id_estado_recomendacion),
    id_agente INTEGER NOT NULL REFERENCES usuario(id_usuario),
    fecha TEXT NOT NULL,
    motivo TEXT
)""",
        """CREATE TABLE usuario_reconocimiento (
    id_usuario_final INTEGER NOT NULL REFERENCES usuario(id_usuario),
    id_reconocimiento INTEGER NOT NULL REFERENCES reconocimiento(id_reconocimiento),
    fecha_obtencion TEXT NOT NULL,
    motivo TEXT NOT NULL,
    PRIMARY KEY (id_usuario_final, id_reconocimiento)
)"""
    )
}