package id.trydev.carimasker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import id.trydev.carimasker.ui.login.LoginActivity
import id.trydev.carimasker.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (mAuth.currentUser==null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else if (
                    mAuth.currentUser?.displayName==null ||
                            mAuth.currentUser?.photoUrl == null
                ) {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                    finish()
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 1500)


    }
}
