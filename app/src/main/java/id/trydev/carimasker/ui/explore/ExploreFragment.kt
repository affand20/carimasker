package id.trydev.carimasker.ui.explore

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.utils.BitmapUtils
import id.trydev.carimasker.R
import id.trydev.carimasker.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_explore.*
import java.lang.ref.WeakReference

class ExploreFragment : Fragment(), OnMapReadyCallback, PermissionsListener {

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    private lateinit var locationEngine: LocationEngine
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L * 60L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private val callback = MapsCallback(this)

    private val mAuth = FirebaseAuth.getInstance()

    private val listLatLng = mutableListOf<LatLng>()
    private val listUser = mutableListOf<User>()
    private lateinit var user: List<User>

    private var lastLongitude = 0.0
    private var lastLatitude = 0.0

    private val ICON_IMG = "MARKER_ICON"

    private lateinit var exploreViewModel: ExploreViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        exploreViewModel =
            ViewModelProviders.of(this).get(ExploreViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_explore, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map_view?.onCreate(savedInstanceState)
        map_view.getMapAsync(this)

//        box_search.setOnClickListener {
//            Toast.makeText(activity?.applicationContext, "Box Search!", Toast.LENGTH_LONG).show()
//        }

        fab_recenter.setOnClickListener {
            flyToCurrentLocation()
        }

        close_card.setOnClickListener {
            Log.d("CLOSE_CARD", "CLICKED")
            if (cardView.visibility == View.VISIBLE) {
                cardView.animate()
                    .translationY(300f)
                    .alpha(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(p0: Animator?) {
                            fab_recenter.show()
                            cardView.visibility = View.GONE
                        }

                    })

            }
        }

        exploreViewModel.getUpdateResponse().observe(this, Observer { response ->
            if (response!=null && response.get("isSuccess")==true) {
                Log.d("UPDATE COORDINATE", "Success!")
            }
        })

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.uiSettings.isCompassEnabled = false

        this.mapboxMap.setStyle(Style.Builder().fromUri(Style.LIGHT)) {style->
            enableLocationComponent(style)

            addMarkerImage(style)

            val symbolManager = SymbolManager(map_view, mapboxMap, style)
            symbolManager.iconAllowOverlap = true


            val symbolList = mutableListOf<SymbolOptions>()

            exploreViewModel.getAllUserLocation().observe(this, Observer { response ->
                if (response!=null) {
                    if (response["isSuccess"]==false) {
                        Toast.makeText(context, "${response["message"]}", Toast.LENGTH_LONG).show()
                    } else {
                        val list = response["data"] as List<User>
                        list.forEach {
                            if ((it.masker!=null || it.handsanitizer!=null || it.apd!=null)
                                && it.uid!=mAuth.currentUser?.uid) {

                                val latlng = LatLng(it.latitude.toString().toDouble(), it.longitude.toString().toDouble())

                                listLatLng.add(latlng)

                                val symbol = SymbolOptions()
                                    .withLatLng(latlng)
                                    .withIconImage(ICON_IMG)
                                    .withIconSize(1f)
                                    .withData(GsonBuilder().create().toJsonTree(it, User::class.java))
                                symbolList.add(symbol)
                                listUser.add(it)
                            }
                        }
                        symbolManager.create(symbolList)
                    }
                }
            })

            symbolManager.addClickListener {
                Log.d("SYMBOL_LONGCLICK", "CLICKED")
                mapboxMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(it.latLng)
                            .zoom(15.0)
                            .build()
                    ), 2500
                )
                val user = Gson().fromJson(it.data, User::class.java)
                fab_recenter.hide()
                cardView.visibility = View.VISIBLE
                cardView.animate()
                    .translationY(-130f)
                    .alpha(1f)
                    .setListener(object: AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            tv_name_penydia.text = user.fullname
                            tv_address.text = user.address
                        }
                    })
                info_selengkapnya.setOnClickListener {
//                    findNavController().navigate(ExploreFragmentDirections.Action_navigation_explore_to_clientKatalogFragment(Gson().toJson(user)))
                var phone = user.phone?.replace("+", "")?.replace(" ", "")
                if (phone?.get(0) == '0') {
                    phone.replaceFirst("0","62")
                }
//                    toNumber = toNumber.replace("+","").replace(" ","")
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.putExtra("jid", "$phone@s.whatsapp.net")
            sendIntent.putExtra(Intent.EXTRA_TEXT, """
                Hai, saya tertarik dengan masker/handsanitizer/apdnya.
                Bolehkah saya dapat info lebih lanjut?
            """.trimIndent())
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.setPackage("com.whatsapp")
            sendIntent.setType("text/plain")
            startActivity(sendIntent)
                }
            }
        }
    }

    private fun addMarkerImage(style:Style) {
        BitmapUtils.getBitmapFromDrawable(ContextCompat.getDrawable(context!!, R.drawable.marker))?.let {
            style.addImage(ICON_IMG,
                it
            )
        }

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
            if (mapboxMap.style !=null) {
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

    private fun flyToCurrentLocation() {
//        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLatitude, lastLongitude), 10.0))
        mapboxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(lastLatitude, lastLongitude))
                    .zoom(10.0)
                    .build()
            ), 2500
        )
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

    inner class MapsCallback(private val fragment:ExploreFragment): LocationEngineCallback<LocationEngineResult> {

        private var activityWeakReference: WeakReference<ExploreFragment> = WeakReference(fragment)

        override fun onSuccess(result: LocationEngineResult?) {
            val homeFragment = activityWeakReference.get()
            if (homeFragment!=null) {
                result?.locations ?: return

                lastLatitude = result.lastLocation?.latitude?:0.0
                lastLongitude = result.lastLocation?.longitude?:0.0

                exploreViewModel.updateCoordinate(lastLatitude, lastLongitude)

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