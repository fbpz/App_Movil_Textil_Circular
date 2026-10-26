package com.example.textilcircular.domain.model

/** Categorías del Directorio Textil Circular (textilcircular.cl). */
enum class Categoria(val etiqueta: String, val descripcion: String) {
    SEGUNDA_MANO("Segunda mano", "Venta de ropa y textiles usados"),
    ARRIENDO("Arriendo", "Arriendo de vestuario y accesorios"),
    REPARACION("Reparación", "Arreglos, costura y restauración de prendas"),
    APRENDIZAJE("Aprendizaje", "Talleres y cursos de economía circular"),
    SUPRARRECICLAJE("Suprarreciclaje", "Upcycling: nuevos productos desde textiles en desuso");

    companion object {
        fun desdeNombre(nombre: String): Categoria? = entries.firstOrNull { it.name == nombre }
    }
}

/** Las 16 regiones de Chile, de norte a sur. */
val REGIONES_CHILE = listOf(
    "Arica y Parinacota",
    "Tarapacá",
    "Antofagasta",
    "Atacama",
    "Coquimbo",
    "Valparaíso",
    "Metropolitana de Santiago",
    "Libertador General Bernardo O'Higgins",
    "Maule",
    "Ñuble",
    "Biobío",
    "La Araucanía",
    "Los Ríos",
    "Los Lagos",
    "Aysén del General Carlos Ibáñez del Campo",
    "Magallanes y de la Antártica Chilena"
)

/** Tipos de prenda aceptados por el modelo Donación Cuidada. */
val TIPOS_PRENDA = listOf(
    "Ropa de adulto",
    "Ropa infantil",
    "Ropa de cama y frazadas",
    "Calzado",
    "Abrigos y parkas",
    "Retazos y textiles en desuso"
)

/** Estado de las prendas donadas: lo no donable se deriva a economía circular. */
enum class EstadoPrenda(val etiqueta: String, val esDonable: Boolean) {
    BUEN_ESTADO("Buen estado, lista para usar", true),
    REQUIERE_REPARACION("Requiere reparación menor", true),
    NO_DONABLE("No donable (rota, manchada)", false)
}
