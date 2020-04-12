package id.trydev.carimasker.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()

    private val response = MutableLiveData<HashMap<String, Any>>()

    fun doLogin(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val response = hashMapOf<String, Any>(
                    "isSuccess" to true
                )
                this.response.postValue(response)
            }
            .addOnFailureListener {
                val response = hashMapOf<String, Any>(
                    "isSuccess" to false,
                    "message" to it.localizedMessage
                )
                this.response.postValue(response)
            }
    }

    fun getResponse(): LiveData<HashMap<String, Any>> {
        return response
    }

}