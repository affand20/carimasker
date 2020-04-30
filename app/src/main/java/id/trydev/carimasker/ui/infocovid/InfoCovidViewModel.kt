package id.trydev.carimasker.ui.infocovid

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import id.trydev.carimasker.model.Corona
import org.json.JSONObject
import java.lang.Exception

class InfoCovidViewModel : ViewModel() {

    private val url = "https://indonesia-covid-19.mathdro.id/api/provinsi"
    private val corona = MutableLiveData<HashMap<String, Any>>()

    fun getCovidUpdate(province:String = "Jawa Timur"): LiveData<HashMap<String, Any>> {
        val client = AsyncHttpClient()

        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try {
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val responseArray = responseObject.getJSONArray("data")

                    Log.d("JSON ARRAY", "$responseArray")

                    for (i in 0 until responseArray.length()) {
                        val data = responseArray.getJSONObject(i)
                        if (data.getString("provinsi") == province) {
                            val corona = Corona()
                            corona.provinsi = data.getString("provinsi")
                            corona.positif = data.getInt("kasusPosi")
                            corona.sembuh = data.getInt("kasusSemb")
                            corona.meninggal = data.getInt("kasusMeni")

                            Log.d("CORONA", "$corona")

                            this@InfoCovidViewModel.corona.postValue(hashMapOf(
                                "isSuccess" to true,
                                "data" to corona
                            ))
                            break
                        }
                    }

                } catch (e:Exception) {
                    this@InfoCovidViewModel.corona.postValue(hashMapOf(
                        "isSuccess" to false,
                        "message" to e.localizedMessage
                    ))
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable?
            ) {
                this@InfoCovidViewModel.corona.postValue(hashMapOf(
                    "isSuccess" to false,
                    "message" to "${error?.printStackTrace()}"
                ))
            }

        })

        return this.corona
    }

}