package com.ashfaque.demopoi.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ashfaque.demopoi.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.ashfaque.demopoi.R
import com.ashfaque.demopoi.Utils
import com.ashfaque.demopoi.adapter.MyAdapter
import com.ashfaque.demopoi.databinding.FragmentPoiMapBinding
import com.ashfaque.demopoi.roomdb.DataBaseName
import com.ashfaque.demopoi.roomdb.EntityDataClass
import com.ashfaque.demopoi.shared_preference.SharedPrefConstants
import com.ashfaque.demopoi.shared_preference.SharedPreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PoiMapFragment : Fragment(), OnMapReadyCallback {

    private var mBinding: FragmentPoiMapBinding? = null

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var radiusInMeters = 10.0f // 1f = 1 meter

    val poiMarkers = mutableListOf<Marker>()
    private var isFirst: Boolean = true
    private var currentMarker: Marker? = null

    private lateinit var dataBase: DataBaseName
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    override fun onResume() {
        super.onResume()

        if (!Utils.isLocationEnabled(requireContext())) {
            Utils.showToast(requireContext(), "Location is disabled, redirecting to settings...")
            openLocationSettings()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentPoiMapBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sharedPreferenceManager = SharedPreferenceManager.getInstance(requireActivity())

        radiusInMeters=sharedPreferenceManager.getFloat(SharedPrefConstants.RADIUS_IN_METER)

//        sharedPreferenceManager.saveString("username", "JohnDoe")
//        val username = sharedPreferenceManager.getString("username")
//        Utils.showToast(requireContext(),"Username: $username")
//        sharedPreferenceManager.saveInt("userAge", 25)
//        val userAge = sharedPreferenceManager.getInt("userAge")
//        sharedPreferenceManager.saveBoolean("isLoggedIn", true)
//        val isLoggedIn = sharedPreferenceManager.getBoolean("isLoggedIn")
//        sharedPreferenceManager.clearAll()



        dataBase = DataBaseName.getDataBase(requireContext())
        return mBinding!!.root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        Utils.logDebug("Map is ready")
        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
            moveToCurrentLocation()
        } else {
            checkLocationPermission()
        }
    }

    private fun moveToCurrentLocation() {
        Utils.logDebug("moveToCurrentLocation")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
                        mMap.addMarker(
                            MarkerOptions().position(currentLatLng).title("You are here")
                        )

                        mMap.addCircle(
                            CircleOptions()
                                .center(currentLatLng)
                                .radius(radiusInMeters.toDouble())
                                .strokeColor(0xFFa9ed9b.toInt())
                                .fillColor(0xFFd4f6cd.toInt())
                                .strokeWidth(2f)
                        )
                        loadPOIData()
                        setupMapClickListener(location.latitude, location.longitude, radiusInMeters)
                    }
                }
        }
    }

    private fun loadPOIData() {
        dataBase.interfaceDao().getAllRecord().observe(requireActivity()) {

            it.forEach { latLngPOI ->
                    markerOption("POI:- ${latLngPOI.lat}, ${latLngPOI.lng}",
                        "TITLE:- ${latLngPOI.title.uppercase()}\n" +
                                "OWNER NAME:- ${latLngPOI.ownerName}\n"+
                                "LOCATION NAME:- ${latLngPOI.locationName}\n"+
                                "ESTABLISHED Date:- ${latLngPOI.establishedDate}\n",
                        LatLng(latLngPOI.lat.toDouble(),latLngPOI.lng.toDouble()),
                        BitmapDescriptorFactory.HUE_ORANGE,true)

            }
        }
    }

    private fun markerOption(title:String,snippet:String,latLngPOI: LatLng, pinColor: Float,isEdit:Boolean) {
        if (pinColor == BitmapDescriptorFactory.HUE_GREEN) {
            if (!isFirst) {
                currentMarker?.remove()
            }
            isFirst = false
        }

        currentMarker = mMap.addMarker(
            MarkerOptions().position(latLngPOI)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(pinColor))
        )

        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                }

                val title = TextView(requireContext()).apply {
                    setTextColor(Color.BLACK)
                    gravity = Gravity.CENTER
                    setTypeface(null, Typeface.BOLD)
                    text = marker.title
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(16, 8, 16, 8) // Margin around the title
                    }
                }

                val snippet = TextView(requireContext()).apply {
                    setTextColor(Color.GRAY)
                    text = marker.snippet
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(16, 8, 16, 8) // Margin around the snippet
                    }
                }

                val button = TextView(requireContext()).apply {
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    setTypeface(null, Typeface.BOLD)
                    text = "Save Pin"
                    setBackgroundResource(R.drawable.circle_border_background) // Circular border and background
                    setPadding(32, 16, 32, 16) // Padding inside the button for a better appearance
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(16, 16, 16, 16) // Margin around the button
                    }

                }

                info.addView(title)
                info.addView(snippet)
                if(!isEdit)
                {
                    info.addView(button)
                }
                return info
            }

        })

        if(!isEdit)
        {
            mMap.setOnInfoWindowClickListener { marker ->

                val entityDataClass= EntityDataClass(
                    0,"","","","", "",
                    latLngPOI.latitude,latLngPOI.longitude, "",
                )

                val dialogFragment = SavePinDialogFragment(entityDataClass,false)
                {
                    Utils.logDebug("SavePinDialogFragment was dismissed")
                    //Reload the screen
                    val fragmentManager = requireActivity().supportFragmentManager
                    val newFragment = PoiMapFragment()
                    fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, newFragment)
                        .addToBackStack(null)
                        .commit()

                }
                dialogFragment.show(parentFragmentManager, "SavePinDialog")
            }
        }

        currentMarker?.let { poiMarkers.add(it) }
    }

    private fun setupMapClickListener(latitude: Double, longitude: Double, radiusInMeters: Float) {
        mMap.setOnMapClickListener { latLngPOI ->
            val markerTitle = "Lat: ${latLngPOI.latitude}, Lng: ${latLngPOI.longitude}"

            val isWithinRadius = Utils.arePointsWithinRadius(latitude, longitude,
                latLngPOI.latitude, latLngPOI.longitude, radiusInMeters)

            if (isWithinRadius) {
                markerOption("POI ${latLngPOI.latitude},\n${latLngPOI.longitude}",
                    "\"My New POI",
                    latLngPOI, BitmapDescriptorFactory.HUE_GREEN,false)

                val currentZoomLevel = mMap.cameraPosition.zoom
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngPOI, currentZoomLevel)
                mMap.animateCamera(cameraUpdate)

                Toast.makeText(
                    requireContext(),
                    "Clicked location: $markerTitle",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showSimpleDialog()
            }
        }
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun enableMyLocation() {
        Utils.logDebug("enableMyLocation")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        }
    }

    private fun checkLocationPermission() {

        Utils.logDebug("checkLocationPermission")
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun showSimpleDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("You are outside the $radiusInMeters meters radius.")

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog when "OK" is clicked
        }

        val dialog = builder.create()
        dialog.show()
    }

}