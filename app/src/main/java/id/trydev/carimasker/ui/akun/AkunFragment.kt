package id.trydev.carimasker.ui.akun

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import id.trydev.carimasker.GlideApp
import id.trydev.carimasker.MainActivity
import id.trydev.carimasker.R
import id.trydev.carimasker.model.User
import id.trydev.carimasker.prefs.AppPreferences
import id.trydev.carimasker.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_akun.*
import kotlinx.android.synthetic.main.layout_dialog_stok.*

class AkunFragment : Fragment() {

    private lateinit var akunViewModel: AkunViewModel

    private lateinit var prefs: AppPreferences
    private lateinit var alertDialog: AlertDialog

    private var jumlahMasker = 0L
    private var hargaMasker = 0L
    private var jumlahHts = 0L
    private var hargahts= 0L
    private var jumlahApd = 0L
    private var hargaApd = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        akunViewModel =
            ViewModelProviders.of(this).get(AkunViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_akun, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = AppPreferences(view.context)

        akunViewModel.getUser()?.observe(this, Observer {response ->
            if (response!=null) {
                progressBar.visibility = View.GONE
                if (response.get("isSuccess")==true) {
                    val user = response.get("data") as User
                    Log.d("USER", "$user")
                    GlideApp.with(view.context)
                        .asBitmap()
                        .load(user.profileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .thumbnail(0.5f)
                        .into(profile_image)

                    tv_profile_name.text = user.fullname
                    tv_phone_number.text = user.phone
                    tv_address.text = user.address

                    if (user.apd == null && user.handsanitizer==null && user.masker == null) {
                        iv_state_empty.visibility = View.VISIBLE
                        tv_state_empty.visibility = View.VISIBLE

                        iv_masker.visibility = View.INVISIBLE
                        tv_jumlah_masker.visibility = View.INVISIBLE
                        tv_masker.visibility = View.INVISIBLE

                        iv_handsanitizer.visibility = View.INVISIBLE
                        tv_jumlah_handsanitizer.visibility = View.INVISIBLE
                        tv_handsanitizer.visibility = View.INVISIBLE

                        iv_apd.visibility = View.INVISIBLE
                        tv_jumlah_apd.visibility = View.INVISIBLE
                        tv_apd.visibility = View.INVISIBLE
                    } else {
                        user.masker?.let {
//                            tv_jumlah_masker.text = "${it["jumlah"]?:"0"}"
                            if (it["jumlah"]==null) {
                                tv_jumlah_masker.text = "0"
                            } else {
                                tv_jumlah_masker.text = "${it["jumlah"]}"
                            }
                            jumlahMasker = (it["jumlah"]?:0) as Long
                            hargaMasker = (it["harga"]?:0) as Long
                        }

                        user.handsanitizer?.let {
                            Log.d("JUMLAH_HTS", "${it["jumlah"]}")
//                            tv_jumlah_handsanitizer.text = "${it["jumlah"]?:"0"}"
                            if (it["jumlah"]==null) {
                                tv_jumlah_handsanitizer.text = "0"
                            } else {
                                tv_jumlah_handsanitizer.text = "${it["jumlah"]}"
                            }
                            jumlahHts = (it["jumlah"]?:0) as Long
                            hargaMasker = (it["harga"]?:0) as Long
                        }

                        user.apd?.let {
                            if (it["jumlah"]==null) {
                                tv_jumlah_apd.text = "0"
                            } else {
                                tv_jumlah_apd.text = "${it["jumlah"]}"
                            }
                            jumlahApd = (it["jumlah"]?:0) as Long
                            hargaMasker = (it["harga"]?:0) as Long
                        }
                    }
                }
            }
        })

        akunViewModel.getUpdateStok().observe(this, Observer {response ->
            if (response!=null) {
                Log.d("RESPONSE", "$response")
                if (response["isSuccess"] == true) {
                    val masker = response["masker"] as HashMap<*, *>?
                    val handsanitizer = response["handsanitizer"] as HashMap<*, *>?
                    val apd = response["apd"] as HashMap<*, *>?

                    if (masker!=null) {
                        if (masker["jumlah"]==null) {
                            tv_jumlah_masker.text = "0"
                        } else {
                            tv_jumlah_masker.text = masker["jumlah"].toString()
                        }
                    }
                    if (handsanitizer!=null) {
                        if (handsanitizer["jumlah"]==null) {
                            tv_jumlah_handsanitizer.text = "0"
                        } else {
                            tv_jumlah_handsanitizer.text = handsanitizer["jumlah"].toString()
                        }
                    }
                    if (apd!=null) {
                        if (apd["jumlah"]==null) {
                            tv_jumlah_apd.text = "0"
                        } else {
                            tv_jumlah_apd.text = apd["jumlah"].toString()
                        }
                    }

                } else if (response["isSuccess"] == false){
                    Toast.makeText(context, "${response["message"]}", Toast.LENGTH_LONG).show()
                }
            }
        })

        val builder = AlertDialog.Builder(view.context)
        val layout = LayoutInflater.from(view.context).inflate(R.layout.layout_dialog_stok, null)
        builder.setView(layout)

        val btnSubmit: Button = layout.findViewById(R.id.submit_stok)
        val progressBar: ProgressBar = layout.findViewById(R.id.progress_bar)

        val btnAddMasker: ImageButton = layout.findViewById(R.id.btn_masker_plus)
        val btnMinMasker: ImageButton = layout.findViewById(R.id.btn_masker_minus)
        val tvCountMasker: TextView = layout.findViewById(R.id.tv_count_masker)
        val cbDonasiMasker: CheckBox = layout.findViewById(R.id.cb_donasi_masker)
        val edtPriceMasker: EditText = layout.findViewById(R.id.edt_harga_masker)
        val tvPcsMasker: TextView = layout.findViewById(R.id.pcs_masker)
        var countMasker = 0



        var hargaMasker = 0
        var hargaHandsanitizer = 0
        var hargaApd = 0

        btnAddMasker.setOnClickListener {
            countMasker++
            tvCountMasker.text = countMasker.toString()
        }

        btnMinMasker.setOnClickListener {
            if (countMasker>0) {
                countMasker--
                tvCountMasker.text = countMasker.toString()
            }
        }

        cbDonasiMasker.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hargaMasker = 0
                edtPriceMasker.visibility = View.GONE
                tvPcsMasker.visibility = View.GONE
            } else {
                edtPriceMasker.text.clear()
                edtPriceMasker.visibility = View.VISIBLE
                tvPcsMasker.visibility = View.VISIBLE
            }
        }


        val btnAddHandsanitizer: ImageButton = layout.findViewById(R.id.btn_handsanitizer_plus)
        val btnMinHandsanitizer: ImageButton = layout.findViewById(R.id.btn_handsanitizer_minus)
        val tvCountHandsanitizer: TextView = layout.findViewById(R.id.tv_count_handsanitizer)
        val cbDonasiHandsanitizer: CheckBox = layout.findViewById(R.id.cb_donasi_handsanitizer)
        val edtPriceHandsanitizer: EditText = layout.findViewById(R.id.edt_harga_handsanitizer)
        val tvPcsHandsanitizer: TextView = layout.findViewById(R.id.pcs_handsanitizer)
        var countHandsanitizer = 0

        btnAddHandsanitizer.setOnClickListener {
            countHandsanitizer++
            tvCountHandsanitizer.text = countHandsanitizer.toString()
        }

        btnMinHandsanitizer.setOnClickListener {
            if (countHandsanitizer>0) {
                countHandsanitizer--
                tvCountHandsanitizer.text = countHandsanitizer.toString()
            }
        }

        cbDonasiHandsanitizer.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hargaHandsanitizer = 0
                edtPriceHandsanitizer.visibility = View.GONE
                tvPcsHandsanitizer.visibility = View.GONE
            } else {
                edtPriceHandsanitizer.text.clear()
                edtPriceHandsanitizer.visibility = View.VISIBLE
                tvPcsHandsanitizer.visibility = View.VISIBLE
            }
        }

        val btnAddApd: ImageButton = layout.findViewById(R.id.btn_apd_plus)
        val btnMinApd: ImageButton = layout.findViewById(R.id.btn_apd_minus)
        val tvCountApd: TextView = layout.findViewById(R.id.tv_count_apd)
        val cbDonasiApd: CheckBox = layout.findViewById(R.id.cb_donasi_apd)
        val edtPriceApd: EditText = layout.findViewById(R.id.edt_harga_apd)
        val tvPcsApd: TextView = layout.findViewById(R.id.pcs_apd)
        var countApd = 0

        btnAddApd.setOnClickListener {
            countApd++
            tvCountApd.text = countApd.toString()
        }

        btnMinApd.setOnClickListener {
            if (countApd>0) {
                countApd--
                tvCountApd.text = countApd.toString()
            }
        }

        cbDonasiApd.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                hargaApd = 0
                edtPriceApd.visibility = View.GONE
                tvPcsApd.visibility = View.GONE
            } else {
                edtPriceApd.text.clear()
                edtPriceApd.visibility = View.VISIBLE
                tvPcsApd.visibility = View.VISIBLE
            }
        }

        fun validate(): Boolean {
            if (countMasker<=0 && countApd<=0 && countHandsanitizer<=0) {
                Toast.makeText(context, "Anda belum menambahkan apa-apa!", Toast.LENGTH_LONG).show()
                return false
            }
            if (cbDonasiMasker.isChecked && countMasker<=0) {
                Toast.makeText(context, "Masker donasi tidak boleh kosong!", Toast.LENGTH_LONG).show()
                return false
            }
            if (cbDonasiHandsanitizer.isChecked && countHandsanitizer<=0) {
                Toast.makeText(context, "Handsanitizer donasi tidak boleh kosong!", Toast.LENGTH_LONG).show()
                return false
            }
            if (cbDonasiApd.isChecked && countApd<=0) {
                Toast.makeText(context, "APD donasi tidak boleh kosong!", Toast.LENGTH_LONG).show()
                return false
            }
            if (!cbDonasiMasker.isChecked && countMasker>0 && hargaMasker <=0) {
                edtPriceMasker.requestFocus()
                edtPriceMasker.error = "Wajib diisi!"
                return false
            }
            if (!cbDonasiHandsanitizer.isChecked && countHandsanitizer>0 && hargaHandsanitizer <=0) {
                edtPriceHandsanitizer.requestFocus()
                edtPriceHandsanitizer.error = "Wajib diisi!"
                return false
            }
            if (!cbDonasiApd.isChecked && countApd>0 && hargaApd <=0) {
                edtPriceApd.requestFocus()
                edtPriceApd.error = "Wajib diisi!"
                return false
            }
            return true
        }

        btnSubmit.setOnClickListener {
            if (validate()){
                if (edtPriceMasker.visibility==View.VISIBLE && edtPriceMasker.text.isNotEmpty()) {
                    hargaMasker = edtPriceMasker.text.toString().toInt()
                }
                if (edtPriceHandsanitizer.visibility==View.VISIBLE && edtPriceHandsanitizer.text.isNotEmpty()) {
                    hargaHandsanitizer = edtPriceHandsanitizer.text.toString().toInt()
                }
                if (edtPriceApd.visibility==View.VISIBLE && edtPriceApd.text.isNotEmpty()) {
                    hargaApd = edtPriceApd.text.toString().toInt()
                }

                val data = hashMapOf<String, HashMap<String, Any>>()
                if (countMasker>0) {
                    data.put("masker", hashMapOf(
                        "harga" to hargaMasker,
                        "jumlah" to countMasker
                    ))
                }
                if (countHandsanitizer>0) {
                    data.put("handsanitizer", hashMapOf(
                        "harga" to hargaHandsanitizer,
                        "jumlah" to countHandsanitizer
                    ))
                }
                if (countApd>0) {
                    data.put("apd", hashMapOf(
                        "harga" to hargaApd,
                        "jumlah" to countApd
                    ))
                }

                progressBar.visibility = View.VISIBLE
                btnSubmit.visibility = View.GONE

                akunViewModel.updateStok(data)?.observe(this, Observer {response ->
                    if (response!=null) {
                        progressBar.visibility = View.GONE
                        if (response["isSuccess"] == true) {
                            iv_state_empty.visibility = View.GONE
                            tv_state_empty.visibility = View.GONE

                            iv_masker.visibility = View.VISIBLE
                            tv_jumlah_masker.visibility = View.VISIBLE
                            tv_masker.visibility = View.VISIBLE

                            iv_handsanitizer.visibility = View.VISIBLE
                            tv_jumlah_handsanitizer.visibility = View.VISIBLE
                            tv_handsanitizer.visibility = View.VISIBLE

                            iv_apd.visibility = View.VISIBLE
                            tv_jumlah_apd.visibility = View.VISIBLE
                            tv_apd.visibility = View.VISIBLE

                            alertDialog.dismiss()

                        } else if (response["isSuccess"] == false){
                            btnSubmit.visibility = View.VISIBLE
                            Toast.makeText(context, "${response["message"]}", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
        }

        btn_tambah_stok.setOnClickListener {
                countMasker = jumlahMasker.toInt()
                hargaMasker = this.hargaMasker.toInt()
                hargaHandsanitizer = hargahts.toInt()
                hargaApd = this.hargaApd.toInt()
                countHandsanitizer = jumlahHts.toInt()
                countApd = jumlahApd.toInt()

            tvCountMasker.text = jumlahMasker.toString()
            tvCountApd.text = jumlahApd.toString()
            tvCountHandsanitizer.text = jumlahHts.toString()

            edtPriceMasker.setText(hargaMasker.toString())
            edtPriceApd.setText(hargaApd.toString())
            edtPriceHandsanitizer.setText(hargahts.toString())
            if (!::alertDialog.isInitialized) {
                alertDialog = builder.show()
            } else {
                alertDialog.show()
            }
        }

        iv_state_empty.setOnClickListener{
            countMasker = jumlahMasker.toInt()
            hargaMasker = this.hargaMasker.toInt()
            hargaHandsanitizer = hargahts.toInt()
            hargaApd = this.hargaApd.toInt()
            countHandsanitizer = jumlahHts.toInt()
            countApd = jumlahApd.toInt()

            tvCountMasker.text = jumlahMasker.toString()
            tvCountApd.text = jumlahApd.toString()
            tvCountHandsanitizer.text = jumlahHts.toString()

            edtPriceMasker.setText(hargaMasker.toString())
            edtPriceApd.setText(hargaApd.toString())
            edtPriceHandsanitizer.setText(hargahts.toString())
            if (!::alertDialog.isInitialized) {
                alertDialog = builder.show()
            } else {
                alertDialog.show()
            }
        }

        tv_state_empty.setOnClickListener {
            countMasker = jumlahMasker.toInt()
            hargaMasker = this.hargaMasker.toInt()
            hargaHandsanitizer = hargahts.toInt()
            hargaApd = this.hargaApd.toInt()
            countHandsanitizer = jumlahHts.toInt()
            countApd = jumlahApd.toInt()

            tvCountMasker.text = jumlahMasker.toString()
            tvCountApd.text = jumlahApd.toString()
            tvCountHandsanitizer.text = jumlahHts.toString()

            edtPriceMasker.setText(hargaMasker.toString())
            edtPriceApd.setText(hargaApd.toString())
            edtPriceHandsanitizer.setText(hargahts.toString())
            if (!::alertDialog.isInitialized) {
                alertDialog = builder.show()
            } else {
                alertDialog.show()
            }
        }

        list_menu_profile.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                1 -> {
                    FirebaseAuth.getInstance().signOut()
                    prefs.resetPreference()
                    startActivity(Intent(context, LoginActivity::class.java))
                    activity?.finish()
                }
            }
        }
    }
}