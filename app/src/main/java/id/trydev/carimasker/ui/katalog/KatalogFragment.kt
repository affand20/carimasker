package id.trydev.carimasker.ui.katalog

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
import androidx.recyclerview.widget.LinearLayoutManager
import id.trydev.carimasker.CustomItemDecoration

import id.trydev.carimasker.R
import id.trydev.carimasker.adapter.KatalogAdapter
import id.trydev.carimasker.model.Katalog
import kotlinx.android.synthetic.main.fragment_katalog.*

class KatalogFragment : Fragment() {

    companion object {
        fun newInstance() = KatalogFragment()
    }

    private lateinit var viewModel: KatalogViewModel
    private lateinit var adapter: KatalogAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_katalog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(KatalogViewModel::class.java)

        adapter = KatalogAdapter(requireContext())

        rv_katalog.layoutManager = GridLayoutManager(requireContext(), 2)
        rv_katalog.adapter = adapter
        rv_katalog.addItemDecoration(CustomItemDecoration(50))

        page_title.text = KatalogFragmentArgs.fromBundle(arguments).category

        viewModel.getKatalog(KatalogFragmentArgs.fromBundle(arguments).category)

        swipe_refresh.setOnRefreshListener {
            viewModel.getKatalog(KatalogFragmentArgs.fromBundle(arguments).category)
        }

        viewModel.observeResponse().observe(this, Observer {response ->
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

        fab_add_katalog.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_upload_katalog)
        }



    }

}
