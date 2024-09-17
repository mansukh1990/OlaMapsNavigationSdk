package com.example.olanavigationnew

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olanavigationnew.adapter.OlaSearchAutoCompleteAdapter
import com.example.olanavigationnew.databinding.ActivityMainBinding
import com.example.olanavigationnew.model.Prediction
import com.example.olanavigationnew.util.DataConstants.API_KEY
import com.example.olanavigationnew.util.DataConstants.MAP_BASE_URL
import com.example.olanavigationnew.util.DataConstants.ORGANIZATION_ID
import com.example.olanavigationnew.viewmodel.MapsViewModel
import com.example.olanavigationnew.viewmodel.OlaSearchAutoCompleteViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.mapboxsdk.geometry.LatLng
import com.ola.maps.mapslibrary.models.OlaLatLng
import com.ola.maps.mapslibrary.models.OlaMapsConfig
import com.ola.maps.mapslibrary.models.OlaMarkerOptions
import com.ola.maps.mapslibrary.utils.MapTileStyle
import com.ola.maps.navigation.ui.v5.MapStatusCallback
import com.ola.maps.navigation.v5.model.route.RouteInfoData
import com.ola.maps.navigation.v5.navigation.NavigationMapRoute
import com.ola.maps.navigation.v5.navigation.OlaMapView
import com.ola.maps.navigation.v5.navigation.direction.transform


class MainActivity : AppCompatActivity(), MapStatusCallback,
    OlaSearchAutoCompleteAdapter.OnAddressClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var olaMapView: OlaMapView

    private var isMapReady = false
    val isAnimRequired = true

    // private lateinit var olaMapView: OlaMapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: MapsViewModel by viewModels()
    private val autoCompleteViewModel: OlaSearchAutoCompleteViewModel by viewModels()
    private lateinit var currentLatLng: LatLng
    private var mCurrentLatLong: String? = "0.0,0.0"
    private var navigationRoute: NavigationMapRoute? = null
    private val searchAdapter = OlaSearchAutoCompleteAdapter(this)
    private val directionsRouteList = arrayListOf<DirectionsRoute>()
    private lateinit var textWatcher: TextWatcher

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            olaMapsInit()
        } else {
            Toast.makeText(this, "The app needs Location permission to work", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel.getAccessToken(
            clientId = BuildConfig.CLIENT_ID,
            clientSecret = BuildConfig.CLIENT_SECRET,
            onSuccess = {
                checkLocationPermission()
            }
        )

        binding.olaMapView.onCreate(savedInstanceState)
        olaMapsInit()
    }

    private val markerViewOptions = OlaMarkerOptions.Builder()
        .setIconIntRes(R.drawable.ic_location)
        .setMarkerId("1")
        .setIconSize(0.05f)
        .build()

    // Override onMapReady method
    override fun onMapReady() {
        binding.olaMapView.apply {
            moveToCurrentLocation()
        }
        navigationRoute = binding.olaMapView.getNavigationMapRoute()
        binding.rvSearchedLocation.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = searchAdapter
        }

        binding.olaMapView.addOnMapClickListener { latLng ->
            setupRoute(latLng)
            true
        }

        currentLocation()
        searchLocation()
        subscribeGetLocation()
    }

    private fun subscribeGetLocation() {
        autoCompleteViewModel.mSearchedLocationLiveData.observe(this) {
            Log.d("Searched", it.predictions.toString())
            searchAdapter.submitList(it.predictions)
            binding.rvSearchedLocation.visibility =
                if (it.predictions?.isNotEmpty() == true) View.VISIBLE else View.GONE
        }
    }

    private fun searchLocation() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                autoCompleteViewModel.callSearchAutoCompleteApi(
                    location = mCurrentLatLong ?: "0.0,0.0",
                    radius = 50000,
                    false,
                    input = s.toString()
                )
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.etSearchLocation.addTextChangedListener(textWatcher)
    }

    /*
   * This function for current location
   */

    private fun currentLocation() {
        binding.cvCurrentLocation.setOnClickListener {
            binding.olaMapView.moveToCurrentLocation()
        }
    }

    private fun setupRoute(latLng: LatLng) {
        binding.directionsBtn.isVisible = true
        binding.olaMapView.updateMarkerView(
            OlaMarkerOptions.Builder()
                .setMarkerId(markerViewOptions.markerId)
                .setPosition(
                    OlaLatLng(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                )
                .setIconIntRes(markerViewOptions.iconIntRes!!)
                .setIconSize(markerViewOptions.iconSize)
                .build()
        )
        navigationRoute?.removeRoute()
        navigationRoute?.animateCamera(latLng, 1.0)

        autoCompleteViewModel.getRouteInfo(
            originLatitudeLongitude = currentLatLng,
            destinationLatitudeLongitude = latLng,
            onSuccess = { routeInfoData ->
                binding.directionsBtn.setOnClickListener {
                    showRoute(routeInfoData, latLng)
                    binding.directionsBtn.isVisible = false
                }
            }
        )
    }

    private fun showRoute(routeInfoData: RouteInfoData, latLng: LatLng) {
        navigationRoute?.removeRoute()
        directionsRouteList.clear()
        directionsRouteList.add(transform(routeInfoData))

        navigationRoute?.addRoutesForRoutePreview(directionsRouteList)

        binding.olaMapView.animateCameraWithLatLngs(
            olaLatLngs = listOf(
                OlaLatLng(currentLatLng.latitude, currentLatLng.longitude),
                OlaLatLng(latLng.latitude, latLng.longitude)
            ),
            paddingLeft = 160,
            paddingBottom = 160,
            paddingRight = 160,
            paddingTop = 160,
        )

        binding.olaMapView.removeMarkerViewWithId(markerViewOptions.markerId)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            olaMapsInit()
        } else {
            locationPermissionRequest.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun olaMapsInit() {
        binding.olaMapView.initialize(
            mapStatusCallback = this,
            olaMapsConfig = OlaMapsConfig.Builder()
                .setApplicationContext(applicationContext) //pass the application context here, it is mandatory
                .setMapBaseUrl(MAP_BASE_URL) // pass the Base URL of Ola-Maps here (Stage/Prod URL), it is mandatory
                .setApiKey(API_KEY)
                .setProjectId(ORGANIZATION_ID) //Pass the Origination ID here, it is mandatory
                .setMapTileStyle(MapTileStyle.DEFAULT_LIGHT_STANDARD) //pass the MapTileStyle here, it is Optional.
                .setMinZoomLevel(3.0).setMaxZoomLevel(21.0).setZoomLevel(14.0).build()
        )

        setCurrentLocationLabel()

    }

    @SuppressLint("MissingPermission")
    private fun setCurrentLocationLabel() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLatLng = LatLng(it.latitude, it.longitude)

                    binding.olaMapView.addMarker(
                        LatLng(
                            latitude = currentLatLng.latitude,
                            longitude = currentLatLng.longitude
                        ),

                        "Current Location",
                        R.drawable.ic_location,
                        isAnimRequired


                    )
                    mCurrentLatLong = "${currentLatLng.latitude},${currentLatLng.longitude}"

                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "unable to fetch current latitude, longitude",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    override fun onMapLoadFailed(p0: String?) {
        Log.d("ola", "map loading failed: $p0")
        p0?.let {
            val errorCode = p0.substring(it.lastIndex - 2)
            if (errorCode == "401") {
                viewModel.getAccessToken(
                    clientId = BuildConfig.CLIENT_ID,
                    clientSecret = BuildConfig.CLIENT_SECRET,
                    onSuccess = { olaMapsInit() }
                )
            }

        }
    }

    //call onStop() of SDK before removing the OlaMaps
    override fun onStop() {
        super.onStop()
        binding.olaMapView.onStop()
    }

    //call onDestroy() of SDK to remove the Maps
    override fun onDestroy() {
        binding.olaMapView.onDestroy()
        super.onDestroy()
    }

    override fun onAddressSelected(
        item: Prediction?,
        position: Int,
        mListener: OlaSearchAutoCompleteAdapter.OnAddressClickListener
    ) {
        binding.etSearchLocation.removeTextChangedListener(textWatcher)
        binding.rvSearchedLocation.visibility = View.GONE
        binding.etSearchLocation.setText(item?.description)
        hideKeyboard()
        binding.etSearchLocation.clearFocus()
        binding.etSearchLocation.addTextChangedListener(textWatcher)
        val latLng = LatLng(item?.geometry?.location?.lat!!, item.geometry.location.lng!!)
        setupRoute(latLng)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearchLocation.windowToken, 0)
    }

}


