package edu.ap.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.myapplication.databinding.ActivityUserExamUserListBinding

class UserExamUserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserExamUserListBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userUserAdapter: UserUserAdapter
    private var examId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserExamUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        examId = intent.getStringExtra("EXAM_ID")
        Log.d("info", "Received EXAM_ID: $examId")

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        userUserAdapter = UserUserAdapter(emptyList(),examId ?: "", this)
        binding.recyclerView.adapter = userUserAdapter

        loadUsers()
    }

    private fun loadUsers() {
        examId?.let { id ->
            firestore.collection("exams").document(id).collection("users")
                .get()
                .addOnSuccessListener { result ->
                    val userIds = result.documents.mapNotNull { it.getString("userId") }
                    loadUserDetails(userIds)
                }
                .addOnFailureListener { exception ->
                    Log.w("UserExamUserListActivity", "Error getting users.", exception)
                }
        }
    }

    private fun loadUserDetails(userIds: List<String>) {
        val userUsers = mutableListOf<UserUser>()

        userIds.forEach { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val userUser = documentSnapshot.toObject(UserUser::class.java)
                    if (userUser != null) {
                        userUser.id = userId
                        Log.d("info","id: ${userUser.id} firstName: ${userUser.firstName}")
                        userUsers.add(userUser)
                        userUserAdapter.submitList(userUsers)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("UserExamUserListActivity", "Error getting user details.", exception)
                }
        }
    }
}