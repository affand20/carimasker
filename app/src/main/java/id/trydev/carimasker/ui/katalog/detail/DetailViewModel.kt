package id.trydev.carimasker.ui.katalog.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.trydev.carimasker.model.Katalog

class DetailViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mStorage = FirebaseStorage.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()

    fun getDetail(id: String): MutableLiveData<HashMap<String, Any?>> {
        val response = MutableLiveData<HashMap<String, Any?>>()
        mAuth.currentUser?.let {user ->
            mFirestore.collection("catalogs")
                .document(id)
                .addSnapshotListener { snapshot, e ->
                    Log.d("ONUPDATE", "UPDATED!")
                    if (e!=null) {
                        response.postValue(hashMapOf(
                            "isSuccess" to false,
                            "message" to e.localizedMessage
                        ))
                    }

                    if (snapshot!=null && snapshot.exists()) {
                        response.postValue(hashMapOf(
                            "isSuccess" to true,
                            "catalog" to snapshot.toObject(Katalog::class.java)
                        ))
                    } else {
                        response.postValue(hashMapOf(
                            "isSuccess" to true,
                            "catalog" to null
                        ))
                        Log.d("DATA NULL", "DATA EMPTY")
                    }
                }
//                .get()
//                .addOnSuccessListener {
//                    response.postValue(hashMapOf(
//                        "isSuccess" to true,
//                        "catalog" to it.toObject(Katalog::class.java)
//                    ))
//                }
//                .addOnFailureListener {
//                    response.postValue(hashMapOf(
//                        "isSuccess" to false,
//                        "message" to it.localizedMessage
//                    ))
//                }
        }
        return response
    }

    fun updateDetail(katalog: Katalog) {
        mAuth.currentUser?.let {user ->
            mFirestore.collection("catalogs")

        }
    }

    fun incrementStock(id: String) {
        FirebaseFirestore.getInstance().collection("catalogs")
            .document(id)
            .update("stock", FieldValue.increment(1))
    }

    fun decrementStock(id:String) {
        FirebaseFirestore.getInstance().collection("catalogs")
            .document(id)
            .update("stock", FieldValue.increment(-1))
    }

    fun deleteStock(id: String) {
        FirebaseFirestore.getInstance().collection("catalogs")
            .document(id)
            .delete()

    }

}
