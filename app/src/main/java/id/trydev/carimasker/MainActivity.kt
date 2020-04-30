package id.trydev.carimasker

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.crashlytics.android.Crashlytics
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import id.trydev.carimasker.prefs.AppPreferences

class MainActivity : AppCompatActivity() {

    lateinit var prefs:AppPreferences
    lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navView = findViewById(R.id.nav_view)

        prefs = AppPreferences(this)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        Crashlytics.getInstance()


        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val listBottomMenu = arrayOf(
            R.id.navigation_explore,
            R.id.navigation_info_covid,
            R.id.navigation_chat,
            R.id.navigation_profile
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in listBottomMenu) {
                navView.visibility = View.VISIBLE
            } else {
                navView.visibility = View.GONE
            }
        }

    }
}
