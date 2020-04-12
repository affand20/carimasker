package id.trydev.carimasker.ui.akun

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import id.trydev.carimasker.GlideApp
import id.trydev.carimasker.R
import id.trydev.carimasker.model.User
import kotlinx.android.synthetic.main.fragment_akun.*

class AkunFragment : Fragment() {

    private lateinit var akunViewModel: AkunViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        akunViewModel =
            ViewModelProviders.of(this).get(AkunViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_akun, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        akunViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        akunViewModel.getUser()?.observe(this, Observer {response ->
            if (response!=null) {
                if (response.get("isSuccess")==true) {
                    val user = response.get("data") as User
                    Log.d("USER", "$user")
                    GlideApp.with(view.context)
                        .asBitmap()
                        .load(user.profileUrl)
                        .thumbnail(0.5f)
                        .into(profile_image)

                    tv_profile_name.text = user.fullname
                    tv_phone_number.text = user.phone
                    tv_address.text = user.address
                }
            }
        })
    }
}