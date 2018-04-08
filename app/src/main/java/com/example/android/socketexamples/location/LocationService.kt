package com.example.android.socketexamples.location

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.example.android.socketexamples.http.SecureSocketClient
import org.json.JSONObject


/**
 * Created by rbmenke on 4/8/18.
 */
class LocationService(
    private val locationManager: LocationManager,
    private val socket : SecureSocketClient
) : LocationListener {



    private val subscribers = hashMapOf<String, (Location) -> Unit>()


    fun beginRequestingLocation() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000L,
                0F,
                this
            )
        }
        catch (e : SecurityException) {
            Log.e("xxx", e.message)
        }
    }

    fun subscribe(name : String, subscriber : (Location) -> Unit) {
        subscribers[name] = subscriber
    }

    override fun onLocationChanged(location: Location?) {
        location?.let { unwrappedLocation ->
            Log.d("xxx location", unwrappedLocation.toString())
            socket.sendString(serializeLocation(unwrappedLocation).toString())
            subscribers.forEach { it.value(unwrappedLocation) }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {
        Log.d("xxx provider enabled", provider)
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d("xxx provider disabled", provider)
    }

    /**
     * JSON encode relevant fields from the Location instance
     */
    private fun serializeLocation(location: Location) : JSONObject {
        return JSONObject(hashMapOf(
            "latitude" to location.latitude.toString(),
            "longitude" to location.longitude.toString(),
            "altitude" to location.altitude.toString(),
            "bearing" to location.bearing.toString(),
            "accuracy" to location.accuracy.toString()
        ))
    }

}