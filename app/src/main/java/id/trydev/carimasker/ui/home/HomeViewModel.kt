package id.trydev.carimasker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    private val updateResponse = MutableLiveData<HashMap<String, Any>>()


    fun updateCoordinate(lat:Double, long:Double) {
        mAuth.currentUser?.uid?.let {uid ->
            mFirestore.collection("users")
                .document(uid)
                .update(mapOf(
                    "latitude" to lat,
                    "longitude" to long
                ))
                .addOnSuccessListener {
                    updateResponse.postValue(hashMapOf(
                        "isSuccess" to true
                    ))
                }
                .addOnFailureListener {
                    updateResponse.postValue(hashMapOf(
                        "isSuccess" to false,
                        "message" to it.localizedMessage
                    ))
                }
        }
    }

    fun getUpdateResponse(): LiveData<HashMap<String, Any>> {
        return updateResponse
    }
}