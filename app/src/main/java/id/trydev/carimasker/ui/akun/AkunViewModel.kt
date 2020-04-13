package id.trydev.carimasker.ui.akun

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import id.trydev.carimasker.model.User

class AkunViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()

    private val user = MutableLiveData<HashMap<String, Any>>()
    private val stok = MutableLiveData<HashMap<String, Any>>()

    fun getUser(): LiveData<HashMap<String, Any>>? {
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

    fun updateStok(data: HashMap<String, HashMap<String, Any>>): LiveData<HashMap<String, Any>>? {
        mAuth.currentUser?.let {user ->
            val ref = mFirestore.collection("users").document(user.uid)
            mFirestore.runBatch {batch ->
                batch.update(ref, "masker", data["masker"])
                batch.update(ref, "handsanitizer", data["handsanitizer"])
                batch.update(ref, "apd", data["apd"])
            }.addOnSuccessListener {
                getUser()
                this.user.postValue(hashMapOf(
                    "isSucces" to true
                ))
            }
            .addOnFailureListener {
                Log.d("ERROR", it.localizedMessage)
                this.user.postValue(hashMapOf(
                    "isSuccess" to false,
                    "message" to it.localizedMessage
                ))
            }
        }
        return this.user
    }

    fun getUpdateStok(): LiveData<HashMap<String, Any>> {
        mAuth.currentUser?.let {user ->
            mFirestore.collection("users")
                .document(user.uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception!=null) {
                        Log.d("ERROR", "${exception.localizedMessage}")
                        stok.postValue(hashMapOf(
                            "isSuccess" to false,
                            "message" to exception.localizedMessage
                        ))
                    }

                    if (snapshot!= null && snapshot.exists()) {

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
        }
        return stok
    }
}