package id.trydev.carimasker.ui.katalog

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.trydev.carimasker.model.Katalog

class KatalogViewModel : ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mStorage = FirebaseStorage.getInstance()

    private val response = MutableLiveData<HashMap<String, Any>>()

    fun getKatalog(category:String) {
        mAuth.currentUser?.let { user ->
            mFirestore.collection("catalogs")
                .whereEqualTo("ownerId", user.uid)
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener {documents ->
                    val listCatalog = mutableListOf<Katalog>()

                    for (doc in documents) {
                        Log.d("DOCS_KATALOG", "$doc")
                        listCatalog.add(doc.toObject(Katalog::class.java))
                    }

                    response.postValue(hashMapOf(
                        "isSuccess" to true,
                        "catalogs" to listCatalog
                    ))
                }
                .addOnFailureListener {
                    response.postValue(hashMapOf(
                        "isSuccess" to false,
                        "message" to it.localizedMessage
                    ))
                }

        }
    }

    fun observeResponse(): MutableLiveData<HashMap<String, Any>> {
        return response
    }

}
