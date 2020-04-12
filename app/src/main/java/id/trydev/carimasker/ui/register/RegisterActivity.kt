package id.trydev.carimasker.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import id.trydev.carimasker.MainActivity
import id.trydev.carimasker.R
import id.trydev.carimasker.ui.login.LoginActivity
import id.trydev.carimasker.ui.onboarding.OnboardingActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        to_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btn_register.setOnClickListener {
            registerViewModel.doRegister(edt_email.text.toString(), edt_password.text.toString())
            progressBar.visibility = View.VISIBLE
        }

        registerViewModel.getResponse().observe(this, Observer { response ->
            if (response != null) {
                progressBar.visibility = View.GONE
                if (response.get("isSuccess") == true) {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "${response.get("message")}", Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun validate(): Boolean {
        if (edt_email.text.toString().isEmpty()) {
            edt_email.requestFocus()
            edt_email.error = "Wajib diisi!"
            return false
        }

        if (edt_password.text.toString().isEmpty()) {
            edt_password.requestFocus()
            edt_password.error = "Wajib diisi!"
            return false
        }

        if (edt_konfirm_password.text.toString().isEmpty()) {
            edt_konfirm_password.requestFocus()
            edt_konfirm_password.error = "Wajib diisi!"
            return false
        }

        if (edt_password.text.toString() != edt_konfirm_password.text.toString()) {
            edt_konfirm_password.requestFocus()
            edt_konfirm_password.error = "Password tidak cocok!"
            return false
        }
        return true
    }
}
