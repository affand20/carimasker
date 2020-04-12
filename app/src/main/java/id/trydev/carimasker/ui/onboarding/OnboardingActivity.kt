package id.trydev.carimasker.ui.onboarding

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.trydev.carimasker.MainActivity
import id.trydev.carimasker.R
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : AppCompatActivity() {

    private lateinit var onboardingViewModel: OnboardingViewModel
    private lateinit var uri:Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        onboardingViewModel = ViewModelProviders.of(this).get(OnboardingViewModel::class.java)

        btn_submit.setOnClickListener {
            if (validate()) {
                onboardingViewModel.uploadPersonalData(
                    edt_name.text.toString(),
                    edt_phone.text.toString(),
                    edt_address.text.toString(),
                    uri
                )

                btn_submit.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
        }

        onboardingViewModel.getResponse().observe(this, Observer {response ->
            if (response!=null) {
                progressBar.visibility = View.GONE
                if (response.get("isSuccess")==true) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    btn_submit.visibility = View.VISIBLE
                    Toast.makeText(this, "${response.get("message")}", Toast.LENGTH_LONG).show()
                }
            }
        })

        btn_change_profile.setOnClickListener {
            if (!hasPermission()){
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(object : MultiplePermissionsListener{
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
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this)
        }

    }

    private fun hasPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun validate(): Boolean {
        if (!this::uri.isInitialized) {
            Toast.makeText(this, "Foto belum dipilih!", Toast.LENGTH_LONG).show()
            return false
        }
        if (edt_name.text.toString().isEmpty()) {
            edt_name.error = "Wajib diisi !"
            edt_name.requestFocus()
            return false
        }

        if (edt_phone.text.toString().isEmpty()) {
            edt_phone.error = "Wajib diisi !"
            edt_phone.requestFocus()
            return false
        }

        if (edt_address.text.toString().isEmpty()) {
            edt_address.error= "Wajib diisi !"
            edt_address.requestFocus()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                profile_image.setImageURI(resultUri)
                uri = resultUri
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}
