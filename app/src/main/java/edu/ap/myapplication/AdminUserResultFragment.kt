
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.myapplication.R
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.File


data class ExamResult(
    var score: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var examTitle: String = ""
)

class AdminUserResultFragment : Fragment() {

    private lateinit var userIdTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adminUserResultAdapter: AdminUserResultAdapter
    private val examResultsList = mutableListOf<ExamResult>()
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_admin_user_result, container, false)

        firstNameTextView = view.findViewById(R.id.textViewFirstName)
        lastNameTextView = view.findViewById(R.id.textViewLastName)
        recyclerView = view.findViewById(R.id.recyclerViewExams)
        mapView = view.findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        val osmdroidBasePath = Environment.getExternalStorageDirectory().toString() + "/osmdroid"
        Configuration.getInstance().setOsmdroidBasePath(File(osmdroidBasePath))
        Configuration.getInstance().setOsmdroidTileCache(File(osmdroidBasePath, "tiles"))


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adminUserResultAdapter = AdminUserResultAdapter(examResultsList)
        recyclerView.adapter = adminUserResultAdapter


        val userId = arguments?.getString("userId")
        val firstName = arguments?.getString("firstName")
        val lastName = arguments?.getString("lastName")

        if (userId != null) {
            Log.d("info",userId)
        }else{
            Log.d("info","userId null")
        }

        firstNameTextView.text = "First Name: $firstName"
        lastNameTextView.text = "Last Name: $lastName"

        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        val mapController: IMapController = mapView.controller
        mapController.setZoom(15.0)


        if (userId != null) {
            fetchExamResultsForUser(userId)
        }

        return view
    }

    private fun fetchExamResultsForUser(userId: String) {
        val db = FirebaseFirestore.getInstance()


        val userRef = db.document("/users/$userId")


        db.collection("results")
            .whereEqualTo("userRef", userRef)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("info", "Success: Fetched ${querySnapshot.size()} exam results")

                examResultsList.clear()

                for (document in querySnapshot.documents) {
                    Log.d("info", "Document data: ${document.getLong("score")}")
                    Log.d("info", "Document data: ${document.getGeoPoint("location")}")
                    Log.d("info", "Document data: ${document.getDocumentReference("examRef")}")

                    val result = document.toObject(ExamResult::class.java)
                    result?.let {

                        it.score = document.getLong("score")?.toInt() ?: 0

                        val geoPoint = document.getGeoPoint("location")
                        geoPoint?.let { point ->
                            it.latitude = point.latitude
                            it.longitude = point.longitude
                        } ?: run {
                            it.latitude = 0.0
                            it.longitude = 0.0
                        }

                        val examRef = document.getDocumentReference("examRef")

                        examRef?.let { ref ->
                            fetchExamTitle(ref, it)
                        }
                    } ?: Log.d("info", "Failed to convert document to ExamResult")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("info", "Failure: Error fetching exam results")
                Log.e("info", "Error fetching exam results: ", exception)
            }
    }

    private fun fetchExamTitle(examRef: DocumentReference, examResult: ExamResult) {
        val db = FirebaseFirestore.getInstance()


        examRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {

                    examResult.examTitle = documentSnapshot.getString("title") ?: "Unknown Title"
                    Log.d("info", "Fetched exam title: ${examResult.examTitle} for examRef: ${examRef.path}")
                } else {
                    examResult.examTitle = "Unknown Title"
                    Log.d("info", "Document does not exist for examRef: ${examRef.path}")
                }

                examResultsList.add(examResult)
                updateMap(examResult.latitude,examResult.longitude,examResult.examTitle)
                adminUserResultAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->

                examResult.examTitle = "Error Fetching Title"
                Log.e("info", "Error fetching exam title for examRef: ${examRef.path}", exception)

                examResultsList.add(examResult)
                adminUserResultAdapter.notifyDataSetChanged()
            }
    }
    private fun updateMap(latitude: Double, longitude: Double, examTitle: String) {
        val mapController: IMapController = mapView.controller
        val geoPoint = GeoPoint(latitude, longitude)
        mapController.setCenter(geoPoint)

        val marker = org.osmdroid.views.overlay.Marker(mapView)
        marker.position = geoPoint
        marker.title = examTitle
        mapView.overlays.add(marker)
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
