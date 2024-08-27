package edu.ap.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.myapplication.databinding.ActivityExamListBinding

class UserExamListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamListBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var examAdapter: UserExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExamListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        examAdapter = UserExamAdapter(emptyList()) { examId ->
            val intent = Intent(this, UserExamUserListActivity::class.java)
            intent.putExtra("EXAM_ID", examId)
            startActivity(intent)
        }

        binding.recyclerView.adapter = examAdapter

        loadExams()
    }

    private fun loadExams() {
        firestore.collection("exams")
            .get()
            .addOnSuccessListener { result ->
                val exams = result.map { document ->
                    Pair(document.id, document.getString("title") ?: "No Title")
                }
                examAdapter.submitList(exams)
            }
            .addOnFailureListener { exception ->
                Log.w("ExamListActivity", "Error getting exams.", exception)
            }
    }
}
