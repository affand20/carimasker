package id.trydev.carimasker.ui.akun

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.carimasker.model.User

class AkunViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()

    private val user = MutableLiveData<HashMap<String, Any>>()

    fun getUser(): MutableLiveData<HashMap<String, Any>>? {
        mAuth.currentUser?.let {
            mFirestore.collection("users")
                .document(it.uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    user.postValue(hashMapOf(
                        "isSuccess" to true,
                        "data" to snapshot.toObject(User::class.java) as Any
                    ))
                }
                .addOnFailureListener {exception ->
                    user.postValue(hashMapOf(
                        "isSuccess" to false,
                        "message" to exception.localizedMessage
                    ))
                }
            return user
        }
        return null
    }
}