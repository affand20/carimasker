package id.trydev.carimasker.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.carimasker.model.User

class HomeViewModel : ViewModel() {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    private val updateResponse = MutableLiveData<HashMap<String, Any>>()
    private val userRespose = MutableLiveData<HashMap<String, Any>>()
    private val stok = MutableLiveData<HashMap<String, Any>>()


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

    fun getAllUserLocation() : LiveData<HashMap<String, Any>> {
        val listUser = mutableListOf<User>()
        mFirestore.collection("users")
            .get()
            .addOnSuccessListener {snapshot ->
                for (data in snapshot) {
                    listUser.add(data.toObject(User::class.java))
                }
                this.userRespose.postValue(hashMapOf(
                    "isSuccess" to true,
                    "data" to listUser
                ))
            }
            .addOnFailureListener {
                this.userRespose.postValue(hashMapOf(
                    "isSuccess" to false,
                    "message" to it.localizedMessage
                ))
            }
        return userRespose
    }

    fun getUpdateStok(uid:String): LiveData<HashMap<String, Any>> {
        mFirestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception!=null) {
                    Log.d("ERROR", "${exception.localizedMessage}")
                    stok.postValue(hashMapOf(
                        "isSuccess" to false,
                        "message" to exception.localizedMessage
                    ))
                }

                if (snapshot!= null && snapshot.exists()) {
                    Log.d("SNAPSHOT", "${snapshot.data?.get("masker")}")
                    val stokMasker = snapshot.data?.get("masker")
                    val stokHandsanitizer = snapshot.data?.get("handsanitizer")
                    val stokApd = snapshot.data?.get("apd")

                    val responseMap = hashMapOf<String, Any>(
                        "isSuccess" to true
                    )
                    if (stokMasker!=null) {
                        responseMap.put("masker", stokMasker)
                    }
                    if (stokHandsanitizer!=null) {
                        responseMap.put("handsanitizer", stokHandsanitizer)
                    }
                    if (stokApd!=null) {
                        responseMap.put("apd", stokApd)
                    }

                    this.stok.postValue(responseMap)
                }
            }
        return stok
    }
}