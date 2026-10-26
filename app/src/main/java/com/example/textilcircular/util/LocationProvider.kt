package com.example.textilcircular.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class Ubicacion(val latitud: Double, val longitud: Double, val comuna: String?, val region: String?)

sealed interface ResultadoUbicacion {
    data class Exito(val ubicacion: Ubicacion) : ResultadoUbicacion
    data object SinPermiso : ResultadoUbicacion
    data object GpsDesactivado : ResultadoUbicacion
    data object NoDisponible : ResultadoUbicacion
}

/**
 * Acceso al GPS del dispositivo mediante LocationManager (sin depender de Google Play Services).
 * Verifica el permiso antes de cada lectura y aplica un tiempo máximo de espera.
 */
class LocationProvider(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun tienePermiso(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    suspend fun obtenerUbicacion(): ResultadoUbicacion {
        if (!tienePermiso()) return ResultadoUbicacion.SinPermiso
        val proveedores = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { runCatching { locationManager.isProviderEnabled(it) }.getOrDefault(false) }
        if (proveedores.isEmpty()) return ResultadoUbicacion.GpsDesactivado

        val location = withTimeoutOrNull(15_000) { ubicacionActual(proveedores.last()) }
            ?: ultimaConocida(proveedores)
            ?: return ResultadoUbicacion.NoDisponible

        val (comuna, region) = geocodificar(location)
        return ResultadoUbicacion.Exito(Ubicacion(location.latitude, location.longitude, comuna, region))
    }

    @SuppressLint("MissingPermission") // verificado en tienePermiso()
    private fun ultimaConocida(proveedores: List<String>): Location? =
        proveedores.mapNotNull { runCatching { locationManager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }

    @SuppressLint("MissingPermission")
    private suspend fun ubicacionActual(proveedor: String): Location? =
        suspendCancellableCoroutine { cont ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val cancelacion = android.os.CancellationSignal()
                locationManager.getCurrentLocation(
                    proveedor, cancelacion, ContextCompat.getMainExecutor(context)
                ) { location -> if (cont.isActive) cont.resume(location) }
                cont.invokeOnCancellation { cancelacion.cancel() }
            } else {
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        locationManager.removeUpdates(this)
                        if (cont.isActive) cont.resume(location)
                    }
                }
                @Suppress("DEPRECATION")
                locationManager.requestSingleUpdate(proveedor, listener, Looper.getMainLooper())
                cont.invokeOnCancellation { locationManager.removeUpdates(listener) }
            }
        }

    /** Traduce coordenadas a comuna/región. Si no hay red o falla, devuelve nulos. */
    private suspend fun geocodificar(location: Location): Pair<String?, String?> {
        if (!Geocoder.isPresent()) return null to null
        val geocoder = Geocoder(context, Locale.Builder().setLanguage("es").setRegion("CL").build())
        return runCatching {
            val direccion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                withTimeoutOrNull(5_000) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) { lista ->
                            if (cont.isActive) cont.resume(lista.firstOrNull())
                        }
                    }
                }
            } else {
                withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
                }
            }
            (direccion?.locality ?: direccion?.subAdminArea) to direccion?.adminArea
        }.getOrDefault(null to null)
    }
}
