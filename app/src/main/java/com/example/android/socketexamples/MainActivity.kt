package com.example.android.socketexamples

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.example.android.socketexamples.http.SecureSocketClient
import com.example.android.socketexamples.location.LocationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var locationService: LocationService
    private var locationEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        broadcast_button.setOnClickListener { startLocationService() }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            LOCATION_SERVICE_PERMISSION -> {
                if(grantResults.isNotEmpty() && grantResults[0] == LOCATION_SERVICE_PERMISSION) {
                    locationEnabled = true
                }
            }
        }
    }

    private fun setupPermissions() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_SERVICE_PERMISSION
            )
        }
        else {
            locationEnabled = true
        }
    }

    /**
     * Assert that the user has granted the fine location permission
     */
    private fun hasLocationPermission() : Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        return permission == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Initialize the location service
     */
    private fun startLocationService() {
        if(locationEnabled) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationService = LocationService(locationManager, SecureSocketClient())
            locationService.beginRequestingLocation()
            locationService.subscribe("Main", { location ->
                events.text = events.text.toString() + "\n" + location.toString()
            })
        }

    }


    companion object {
        const val LOCATION_SERVICE_PERMISSION = 1
    }

}
