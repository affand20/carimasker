package id.trydev.carimasker.ui.clientkatalog

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import id.trydev.carimasker.CustomItemDecoration

import id.trydev.carimasker.R
import id.trydev.carimasker.adapter.ClientCatalogAdapter
import id.trydev.carimasker.model.Katalog
import id.trydev.carimasker.model.User
import kotlinx.android.synthetic.main.fragment_client_katalog.*

class ClientKatalogFragment : Fragment() {

    companion object {
        fun newInstance() = ClientKatalogFragment()
    }

    private lateinit var viewModel: ClientKatalogViewModel
    private lateinit var adapter: ClientCatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_client_katalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ClientKatalogViewModel::class.java)

        adapter = ClientCatalogAdapter(requireContext())

        rv_katalog.layoutManager = GridLayoutManager(requireContext(), 2)
        rv_katalog.adapter = adapter
        rv_katalog.addItemDecoration(CustomItemDecoration(50))

        val user = Gson().fromJson(ClientKatalogFragmentArgs.fromBundle(arguments).user, User::class.java)

        adapter.setUser(user)

        page_title.text = "Katalog"

        viewModel.clientGetKatalog(user.uid.toString())

        swipe_refresh.setOnRefreshListener {
            viewModel.clientGetKatalog(user.uid.toString())
        }

        viewModel.getResponse().observe(this, Observer {response ->
            swipe_refresh.isRefreshing = true
            if (response!=null) {
                swipe_refresh.isRefreshing = false
                if (response["isSuccess"] == true) {
                    adapter.setData(response["catalogs"] as List<Katalog>)
                } else {
                    Toast.makeText(requireContext(), "Gagal mendapatkan data, coba lagi nanti", Toast.LENGTH_SHORT).show()
                }
            }
        })

        nav_back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}
