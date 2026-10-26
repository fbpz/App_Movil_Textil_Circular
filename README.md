# Textil Circular — App móvil

Aplicación Android (Kotlin + Jetpack Compose + Material 3) basada en la **Fundación Textil Circular**
([textilcircular.cl](https://textilcircular.cl)), que promueve la economía circular textil en Chile.

La app digitaliza dos líneas de trabajo de la fundación:

- **Directorio Textil Circular**: registro y búsqueda de emprendimientos de economía circular
  (segunda mano, arriendo, reparación, aprendizaje y suprarreciclaje) en las 16 regiones de Chile.
- **Donación Cuidada**: registro de donaciones de prendas con su estado. Lo que no es donable se deriva a reciclaje.

> Asignatura DSY1105 — Desarrollo de Aplicaciones Móviles · Evaluación Parcial 2

## Integrantes

| Nombre | Rol principal |
|---|---|
| _Nombre Apellido 1_ | _UI / Navegación / Animaciones_ |
| _Nombre Apellido 2_ | _Datos / ViewModels / Recursos nativos_ |

Equipo N° _XX_ · Planificación en Trello: _enlace al tablero_

## Funcionalidades implementadas

| Requisito | Implementación |
|---|---|
| **Diseño visual Material 3** | Tema propio con la paleta de textilcircular.cl (cyan `#05C9CD`, amarillo `#FFDF01`, degradado `#111 → #1A3A3A → #0A2A2B`), fuentes de la web (Big Shoulders Display + Nunito), modo claro y oscuro |
| **Formularios validados** | Login, Registro de usuario, Registro/edición de emprendimiento y Donación cuidada. Cada campo tiene ícono, ✓/⚠ de estado, mensaje de error animado y texto de ayuda |
| **Validaciones desacopladas** | `domain/validation/Validadores.kt` (Kotlin puro, sin Android) + `CampoFormulario`. Los ViewModels validan y la UI solo muestra el estado. Hay 10 tests unitarios (`ValidadoresTest`) |
| **Navegación funcional** | Navigation Compose: Splash → Login/Registro → pestañas (Inicio, Directorio, Donar, Perfil) → Detalle / Formulario, con barra inferior y back stack controlado |
| **Gestión de estado** | `StateFlow` + `UiState` inmutable por pantalla, `collectAsStateWithLifecycle`, eventos de una sola vez con `Channel` |
| **Almacenamiento local** | **Room** (usuarios, emprendedoras, donaciones) + **DataStore** (sesión activa y último correo usado). Las fotos se guardan en el almacenamiento interno |
| **Recursos nativos** | **Cámara** (`TakePicture` + `FileProvider` + permiso en tiempo de ejecución), **GPS** (`LocationManager` + `Geocoder` para autocompletar comuna/región), **Galería** (Photo Picker), **vibración háptica**, e intents a **teléfono, correo, mapas y compartir** |
| **Animaciones** | Splash animado, transiciones entre pantallas, contadores de impacto, entrada escalonada, "sacudida" del formulario con errores, íconos de validación, botón con estado de carga, corazón de favorito con rebote, pulso del GPS, barra de seguridad de contraseña, `animateItem` en listas |

## Arquitectura (MVVM)

```
app/src/main/java/com/example/textilcircular/
├── TextilCircularApp.kt        # Application: crea el AppContainer
├── MainActivity.kt
├── di/AppContainer.kt          # Inyección de dependencias manual
├── data/
│   ├── local/                  # Room: AppDatabase, entity/, dao/
│   ├── preferences/            # DataStore: SessionManager
│   └── repository/             # AuthRepository, EmprendedoraRepository, DonacionRepository
├── domain/
│   ├── model/                  # Categorías, regiones, tipos y estados de prenda
│   ├── validation/             # Validadores + CampoFormulario (lógica pura)
│   └── security/               # PasswordHasher (SHA-256 con sal)
├── util/                       # ImageStorage (cámara/galería), LocationProvider (GPS)
├── viewmodel/                  # Un ViewModel por pantalla + AppViewModelProvider
└── ui/
    ├── theme/                  # Colores, tipografía y formas de la marca
    ├── components/             # CampoValidado, CampoSelector, BotonPrincipal, SelectorFoto, TarjetaUbicacion…
    ├── navigation/             # AppNavHost y rutas
    └── screens/                # splash, auth, home, directorio, emprendedora, donacion, perfil
```

Flujo: **UI (Composable) → evento → ViewModel → Repository → Room/DataStore → Flow → StateFlow → UI**.

## Cómo ejecutar

1. Clonar el repositorio:
   ```bash
   git clone <url-del-repositorio>
   ```
2. Abrir la carpeta en **Android Studio** (versión compatible con AGP 9.4 y JDK 17+).
3. Esperar la sincronización de Gradle (descarga Room, KSP, Navigation, DataStore y Coil).
4. Ejecutar en un emulador o dispositivo con **Android 7.0 (API 24) o superior** usando ▶ *Run 'app'*.
5. Crear una cuenta en *Regístrate* (ej. RUT de prueba `11.111.111-1`, contraseña `Textil2025`).

Pruebas unitarias:

```bash
./gradlew testDebugUnitTest
```

### Probar los recursos nativos en el emulador

- **Cámara**: el emulador usa una escena virtual. Al presionar *Cámara* se pide el permiso.
- **GPS**: en *Extended controls (⋯) → Location* fija una ubicación (ej. Santiago) y presiona *Usar* en la tarjeta de ubicación.

## Tecnologías

Kotlin 2.2 · Jetpack Compose (BOM 2026.02) · Material 3 · Navigation Compose 2.9 · Lifecycle/ViewModel 2.9 ·
Room 2.8 (KSP) · DataStore Preferences 1.1 · Coil 2.7 · JUnit 4
