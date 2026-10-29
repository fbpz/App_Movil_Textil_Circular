## App Móvil Textil Circular

Aplicación móvil Android orientada al registro de emprendimientos textiles y a la captura de fotografías de productos. Este repositorio corresponde a una versión inicial del proyecto; las funcionalidades de economía circular que se incorporen deberán documentarse a medida que se desarrollen.

## Descripción

La aplicación permite ingresar información básica de una emprendedora o marca textil y avanzar a una pantalla desde la que se puede tomar una fotografía con la cámara del dispositivo.

## Funcionalidades actuales

- Formulario para registrar:
  - Nombre de la emprendedora.
  - Nombre comercial o marca.
  - Rubro del emprendimiento.
- Validación de campos obligatorios.
- Persistencia local de los datos mediante Room.
- Navegación entre el formulario y la pantalla de cámara.
- Captura y visualización de una fotografía del producto.

> **Nota:** la asociación permanente de la fotografía con un registro debe verificarse en la versión actual del código. Las funciones de catálogo textil, trazabilidad, clasificación de materiales o gestión de reciclaje no deben considerarse disponibles hasta que estén implementadas.

## Tecnologías

- **Kotlin** — lenguaje de programación.
- **Android Studio** — entorno recomendado de desarrollo.
- **Jetpack Compose** — construcción de la interfaz.
- **Navigation Compose** — navegación entre pantallas.
- **Room** — persistencia local de datos.
- **Coil** — carga y visualización de imágenes.
- **Gradle Kotlin DSL** — configuración y compilación del proyecto.

## Requisitos

- Android Studio compatible con la versión de Android Gradle Plugin y Gradle configuradas en el proyecto.
- JDK compatible con la configuración del proyecto.
- Emulador o dispositivo Android compatible con la versión mínima definida en `app/build.gradle.kts` (API 24 o superior, según la configuración actual).
- Para probar la captura fotográfica, un dispositivo con cámara o un emulador que admita esta función.

## Instalación y ejecución

1. Clona el repositorio:

   ```bash
   git clone https://github.com/fbpz/App_Movil_Textil_Circular.git
   ```

2. Abre **Android Studio**.
3. Selecciona **Open** y elige la carpeta `App_Movil_Textil_Circular`.
4. Espera a que Gradle termine la sincronización de dependencias.
5. Selecciona un emulador o conecta un dispositivo Android con la depuración USB habilitada.
6. Ejecuta la configuración `app` con el botón **Run**.

También puedes compilar el proyecto desde la terminal, usando el wrapper de Gradle incluido en el repositorio:

```bash
./gradlew assembleDebug
```

En Windows:

```bat
gradlew.bat assembleDebug
```

## Estructura general

La organización exacta puede cambiar durante el desarrollo. En la versión actual, los archivos principales incluyen:

```text
app/
└── src/
    └── main/
        ├── java/com/example/textilcircular/
        │   ├── data/
        │   │   ├── AppDatabase.kt
        │   │   └── Emprendedora.kt
        │   ├── view/
        │   │   ├── FormularioScreen.kt
        │   │   └── CameraScreen.kt
        │   └── MainActivity.kt
        └── AndroidManifest.xml
```

- `MainActivity.kt`: punto de entrada de la aplicación.
- `view/FormularioScreen.kt`: pantalla de ingreso de datos.
- `view/CameraScreen.kt`: pantalla asociada a la captura de fotografías.
- `data/Emprendedora.kt`: modelo de datos de la emprendedora.
- `data/AppDatabase.kt`: configuración de la base de datos local Room.
- `AndroidManifest.xml`: configuración general de la aplicación y componentes Android.

## Flujo básico de uso

1. Abrir la aplicación.
2. Completar el nombre, el nombre comercial y el rubro.
3. Seleccionar **Guardar y Continuar**.
4. Acceder a la pantalla de cámara.
5. Seleccionar **Tomar Foto del Producto** y completar la captura.
6. Volver al formulario cuando sea necesario.

Los nombres de botones y el comportamiento exacto pueden variar según la versión del código.

## Datos y privacidad

- Los datos de la entidad `Emprendedora` se almacenan localmente mediante Room.
- La aplicación puede utilizar la cámara para capturar fotografías.
- Antes de publicar una versión de producción, se recomienda documentar expresamente qué información se guarda, dónde se almacenan las imágenes y si existe transmisión de datos a servicios externos.
- No agregues contraseñas, claves, tokens ni otros secretos al repositorio.

## Estado del proyecto

**Estado:** versión inicial / en desarrollo.

Posibles mejoras futuras, sujetas a definición del equipo:

- Asociar y recuperar de forma persistente cada fotografía con su registro.
- Crear un catálogo de productos textiles.
- Registrar materiales, estado, reutilización y destino de reciclaje.
- Incorporar pruebas automatizadas y mensajes de error más completos.
- Definir reglas de negocio para representar la circularidad textil.

Repositorio: https://github.com/fbpz/App_Movil_Textil_Circular
"""
path = Path("/mnt/data/README_App_Movil_Textil_Circular.md")
path.write_text(readme, encoding="utf-8")
print(path)
