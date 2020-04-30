package id.trydev.carimasker.ui.katalog.upload


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import id.trydev.carimasker.R
import id.trydev.carimasker.adapter.UploadCatalogAdapter
import id.trydev.carimasker.model.Katalog
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_upload_katalog.*
import kotlinx.android.synthetic.main.fragment_upload_katalog.page_title

class UploadKatalogFragment : Fragment() {

    companion object {
        const val rcChoose = 100
    }

    private val listImage = mutableListOf<String>()
    private lateinit var viewModel: UploadKatalogViewModel
    private lateinit var adapter: UploadCatalogAdapter
    private lateinit var katalog: Katalog
    private lateinit var idCatalog: String

    private var stock = 0
    private var price = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(UploadKatalogViewModel::class.java)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_katalog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        adapter = UploadCatalogAdapter(requireContext())


        if (arguments!=null) {
            idCatalog = UploadKatalogFragmentArgs.fromBundle(arguments).id_catalog
            viewModel.getKatalog(idCatalog).observe(this, Observer {response ->
                if (response==null) {
                    edt_judul.isEnabled = false
                    edt_harga.isEnabled = false
                    edt_deskripsi.isEnabled = false
                    sw_donasi.isEnabled = false
                    spinner_category.isEnabled = false
                    edt_stock.isEnabled = false

                } else {
                    if (response["isSuccess"]==false) {
                        Toast.makeText(requireContext(), "${response["message"]}", Toast.LENGTH_LONG).show()
                        Log.d("FAILED_GET_KATALOG", "${response["message"]}")
                    } else {
                        edt_judul.isEnabled = true
                        edt_harga.isEnabled = true
                        edt_deskripsi.isEnabled = true
                        sw_donasi.isEnabled = true
                        spinner_category.isEnabled = true
                        edt_stock.isEnabled = true

                        katalog = response["catalog"] as Katalog
                        val listCategory = resources.getStringArray(R.array.catalog_category)
                        edt_judul.setText(katalog.title)
                        edt_harga.setText(katalog.price.toString())
                        edt_deskripsi.setText(katalog.description)
                        sw_donasi.isChecked = katalog.isDonation==true
                        edt_stock.setText(katalog.stock.toString())
                        spinner_category.setSelection(listCategory.indexOf(katalog.category), true)
                        katalog.photo?.let {
                            adapter.setData(it)
                        }
                    }

                }
            })
        }

        page_title.setOnClickListener {
            findNavController().popBackStack()
        }

        rv_katalog_photo.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rv_katalog_photo.adapter = adapter

        sw_donasi.setOnCheckedChangeListener { _, isChecked ->
            edt_harga.isEnabled = !isChecked
        }

        btn_increment.setOnClickListener {
            stock++
            edt_stock.setText(stock.toString())
        }

        btn_decrement.setOnClickListener {
            if (stock>0) {
                stock --
                edt_stock.setText(stock.toString())
            }
        }

        edt_stock.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                Log.d("ON_TEXT_CHANGE", p0.toString())
                stock = p0.toString().toInt()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        btn_add_katalog_photo.setOnClickListener {
            if (!hasPermission()) {
                Dexter.withContext(requireContext())
                    .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            report?.let {
                                if (report.areAllPermissionsGranted()) {

                                }
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                        ) {
                            token?.continuePermissionRequest()
                        }
                    }).onSameThread()
                    .check()
            }
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(intent, "Pilih File"), rcChoose)
        }

        btn_upload_catalog.setOnClickListener{
            if (validate()) {
                if (arguments!=null) {
                    val updatedKatalog = hashMapOf<String, Any>()
                    if (edt_judul.text.toString()!=katalog.title) {
                        updatedKatalog["title"] = edt_judul.text.toString()
                    }
                    if (edt_deskripsi.text.toString()!=katalog.description) {
                        updatedKatalog["description"] = edt_deskripsi.text.toString()
                    }
                    if (price != katalog.price) {
                        updatedKatalog["price"] = price
                    }
                    if (sw_donasi.isChecked != katalog.isDonation) {
                        updatedKatalog["donation"] = sw_donasi.isChecked
                    }
                    if (spinner_category.selectedItem.toString()!=katalog.category) {
                        updatedKatalog["category"] = spinner_category.selectedItem.toString()
                    }
                    if (stock != katalog.stock) {
                        updatedKatalog["stock"] = stock
                    }
                    Log.d("IS EQUAL?", "$listImage, ${katalog.photo} ${listImage == katalog.photo}")
                    if (listImage!=null && listImage != katalog.photo) {
                        updatedKatalog["photo"] = listImage
                    }
                    viewModel.updateKatalog(idCatalog, updatedKatalog)
                } else {
                    val katalog = Katalog(
                        null,
                        edt_judul.text.toString(),
                        edt_deskripsi.text.toString(),
                        price,
                        sw_donasi.isChecked,
                        spinner_category.selectedItem.toString(),
                        stock,
                        listImage,
                        null
                    )

                    viewModel.uploadKatalog(katalog)
                }
                btn_upload_catalog.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
        }

        viewModel.getResponse().observe(this, Observer {response ->
            if (response!=null) {
                progressBar.visibility = View.GONE
                if (response["isSuccess"] ==true) {
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "${response["message"]}", Toast.LENGTH_LONG).show()
                } else {
                    btn_upload_catalog.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcChoose && resultCode == Activity.RESULT_OK && data!=null && data.data!=null) {
            val uri = data.data.toString()
            uri?.let { listImage.add(it) }
            adapter.setData(listImage)
        }
    }

    private fun hasPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun validate(): Boolean {

        var result = true

        if (edt_judul.text.toString().isEmpty()) {
            edt_judul.error = "Wajib diisi"
            edt_judul.requestFocus()
            result = false
        }

        if (!sw_donasi.isChecked) {
            if (edt_harga.text.toString().isEmpty()) {
                edt_harga.error = "Wajib diisi"
                edt_harga.requestFocus()
                result = false
            } else {
                price = edt_harga.text.toString().toInt()
            }
        } else {
            price = 0
        }

        if (stock <= 0) {
            edt_stock.error = "Masukkan jumlah stock"
            edt_stock.requestFocus()
            result = false
        }

        if (edt_deskripsi.text.toString().isEmpty()) {
            edt_deskripsi.error = "Wajib diisi"
            edt_deskripsi.requestFocus()
            result = false
        }

        return result
    }


}
