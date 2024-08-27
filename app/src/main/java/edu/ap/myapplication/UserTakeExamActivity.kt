package edu.ap.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Date

class UserTakeExamActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firestore: FirebaseFirestore
    private var examId: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_take_exam)

        firestore = FirebaseFirestore.getInstance()

        examId = intent.getStringExtra("EXAM_DOCUMENT_ID")
        userId = intent.getStringExtra("USER_DOCUMENT_ID")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocationAndSaveResult()
    }

    private fun getLastLocationAndSaveResult() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    val address = getAddressFromAPI(location.latitude, location.longitude)
                    saveResult(location.latitude, location.longitude, address)
                }
            } else {
                Toast.makeText(
                    this,
                    "Unable to get location. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                CoroutineScope(Dispatchers.Main).launch {
                    val address = getAddressFromAPI(0.00, 0.00)
                    saveResult(0.00, 0.00, address)
                }
            }
        }
    }
    private fun saveResult(latitude: Double, longitude: Double, address: String?) {
        val resultData = hashMapOf(
            "userRef" to firestore.collection("users").document(userId!!),
            "examRef" to firestore.collection("exams").document(examId!!),
            "startTime" to Timestamp(Date()),
            "location" to GeoPoint(latitude, longitude),
            "address" to address,
        )
        Log.d("info", address.toString())
        firestore.collection("results")
            .add(resultData)
            .addOnSuccessListener {
                Toast.makeText(this, "Exam started successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserQuizActivity::class.java)
                intent.putExtra("EXAM_DOCUMENT_ID", examId)
                intent.putExtra("USER_DOCUMENT_ID", userId)
                intent.putExtra("RESULT_DOCUMENT_ID", it.id)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to start exam: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private suspend fun getAddressFromAPI(lat: Double, lon: Double): String? {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lon"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                return@withContext if (response.isSuccessful) {
                    val json = JSONObject(response.body?.string() ?: "")
                    json.getString("display_name")
                } else {
                    null
                }
            }
        }
    }

    private fun navigateToRootActivity() {
        Toast.makeText(this, "Unable to get location. Returning to Home.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RootActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
