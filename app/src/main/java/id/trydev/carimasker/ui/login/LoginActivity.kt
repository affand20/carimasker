package id.trydev.carimasker.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import id.trydev.carimasker.MainActivity
import id.trydev.carimasker.R
import id.trydev.carimasker.ui.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        to_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        btn_login.setOnClickListener {
            if (validate()) {
                loginViewModel.doLogin(edt_email.text.toString(), edt_password.text.toString())
                progressBar.visibility = View.VISIBLE
            }
        }

        loginViewModel.getResponse().observe(this, Observer { response ->
            if (response != null) {
                progressBar.visibility = View.GONE
                if (response.get("isSuccess")==true) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "${response.get("message")}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun validate():Boolean {
        if (edt_email.text.toString().isEmpty()) {
            edt_email.error = "Wajib diisi!"
            edt_email.requestFocus()
            return false
        }
        if (edt_password.text.toString().isEmpty()) {
            edt_password.error = "Wajib diisi!"
            edt_password.requestFocus()
            return false
        }
        return true
    }
}
