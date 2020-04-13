package id.trydev.carimasker.ui.covid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import id.trydev.carimasker.R
import id.trydev.carimasker.model.Corona
import kotlinx.android.synthetic.main.fragment_covid.*

class CovidFragment : Fragment() {

    private lateinit var covidViewModel: CovidViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        covidViewModel =
            ViewModelProviders.of(this).get(CovidViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_covid, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        btn_checkup.setOnClickListener {
//            Toast.makeText(context, "Clicked!", Toast.LENGTH_SHORT).show()
//        }

        context?.let { context ->
            ArrayAdapter.createFromResource(context, R.array.provinsi, R.layout.item_spinner)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner_provinsi.adapter = adapter
                }
        }

        covidViewModel.getCovidUpdate().observe(this, Observer {response ->
            if (response!=null) {
                if (response["isSuccess"] ==true) {

                    val corona = response["data"] as Corona
                    tv_positif.text = corona.positif.toString()
                    tv_sembuh.text = corona.sembuh.toString()
                    tv_meninggal.text = corona.meninggal.toString()

                } else {
                    Toast.makeText(context, "${response["message"]}", Toast.LENGTH_SHORT).show()
                }
            }
        })

        spinner_provinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }

            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val prov = spinner_provinsi.getItemAtPosition(position).toString()
                covidViewModel.getCovidUpdate(prov)
                tv_positif.text = "..."
                tv_sembuh.text = "..."
                tv_meninggal.text = "..."
            }

        }

    }
}