package id.trydev.carimasker.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.carimasker.model.User

class RegisterViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()

    private val response = MutableLiveData<HashMap<String, Any>>()

    fun doRegister(email:String, password:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                it.user?.let {user ->
                    val data = User(user.uid, email)
                    mFirestore.collection("users")
                        .document(user.uid)
                        .set(data)
                        .addOnSuccessListener {
                            val response = hashMapOf<String, Any>(
                                "isSuccess" to true
                            )
                            this.response.postValue(response)
                        }
                        .addOnFailureListener { exception ->
                            val response = hashMapOf<String, Any>(
                                "isSuccess" to false,
                                "message" to exception.localizedMessage
                            )
                            this.response.postValue(response)
                        }
                }

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