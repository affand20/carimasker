package id.trydev.carimasker.ui.katalog.upload

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.trydev.carimasker.model.Katalog

class UploadKatalogViewModel: ViewModel() {

    private val response = MutableLiveData<HashMap<String, Any>>()

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val mStorage = FirebaseStorage.getInstance()

    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    fun uploadKatalog(katalog: Katalog) {
        val id = mFirestore.collection("catalogs").document().id
        val listImage = katalog.photo
        katalog.id = id
        katalog.photo = listOf()

        val ref = mFirestore.collection("catalogs").document(id)

        mAuth.currentUser?.let {user ->
            katalog.ownerId = user.uid
            ref.set(katalog)
                .addOnSuccessListener {
                    uploadKatalogImage(listImage, ref)
                    response.postValue(hashMapOf(
                        "isSuccess" to true,
                        "message" to "Foto anda sedang di upload, mungkin membtuhkan waktu beberapa menit untuk menyelesaikannya"
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

    private fun uploadKatalogImage(listImage:List<String>?, docRef: DocumentReference) {
        listImage.let {
            it?.forEachIndexed {index, item ->
                Log.d("FOR_EACH_KATALOG", "$item, $index")
                val ref =  mStorage.getReference("catalogs/$uid/${docRef.id}/${item.toUri().lastPathSegment}")

                ref.putFile(item.toUri())
                    .continueWithTask {task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {exception ->
                                throw exception
                            }
                        }
                        ref.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val url = task.result.toString()
                            if (index==0) {
                                docRef.update(mapOf(
                                    "photo" to listOf(url)
                                ))
                            } else {
                                docRef.update("photo", FieldValue.arrayUnion(url))
                            }

                        } else {
                            response.postValue(hashMapOf(
                                "isSuccess" to false,
                                "message" to "${task.exception?.localizedMessage}"
                            ))
                            return@addOnCompleteListener
                        }
                    }
            }
        }
    }

    fun updateKatalog(idCategory: String, katalog: HashMap<String, Any>) {
        val ref = mFirestore.collection("catalogs").document(idCategory)

        ref.update(katalog)
            .addOnSuccessListener {
                Log.d("PHOTO", "${katalog["photo"]}")
                if (katalog["photo"]!=null) {
                    uploadKatalogImage(katalog["photo"] as List<String>, ref)
                }
                response.postValue(hashMapOf(
                    "isSuccess" to true,
                    "message" to "Foto anda sedang di update, refresh halaman beberapa saaat lagi"
                ))
            }
            .addOnFailureListener {
                response.postValue(hashMapOf(
                    "isSuccess" to false,
                    "message" to it.localizedMessage
                ))
            }

    }

    fun getKatalog(idCategory:String): MutableLiveData<HashMap<String, Any?>> {
        val response = MutableLiveData<HashMap<String, Any?>>()
        mFirestore.collection("catalogs")
            .document(idCategory)
            .get()
            .addOnSuccessListener {snapshot ->
                response.postValue(hashMapOf(
                    "isSuccess" to true,
                    "catalog" to snapshot.toObject(Katalog::class.java)
                ))
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                response.postValue(hashMapOf(
                    "isSuccess" to false,
                    "message" to it.localizedMessage
                ))
            }
        return response
    }

    fun getResponse(): LiveData<HashMap<String, Any>> {
        return response
    }

}