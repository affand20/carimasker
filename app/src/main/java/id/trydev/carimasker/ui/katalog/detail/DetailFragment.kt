package id.trydev.carimasker.ui.katalog.detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import id.trydev.carimasker.R
import id.trydev.carimasker.adapter.DetailCatalogAdapter
import id.trydev.carimasker.model.Katalog
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.page_title

class DetailFragment : Fragment() {

    private lateinit var adapter: DetailCatalogAdapter

    companion object {
        fun newInstance() =
            DetailFragment()
    }

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)

        val args = DetailFragmentArgs.fromBundle(arguments)

        val idCatalog = args.id_catalog
        val categoryCatalog = args.category

        if (args.user!=null) {
            btn_add_stock.visibility = View.GONE
            btn_min_stock.visibility = View.GONE
            btn_delete_catalog.visibility = View.GONE

        }

        page_title.text = categoryCatalog
        tv_category.text = categoryCatalog

        adapter = DetailCatalogAdapter(requireContext())

        rv_katalog_preview.adapter = adapter
        rv_katalog_preview.setItemTransformer(ScaleTransformer.Builder()
            .setMinScale(0.9f)
            .build()
        )

        viewModel.getDetail(idCatalog).observe(this, Observer { response ->
            if (response["isSuccess"] == false) {
                Toast.makeText(requireContext(), "${response["message"]}", Toast.LENGTH_LONG).show()
                Log.d("FAILED", response["message"].toString())
            } else {
                if (response["catalog"]!=null) {
                    val katalog = response["catalog"] as Katalog

                    katalog.photo?.let { adapter.setListImage(it) }
                    tv_title.text = katalog.title
                    if (katalog.isDonation==true) {
                        tv_price.text = "Gratis"
                    } else {
                        tv_price.text = katalog.price.toString()
                    }
                    tv_description.text = katalog.description
                    tv_stock_count.text = katalog.stock.toString()
                } else {
                    findNavController().popBackStack()
                }

            }
        })

        btn_add_stock.setOnClickListener {
            viewModel.incrementStock(idCatalog)
        }

        btn_min_stock.setOnClickListener {
            viewModel.decrementStock(idCatalog)
        }

        nav_back.setOnClickListener {
            findNavController().popBackStack()
        }

        btn_delete_catalog.setOnClickListener {
            viewModel.deleteStock(idCatalog)
        }

        fab_edit.setOnClickListener {
            findNavController().navigate(DetailFragmentDirections.navigate_edit_catalog(idCatalog))
        }

    }

}
