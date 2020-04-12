package id.trydev.carimasker.ui.onboarding

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class OnboardingViewModel: ViewModel() {

    private val mAuth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mStorage = FirebaseStorage.getInstance()

    private val response = MutableLiveData<HashMap<String, Any>>()

    fun uploadPersonalData(name:String, phone:String, address:String, profileImage: Uri) {
        val ref = mStorage.getReference("profile/${mAuth.currentUser?.uid}")
            ref.putFile(profileImage)
            .continueWithTask {task ->
                if (!task.isSuccessful) {
                    task?.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        val url = task.result
                        Log.d("ON COMPLETE UPLOAD", "$url")

                        mAuth.currentUser?.let {user ->
                            mFirestore.collection("users")
                                .document(user.uid)
                                .update(mapOf<String, Any>(
                                    "fullname" to name,
                                    "phone" to phone,
                                    "address" to address,
                                    "profileUrl" to "$url"
                                ))
                                .addOnSuccessListener {
                                    val profileUpdate = UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .setPhotoUri(url)
                                        .build()
                                    user.updateProfile(profileUpdate)
                                        .addOnSuccessListener {
                                            response.postValue(hashMapOf(
                                                "isSuccess" to true
                                            ))
                                        }
                                        .addOnFailureListener {
                                            response.postValue(hashMapOf(
                                                "isSuccess" to false,
                                                "message" to "${task.exception?.localizedMessage}"
                                            ))
                                        }
                                }
                                .addOnFailureListener {
                                    response.postValue(hashMapOf(
                                        "isSuccess" to false,
                                        "message" to "${task.exception?.localizedMessage}"
                                    ))
                                }
                        }

                    } else {
                        response.postValue(hashMapOf(
                            "isSuccess" to false,
                            "message" to "${task.exception?.localizedMessage}"
                        ))
                    }
                }
    }

    fun getResponse(): LiveData<HashMap<String, Any>> {
        return response
    }

}