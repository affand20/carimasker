package id.trydev.carimasker.ui.clientkatalog

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import id.trydev.carimasker.model.Katalog

class ClientKatalogViewModel : ViewModel() {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val response = MutableLiveData<HashMap<String, Any>>()

    fun clientGetKatalog(userId: String) {
        mFirestore.collection("catalogs")
            .whereEqualTo("ownerId", userId)
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

    fun getResponse(): MutableLiveData<HashMap<String, Any>> {
        return response
    }
}
