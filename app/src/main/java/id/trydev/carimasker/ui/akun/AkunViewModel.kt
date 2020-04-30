package id.trydev.carimasker.ui.akun

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import id.trydev.carimasker.model.Katalog
import id.trydev.carimasker.model.User

class AkunViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()

    private val user = MutableLiveData<HashMap<String, Any>>()
    private val stock = MutableLiveData<HashMap<String, Any>>()

    private val maskStock = MutableLiveData<HashMap<String, Any>>()
    private val apdStock = MutableLiveData<HashMap<String, Any>>()
    private val handsanitizerStock = MutableLiveData<HashMap<String, Any>>()
    private val thermalgunStock = MutableLiveData<HashMap<String, Any>>()
    private val glovesStock = MutableLiveData<HashMap<String, Any>>()
    private val othersStock = MutableLiveData<HashMap<String, Any>>()

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

    fun getStock(): MutableLiveData<HashMap<String, Any>> {
        mAuth.currentUser?.let {user ->

            val listKatalog = mutableListOf<Katalog>()

            mFirestore.collection("catalogs")
                .whereEqualTo("ownerId", user.uid)
                .get()
                .addOnSuccessListener {result ->
                    Log.d("ONSUCCESS",result.documents.toString())
                    for (document in result) {
                        Log.d("RESULT", "${document.id} => ${document.data}")
                        listKatalog.add(document.toObject(Katalog::class.java))
                    }

                    stock.postValue(hashMapOf(
                        "isSuccess" to true,
                        "catalogs" to listKatalog
                    ))

                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    Log.d("ERROR", it.localizedMessage)

                    stock.postValue(hashMapOf(
                        "isSuccess" to false,
                        "message" to it.localizedMessage
                    ))

                    return@addOnFailureListener
                }

        }

        return stock
    }

    fun observeStock(category:String): MutableLiveData<HashMap<String, Any>> {
        return when (category) {
            "mask" -> maskStock
            "apd" -> apdStock
            "handsanitizer" -> handsanitizerStock
            "gloves" -> glovesStock
            "thermalgun" -> thermalgunStock

            else -> stock
        }
    }
}