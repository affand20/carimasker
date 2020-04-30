package id.trydev.carimasker.ui.akun

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.engine.DiskCacheStrategy
import id.trydev.carimasker.GlideApp
import id.trydev.carimasker.R
import id.trydev.carimasker.model.Katalog
import id.trydev.carimasker.model.User
import id.trydev.carimasker.prefs.AppPreferences
import kotlinx.android.synthetic.main.fragment_akun.*

class AkunFragment : Fragment() {

    private lateinit var akunViewModel: AkunViewModel

    private lateinit var prefs: AppPreferences
    private val listMasker = mutableListOf<Katalog>()
    private val listHandsanitizer = mutableListOf<Katalog>()
    private val listApd = mutableListOf<Katalog>()
    private val listGloves = mutableListOf<Katalog>()
    private val listOthers = mutableListOf<Katalog>()
    private val listThermalgun = mutableListOf<Katalog>()
    private val listAllKatalog = mutableListOf<Katalog>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        akunViewModel =
            ViewModelProviders.of(this).get(AkunViewModel::class.java)
        return inflater.inflate(R.layout.fragment_akun, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = AppPreferences(view.context)

        akunViewModel.getUser()?.observe(this, Observer {response ->
            if (response!=null) {
                if (response.get("isSuccess")==true) {
                    val user = response.get("data") as User
                    Log.d("USER",  "$user")
                    GlideApp.with(view.context)
                        .asBitmap()
                        .load(user.profileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .thumbnail(0.5f)
                        .into(profile_image)

                    tv_profile_name.text = user.fullname
                    tv_address.text = user.address
                }
            }
        })

        btn_settings.setOnClickListener {

        }

        akunViewModel.getStock().observe(this, Observer {response ->
            if (response!=null) {
                if (response["isSuccess"] == false) {
                    Toast.makeText(requireContext(), "${response["isSuccess"]}", Toast.LENGTH_LONG).show()
                } else {
                    listAllKatalog.clear()
                    listAllKatalog.addAll(response["catalogs"] as List<Katalog>)

                    listMasker.clear()
                    listMasker.addAll(listAllKatalog.filter {
                        it.category=="Masker"
                    })
                    tv_total_masker.text = String.format(resources.getString(R.string.total_catalogs, listMasker.size))

                    listHandsanitizer.clear()
                    listHandsanitizer.addAll(listAllKatalog.filter {
                        it.category=="Hand Sanitizer"
                    })
                    tv_total_handsanitizer.text = String.format(resources.getString(R.string.total_catalogs, listHandsanitizer.size))

                    listThermalgun.clear()
                    listThermalgun.addAll(listAllKatalog.filter {
                        it.category=="Thermal Gun"
                    })
                    tv_total_thermalgun.text = String.format(resources.getString(R.string.total_catalogs, listThermalgun.size))

                    listApd.clear()
                    listApd.addAll(listAllKatalog.filter {
                        it.category=="Alat Pelindung Diri"
                    })
                    tv_total_apd.text = String.format(resources.getString(R.string.total_catalogs, listApd.size))

                    listGloves.clear()
                    listGloves.addAll(listAllKatalog.filter {
                        it.category=="Sarung Tangan"
                    })
                    tv_total_gloves.text = String.format(resources.getString(R.string.total_catalogs, listGloves.size))

                    listOthers.clear()
                    listOthers.addAll(listAllKatalog.filter {
                        it.category=="Lainnya"
                    })
                    tv_total_others.text = String.format(resources.getString(R.string.total_catalogs, listOthers.size))

                }
            }
        })

        tv_total_masker.text = String.format(resources.getString(R.string.total_catalogs, 0))
        tv_total_handsanitizer.text = String.format(resources.getString(R.string.total_catalogs), 0)
        tv_total_thermalgun.text = String.format(resources.getString(R.string.total_catalogs), 0)
        tv_total_apd.text = String.format(resources.getString(R.string.total_catalogs), 0)
        tv_total_gloves.text = String.format(resources.getString(R.string.total_catalogs), 0)
        tv_total_others.text = String.format(resources.getString(R.string.total_catalogs), 0)


        iv_mask.setOnClickListener{
            findNavController().navigate(AkunFragmentDirections.navigate_to_katalog("Masker"))
        }

        iv_handsanitizer.setOnClickListener{
            findNavController().navigate(AkunFragmentDirections.navigate_to_katalog("Hand Sanitizer"))
        }

        iv_gloves.setOnClickListener{
            findNavController().navigate(AkunFragmentDirections.navigate_to_katalog("Sarung Tangan"))
        }

        iv_thermalgun.setOnClickListener{
            findNavController().navigate(AkunFragmentDirections.navigate_to_katalog("Thermal Gun"))
        }

        iv_apd.setOnClickListener{
            findNavController().navigate(AkunFragmentDirections.navigate_to_katalog("Alat Pelindung Diri"))
        }

        iv_others.setOnClickListener{
            findNavController().navigate(AkunFragmentDirections.navigate_to_katalog("Lainnya"))
        }

    }
}