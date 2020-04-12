package id.trydev.carimasker.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import id.trydev.carimasker.R
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception
import java.lang.ref.WeakReference

class HomeFragment : Fragment(), OnMapReadyCallback, PermissionsListener {

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    private lateinit var locationEngine: LocationEngine
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L * 3600L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private val callback = MapsCallback(this)

    private var lastLongitude = 0.0
    private var lastLatitude = 0.0

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        Log.d("CONTEXT","${context==null}")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_view?.onCreate(savedInstanceState)
        map_view.getMapAsync(this)

        box_search.setOnClickListener {
            Toast.makeText(activity?.applicationContext, "Box Search!", Toast.LENGTH_LONG).show()
        }

        fab_recenter.setOnClickListener {
            flyToCurrentLocation()
        }

        homeViewModel.getUpdateResponse().observe(this, Observer {response ->
            if (response!=null && response.get("isSuccess")==true) {
//                Toast.makeText(context, "Coordinate Updated!", Toast.LENGTH_SHORT).show()
            }
        })

    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(context)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(context!!)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(context!!, R.color.colorAccent))
                .build()


            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(context!!, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .useDefaultLocationEngine(false)
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }

            initLocationEngine()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(activity, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            if (mapboxMap.getStyle()!=null) {
                enableLocationComponent(mapboxMap.style!!)
            }
        } else {
            Toast.makeText(
                activity,
                R.string.user_location_permission_not_granted,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        map_view?.onResume()
    }

    override fun onStart() {
        super.onStart()
        map_view?.onStart()
    }

    override fun onStop() {
        super.onStop()
        map_view?.onStop()
    }

    override fun onPause() {
        super.onPause()
        map_view?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_view?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view?.onDestroy()
        if (locationEngine!=null) {
            locationEngine.removeLocationUpdates(callback)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view?.onSaveInstanceState(outState)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.uiSettings.isCompassEnabled = false
        this.mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)

            // add map layer here
        }
    }

    private fun flyToCurrentLocation() {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLatitude, lastLongitude), 10.0))
    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        context?.let {context ->
            locationEngine = LocationEngineProvider.getBestLocationEngine(context)

            val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build()

            locationEngine.requestLocationUpdates(request, callback, Looper.getMainLooper())
            locationEngine.getLastLocation(callback)
        }
    }

    inner class MapsCallback(private val fragment:HomeFragment): LocationEngineCallback<LocationEngineResult> {

        private var activityWeakReference: WeakReference<HomeFragment> = WeakReference(fragment)

        override fun onSuccess(result: LocationEngineResult?) {
            val homeFragment = activityWeakReference.get()
            if (homeFragment!=null) {
                result?.locations ?: return

                // if locations not null, then update lat long to firebase here


//                Toast.makeText(fragment.context, "Lat: ${result.lastLocation?.latitude}, Long: ${result.lastLocation?.longitude}", Toast.LENGTH_SHORT).show()
                lastLatitude = result.lastLocation?.latitude?:0.0
                lastLongitude = result.lastLocation?.longitude?:0.0

                homeViewModel.updateCoordinate(lastLatitude, lastLongitude)

                if (mapboxMap != null && result.lastLocation!=null) {
                    mapboxMap.locationComponent.forceLocationUpdate(result.lastLocation)
                }
            }
        }

        override fun onFailure(exception: Exception) {
            val fragment = activityWeakReference.get()
            if (fragment!=null) {
                Toast.makeText(fragment.context, "Error: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}